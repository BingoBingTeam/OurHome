package com.lotus.ourhome.ui.bill.fragment;

import android.os.AsyncTask;
import android.view.View;
import android.widget.GridView;

import com.lotus.base.widget.TitleBarLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.GoodsTypeBean;
import com.lotus.ourhome.model.bean.LedgerBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.db.LedgerBeanManager;
import com.lotus.ourhome.ui.bill.adapter.LedgerListAdapter;
import com.lotus.ourhome.ui.user.fragment.GoodsTypeFragment;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.util.ToastUtil;
import com.lotus.ourhome.widget.CommonAddDataPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * 账本
 */
public class LedgerFragment extends SimpleFragment {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.grid_view)
    GridView gridView;

    private String mUserId = "";
    private LedgerListAdapter mAdapter;
    private CommonAddDataPopupWindow mAddDataPopview;
    private SaveDataAsync mSaveDataAsync;
    private LedgerBeanManager mLedgerBeanManager;
    private QueryListDataAsync mQueryListDataAsync;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_ledger;
    }

    @Override
    protected void initEventAndData() {
        mLedgerBeanManager = new LedgerBeanManager(mContext);
        mUserId = CookieUtil.getUserInfo().getId();

        titleBar.setTitle("账本");

        mAdapter = new LedgerListAdapter(mContext);
        mAdapter.setEnabled(true,false);
        mAdapter.setOnListItemClickListener(new LedgerListAdapter.OnListItemClickListener() {
            @Override
            public void showDetail(int position, LedgerBean data) {
                CookieUtil.setDefaultShowLedger(data.getId());
                EventBus.getDefault().post(data);
                _mActivity.onBackPressed();
            }

            @Override
            public void add() {
                showAddDataPopview();
            }

            @Override
            public void delete(int position, LedgerBean data) {

            }
        });
        gridView.setAdapter(mAdapter);
        getListData();
    }

    /**
     * 添加数据
     */
    private void showAddDataPopview(){
        if (mAddDataPopview == null) {
            mAddDataPopview = new CommonAddDataPopupWindow(mActivity);
            mAddDataPopview.setTypeGridViewData(LedgerBean.getDefaultColorList());
            mAddDataPopview.setOnPopupWinCallBack(new CommonAddDataPopupWindow.OnPopupWinCallBack() {
                @Override
                public void add(Integer iconId,String name,String remark) {
                    LedgerBean bean = new LedgerBean();
                    bean.setId(LedgerBean.createId(mUserId));
                    bean.setName(name);
                    bean.setUserId(mUserId);
                    bean.setCreateTime(System.currentTimeMillis());
                    bean.setColor(String.valueOf(iconId));
                    saveData(bean);
                }
            });
        }
        if(mAddDataPopview.isShowing()){
            mAddDataPopview.dismiss();
        }
        mAddDataPopview.show(getView());
    }

    private void saveData(LedgerBean bean){
        if(mSaveDataAsync != null){
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(bean);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SaveDataAsync extends AsyncTask<Object, Integer, Boolean> {
        LedgerBean data = null;

        private SaveDataAsync(LedgerBean bean){
            this.data = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return mLedgerBeanManager.saveLedgerBean(data);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                ToastUtil.shortShow(mContext,"添加成功");
                mAddDataPopview.dismiss();
                mAdapter.addData(data);
            }else {
                ToastUtil.shortShow(mContext,"添加失败");
            }
        }
    }

    /**
     * 获取列表数据
     */
    private void getListData() {
        if(mQueryListDataAsync != null){
            mQueryListDataAsync.cancel(true);
        }
        mQueryListDataAsync = new QueryListDataAsync();
        mQueryListDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryListDataAsync extends AsyncTask<Object, Integer, List<LedgerBean>> {

        @Override
        protected List<LedgerBean> doInBackground(Object... objects) {
            return mLedgerBeanManager.getLedgerByUserId(mUserId);
        }

        @Override
        protected void onPostExecute(List<LedgerBean> result) {
            super.onPostExecute(result);
            mAdapter.initData(result,true);
            mAdapter.notifyDataSetChanged();
        }
    }

}
