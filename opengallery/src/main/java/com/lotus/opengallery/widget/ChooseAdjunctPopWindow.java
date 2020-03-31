package com.lotus.opengallery.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.lotus.opengallery.R;
import com.lotus.opengallery.activity.ChooseFileActivity;
import com.lotus.opengallery.utils.ToastUtil;
import com.lotus.base.constants.BaseConstants;
import com.lotus.base.utils.apps.AppUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChooseAdjunctPopWindow extends PopupWindow implements View.OnClickListener {

    /**
     * 上下文
     */
    private Activity mContext;
    public static final int SHOW_TYPE_PICTURE = 0;
    public static final int SHOW_TYPE_VIDEO = 1;

    /**
     * 根布局
     */
    private View mRootView;
    private TextView mTvTakePicture;
    private TextView mTvTakeVideo;
    private TextView mTvOpenAlbum;
    private TextView mTvCancle;

    public ChooseAdjunctPopWindow(Activity activity) {
        this.mContext = activity;
        initConfig(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initConfig(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);
        this.setElevation(25f);
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//达到背景全部变暗的效果
//        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.setAnimationStyle(R.style.BottomPopuAnim);
        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = mContext.getWindow().getAttributes();
                mContext.getWindow().setDimAmount(0f);
                params.alpha = 1.0f;
                mContext.getWindow().setAttributes(params);
            }
        });
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pop_window_choose_adjunct, null, false);
        mTvTakePicture = mRootView.findViewById(R.id.tv_take_photo);
        mTvTakeVideo = mRootView.findViewById(R.id.tv_take_video);
        mTvOpenAlbum = mRootView.findViewById(R.id.tv_open_album);
        mTvCancle = mRootView.findViewById(R.id.tv_cancel);

        mTvTakePicture.setOnClickListener(this);
        mTvTakeVideo.setOnClickListener(this);
        mTvOpenAlbum.setOnClickListener(this);
        mTvCancle.setOnClickListener(this);
        setContentView(mRootView);
    }

    /**
     * 显示
     *
     * @param parent 关联父布局
     */
    public void show(View parent) {
        WindowManager.LayoutParams params = mContext.getWindow().getAttributes();
        params.alpha = 0.7f;
        mContext.getWindow().setDimAmount(0.7f);
        mContext.getWindow().setAttributes(params);
        this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

        AppUtil.hideSoftInputFromWindow(mContext);
    }

    @Override
    public void onClick(View v) {
         int i = v.getId();
        if (i == R.id.tv_take_photo) {//拍照
            takePicture();
            dismiss();
        } else if (i == R.id.tv_take_video) {//录像
            takeVideo();
            dismiss();
        } else if (i == R.id.tv_open_album) {//打开相册
            Intent videoIntent = new Intent(mContext, ChooseFileActivity.class);
            videoIntent.putExtra(ChooseFileActivity.DATA_FILE_TYPE, ChooseFileActivity.TYPE_IMAGE_AND_VIDEO);
            videoIntent.putExtra(ChooseFileActivity.DATA_IS_SINGLE, false);
            mContext.startActivity(videoIntent);
            dismiss();
        } else if (i == R.id.tv_cancel) {
            dismiss();
        }
    }

    private void showToast(String msg){
        ToastUtil.showToast(mContext,msg);
    }
/** ============================================== 拍照、录像、选择图片和视频=============================================================*/

    /**
     * 拍摄图片后缀
     */
    private final String IMG_SUFFIX = ".jpg";

    /**
     * 录像文件后缀
     */
    private final String VIDEO_SUFFIX = ".mp4";

    /**
     * 拍照的图片地址
     */
    private String imgFilePath;

    public String getImgFilePath() {
        return imgFilePath;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    /**
     * 录像的保存地址
     */
    private String videoFilePath;

    /**
     * 拍照的跳转
     */
    public static final int TAKE_PICTURE = 1;

    /**
     * 录像的跳转
     */
    public static final int TAKE_VIDEO = 2;

    /**
     * 打开相机--拍照
     */
    private void takePicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// 读写存储权限
                showToast("请打开文件读写权限");
                return;
            }

            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {// 相机权限
                showToast("请打开相机权限");
                return;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String time = format.format(new Date());
        File folder = new File(BaseConstants.IMAGE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String name = "IMG_" + time + "_" + System.currentTimeMillis() / 1000 + IMG_SUFFIX;
        File photFile = new File(folder, name);
        if (!photFile.exists()) {
            try {
                photFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!photFile.exists()) {
            Toast.makeText(mContext, "相机调用失败", Toast.LENGTH_SHORT).show();
            return;
        }
        imgFilePath = photFile.getAbsolutePath();

        Intent image = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (image.resolveActivity(mContext.getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT <= 23) {
                image.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photFile));
                image.putExtra(MediaStore.Images.Media.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mContext.startActivityForResult(image, TAKE_PICTURE);
            } else {
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", photFile);
                image.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                mContext.startActivityForResult(image, TAKE_PICTURE);
            }
        } else {
            Toast.makeText(mContext, "未发现可用相机应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开相机--录像
     */
    private void takeVideo() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// 读写存储权限
                showToast("请打开文件读写权限");
                return;
            }

            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {// 相机权限
                showToast("请打开相机权限");
                return;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String time = format.format(new Date());

        File folder = new File(BaseConstants.VIDEO_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = "VIDEO_" + time + "_" + System.currentTimeMillis() / 1000 + VIDEO_SUFFIX;
        File videoFile = new File(folder, fileName);
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!videoFile.exists()) {
            Toast.makeText(mContext, "相机调用失败", Toast.LENGTH_SHORT).show();
            return;
        }
        videoFilePath = videoFile.getAbsolutePath();

        Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (video.resolveActivity(mContext.getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT <= 23) {
                video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                video.putExtra(MediaStore.Images.Media.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mContext.startActivityForResult(video, TAKE_VIDEO);
            } else {
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() +".fileprovider", videoFile);
                video.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                mContext.startActivityForResult(video, TAKE_VIDEO);
            }
        } else {
            Toast.makeText(mContext, "未发现可用相机应用", Toast.LENGTH_SHORT).show();
        }
    }
}

//    //==============================================================  选择附件 =======================================================
//
//    /**
//     * 显示选择附件的弹出框
//     * @param showType
//     */
//    private void showChooseAdjunctPopWindow(int showType) {
//        if (mChooseAdjunctPopWindow == null) {
//            mChooseAdjunctPopWindow = new ChooseAdjunctPopWindow(EventReportActivity.this);
//        }
//        if (mChooseAdjunctPopWindow.isShowing()) {
//            mChooseAdjunctPopWindow.dismiss();
//        }
//        mChooseAdjunctPopWindow.show(mLayoutRoot);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_CANCELED) {
//            ToastUtil.showToast(this, "取消调用");
//            return;
//        }
//        switch (requestCode) {
//            case LocationAddressActivity.RESULT_CODE://定位ic_home_map_location
//                if (data != null) {
//                    String address = data.getStringExtra(LocationAddressActivity.DATA_ADDRESS);
//                    Double longitude = data.getDoubleExtra(LocationAddressActivity.DATA_LONGITUDE, 0.0);
//                    Double latitude = data.getDoubleExtra(LocationAddressActivity.DATA_LATITUDE, 0.0);
//                    mEdAdress.setTextValue(address);
//                    mCaseLatLng = new LatLng(latitude, longitude);
//                }
//                break;
//            case ChooseAdjunctPopWindow.TAKE_PICTURE://拍照
//                if (mChooseAdjunctPopWindow != null && !new File(mChooseAdjunctPopWindow.getImgFilePath()).exists()) {
//                    ToastUtil.showToast(this, getString(R.string.take_picture_fail));
//                }
//                DailyAttachment imageAttachment = new DailyAttachment();
//                imageAttachment.setFileUrl(mChooseAdjunctPopWindow.getImgFilePath());
//                imageAttachment.setFileType(SyncDailyConstants._ATTACHMENT_TYPE_IMAGE);
//                mAttachmentAdapter.updateData(Arrays.asList(imageAttachment));
//                break;
//            case ChooseAdjunctPopWindow.TAKE_VIDEO://录像
//                if (mChooseAdjunctPopWindow != null && !new File(mChooseAdjunctPopWindow.getVideoFilePath()).exists()) {
//                    ToastUtil.showToast(this, getString(R.string.take_video_fail));
//                }
//                DailyAttachment videoAttachment = new DailyAttachment();
//                videoAttachment.setFileUrl(mChooseAdjunctPopWindow.getVideoFilePath());
//                videoAttachment.setFileType(SyncDailyConstants._ATTACHMENT_TYPE_VIDEO);
//                mAttachmentAdapter.updateData(Arrays.asList(videoAttachment));
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 广播注册
//     */
//    private void registerReceiver() {
//        IntentFilter fileBackFilter = new IntentFilter();
//        fileBackFilter.addAction("CHOOSE_FILE_BACK_ACTION");
//        registerReceiver(mFileBackReceiver, fileBackFilter);
//    }
//
//    /**
//     * 注销广播
//     */
//    private void unRegisterReceiver() {
//        unregisterReceiver(mFileBackReceiver);
//    }
//
//    /**
//     * 文件选择返回的监听
//     */
//    BroadcastReceiver mFileBackReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ChooseFileActivity.CHOOSE_FILE_BACK_ACTION)) {//文件选择回调
//                //选择的附件路径
//                ArrayList<String> selectPaths = intent.getStringArrayListExtra(ChooseFileActivity.DATA_CHOSE_FILE);
//                if (selectPaths == null || selectPaths.size() == 0) {
//                    return;
//                }
//                List<DailyAttachment> imageAttachmentList = new ArrayList<>();
//                for (String path : selectPaths) {
//                    DailyAttachment imageAttachment = new DailyAttachment();
//                    String[] split = path.split(ChooseFileActivity.DATA_SPLIT_LOG);
//                    String filePath = split[0];
//                    String fileType = split[1];
//                    imageAttachment.setFileUrl(filePath);
//                    if (ChooseFileActivity.TYPE_IMAGE.equals(fileType)) {// 图片
//                        imageAttachment.setFileType(SyncDailyConstants._ATTACHMENT_TYPE_IMAGE);
//                    } else if (ChooseFileActivity.TYPE_VIDEO.equals(fileType)) {// 视频
//                        imageAttachment.setFileType(SyncDailyConstants._ATTACHMENT_TYPE_VIDEO);
//                    }
//                    imageAttachmentList.add(imageAttachment);
//                }
//                mAttachmentAdapter.updateData(imageAttachmentList);
//            }
//        }
//    };
