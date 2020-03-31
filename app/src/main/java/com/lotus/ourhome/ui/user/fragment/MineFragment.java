package com.lotus.ourhome.ui.user.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lotus.ourhome.R;
import com.lotus.ourhome.app.App;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.ui.main.fragment.MainFragment;
import com.lotus.ourhome.util.DialogUtil;
import com.lotus.ourhome.widget.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.views.SweetAlertDialog;

public class MineFragment extends SimpleFragment {

    @BindView(R.id.img_user)
    CircleImageView imgUser;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.rl_version)
    RelativeLayout rlVersion;
    @BindView(R.id.btn_logout)
    TextView btnLogout;
    Unbinder unbinder;
    @BindView(R.id.btn_user_info)
    TextView btnUserInfo;
    @BindView(R.id.btn_family_member)
    TextView btnFamilyMember;
    @BindView(R.id.btn_money_use_type)
    TextView btnMoneyUseType;
    @BindView(R.id.btn_goods_type)
    TextView btnGoodsType;
    @BindView(R.id.btn_goods_save_place)
    TextView btnGoodsSavePlace;
    @BindView(R.id.btn_app_psw)
    TextView btnAppPsw;
    @BindView(R.id.btn_about)
    TextView btnAbout;

    private SweetAlertDialog mWarningDialog;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initEventAndData() {
//        UserBean userBean = mImplPreferencesHelper.getUserInfo();
//        if (userBean != null && userBean.getData() != null) {
//            tvUserName.setText(userBean.getData().getUsername());
//        }
    }

    @OnClick({R.id.img_user, R.id.tv_user_name, R.id.btn_user_info,
            R.id.btn_family_member, R.id.btn_money_use_type,
            R.id.btn_goods_type, R.id.btn_goods_save_place,
            R.id.btn_app_psw, R.id.rl_version, R.id.btn_about, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_user:
                break;
            case R.id.tv_user_name:
                break;
            case R.id.btn_user_info:
                break;
            case R.id.btn_family_member:
                ((MainFragment) getParentFragment()).start(FamilyMemberFragment.newInstance());
                break;
            case R.id.btn_money_use_type:
                ((MainFragment) getParentFragment()).start(MoneyUserTypeFragment.newInstance());
                break;
            case R.id.btn_goods_type:
                ((MainFragment) getParentFragment()).start(GoodsTypeFragment.newInstance());
                break;
            case R.id.btn_goods_save_place:
                ((MainFragment) getParentFragment()).start(GoodsSavePlaceFragment.newInstance());
                break;
            case R.id.btn_app_psw:
                break;
            case R.id.rl_version:
                break;
            case R.id.btn_about:
                break;
            case R.id.btn_logout:
                if (mWarningDialog == null) {
                    mWarningDialog = DialogUtil.getWarningDialog(mContext, "提醒", "确定退出登录吗？");
                }
                mWarningDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog, int buttonId) {
                        mWarningDialog.dismiss();
                    }
                });
                mWarningDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog, int buttonId) {
                        mWarningDialog.dismiss();
                        App.getInstance().logout(mActivity);
                    }
                });
                mWarningDialog.show();
                break;
        }
    }
}
