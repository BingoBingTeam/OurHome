package com.lotus.ourhome.ui.main.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleActivity;
import com.lotus.ourhome.ui.main.fragment.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends SimpleActivity {

    @BindView(R.id.frame_layout)
    FrameLayout flContainer;

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.frame_layout, MainFragment.newInstance());
        }
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
//        App.getInstance().exitApp();  
    }

}
