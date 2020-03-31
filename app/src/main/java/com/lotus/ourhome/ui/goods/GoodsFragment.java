package com.lotus.ourhome.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lotus.base.widget.TitleBarLayout;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 物品页面
 */
public class GoodsFragment extends SimpleFragment {

    @BindView(R.id.title_bar)
    TitleBarLayout titleBar;
    @BindView(R.id.btn_camera)
    TextView btnCamera;
    @BindView(R.id.btn_open_no_logo_app)
    TextView btnOpenNoLogoApp;
    Unbinder unbinder;

    public static GoodsFragment newInstance() {
        Bundle args = new Bundle();
        GoodsFragment fragment = new GoodsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_goods;
    }

    @Override
    protected void initEventAndData() {
        titleBar.setTitle(getString(R.string.bottom_bar_title_goods));
        titleBar.setClickListenerIvBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 唤醒无图标的APP
     */
    private void openNoLogoApp(){
        final String packageName = "com.tuanzi.testdemo";
        final String urlString = "abc";

//        /**============================  a应用启动b应用 ======================================================*/
//        Intent intent = new Intent();
//        //ComponentName（组件名称）是用来打开其他应用程序中的Activity或服务的。
//        ComponentName cn = new ComponentName(packageName, packageName + ".MainActivity");
//        intent.setComponent(cn);
//        Uri uri = Uri.parse(urlString);// 此处应与B程序中Data中标签一致
//        intent.setData(uri);
//        getActivity().startActivity(intent);

        /**==================================================================================*/
        // 通过包名获取要跳转的app，创建intent对象
        Intent intent1 = _mActivity.getPackageManager().getLaunchIntentForPackage(packageName);
        if ( intent1 != null) {
            _mActivity.startActivity(intent1);
        }else {
            ToastUtil.shortShow(getContext(),"intent1为空");
        }
    }

    @OnClick({R.id.btn_camera, R.id.btn_open_no_logo_app})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                break;
            case R.id.btn_open_no_logo_app:
                openNoLogoApp();
                break;
        }
    }
}
