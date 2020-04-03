package com.lotus.ourhome.ui.bill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lotus.base.widget.square.SquareLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.LedgerBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LedgerListAdapter extends BaseAdapter {
    private final String TAG = LedgerListAdapter.class.getSimpleName();

    private Context mContext;
    private List<LedgerBean> mData = new ArrayList<>();//所有附件
    private boolean mIsEditableAdd = true;//是否可添加
    private boolean mIsEditableDelete = true;//是否可删除
    private final int _TYPE_EDITABLE_ADD_VIEW = 1;//可添加模式
    private final int _TYPE_EDITABLE_DELETE_VIEW = 2;//可删除模式
    private final int _TYPE_DATA_VIEW = 0;//展示数据模式

    private OnListItemClickListener mOnListItemClickListener;

    public LedgerListAdapter(Context context) {
        this.mContext = context;
    }

    public List<LedgerBean> getData() {
        return new ArrayList<>(mData);
    }

    public void setOnListItemClickListener(OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    /**
     * 新增数据：得到此次选择的数据后，更新数据
     *
     * @param list
     */
    public void initData(List<LedgerBean> list, boolean isReset) {
        if (isReset) {
            mData.clear();
        }
        if (list != null && list.size() > 0) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 新增数据
     */
    public void addData(LedgerBean data) {
        if (data != null) {
            mData.add(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除数据
     */
    public void deleteData(LedgerBean data) {
        if (data != null && mData.contains(data)) {
            mData.remove(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mIsEditableAdd ? mData.size() + 1 : mData.size();
    }

    @Override
    public LedgerBean getItem(int position) {
        if (mIsEditableAdd) {
            int realIndex = position - 1;
            if (realIndex >= 0 && realIndex < mData.size()) {
                return mData.get(realIndex);
            } else {
                return null;
            }
        } else {
            return mData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 是否可编辑
     */
    public void setEnabled(boolean isEditable) {
        mIsEditableAdd = isEditable;
        mIsEditableDelete = isEditable;
        notifyDataSetChanged();
    }

    /**
     * 是否可添加和删除
     */
    public void setEnabled(boolean isEditableAdd, boolean isEditableDelete) {
        mIsEditableAdd = isEditableAdd;
        mIsEditableDelete = isEditableDelete;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsEditableAdd && position == 0) {
            return _TYPE_EDITABLE_ADD_VIEW;
        } else if (mIsEditableDelete) {
            return _TYPE_EDITABLE_DELETE_VIEW;
        } else {
            return _TYPE_DATA_VIEW;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_cell_ledger, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setVisibility(getItemViewType(position) != _TYPE_EDITABLE_ADD_VIEW ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(getItemViewType(position) == _TYPE_EDITABLE_DELETE_VIEW ? View.VISIBLE : View.GONE);
        holder.imageAdd.setVisibility(getItemViewType(position) == _TYPE_EDITABLE_ADD_VIEW ? View.VISIBLE : View.GONE);

        LedgerBean data = getItem(position);
        if (data != null) {
            holder.tvName.setText(data.getName());
            holder.imageView.setBackgroundResource(Integer.parseInt(data.getColor()));
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditableDelete && mOnListItemClickListener != null) {//删除
                    int realIndex = (mIsEditableAdd ? position - 1 : position);
                    mOnListItemClickListener.delete(realIndex, mData.get(realIndex));
                }
            }
        });

        //新增、查看详情
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnListItemClickListener != null) {
                    if (mIsEditableAdd && position == 0) {//新增
                        mOnListItemClickListener.add();
                    } else {//查看详情
                        int realIndex = (mIsEditableAdd ? position - 1 : position);
                        mOnListItemClickListener.showDetail(realIndex, mData.get(realIndex));
                    }
                }
            }
        });
        return convertView;
    }

    public interface OnListItemClickListener {
        public void showDetail(int position, LedgerBean data);

        public void add();

        public void delete(int position, LedgerBean data);
    }

    static class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.btn_delete)
        ImageView btnDelete;
        @BindView(R.id.image_add)
        ImageView imageAdd;
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.rl_item)
        SquareLayout rlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
