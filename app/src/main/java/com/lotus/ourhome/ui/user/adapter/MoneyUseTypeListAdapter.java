package com.lotus.ourhome.ui.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoneyUseTypeListAdapter  extends BaseAdapter {

    private Context mContext;
    private List<MoneyUseTypeBean> mData = new ArrayList<>();

    public MoneyUseTypeListAdapter(Context context) {
        this.mContext = context;
    }

    public void initData(List<MoneyUseTypeBean> data,boolean isClean) {
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
    public MoneyUseTypeBean getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_cell_family_member, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MoneyUseTypeBean data = getItem(position);
        viewHolder.imageView.setImageResource(Integer.parseInt(data.getIcon()));
        viewHolder.tvName.setText(data.getName());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_remark)
        TextView tvRemark;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
