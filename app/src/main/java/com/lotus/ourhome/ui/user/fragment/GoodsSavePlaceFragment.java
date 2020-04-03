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
import com.lotus.ourhome.model.bean.GoodsSavePlaceBean;
import com.lotus.ourhome.model.bean.GoodsTypeBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.db.GoodsSavePlaceBeanManager;
import com.lotus.ourhome.model.db.GoodsTypeBeanManager;
import com.lotus.ourhome.ui.user.adapter.GoodsSavePlaceListAdapter;
import com.lotus.ourhome.ui.user.adapter.GoodsTypeListAdapter;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.util.ToastUtil;
import com.lotus.ourhome.widget.CommonAddDataPopupWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class GoodsSavePlaceFragment extends SimpleFragment implements OnRefreshListener {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private GoodsSavePlaceListAdapter mAdapter;
    private CommonAddDataPopupWindow mAddDataPopview;
    private GoodsSavePlaceBeanManager mBeanManager;
    private SaveDataAsync mSaveDataAsync;
    private QueryListDataAsync mQueryListDataAsync;
    private String mUserId = "";

    public static GoodsSavePlaceFragment newInstance() {
        Bundle args = new Bundle();
        GoodsSavePlaceFragment fragment = new GoodsSavePlaceFragment();
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
        mBeanManager = new GoodsSavePlaceBeanManager(mContext);
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

        mAdapter = new GoodsSavePlaceListAdapter(mContext);

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
     * 添加
     */
    private void showAddMoneyUseTypePopview(){
        if (mAddDataPopview == null) {
            mAddDataPopview = new CommonAddDataPopupWindow(mActivity);
            mAddDataPopview.setOnPopupWinCallBack(new CommonAddDataPopupWindow.OnPopupWinCallBack() {
                @Override
                public void add(Integer iconId, String name,String remark) {
                    GoodsSavePlaceBean bean = new GoodsSavePlaceBean();
                    bean.setId(MoneyUseTypeBean.createId(mUserId));
                    bean.setName(name);
                    bean.setUserId(mUserId);
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

    private void saveData(GoodsSavePlaceBean bean){
        if(mSaveDataAsync != null){
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(bean);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryListDataAsync extends AsyncTask<Object, Integer, List<GoodsSavePlaceBean>> {

        @Override
        protected List<GoodsSavePlaceBean> doInBackground(Object... objects) {
            return mBeanManager.getGoodsSavePlaceListByUserId(mUserId);
        }

        @Override
        protected void onPostExecute(List<GoodsSavePlaceBean> result) {
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
        GoodsSavePlaceBean data = null;

        private SaveDataAsync(GoodsSavePlaceBean bean){
            this.data = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return mBeanManager.saveGoodsSavePlaceBean(data);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                mAddDataPopview.dismiss();
                onRefresh(layoutRefresh);
                ToastUtil.shortShow(mContext,"添加成功");
            }else {
                ToastUtil.shortShow(mContext,"添加失败");
            }
        }
    }
}

