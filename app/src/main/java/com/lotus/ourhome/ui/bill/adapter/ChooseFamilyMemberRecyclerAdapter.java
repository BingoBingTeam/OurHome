package com.lotus.ourhome.ui.bill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.FamilyMemberBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseFamilyMemberRecyclerAdapter  extends RecyclerView.Adapter<ChooseFamilyMemberRecyclerAdapter.ViewHolder> {

    private ArrayList<FamilyMemberBean> mData = new ArrayList<>();
    private Context mContext;
    private FamilyMemberBean mChoseData = null;
    private OnFamilyMemberListItemClick mOnListItemClick = null;

    public ChooseFamilyMemberRecyclerAdapter(Context context){
        this.mContext = context;
    }

    public void setOnListItemClick(OnFamilyMemberListItemClick mOnListItemClick) {
        this.mOnListItemClick = mOnListItemClick;
    }

    public void setChoseData(FamilyMemberBean choseData) {
        this.mChoseData = choseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_cell_choose_family_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMemberBean data = mData.get(position);
        holder.tvName.setText(data.getName());
        if (data.equals(mChoseData)) {
            holder.tvName.setBackgroundResource(R.drawable.bg_filtrate_text_selected);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.color_filtrate_text_selected));
        } else {
            holder.tvName.setBackgroundResource(R.drawable.bg_filtrate_text_default);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.color_filtrate_text_default));
        }
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnListItemClick != null) {
                    mChoseData = data;
                    mOnListItemClick.onItemClick(mChoseData);
                    notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 增加列表数据
     */
    public void initData(List<FamilyMemberBean> list, boolean isReset) {
        if(isReset){
            mData.clear();
        }
        if(list == null || list.size() == 0){
            return;
        }
        int startIndex= mData.size();
        mData.addAll(list);
        notifyItemRangeInserted(startIndex,list.size());
    }

    /**
     * 新增item
     */
    public void addNewItem(FamilyMemberBean imagePath) {
        if (imagePath == null) {
            return;
        }
        mData.add(imagePath);
        notifyItemInserted(mData.size() - 1);
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
    public ArrayList<FamilyMemberBean> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnFamilyMemberListItemClick {
        public void onItemClick(FamilyMemberBean content);
    }
}
