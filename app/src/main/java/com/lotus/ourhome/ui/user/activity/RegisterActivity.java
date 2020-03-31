package com.lotus.ourhome.ui.user.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lotus.base.widget.TitleBarLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleActivity;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.UserBean;
import com.lotus.ourhome.model.db.FamilyMemberBeanManager;
import com.lotus.ourhome.model.db.UserBeanManager;
import com.lotus.ourhome.util.ToastUtil;

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

    private SaveDataAsync mSaveDataAsync;
    private UserBeanManager mBeanManager;
    private FamilyMemberBeanManager mFamilyMemberBeanManager;

    @Override
    protected int setLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventAndData() {
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
            FamilyMemberBean bean = new FamilyMemberBean();
            bean.setId(FamilyMemberBean.createId(data.getId()));
            bean.setName("我");
            bean.setRemark("");
            bean.setUserId(data.getId());
            bean.setCreateTime(System.currentTimeMillis());
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
        userBean.setId(UserBean.createId(username,phone));
        saveData(userBean);
    }
}
