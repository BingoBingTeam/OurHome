package com.lotus.ourhome.ui.statistic;

import android.os.Bundle;

import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;

/**
 * 统计
 */
public class StatisticFragment extends SimpleFragment {

    public static StatisticFragment newInstance() {

        Bundle args = new Bundle();

        StatisticFragment fragment = new StatisticFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void initEventAndData() {

    }
}