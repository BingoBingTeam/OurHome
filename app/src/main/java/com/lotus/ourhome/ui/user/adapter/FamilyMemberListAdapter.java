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
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyMemberListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FamilyMemberBean> mData = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;

    public FamilyMemberListAdapter(Context context) {
        this.mContext = context;
    }

    public void initData(List<FamilyMemberBean> data, boolean isClean) {
        if (isClean) {
            mData.clear();
        }
        if (data == null || data.size() == 0) {
            return;
        }
        mData.addAll(data);
    }

    public void setOnListItemClickListener(OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public FamilyMemberBean getItem(int position) {
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
        FamilyMemberBean data = getItem(position);
        String name = data.getName();
        viewHolder.tvName.setText(name);
        viewHolder.tvRemark.setText(data.getRemark());
        if(FamilyMemberBean.DEFAULT_MEMBER_FAMILY.equals(name) || FamilyMemberBean.DEFAULT_MEMBER_SELF.equals(name)){
            viewHolder.rlCell.setSwipeEnable(false);
        }else {
            viewHolder.rlCell.setSwipeEnable(true);
        }
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnListItemClickListener != null){
                    mOnListItemClickListener.delete(position,data);
                }
            }
        });
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnListItemClickListener != null){
                    mOnListItemClickListener.edit(position,data);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.rl_cell)
        SwipeMenuLayout rlCell;
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.btn_edit)
        TextView btnEdit;
        @BindView(R.id.btn_delete)
        TextView btnDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnListItemClickListener{
        public void edit(int position,FamilyMemberBean data);
        public void delete(int position,FamilyMemberBean data);
    }
}
