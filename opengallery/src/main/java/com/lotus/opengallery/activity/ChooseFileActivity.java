package com.lotus.opengallery.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.opengallery.adapter.ChooseFileRecyclerAdapter;
import com.lotus.opengallery.R;
import com.lotus.opengallery.fragment.ShowFileFragment;
import com.lotus.opengallery.entity.FileBean;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 图片文件夹查看页面
 */
public class ChooseFileActivity extends AppCompatActivity {

    /**
     * 文件选择监听action
     */
    public static final String CHOOSE_FILE_BACK_ACTION = "CHOOSE_FILE_BACK_ACTION";

    public static final String DATA_SHOW_FILE = "show_file";//选择的文件：ShowFileFragment显示的文件，即可以选择的文件
    public static final String DATA_CHOSE_FILE = "chose_file";//选择的文件：ShowFileFragment点击“确定”后使用广播发送已选的文件

    public static final String DATA_IS_SINGLE = "is_single";//是否单选：通过调用界面传入
    public static final String DATA_FILE_TYPE = "file_type";//文件类型：通过调用界面传入，ChooseFileActivity显示什么类型的文件
    public static final String DATA_MAX_CHOOSE_NUMBER = "max_choose_number";//最多可选的文件数：通过调用界面传入，ShowFileFragment选择文件时将提示剩余的可选文件个数

    public static final String DATA_SPLIT_LOG = "FileTypeSplitLog";//拆分符号
    public static final String TYPE_IMAGE = "image";//选择图片模式
    public static final String TYPE_VIDEO = "video";//选择视频模式
    public static final String TYPE_IMAGE_AND_VIDEO = "image_and_video";//选择图片和视频模式
    public static final String TYPE_AUDIO = "audio";//选择音频模式
    public static final String TYPE_FILE = "file";//选择文件模式

    private RecyclerView mRecyclerViewChooseFile;

    private String mFileType = "";
    private boolean mIsSingle = false;
    private final static int NO_MAX_CHOOSE_NUMBER = -1;
    private int mMaxChooseNumber = NO_MAX_CHOOSE_NUMBER;//最多可选择文件的个数，默认无最大限制

    private ChooseFileRecyclerAdapter mAdapter;
    private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();
    private QueryFileAsyncTask mQueryFileAsyncTask = null;

    private final static int SCAN_OK = 1;
    private Handler mHandler = new RefreshHandler(ChooseFileActivity.this);

    private static class RefreshHandler extends Handler {
       final WeakReference<ChooseFileActivity> mContextReference;

        public RefreshHandler(ChooseFileActivity activity) {
            mContextReference = new WeakReference<ChooseFileActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ChooseFileActivity activity = (ChooseFileActivity) mContextReference.get();
            if (activity != null) {
                if (msg.what == SCAN_OK) {
                    activity.mAdapter.addData(activity.subGroupOfFile(activity.mGroupMap), true);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        mRecyclerViewChooseFile = this.findViewById(R.id.recycler_view_file_select);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DATA_FILE_TYPE)) {
            mFileType = intent.getStringExtra(DATA_FILE_TYPE);
            if (intent.hasExtra(DATA_IS_SINGLE)) {
                mIsSingle = intent.getBooleanExtra(DATA_IS_SINGLE, false);
            }
            if(mIsSingle){
                mMaxChooseNumber = 1;
            }else {
                if (intent.hasExtra(DATA_MAX_CHOOSE_NUMBER)) {
                    mMaxChooseNumber = intent.getIntExtra(DATA_MAX_CHOOSE_NUMBER,NO_MAX_CHOOSE_NUMBER);
                }
            }
        }
        initView();
    }

    private void initView() {
        mAdapter = new ChooseFileRecyclerAdapter(this, mFileType);
        mAdapter.setItemClickListener(new ChooseFileRecyclerAdapter.onRecyclerItemClickListener() {
            @Override
            public void itemClick(String item, int position, RecyclerView.ViewHolder viewHolder) {
                List<String> childList = mGroupMap.get(mAdapter.getData().get(position).getFolderName());
                Bundle bundle = new Bundle();
                bundle.putString(DATA_FILE_TYPE, mFileType);
                bundle.putBoolean(DATA_IS_SINGLE, mIsSingle);
                bundle.putStringArrayList(DATA_SHOW_FILE, (ArrayList<String>) childList);
                bundle.putInt(DATA_MAX_CHOOSE_NUMBER, mMaxChooseNumber);

                ShowFileFragment showPictureFragment = new ShowFileFragment();
                showPictureFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment, showPictureFragment, ShowFileFragment.TAG)
                        .addToBackStack(ShowFileFragment.TAG).commit();

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewChooseFile.setLayoutManager(gridLayoutManager);
        mRecyclerViewChooseFile.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFileData();
    }

    /**
     * 获取系统File
     */
    private void getFileData() {
        mGroupMap.clear();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mQueryFileAsyncTask != null){
            mQueryFileAsyncTask.cancel(true);
            mQueryFileAsyncTask = null;
        }
        mQueryFileAsyncTask = new QueryFileAsyncTask(mFileType);
        mQueryFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 组装分组界面RecyclerView的数据源，扫描手机的时候将图片信息放在HashMap中
     * 遍历HashMap将数据组装成List
     *
     * @param mGruopMap
     * @return
     */
    private List<FileBean> subGroupOfFile(HashMap<String, List<String>> mGruopMap) {
        List<FileBean> list = new ArrayList<FileBean>();
        if (mGruopMap.size() == 0) {
            return list;
        }
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            FileBean fileBean = new FileBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            fileBean.setFolderName(key);
            fileBean.setFileCounts(value.size());
            fileBean.setTopFilePath(value.get(0));//获取该组的第一张图片

            list.add(fileBean);
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private class QueryFileAsyncTask extends AsyncTask<Object, Integer, HashMap<String, List<String>>> {

        private String mQueryFileType = "";
        private boolean mIsImageAndVideo = false;

        public QueryFileAsyncTask(String fileType){
            if(TYPE_IMAGE_AND_VIDEO.equals(fileType)){//查询图片和视频，则以查询图片开始
                this.mQueryFileType = TYPE_IMAGE;
                this.mIsImageAndVideo = true;
            }else {
                this.mQueryFileType = fileType;
                this.mIsImageAndVideo = false;
            }
        }

        @Override
        protected HashMap<String, List<String>> doInBackground(Object... objects) {
            Uri uri = null;
            switch (mQueryFileType){
                case TYPE_IMAGE:
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    break;
                case TYPE_VIDEO:
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    break;
            }
            if (uri != null) {
                ContentResolver mContentResolver = ChooseFileActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(uri, null, null, null, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        String path = "";
                        if(TYPE_IMAGE.equals(mQueryFileType)){
                            path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            if (new File(path).length() == 0) {
                                continue;
                            }
                        }else if(TYPE_VIDEO.equals(mQueryFileType)){
                            path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                            if (new File(path).length() == 0) {
                                continue;
                            }
                        }
                        //获取该File的父路径名
                        if (new File(path).exists()) {
                            String parentName = new File(path).getParentFile().getName();
                            if (!mGroupMap.containsKey(parentName)) {//不同路劲的文件区分
                                List<String> chileList = new ArrayList<String>();
                                if(TYPE_IMAGE.equals(mQueryFileType)){
                                    path = path + DATA_SPLIT_LOG + TYPE_IMAGE;
                                }else if(TYPE_VIDEO.equals(mQueryFileType)){
                                    path = path + DATA_SPLIT_LOG + TYPE_VIDEO;
                                }
                                chileList.add(path);
                                mGroupMap.put(parentName, chileList);
                            } else {
                                if(TYPE_IMAGE.equals(mQueryFileType)){
                                    path = path + DATA_SPLIT_LOG + TYPE_IMAGE;
                                }else if(TYPE_VIDEO.equals(mQueryFileType)){
                                    path = path + DATA_SPLIT_LOG + TYPE_VIDEO;
                                }
                                mGroupMap.get(parentName).add(path);
                            }
                        }
                    }
                    mCursor.close();
                }
            }
            return mGroupMap;
        }



        @Override
        protected void onPostExecute(HashMap<String, List<String>> result) {
            super.onPostExecute(result);
            if(mIsImageAndVideo){
                mQueryFileAsyncTask = new QueryFileAsyncTask(TYPE_VIDEO);
                mQueryFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else {
                mHandler.sendEmptyMessage(SCAN_OK);
                if(mQueryFileAsyncTask != null){
                    mQueryFileAsyncTask.cancel(true);
                    mQueryFileAsyncTask = null;
                }
            }
        }
    }

}

//使用方法：
//
//多选但有个数限制的图片模式
//Intent videoIntent = new Intent(mContext, ChooseFileActivity.class);
//videoIntent.putExtra(ChooseFileActivity.DATA_FILE_TYPE, ChooseFileActivity.TYPE_IMAGE);
//videoIntent.putExtra(ChooseFileActivity.DATA_IS_SINGLE, false);
//videoIntent.putExtra(ChooseFileActivity.DATA_MAX_CHOOSE_NUMBER, 9 - mAttachmentList.size());
//mContext.startActivity(videoIntent);
//
//多选的视频模式
//Intent videoIntent = new Intent(mContext, ChooseFileActivity.class);
//videoIntent.putExtra(ChooseFileActivity.DATA_FILE_TYPE, ChooseFileActivity.TYPE_VIDEO);
//videoIntent.putExtra(ChooseFileActivity.DATA_IS_SINGLE, false);
//mContext.startActivity(videoIntent);
//
//单选的图片模式
//Intent videoIntent = new Intent(mContext, ChooseFileActivity.class);
//videoIntent.putExtra(ChooseFileActivity.DATA_FILE_TYPE, ChooseFileActivity.TYPE_IMAGE);
//videoIntent.putExtra(ChooseFileActivity.DATA_IS_SINGLE, true);
//mContext.startActivity(videoIntent);
//
//注册广播
//IntentFilter fileBackFilter = new IntentFilter();
//fileBackFilter.addAction("CHOOSE_FILE_BACK_ACTION");
//mContext.registerReceiver(mFileBackReceiver, fileBackFilter);
//
//注销广播
//mContext.unregisterReceiver(mFileBackReceiver);
//
//广播接收
//BroadcastReceiver mFileBackReceiver = new BroadcastReceiver() {
//
//   @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(ChooseFileActivity.CHOOSE_FILE_BACK_ACTION)) {//文件选择回调
//             ArrayList<String> selectPaths = intent.getStringArrayListExtra(ChooseFileActivity.DATA_CHOSE_FILE);
//             if (selectPaths == null || selectPaths.size() == 0) {
//                  return;
//             }
//             for (String path : selectPaths) {
//                  String[] split = path.split(ChooseFileActivity.DATA_SPLIT_LOG);
//                  String filePath = split[0];//文件路径
//                  String fileType = split[1];//文件类型
//             }
//        }
//    }
//};


