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
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.db.FamilyMemberBeanManager;
import com.lotus.ourhome.ui.user.adapter.FamilyMemberListAdapter;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.util.ToastUtil;
import com.lotus.ourhome.widget.CommonAddDataPopupWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 家庭成员列表
 */
public class FamilyMemberFragment extends SimpleFragment implements OnRefreshListener {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private FamilyMemberListAdapter mAdapter;
    private CommonAddDataPopupWindow mAddDataPopview;
    private FamilyMemberBeanManager mFamilyMemberBeanManager;
    private SaveMemberAsync mSaveMemberAsync;
    private QueryMemberAsync mQueryMemberAsync;
    private String mUserId = "";

    public static FamilyMemberFragment newInstance() {
        Bundle args = new Bundle();
        FamilyMemberFragment fragment = new FamilyMemberFragment();
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
        mFamilyMemberBeanManager = new FamilyMemberBeanManager(mContext);
        titleBar.setTitle("家庭成员列表");
        titleBar.setClickListenerIvAdd(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFamilyMemberPopview();
            }
        });
        titleBar.setClickListenerIvBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });

        mAdapter = new FamilyMemberListAdapter(mContext);
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
        if(mQueryMemberAsync != null){
            mQueryMemberAsync.cancel(true);
        }
        mQueryMemberAsync = new QueryMemberAsync();
        mQueryMemberAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 添加家庭成员
     */
    private void showAddFamilyMemberPopview(){
        if (mAddDataPopview == null) {
            mAddDataPopview = new CommonAddDataPopupWindow(mActivity);
            mAddDataPopview.setOnPopupWinCallBack(new CommonAddDataPopupWindow.OnPopupWinCallBack() {
                @Override
                public void add(Integer iconId,String name,String remark) {
                    FamilyMemberBean bean = new FamilyMemberBean();
                    bean.setId(FamilyMemberBean.createId(mUserId));
                    bean.setName(name);
                    bean.setRemark(remark);
                    bean.setUserId(mUserId);
                    bean.setCreateTime(System.currentTimeMillis());
                    saveMember(bean);
                }
            });
        }
        if(mAddDataPopview.isShowing()){
            mAddDataPopview.dismiss();
        }
        mAddDataPopview.show(getView());
    }

    private void saveMember(FamilyMemberBean bean){
        if(mSaveMemberAsync != null){
            mSaveMemberAsync.cancel(true);
        }
        mSaveMemberAsync = new SaveMemberAsync(bean);
        mSaveMemberAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryMemberAsync extends AsyncTask<Object, Integer, List<FamilyMemberBean>> {

        @Override
        protected List<FamilyMemberBean> doInBackground(Object... objects) {
            return mFamilyMemberBeanManager.getFamilyMemberByUserId(mUserId);
        }

        @Override
        protected void onPostExecute(List<FamilyMemberBean> result) {
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

    private class SaveMemberAsync extends AsyncTask<Object, Integer, Boolean> {
        FamilyMemberBean bean = null;

        private SaveMemberAsync(FamilyMemberBean bean){
            this.bean = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return mFamilyMemberBeanManager.saveFamilyMember(bean);
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
