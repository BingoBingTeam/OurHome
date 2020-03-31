package com.pager.indicator.morepopuindicator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pager.indicator.R;

import java.util.List;

/**
 * Copyright (C), 合能创越
 * Author: 蒲小飞
 * Date: 2019/3/15 11:44
 * Description: ${DESCRIPTION}
 */
public class IndicatoerMenuAdapter extends RecyclerView.Adapter<IndicatoerMenuAdapter.MyViewHolder>{

    private Context mContext;
    private List<IndicatorContentData> mDataList;
    private OnItemClickCallBack mOnItemClickCallBack;

    public IndicatoerMenuAdapter(Context context, List<IndicatorContentData> dataList){

        this.mContext = context;
        this.mDataList = dataList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_indicator_more_cell,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        final IndicatorContentData data = mDataList.get(i);
        try {
            if (data.getImg() != 0){
                myViewHolder.imageView.setImageResource(data.getImg());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        myViewHolder.textView.setText(data.getTitle());
        myViewHolder.divider.setVisibility(i%2 == 0 ? View.INVISIBLE : View.VISIBLE);

        myViewHolder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickCallBack != null){
                    mOnItemClickCallBack.onClick(myViewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 设置点击事件
     * @param onItemClickCallBack
     */
    public void setOnItemClickCallBack(OnItemClickCallBack onItemClickCallBack) {
        this.mOnItemClickCallBack = onItemClickCallBack;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout mRoot;
        private View divider;
        private ImageView imageView;
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.root);
            divider = itemView.findViewById(R.id.v_divider);
            imageView = itemView.findViewById(R.id.iv_content);
            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public interface OnItemClickCallBack{
        void onClick(int index);
    }
}
