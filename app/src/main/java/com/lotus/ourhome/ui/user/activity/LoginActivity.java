package com.lotus.ourhome.ui.user.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.app.App;
import com.lotus.ourhome.base.SimpleActivity;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.GoodsSavePlaceBean;
import com.lotus.ourhome.model.bean.GoodsTypeBean;
import com.lotus.ourhome.model.bean.LedgerBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.bean.UserBean;
import com.lotus.ourhome.model.db.GoodsSavePlaceBeanManager;
import com.lotus.ourhome.model.db.GoodsTypeBeanManager;
import com.lotus.ourhome.model.db.MoneyUseTypeBeanManager;
import com.lotus.ourhome.model.db.UserBeanManager;
import com.lotus.ourhome.ui.main.activity.MainActivity;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.util.DialogUtil;
import com.lotus.ourhome.util.PermissionUtil;
import com.lotus.ourhome.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.views.SweetAlertDialog;

public class LoginActivity extends SimpleActivity {

    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.ed_psw)
    EditText edPsw;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.btn_register)
    TextView btnRegister;
    @BindView(R.id.iv_user)
    ImageView ivUser;
    @BindView(R.id.iv_psw)
    ImageView ivPsw;
    @BindView(R.id.ll_psw)
    LinearLayout llPsw;
    @BindView(R.id.line_psw)
    View linePsw;
    @BindView(R.id.check_auto_login)
    CheckBox checkAutoLogin;
    @BindView(R.id.check_remember_psw)
    CheckBox checkRememberPsw;

    private Context mContext;
    private UserBean mUserBean;
    private QueryDataAsync mQueryDataAsync;
    private PermissionUtil mPermissionUtil;
    private UserBeanManager mBeanManager;
    private SaveDataAsync mSaveDataAsync;

    @Override
    protected int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        mBeanManager = new UserBeanManager(this);
        judgePermission();
        checkAutoLogin.setChecked(CookieUtil.getAutoLogin());
        checkRememberPsw.setChecked(CookieUtil.getRememberPsw());
        if (CookieUtil.getRememberPsw()) {//记住密码，自动显示信息
            mUserBean = CookieUtil.getUserInfo();
            if (mUserBean != null) {
                edName.setText(mUserBean.getName());
                edPsw.setText(mUserBean.getPassword());
            }
        }
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(s.toString())) {
                    ivUser.setImageResource(R.mipmap.ic_user_gray);
                } else {
                    ivUser.setImageResource(R.mipmap.ic_user);
                }
            }
        });
        edPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(s.toString())) {
                    ivPsw.setImageResource(R.mipmap.ic_psw_gray);
                } else {
                    ivPsw.setImageResource(R.mipmap.ic_psw);
                }
            }
        });
        edPsw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    private void jumpToMain() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void login() {
        String username = edName.getText().toString().trim();
        String psw = edPsw.getText().toString().trim();
        if (mQueryDataAsync != null) {
            mQueryDataAsync.cancel(true);
        }
        mQueryDataAsync = new QueryDataAsync(username, psw);
        mQueryDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryDataAsync extends AsyncTask<Object, Integer, UserBean> {
        private String name;
        private String psw;

        public QueryDataAsync(String name, String psw) {
            this.name = name;
            this.psw = psw;
        }

        @Override
        protected UserBean doInBackground(Object... objects) {
            return mBeanManager.getUserByNameAndPsw(name, psw);
        }

        @Override
        protected void onPostExecute(UserBean result) {
            super.onPostExecute(result);
            if (result != null) {
                ToastUtil.longShow(LoginActivity.this, "登陆成功");
                CookieUtil.setAuth(true);
                CookieUtil.setAutoLogin(checkAutoLogin.isChecked());
                CookieUtil.setRememberPsw(checkRememberPsw.isChecked());
                CookieUtil.saveUserInfo(result);
                saveData(result.getId());
            } else {
                ToastUtil.longShow(LoginActivity.this, "登陆失败,用户名或密码不匹配");
            }
        }
    }


    private void saveData(String userId) {
        if (mSaveDataAsync != null) {
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(userId);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SaveDataAsync extends AsyncTask<Object, Integer, Boolean> {
        String userId = "";

        private SaveDataAsync(String userId) {
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            //若无数据，添加默认的家庭成员
            FamilyMemberBeanManager mFamilyMemberBeanManager = new FamilyMemberBean();
            List<FamilyMemberBean> familyMemberBeanList = mFamilyMemberBeanManager.getFamilyMemberByUserId(userId);
            if (familyMemberBeanList == null || familyMemberBeanList.size() == 0) {
                FamilyMemberBean bean = new FamilyMemberBean();
                bean.setId(FamilyMemberBean.createId(userId));
                bean.setName("我");
                bean.setRemark("");
                bean.setUserId(userId);
                bean.setCreateTime(System.currentTimeMillis());
                mFamilyMemberBeanManager.saveFamilyMember(bean);
            }

            //若无数据，添加一个默认的账本
            LedgerBeanManager ledgerBeanManager = new LedgerBeanManager(mContext);
            List<LedgerBean> ledgerBeanList = ledgerBeanManager.getLedgerByUserIdAndUserName(userId);
            if (ledgerBeanList == null || ledgerBeanList.size() == 0) {
                LedgerBean ledgerBean = new LedgerBean();
                ledgerBean.setUserId(userId);
                ledgerBean.setCreateTime(System.currentTimeMillis());
                ledgerBean.setId(LedgerBean.createId(userId));
                ledgerBean.setName(LedgerBean.DEFAULT_LEDGER);
                ledgerBeanManager.saveLedgerBean(ledgerBean);
                CookieUtil.setDefaultShowLedger(ledgerBean.getId());//保存默认显示的账本id
            }

            //若无数据，添加默认的钱的使用类别
            MoneyUseTypeBeanManager moneyUseTypeBeanManager = new MoneyUseTypeBeanManager(mContext);
            List<MoneyUseTypeBean> expensesMoneyUseTypeBeanList = moneyUseTypeBeanManager.getMoneyUseTypeByUserIdAndType(userId, BillBean.TYPE_EXPENSES);
            if (expensesMoneyUseTypeBeanList == null || expensesMoneyUseTypeBeanList.size() == 0) {
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
            }
            List<MoneyUseTypeBean> incomeMoneyUseTypeBeanList = moneyUseTypeBeanManager.getMoneyUseTypeByUserIdAndType(userId, BillBean.TYPE_INCOME);
            if (incomeMoneyUseTypeBeanList == null || incomeMoneyUseTypeBeanList.size() == 0) {
                List<Integer> incomeIconList = MoneyUseTypeBean.getMoneyUserTypeIconList(BillBean.TYPE_INCOME);
                List<String> incomeNameList = MoneyUseTypeBean.getMoneyUserTypeNameList(BillBean.TYPE_INCOME);
                for (int i = 0; i < incomeIconList.size(); i++) {
                    MoneyUseTypeBean moneyUseTypeBean = new MoneyUseTypeBean();
                    moneyUseTypeBean.setId(MoneyUseTypeBean.createId(userId));
                    moneyUseTypeBean.setUserId(userId);
                    moneyUseTypeBean.setCreateTime(System.currentTimeMillis());
                    moneyUseTypeBean.setType(BillBean.TYPE_INCOME);
                    moneyUseTypeBean.setName(incomeNameList.get(i));
                    moneyUseTypeBean.setIcon(String.valueOf(incomeIconList.get(i)));
                    moneyUseTypeBeanManager.saveMoneyUseTypeBean(moneyUseTypeBean);
                }
            }

            //若无数据，添加默认的物品类别
            GoodsTypeBeanManager goodsTypeBeanManager = new GoodsTypeBeanManager(mContext);
            List<GoodsTypeBean> goodsTypeBeanList = goodsTypeBeanManager.getGoodsTypeListByUserId(userId);
            if (goodsTypeBeanList == null || goodsTypeBeanList.size() == 0) {
                List<Integer> goodsTypeIconList = GoodsTypeBean.getGoodsTypeIconList();
                List<String> goodsTypeNameList = GoodsTypeBean.getGoodsTypeNameList();
                for (int i = 0; i < goodsTypeIconList.size(); i++) {
                    GoodsTypeBean goodsTypeBean = new GoodsTypeBean();
                    goodsTypeBean.setUserId(userId);
                    goodsTypeBean.setCreateTime(System.currentTimeMillis());
                    goodsTypeBean.setId(GoodsTypeBean.createId(userId));
                    goodsTypeBean.setName(goodsTypeNameList.get(i));
                    goodsTypeBean.setIcon(goodsTypeIconList.get(i));
                    goodsTypeBeanManager.saveGoodsType(goodsTypeBean);
                }
            }

            //若无数据，添加默认的物品保存地址
            GoodsSavePlaceBeanManager goodsSavePlaceBeanManager = new GoodsSavePlaceBeanManager(mContext);
            List<GoodsSavePlaceBean> goodsSavePlaceBeanList = goodsSavePlaceBeanManager.getGoodsSavePlaceListByUserId(userId);
            if (goodsSavePlaceBeanList == null || goodsSavePlaceBeanList.size() == 0) {
                List<Integer> goodsSavePlaceIconList = GoodsSavePlaceBean.getGoodsPlaceTypeIconList();
                List<String> goodsSavePlaceNameList = GoodsSavePlaceBean.getGoodsPlaceTypeNameList();
                for (int i = 0; i < goodsSavePlaceIconList.size(); i++) {
                    GoodsSavePlaceBean goodsSavePlaceBean = new GoodsSavePlaceBean();
                    goodsSavePlaceBean.setUserId(userId);
                    goodsSavePlaceBean.setId(GoodsSavePlaceBean.createId(userId));
                    goodsSavePlaceBean.setCreateTime(System.currentTimeMillis());
                    goodsSavePlaceBean.setIcon(goodsSavePlaceIconList.get(i));
                    goodsSavePlaceBean.setName(goodsSavePlaceNameList.get(i));
                    goodsSavePlaceBeanManager.saveGoodsSavePlaceBean(goodsSavePlaceBean);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            jumpToMain();
        }
    }

    private void jumpToRegister() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                jumpToRegister();
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        App.getInstance().exitApp();
    }

    /**
     * 权限判断
     */
    private void judgePermission() {
        mPermissionUtil = new PermissionUtil(this);
        int result = mPermissionUtil.checkPermissions();
        switch (result) {
            case PermissionUtil.ACTION_GRANTED_ALL_PERMISSION://通过了所有的权限
                if (CookieUtil.getAuth()) {
                    jumpToMain();
                    return;
                }
                if (CookieUtil.getAutoLogin()) {
                    login();
                }
                break;
            case PermissionUtil.ACTION_START_ACTIVITY_FOR_RESULT://startActivityForResult
                startActivityForResult(mPermissionUtil.getIntent(), PermissionUtil.REQUEST_CODE_FOR_OVER_DRAW);//上层绘制权限检测
                break;
            case PermissionUtil.ACTION_REQUEST_PERMISSIONS://requestPermissions
                requestPermissions(mPermissionUtil.getPermissionsArray(), PermissionUtil.REQUEST_CODE_FOR_MULTIPLE_PERMISSION);//权限检测
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtil.REQUEST_CODE_FOR_OVER_DRAW) {
            judgePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.REQUEST_CODE_FOR_MULTIPLE_PERMISSION && grantResults.length > 0) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {// 某项权限被拒绝
                    mPermissionUtil.finishActivity(true);//关闭界面，退出程序
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                    }
                }
                if (!allGranted) {
                    ToastUtil.shortShow(this, getString(R.string.system_permission_login_need_all_granted));
                }
            }
        }
    }
}
