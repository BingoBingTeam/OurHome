package com.lotus.ourhome.ui.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lotus.base.widget.CircleTextView;
import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.LedgerBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseTypeGridAdapter extends BaseAdapter {

    public static final int _TYPE_CHOOSE_IMAGE = 1;
    public static final int _TYPE_CHOOSE_COLOR = 2;

    private Context mContext;
    private List<Integer> mData = new ArrayList<>();
    private int mType = _TYPE_CHOOSE_IMAGE;

    public void setSelectedData(Integer mSelectedData) {
        this.mSelectedData = mSelectedData;
        notifyDataSetChanged();
    }

    private Integer mSelectedData = MoneyUseTypeBean.DEFAULT_NO_SELECTED_TYPE_ICON;

    public void setType(int mType) {
        this.mType = mType;
        if (_TYPE_CHOOSE_COLOR == mType) {
            mSelectedData = LedgerBean.DEFAULT_NO_SELECTED_TYPE_COLOR;
        } else {
            mSelectedData = MoneyUseTypeBean.DEFAULT_NO_SELECTED_TYPE_ICON;
        }
    }

    public ChooseTypeGridAdapter(Context context) {
        this.mContext = context;
    }

    public void initData(List<Integer> data, boolean isClean) {
        if (isClean) {
            mData.clear();
        }
        if (data == null || data.size() == 0) {
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
        if (data.equals(mSelectedData)) {
            viewHolder.llCell.setBackgroundColor(mContext.getColor(R.color.Gray));
        } else {
            viewHolder.llCell.setBackgroundColor(mContext.getColor(R.color.white));
        }
        if (_TYPE_CHOOSE_COLOR == mType) {
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.circleView.setVisibility(View.VISIBLE);
            viewHolder.circleView.setBgColor(data);
        } else {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.circleView.setVisibility(View.GONE);
            viewHolder.imageView.setImageResource(data);
        }
        viewHolder.llCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnListItemClickListener != null) {
                    if (data.equals(mSelectedData)) {
                        if (_TYPE_CHOOSE_COLOR == mType) {
                            mSelectedData = LedgerBean.DEFAULT_NO_SELECTED_TYPE_COLOR;
                        } else {
                            mSelectedData = MoneyUseTypeBean.DEFAULT_NO_SELECTED_TYPE_ICON;
                        }
                    } else {
                        mSelectedData = data;
                    }
                    mOnListItemClickListener.selected(position, data);
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.tvName.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.circle_view)
        CircleTextView circleView;
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_cell)
        LinearLayout llCell;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    private OnListItemClickListener mOnListItemClickListener;

    public interface OnListItemClickListener {
        public void selected(int position, Integer data);
    }
}

