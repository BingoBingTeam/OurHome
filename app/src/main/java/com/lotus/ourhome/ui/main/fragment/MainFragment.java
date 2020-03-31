package com.lotus.ourhome.ui.main.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.base.event.TabSelectedEvent;
import com.lotus.ourhome.base.widget.BottomBar;
import com.lotus.ourhome.base.widget.BottomBarTab;
import com.lotus.ourhome.component.EventBusActivityScope;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.ui.bill.fragment.BillListFragment;
import com.lotus.ourhome.ui.goods.GoodsFragment;
import com.lotus.ourhome.ui.statistic.StatisticFragment;
import com.lotus.ourhome.ui.user.fragment.MineFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends SimpleFragment {

    @BindView(R.id.fl_tab_container)
    FrameLayout flTabContainer;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    Unbinder unbinder;

    private static final int REQ_MSG = 10;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private SupportFragment[] mFragments = new SupportFragment[4];

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(BillListFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = BillListFragment.newInstance();
            mFragments[SECOND] = StatisticFragment.newInstance();
            mFragments[THIRD] = GoodsFragment.newInstance();
            mFragments[FOURTH] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(BillListFragment.class);
            mFragments[THIRD] = findChildFragment(GoodsFragment.class);
            mFragments[FOURTH] = findChildFragment(MainFragment.class);
        }
    }

    @Override
    protected void initEventAndData() {
        bottomBar
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_account_book_default, getString(R.string.bottom_bar_title_bill)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_statistics_default, getString(R.string.bottom_bar_title_statistics)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_goods_default, getString(R.string.bottom_bar_title_goods)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_main_default, getString(R.string.bottom_bar_title_main)));

        // 模拟未读消息
        bottomBar.getItem(FIRST).setUnreadCount(9);

        bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);

                BottomBarTab tab = bottomBar.getItem(FIRST);
                if (position == FIRST) {
                    tab.setUnreadCount(0);
                } else {
                    tab.setUnreadCount(tab.getUnreadCount() + 1);
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
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

}
