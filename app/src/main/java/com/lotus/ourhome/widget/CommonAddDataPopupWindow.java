package com.lotus.ourhome.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lotus.base.widget.abslist.ExpandingGridView;
import com.lotus.ourhome.R;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.ui.user.adapter.ChooseTypeGridAdapter;
import com.lotus.ourhome.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommonAddDataPopupWindow extends PopupWindow {

    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.btn_cancel)
    TextView btnCancel;
    @BindView(R.id.btn_ok)
    TextView btnOk;
    @BindView(R.id.ed_remark)
    EditText edRemark;
    @BindView(R.id.grid_view)
    ExpandingGridView gridView;

    private Activity mActivity;
    private OnPopupWinCallBack mOnPopupWinCallBack;
    private ChooseTypeGridAdapter mAdapter;

    private Integer mSelectedTypeIconId = MoneyUseTypeBean.DEFAULT_NO_SELECTED_TYPE_ICON;


    /**
     * 构造
     *
     * @param activity
     */
    public CommonAddDataPopupWindow(Activity activity) {
        this.mActivity = activity;
        initConfig();
        initView();
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//dip2px(240f));//通用弹窗固定240dp
        this.setFocusable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);//默认为true
        this.setElevation(25f);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.setAnimationStyle(R.style.CommonBottomPopuAnim);
        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                mActivity.getWindow().setDimAmount(0f);
                params.alpha = 1.0f;
                mActivity.getWindow().setAttributes(params);
            }
        });

    }

    /**
     * 显示
     *
     * @param parent 关联父布局
     */
    public void show(View parent) {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.7f;
        mActivity.getWindow().setDimAmount(0.7f);
        mActivity.getWindow().setAttributes(params);
        this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 初始化View
     */
    private void initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_add_family_member, null, false);
        ButterKnife.bind(this, view);

        mAdapter = new ChooseTypeGridAdapter(mActivity);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    mAdapter.setSelectedData(mSelectedTypeIconId);
                    mSelectedTypeIconId = mAdapter.getItem(position);
                }
            }
        });
        setContentView(view);
    }

    public void setTypeGridViewData(List<Integer> datas) {
        gridView.setVisibility(View.VISIBLE);
        if (mAdapter != null) {
            mAdapter.initData(datas, true);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnPopupWinCallBack(OnPopupWinCallBack mOnPopupWinCallBack) {
        this.mOnPopupWinCallBack = mOnPopupWinCallBack;
    }

    private int dip2px(float dpValue) {
        float scale = mActivity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                String name = edName.getText().toString().trim();
                if (StringUtils.isEmpty(name)) {
                    ToastUtil.shortShow(mActivity, "名字不能为空");
                } else if (mOnPopupWinCallBack != null) {
                    mOnPopupWinCallBack.add(mSelectedTypeIconId, name, edRemark.getText().toString().trim());
                }
                break;
        }
    }

    public interface OnPopupWinCallBack {
        public void add(Integer iconId, String name, String remark);
    }
}

