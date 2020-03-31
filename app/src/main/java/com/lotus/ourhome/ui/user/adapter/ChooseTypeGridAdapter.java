package com.lotus.ourhome.ui.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lotus.ourhome.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseTypeGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> mData = new ArrayList<>();

    public void setSelectedData(Integer mSelectedData) {
        this.mSelectedData = mSelectedData;
        notifyDataSetChanged();
    }

    private Integer mSelectedData = null;

    public ChooseTypeGridAdapter(Context context) {
        this.mContext = context;
    }

    public void initData(List<Integer> data,boolean isClean) {
        if(isClean){
            mData.clear();
        }
        if(data == null || data.size() == 0){
            return;
        }
        mData.addAll(data);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Integer getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_cell_choose_money_use_type, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Integer data = getItem(position);
        if(data.equals(mSelectedData)){
            viewHolder.llCell.setBackground(mContext.getDrawable(R.drawable.bg_selected));
        }else {
            viewHolder.llCell.setBackgroundColor(mContext.getColor(R.color.app_transparent));
        }
        viewHolder.imageView.setImageResource(data);
        viewHolder.tvName.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ll_cell)
        LinearLayout llCell;
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

