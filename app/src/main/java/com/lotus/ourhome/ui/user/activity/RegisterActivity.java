package com.lotus.ourhome.ui.user.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lotus.base.widget.TitleBarLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleActivity;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.GoodsSavePlaceBean;
import com.lotus.ourhome.model.bean.GoodsTypeBean;
import com.lotus.ourhome.model.bean.LedgerBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.bean.UserBean;
import com.lotus.ourhome.model.db.FamilyMemberBeanManager;
import com.lotus.ourhome.model.db.GoodsSavePlaceBeanManager;
import com.lotus.ourhome.model.db.GoodsTypeBeanManager;
import com.lotus.ourhome.model.db.LedgerBeanManager;
import com.lotus.ourhome.model.db.MoneyUseTypeBeanManager;
import com.lotus.ourhome.model.db.UserBeanManager;
import com.lotus.ourhome.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册
 */

public class RegisterActivity extends SimpleActivity {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.ed_psw)
    EditText edPsw;
    @BindView(R.id.ed_repsw)
    EditText edRepsw;
    @BindView(R.id.btn_register)
    TextView btnRegister;
    @BindView(R.id.ed_phone_number)
    EditText edPhoneNumber;

    private Context mContext;
    private SaveDataAsync mSaveDataAsync;
    private UserBeanManager mBeanManager;
    private FamilyMemberBeanManager mFamilyMemberBeanManager;

    @Override
    protected int setLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        mFamilyMemberBeanManager = new FamilyMemberBeanManager(this);
        mBeanManager = new UserBeanManager(this);

        titleBar.setTitle(getResources().getString(R.string.register_title));
        titleBar.setClickListenerIvBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveData(UserBean userBean) {
        if (mSaveDataAsync != null) {
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(userBean);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private class SaveDataAsync extends AsyncTask<Object, Integer, Boolean> {
        UserBean data = null;

        private SaveDataAsync(UserBean bean) {
            this.data = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            String userId = data.getId();

            //添加默认的家庭成员
            FamilyMemberBean bean = new FamilyMemberBean();
            bean.setId(FamilyMemberBean.createId(userId));
            bean.setName("我");
            bean.setRemark("");
            bean.setUserId(userId);
            bean.setCreateTime(System.currentTimeMillis());

            //添加一个默认的账本
            LedgerBeanManager ledgerBeanManager = new LedgerBeanManager(mContext);
            LedgerBean ledgerBean = new LedgerBean();
            ledgerBean.setUserId(userId);
            ledgerBean.setCreateTime(System.currentTimeMillis());
            ledgerBean.setId(LedgerBean.createId(userId));
            ledgerBean.setName(LedgerBean.DEFAULT_LEDGER);
            ledgerBeanManager.saveLedgerBean(ledgerBean);

            //添加默认的钱的使用类别
            MoneyUseTypeBeanManager moneyUseTypeBeanManager = new MoneyUseTypeBeanManager(mContext);
            List<Integer> expensesIconList = MoneyUseTypeBean.getMoneyUserTypeIconList(BillBean.TYPE_EXPENSES);
            List<String> expensesNameList = MoneyUseTypeBean.getMoneyUserTypeNameList(BillBean.TYPE_EXPENSES);
            for (int i = 0; i < expensesIconList.size(); i++) {
                MoneyUseTypeBean moneyUseTypeBean = new MoneyUseTypeBean();
                moneyUseTypeBean.setId(MoneyUseTypeBean.createId(userId));
                moneyUseTypeBean.setUserId(userId);
                moneyUseTypeBean.setCreateTime(System.currentTimeMillis());
                moneyUseTypeBean.setType(BillBean.TYPE_EXPENSES);
                moneyUseTypeBean.setName(expensesNameList.get(i));
                moneyUseTypeBean.setIcon(String.valueOf(expensesIconList.get(i)));
                moneyUseTypeBeanManager.saveMoneyUseTypeBean(moneyUseTypeBean);
            }

            List<Integer> incomIconList = MoneyUseTypeBean.getMoneyUserTypeIconList(BillBean.TYPE_INCOME);
            List<String> incomNameList = MoneyUseTypeBean.getMoneyUserTypeNameList(BillBean.TYPE_INCOME);
            for (int i = 0; i < expensesIconList.size(); i++) {
                MoneyUseTypeBean moneyUseTypeBean = new MoneyUseTypeBean();
                moneyUseTypeBean.setId(MoneyUseTypeBean.createId(userId));
                moneyUseTypeBean.setUserId(userId);
                moneyUseTypeBean.setCreateTime(System.currentTimeMillis());
                moneyUseTypeBean.setType(BillBean.TYPE_INCOME);
                moneyUseTypeBean.setName(incomNameList.get(i));
                moneyUseTypeBean.setIcon(String.valueOf(incomIconList.get(i)));
                moneyUseTypeBeanManager.saveMoneyUseTypeBean(moneyUseTypeBean);
            }

            //添加默认的物品类别
            GoodsTypeBeanManager goodsTypeBeanManager = new GoodsTypeBeanManager(mContext);
            List<Integer> goodsTypeIconList = GoodsTypeBean.getGoodsTypeIconList();
            List<String> goodsTypeNameList = GoodsTypeBean.getGoodsTypeNameList();
            for (int i = 0; i < goodsTypeIconList.size(); i++) {
                GoodsTypeBean goodsTypeBean = new GoodsTypeBean();
                goodsTypeBean.setUserId(userId);
                goodsTypeBean.setCreateTime(System.currentTimeMillis());
                goodsTypeBean.setId(GoodsTypeBean.createId(userId));
                goodsTypeBean.setName(goodsTypeNameList.get(i));
                goodsTypeBean.setIcon(String.valueOf(goodsTypeIconList.get(i)));
                goodsTypeBeanManager.saveGoodsType(goodsTypeBean);
            }

            //添加默认的物品保存地址
            GoodsSavePlaceBeanManager goodsSavePlaceBeanManager = new GoodsSavePlaceBeanManager(mContext);
            List<Integer> goodsSavePlaceIconList = GoodsSavePlaceBean.getGoodsPlaceTypeIconList();
            List<String> goodsSavePlaceNameList = GoodsSavePlaceBean.getGoodsPlaceTypeNameList();
            for (int i = 0; i < goodsSavePlaceIconList.size(); i++) {
                GoodsSavePlaceBean goodsSavePlaceBean = new GoodsSavePlaceBean();
                goodsSavePlaceBean.setUserId(userId);
                goodsSavePlaceBean.setId(GoodsSavePlaceBean.createId(userId));
                goodsSavePlaceBean.setCreateTime(System.currentTimeMillis());
                goodsSavePlaceBean.setIcon(String.valueOf(goodsSavePlaceIconList.get(i)));
                goodsSavePlaceBean.setName(goodsSavePlaceNameList.get(i));
                goodsSavePlaceBeanManager.saveGoodsSavePlaceBean(goodsSavePlaceBean);
            }

            return mFamilyMemberBeanManager.saveFamilyMember(bean) && mBeanManager.saveUserBean(data);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                ToastUtil.shortShow(mContext, "注册成功");
                finish();
            } else {
                ToastUtil.shortShow(mContext, "注册失败");
            }
        }
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        String username = edName.getText().toString().trim();
        String phone = edPhoneNumber.getText().toString().trim();
        String psw = edPsw.getText().toString().trim();
        String rePsw = edRepsw.getText().toString().trim();

        UserBean userBean = new UserBean();
        userBean.setPassword(psw);
        userBean.setName(username);
        userBean.setPhoneNumber(phone);
        userBean.setId(UserBean.createId(username, phone));
        saveData(userBean);
    }
}
