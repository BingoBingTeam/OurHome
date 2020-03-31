package com.lotus.opengallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.opengallery.R;
import com.lotus.opengallery.activity.ChooseFileActivity;
import com.lotus.opengallery.adapter.ShowFileRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowFileFragment extends Fragment {
    public static final String TAG = ShowFileFragment.class.getSimpleName();

    protected View mRootView;
    private RecyclerView mRecyclerViewFileShow;

    private String mFileType;
    private List<String> mShowFilePaths = new ArrayList<>();
    public static final String DATA_IS_SINGLE = "is_single";
    private ShowFileRecyclerAdapter mAdapter;
    private boolean mIsSingle = false;
    private final static int NO_MAX_CHOOSE_NUMBER = -1;
    private int mMaxChooseNumber = NO_MAX_CHOOSE_NUMBER;//最多可选择文件的个数，默认无最大限制

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_show_file, null);
        mRecyclerViewFileShow = (RecyclerView) mRootView.findViewById(R.id.list_file_show);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle && bundle.containsKey(ChooseFileActivity.DATA_SHOW_FILE)) {
            mShowFilePaths = bundle.getStringArrayList(ChooseFileActivity.DATA_SHOW_FILE);
            if (bundle.containsKey(ChooseFileActivity.DATA_FILE_TYPE)) {
                mFileType = bundle.getString(ChooseFileActivity.DATA_FILE_TYPE);
            }
            if (bundle.containsKey(DATA_IS_SINGLE)) {
                mIsSingle = bundle.getBoolean(DATA_IS_SINGLE);
            }
            if (mIsSingle) {
                mMaxChooseNumber = 1;
            } else {
                if (bundle.containsKey(ChooseFileActivity.DATA_MAX_CHOOSE_NUMBER)) {
                    mMaxChooseNumber = bundle.getInt(ChooseFileActivity.DATA_MAX_CHOOSE_NUMBER, NO_MAX_CHOOSE_NUMBER);
                }
            }
            initView();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void initView() {
//        if(mMaxChooseNumber == NO_MAX_CHOOSE_NUMBER){
//            mTbarLayout.getRightButton().setText(R.string.positive);
//        }else {
//            mTbarLayout.getRightButton().setText("确定(0/"+ mMaxChooseNumber+")");
//        }
//        mTbarLayout.setOnTbarListener(onTbarListener);
//        if (mFileType.equals(ChooseFileActivity.TYPE_IMAGE)) {
//            mTbarLayout.getCenterTextView().setText("图片选择");
//        } else if (mFileType.equals(ChooseFileActivity.TYPE_VIDEO)) {
//            mTbarLayout.getCenterTextView().setText("视频选择");
//        } else if (mFileType.equals(ChooseFileActivity.TYPE_AUDIO)) {
//            mTbarLayout.getCenterTextView().setText("音频选择");
//        } else if (mFileType.equals(ChooseFileActivity.TYPE_IMAGE_AND_VIDEO)) {
//            mTbarLayout.getCenterTextView().setText("图片视频选择");
//        } else {
//            mTbarLayout.getCenterTextView().setText("选择文件");
//        }

        mAdapter = new ShowFileRecyclerAdapter(getContext(), mIsSingle);
        mAdapter.setMaxChooseNumber(mMaxChooseNumber);
        mAdapter.addData(mShowFilePaths, true);
        if(mMaxChooseNumber != NO_MAX_CHOOSE_NUMBER){
            mAdapter.setOnItemClickListener(new ShowFileRecyclerAdapter.OnItemClickListener() {
                @Override
                public void changeChoseData(int choseSumNumber) {
//                    mTbarLayout.getRightButton().setText("确定("+ choseSumNumber + "/" + mMaxChooseNumber+")");
                }
            });
        }
        mRecyclerViewFileShow.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewFileShow.setLayoutManager(linearLayoutManager);
    }

//    TbarOptimaLayout.OnTbarListener onTbarListener = new TbarOptimaLayout.OnTbarListener() {
//
//        @Override
//        public void imageButtonClick(ImageButton imageButton, boolean isLongClick) {
//            getActivity().getSupportFragmentManager().popBackStack();
//        }
//
//        @Override
//        public void centerClick(TextView center, boolean isLongClick) {
//
//        }
//
//        @Override
//        public void buttonClick(Button button, boolean isLongClick) {//点击完成，保存图片
//            List<String> choseFilePaths = new ArrayList<>();
//            choseFilePaths = mAdapter.getSelectItems();
//            if (null == choseFilePaths || choseFilePaths.size() < 0) {
//                return;
//            }
//            EventBus.getDefault().post(choseFilePaths);//发送消息
//            Intent intent = new Intent();
//            intent.setAction(ChooseFileActivity.CHOOSE_FILE_BACK_ACTION);
//            intent.putStringArrayListExtra(ChooseFileActivity.DATA_CHOSE_FILE, (ArrayList<String>) choseFilePaths);
//            mContext.sendBroadcast(intent);
//            getActivity().finish();
//        }
//    };

}
