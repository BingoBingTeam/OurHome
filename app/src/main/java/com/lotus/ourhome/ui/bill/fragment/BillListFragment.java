package com.lotus.ourhome.ui.bill.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.db.BillBeanManager;
import com.lotus.ourhome.ui.bill.adapter.BillRecyclerAdapter;
import com.lotus.ourhome.ui.main.fragment.MainFragment;
import com.lotus.ourhome.util.CookieUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 账单页面
 */
public class BillListFragment extends SimpleFragment implements OnRefreshListener {

    @BindView(R.id.img_more_ledger)
    ImageView imgMoreLedger;
    @BindView(R.id.img_add_bill)
    ImageView imgAddBill;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.tv_expenses)
    TextView tvExpenses;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private BillRecyclerAdapter mAdapter;
    private BillBeanManager mBillBeanManager;
    private String mUserId = "";
    private QueryListDataAsync mQueryListDataAsync;

    public static BillListFragment newInstance() {
        Bundle args = new Bundle();
        BillListFragment fragment = new BillListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_bill_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnDataChanged(BillBean mode) {
        if (mode != null) {
            if(mode.getId() != null && mode.getId().length() > 0){
                onRefresh(layoutRefresh);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initEventAndData() {
        mUserId = CookieUtil.getUserInfo().getId();
        mBillBeanManager = new BillBeanManager(mContext);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new BillRecyclerAdapter(mContext);
        recyclerView.setAdapter(mAdapter);

        layoutRefresh.setOnRefreshListener(this);
        onRefresh(layoutRefresh);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getListData();
    }

    /**
     * 获取列表数据
     */
    private void getListData() {
        layoutRefresh.finishRefresh(true);
        if (mQueryListDataAsync != null) {
            mQueryListDataAsync.cancel(true);
        }
        mQueryListDataAsync = new QueryListDataAsync();
        mQueryListDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryListDataAsync extends AsyncTask<Object, Integer, List<BillBean>> {

        @Override
        protected List<BillBean> doInBackground(Object... objects) {
            return mBillBeanManager.getBillListToCurrentTime(mUserId, System.currentTimeMillis());
        }

        @Override
        protected void onPostExecute(List<BillBean> result) {
            super.onPostExecute(result);
            if (result != null && result.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                rlNoData.setVisibility(View.GONE);
                mAdapter.initData(result, true);
            } else {
                recyclerView.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示所有账本
     */
    private void showMoreLedger() {

    }

    /**
     * 新增
     */
    private void addBill() {
        ((MainFragment) getParentFragment()).start(AddBillFragment.newInstance());
    }

    @OnClick({R.id.img_more_ledger, R.id.img_add_bill})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_more_ledger:
                showMoreLedger();
                break;
            case R.id.img_add_bill:
                addBill();
                break;
        }
    }

//     if (mIsShowFiltrateDataPopupWindow) {
//        alphaLayoutFiltrate(1.0f, 0.1f);
//    } else {
//        alphaLayoutFiltrate(0.1f, 1.0f);
//    }
//    /**
//     * 视图动画
//     */
//    private void alphaLayoutFiltrate(final float start, final float end) {
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(mLayoutFiltrate, "alpha", start, end).setDuration(300);
//        alpha.setInterpolator(new LinearInterpolator());
//        alpha.addListener(new AnimatorListenerAdapter() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//                if (start == 0.1f) {
//                    mLayoutFiltrate.setVisibility(View.VISIBLE);
//                    mIsShowFiltrateDataPopupWindow = true;
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                if (end == 0.1f) {
//                    mLayoutFiltrate.setVisibility(View.GONE);
//                    mIsShowFiltrateDataPopupWindow = false;
//                }
//            }
//        });
//        alpha.start();
//    }

}
