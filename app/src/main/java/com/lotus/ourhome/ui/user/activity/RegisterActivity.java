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

import org.apache.commons.lang.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.utils.DialogUtils;
import cn.pedant.SweetAlert.views.SweetAlertDialog;

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
    private SweetAlertDialog mLoadingDialog = null;

    @Override
    protected int setLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        mBeanManager = new UserBeanManager(this);

        mLoadingDialog = DialogUtils.getLoadingDialog(mContext);

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
            return mBeanManager.saveUserBean(data);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                mLoadingDialog.dismiss();
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

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(psw)){
            ToastUtil.longShow(mContext,"用户名或密码不能为空");
            return;
        }
        if(StringUtils.isEmpty(phone)){
            ToastUtil.longShow(mContext,"电话号码不能为空");
            return;
        }
        if(StringUtils.isEmpty(rePsw) || !rePsw.equals(psw)){
            ToastUtil.longShow(mContext,"输入的两次密码不一致");
            return;
        }

        if(mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
        UserBean userBean = new UserBean();
        userBean.setPassword(psw);
        userBean.setName(username);
        userBean.setPhoneNumber(phone);
        userBean.setId(UserBean.createId(username, phone));
        saveData(userBean);
    }
}
