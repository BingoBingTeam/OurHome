package com.lotus.ourhome.ui.user.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.lotus.base.widget.TitleBarLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.db.MoneyUseTypeBeanManager;
import com.lotus.ourhome.ui.user.adapter.MoneyUseTypeListAdapter;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.util.ToastUtil;
import com.lotus.ourhome.widget.CommonAddDataPopupWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class MoneyUserTypeFragment  extends SimpleFragment implements OnRefreshListener {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private MoneyUseTypeListAdapter mAdapter;
    private CommonAddDataPopupWindow mAddDataPopview;
    private MoneyUseTypeBeanManager mMoneyUseTypeBeanManager;
    private SaveDataAsync mSaveDataAsync;
    private QueryListDataAsync mQueryListDataAsync;
    private String mUserId = "";

    public static MoneyUserTypeFragment newInstance() {
        Bundle args = new Bundle();
        MoneyUserTypeFragment fragment = new MoneyUserTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_family_member_list;
    }

    @Override
    protected void initEventAndData() {
        mUserId = CookieUtil.getUserInfo().getId();
        mMoneyUseTypeBeanManager = new MoneyUseTypeBeanManager(mContext);
        titleBar.setTitle("账单使用类别");
        titleBar.setClickListenerIvAdd(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMoneyUseTypePopview();
            }
        });
        titleBar.setClickListenerIvBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });

        mAdapter = new MoneyUseTypeListAdapter(mContext);
        listView.setAdapter(mAdapter);
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
        if(mQueryListDataAsync != null){
            mQueryListDataAsync.cancel(true);
        }
        mQueryListDataAsync = new QueryListDataAsync();
        mQueryListDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 添加家庭成员
     */
    private void showAddMoneyUseTypePopview(){
        if (mAddDataPopview == null) {
            mAddDataPopview = new CommonAddDataPopupWindow(mActivity);
            mAddDataPopview.setTypeGridViewData(MoneyUseTypeBean.getMoneyUserTypeIconList(BillBean.TYPE_EXPENSES));
            mAddDataPopview.setOnPopupWinCallBack(new CommonAddDataPopupWindow.OnPopupWinCallBack() {
                @Override
                public void add(Integer iconId,String name,String remark) {
                    MoneyUseTypeBean bean = new MoneyUseTypeBean();
                    bean.setId(MoneyUseTypeBean.createId(mUserId));
                    bean.setName(name);
                    bean.setIcon(String.valueOf(iconId));
                    bean.setUserId(mUserId);
                    bean.setType(BillBean.TYPE_EXPENSES);
                    bean.setCreateTime(System.currentTimeMillis());
                    saveData(bean);
                }
            });
        }
        if(mAddDataPopview.isShowing()){
            mAddDataPopview.dismiss();
        }
        mAddDataPopview.show(getView());
    }

    private void saveData(MoneyUseTypeBean bean){
        if(mSaveDataAsync != null){
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(bean);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryListDataAsync extends AsyncTask<Object, Integer, List<MoneyUseTypeBean>> {

        @Override
        protected List<MoneyUseTypeBean> doInBackground(Object... objects) {
            return mMoneyUseTypeBeanManager.getMoneyUseTypeByUserId(mUserId);
        }

        @Override
        protected void onPostExecute(List<MoneyUseTypeBean> result) {
            super.onPostExecute(result);
            if(result != null && result.size() > 0){
                listView.setVisibility(View.VISIBLE);
                rlNoData.setVisibility(View.GONE);
            }else {
                listView.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
            mAdapter.initData(result,true);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class SaveDataAsync extends AsyncTask<Object, Integer, Boolean> {
        MoneyUseTypeBean moneyUseTypeBean = null;

        private SaveDataAsync(MoneyUseTypeBean bean){
            this.moneyUseTypeBean = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return mMoneyUseTypeBeanManager.saveMoneyUseTypeBean(moneyUseTypeBean);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                mAddDataPopview.dismiss();
                ToastUtil.shortShow(mContext,"添加成功");
                onRefresh(layoutRefresh);
            }else {
                ToastUtil.shortShow(mContext,"添加失败");
            }
        }
    }
}

