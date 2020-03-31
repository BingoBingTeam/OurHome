package com.lotus.opengallery.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lotus.opengallery.R;
import com.lotus.opengallery.activity.ChooseFileActivity;
import com.lotus.opengallery.activity.ShowAdjunctActivity;
import com.lotus.opengallery.utils.NativeImageLoader;
import com.lotus.opengallery.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 文件选择adapter，根据不同type进行设置
 * 图片、视频、音频通用
 *
 * @author xhq
 */
public class ShowFileRecyclerAdapter extends RecyclerView.Adapter<ShowFileRecyclerAdapter.ViewHolder> {

    /** 附件 - 类型 - 音频 */
    public static final String _ATTACHMENT_TYPE_VOICE = "voice";
    /** 附件 - 类型 - 图片 */
    public static final String _ATTACHMENT_TYPE_IMAGE = "image";
    /** 附件 - 类型 - 视频 */
    public static final String _ATTACHMENT_TYPE_VIDEO = "video";

    private List<String> mDatas = new ArrayList<>();
    private List<String> mChoseDatas = new ArrayList<>();
    private Context mContext;
    private boolean mIsSingle = false;//是否是单选
    private int mChosePosition = -1;//上次选择的位序
    private final static int NO_MAX_CHOOSE_NUMBER = -1;
    private int mMaxChooseNumber = NO_MAX_CHOOSE_NUMBER;//最多可选择文件的个数，默认无最大限制
    private OnItemClickListener mOnItemClickListener;

    public ShowFileRecyclerAdapter(Context context, boolean isSingle) {
        this.mContext = context;
        this.mIsSingle = isSingle;
        this.mChoseDatas = new ArrayList<>();
    }

    public void addData(List<String> data, boolean isReset) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        if (isReset) {
            mDatas.clear();
        }
        if (data != null && data.size() > 0) {
            int positionStart = mDatas.size();
            mDatas.addAll(data);
            this.notifyItemRangeChanged(positionStart, data.size());
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setMaxChooseNumber(int mMaxChooseNumber) {
        this.mMaxChooseNumber = mMaxChooseNumber;
    }

    /**
     * 获取选中的文件
     */
    public List<String> getSelectItems() {
        List<String> list = new ArrayList<>(mChoseDatas);
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_show_file, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        String item = mDatas.get(position);
        String[] split = item.split(ChooseFileActivity.DATA_SPLIT_LOG);
        String filePath = split[0];
        String fileType = split[1];
        if (mChoseDatas.contains(item)) {
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }
        viewHolder.mVideoPlay.setVisibility(View.GONE);
        viewHolder.mLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseDate(viewHolder);
            }
        });
        viewHolder.mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate(viewHolder);
            }
        });
        switch (fileType) {
            case ChooseFileActivity.TYPE_IMAGE:
                Glide.with(mContext).load(filePath).into(viewHolder.mImageView);
                break;
            case ChooseFileActivity.TYPE_VIDEO:
                viewHolder.mVideoPlay.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(filePath).into(viewHolder.mImageView);
                break;
            case ChooseFileActivity.TYPE_FILE:
                break;
        }
        viewHolder.mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看文件详情
                int index = viewHolder.getAdapterPosition();
                Intent intent = null;
                Bundle bundle = null;
                String item = mDatas.get(index);
                String[] split = item.split(ChooseFileActivity.DATA_SPLIT_LOG);
                String filePath = split[0];
                String fileType = split[1];
                switch (fileType) {
                    case _ATTACHMENT_TYPE_IMAGE:
                        intent = new Intent(mContext, ShowAdjunctActivity.class);
                        bundle = new Bundle();
                        bundle.putString(ShowAdjunctActivity.DATA_FILE_TYPE, ShowAdjunctActivity.DATA_FILE_TYPE_IMAGE);
                        bundle.putString(ShowAdjunctActivity.DATA_FILE_PATH_STRING, filePath);
                        bundle.putInt(ShowAdjunctActivity.DATA_INDEX, index);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        break;
                    case _ATTACHMENT_TYPE_VIDEO:
                        intent = new Intent(mContext, ShowAdjunctActivity.class);
                        bundle = new Bundle();
                        bundle.putString(ShowAdjunctActivity.DATA_FILE_TYPE, ShowAdjunctActivity.DATA_FILE_TYPE_VIDEO);
                        bundle.putString(ShowAdjunctActivity.DATA_FILE_PATH_STRING, filePath);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        File file = new File(filePath);
        Date date = new Date(file.lastModified());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
        String time = format.format(date);
        viewHolder.txtTime.setText(time);
        viewHolder.txtName.setText(getName(filePath));
        try {/** 文件大小*/
            viewHolder.txtSize.setText(NativeImageLoader.getInstance().getFileSize(new File(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 给CheckBox加点击动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, View.SCALE_X, vaules),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, vaules));
        set.setDuration(150);
        set.start();
    }

    /**
     * 点击后，对数据的处理
     *
     * @param viewHolder
     */
    private void chooseDate(ViewHolder viewHolder) {
        int index = viewHolder.getAdapterPosition();//当前点击的位序
        String data = mDatas.get(index);//当前点击的数据项
        //取消已选的
        if (mChoseDatas.contains(data)) {
            mChoseDatas.remove(data);
            notifyItemChanged(index);
        } else {//新增选项
            if(mMaxChooseNumber != NO_MAX_CHOOSE_NUMBER && mChoseDatas.size() >= mMaxChooseNumber){
                ToastUtil.showToast(mContext,"最多可选择" + mMaxChooseNumber + "个");
            }else {
                addAnimation(viewHolder.mCheckBox);//如果是未选中的CheckBox,则添加动画
                if (mIsSingle) {//单选模式
                    mChoseDatas.clear();
                }
                mChoseDatas.add(data);
                if (mIsSingle && mChosePosition >= 0) {
                    notifyItemChanged(mChosePosition);
                }
                notifyItemChanged(index);
                mChosePosition = index;
            }
        }
        if(mOnItemClickListener != null){
            mOnItemClickListener.changeChoseData(mChoseDatas.size());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mVideoPlay;
        CheckBox mCheckBox;
        TextView txtSize, txtName, txtTime;
        RelativeLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mVideoPlay = (ImageView) itemView.findViewById(R.id.image_play);
            mImageView = (ImageView) itemView.findViewById(R.id.image_adapter_file_show);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_adapter_file_show);
            txtSize = (TextView) itemView.findViewById(R.id.txt_size_adapter_file_show);
            txtName = (TextView) itemView.findViewById(R.id.txt_name_adapter_file_show);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time_adapter_file_show);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_adapter_file_show);
        }
    }

    public interface OnItemClickListener{
        public void changeChoseData(int choseSumNumber);
    }
}
