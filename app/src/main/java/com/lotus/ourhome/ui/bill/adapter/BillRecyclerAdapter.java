package com.lotus.ourhome.ui.bill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillRecyclerAdapter extends RecyclerView.Adapter<BillRecyclerAdapter.ViewHolder> {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd", Locale.CHINA);
    private ArrayList<BillBean> mData = new ArrayList<>();
    private Context mContext;

    public BillRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_cell_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillBean data = mData.get(position);
        if (BillBean.TYPE_EXPENSES.equals(data.getType())) {
            holder.llLeft.setVisibility(View.INVISIBLE);
            holder.llRight.setVisibility(View.VISIBLE);
        } else {
            holder.llLeft.setVisibility(View.VISIBLE);
            holder.llRight.setVisibility(View.INVISIBLE);
        }
        holder.tvLeftMoney.setText(data.getMoney());
        holder.tvLeftRemark.setText(data.getRemark());

        MoneyUseTypeBean moneyUseTypeBean = data.getMoneyUseTypeBean();
        if (moneyUseTypeBean != null) {
            holder.tvRightName.setText( moneyUseTypeBean.getName());
            holder.tvRightName.setText( moneyUseTypeBean.getName());
            holder.imgIcon.setImageResource(Integer.parseInt(data.getMoneyUseTypeBean().getIcon()));
        } else {
            holder.imgIcon.setImageResource(R.mipmap.ic_money);
        }
        holder.tvRightMoney.setText(data.getMoney());
        FamilyMemberBean familyMemberBean = data.getFamilyMemberBean();
        String personName = "";
        if (familyMemberBean != null) {
            personName = familyMemberBean.getName();
        }
        holder.tvRightRemark.setText(mSimpleDateFormat.format(data.getHappenTime()) + "  " +personName + "\n" + data.getRemark());
    }

    /**
     * 增加列表数据
     */
    public void initData(List<BillBean> list, boolean isReset) {
        if (isReset) {
            mData.clear();
        }
        if (list == null || list.size() == 0) {
            return;
        }
        int startIndex = mData.size();
        mData.addAll(list);
        notifyItemRangeChanged(startIndex, list.size());
        notifyDataSetChanged();
    }

    /**
     * 新增item
     */
    public void addNewItem(BillBean imagePath) {
        if (imagePath == null) {
            return;
        }
        mData.add(imagePath);
        notifyItemInserted(0);
    }

    /**
     * 删除item
     */
    public void deleteItem(int position) {
        if (mData == null || mData.size() == 0 || position >= mData.size()) {
            return;
        }
        mData.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 获得列表数据
     *
     * @return
     */
    public ArrayList<BillBean> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_left)
        LinearLayout llLeft;
        @BindView(R.id.tv_left_name)
        TextView tvLeftName;
        @BindView(R.id.tv_left_money)
        TextView tvLeftMoney;
        @BindView(R.id.tv_left_remark)
        TextView tvLeftRemark;

        @BindView(R.id.img_icon)
        ImageView imgIcon;

        @BindView(R.id.ll_right)
        LinearLayout llRight;
        @BindView(R.id.tv_right_name)
        TextView tvRightName;
        @BindView(R.id.tv_right_money)
        TextView tvRightMoney;
        @BindView(R.id.tv_right_remark)
        TextView tvRightRemark;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
