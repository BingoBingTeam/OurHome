package com.lotus.opengallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lotus.opengallery.R;
import com.lotus.opengallery.activity.ChooseFileActivity;
import com.lotus.opengallery.entity.FileBean;

import java.util.ArrayList;
import java.util.List;

public class ChooseFileRecyclerAdapter extends RecyclerView.Adapter<ChooseFileRecyclerAdapter.ViewHolder> {

    /**
     * 文件类型
     */
    public static final String TYPE_FILE = "file";

    private List<FileBean> mData;
    private Context mContext;
    private String type;
    private onRecyclerItemClickListener mItemClickListener;

    public ChooseFileRecyclerAdapter(Context context, String type) {
        this.mContext = context;
        this.type = type;
    }

    public void setItemClickListener(onRecyclerItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void addData(List<FileBean> data, boolean isReset) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (isReset) {
            mData.clear();
        }
        if (data != null && data.size() > 0) {
            int positionStart = mData.size();
            mData.addAll(data);
            this.notifyItemRangeChanged(positionStart, data.size());
        }
    }

    public List<FileBean> getData() {
        List<FileBean> data = new ArrayList<>();
        if(mData != null){
            data.addAll(mData);
        }
        return data;
    }

    @NonNull
    @Override
    public ChooseFileRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_choose_file, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChooseFileRecyclerAdapter.ViewHolder viewHolder, int position) {
        FileBean mFileBean = mData.get(position);
        final String path = mFileBean.getTopFilePath();
        String[] split = path.split(ChooseFileActivity.DATA_SPLIT_LOG);
        Glide.with(mContext).load(split[0]).into(viewHolder.imageView);
        viewHolder.tvTile.setText(mFileBean.getFolderName());
        viewHolder.tvCount.setText(String.valueOf(mFileBean.getFileCounts()));
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null){
                    mItemClickListener.itemClick(path,viewHolder.getAdapterPosition(), viewHolder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvCount;
        TextView tvTile;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            tvTile = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public interface onRecyclerItemClickListener {
        public void itemClick(String item, int position, RecyclerView.ViewHolder viewHolder);
    }
}
