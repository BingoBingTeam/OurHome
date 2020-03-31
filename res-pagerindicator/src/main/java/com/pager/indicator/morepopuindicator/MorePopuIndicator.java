package com.pager.indicator.morepopuindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.pager.indicator.R;
import com.pager.indicator.tabpagerindctor.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 带有更多参数的viewpager的tab，tab超过三个显示更多按钮
 */
public class MorePopuIndicator extends RelativeLayout implements View.OnClickListener {
    
    private Context mContext;

    /** 顶部滑动*/
    private TabPageIndicator mTabPageIndicator;

    /**全部数据标题布局*/
    private RelativeLayout mRlAllData;

    /**更多布局背景*/
    private FrameLayout mFlMoreBg;


    /**更多按钮*/
    private ImageView mIvMore;

    /**更多弹窗是否展开*/
    private boolean mIsMoreWindowExpanding = false;

    /**tab数量*/
    private int mSize;

    /**菜单*/
    private RecyclerView mRecyclerView;

    /**更多菜单适配器*/
    private IndicatoerMenuAdapter mIndicatoerMenuAdapter;

    private LinearLayout mLlList;

    public MorePopuIndicator(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MorePopuIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public MorePopuIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        inflate(mContext, R.layout.view_more_popu_indicator,this);

        mTabPageIndicator = findViewById(R.id.tab_indicator);
        mFlMoreBg = findViewById(R.id.fl_more_bg);
        mIvMore = findViewById(R.id.iv_more);
        mRlAllData = findViewById(R.id.rl_all_data);
        mLlList = findViewById(R.id.ll_list);
        mRecyclerView = findViewById(R.id.rv_list);

        RecyclerView.LayoutManager layoutManager = new WrapContentGridLayoutManager(mContext,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mIvMore.setOnClickListener(this);
        mLlList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_more) {
            moreClick();
        } else if (id == R.id.ll_list) {
            moreClick();
        }
    }

    /**
     * 更多点击
     */
    public void moreClick(){
        if (mIsMoreWindowExpanding){
            rotateImgMore(-90f,0f);
            alphaAllDataLayout(1.0f,0.1f);
            showMenuAnim(R.anim.indicator_menu_out,GONE);
            alphaMoreBgLayout(0.1f,1.0f);
        }else {
            rotateImgMore(0f,-90f);
            alphaAllDataLayout(0.1f,1.0f);
            mLlList.setVisibility(VISIBLE);
            showMenuAnim(R.anim.indicator_menu_in,VISIBLE);
            alphaMoreBgLayout(1.0f,0.1f);
        }
        mIsMoreWindowExpanding = !mIsMoreWindowExpanding;
    }


    /**
     * 显示菜单
     */
    private void showMenuAnim(int id, final int endVisible){

        Animation animation = AnimationUtils.loadAnimation(mContext,id);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLlList.setVisibility(endVisible);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRecyclerView.startAnimation(animation);
    }

    /**
     * 更多布局背景动画
     * @param start
     * @param end
     */
    private void alphaMoreBgLayout(float start, float end){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFlMoreBg, "alpha", start, end).setDuration(300);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        alpha.start();
    }


    /**
     * 全部数据布局动画
     * @param start
     * @param end
     */
    private void alphaAllDataLayout(final float start, final float end){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mRlAllData, "alpha", start, end).setDuration(300);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (start == 0.1f){
                    mRlAllData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (end == 0.1f){
                    mRlAllData.setVisibility(View.GONE);
                }
            }
        });
        alpha.start();
    }

    /**
     * 更多图片旋转
     * @param start
     * @param end
     */
    private void rotateImgMore(float start, float end){
        ObjectAnimator rotate = ObjectAnimator.ofFloat(mIvMore, "rotation", start, end).setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIvMore.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIvMore.setClickable(false);
            }
        });
        rotate.start();
    }

    /**
     * 设置tab的数量
     * @param dataList
     */
    public void setData( List<IndicatorContentData> dataList, IndicatoerMenuAdapter.OnItemClickCallBack clickCallBack){
        this.mSize = dataList.size();
        mIvMore.setVisibility(mSize > 2 ? View.VISIBLE : View.GONE);
        mFlMoreBg.setVisibility(mSize > 2 ? View.VISIBLE : View.GONE);
        mIndicatoerMenuAdapter = new IndicatoerMenuAdapter(mContext,dataList);
        mIndicatoerMenuAdapter.setOnItemClickCallBack(clickCallBack);
        mRecyclerView.setAdapter(mIndicatoerMenuAdapter);
    }

    /**
     * 获取 TabPageIndicator
     * @return TabPageIndicator
     */
    public TabPageIndicator getTabPageIndicator() {
        return mTabPageIndicator;
    }
}

//================== 使用 ===================
//<!--  列表-->
//<RelativeLayout
//            android:layout_width="match_parent"
//                    android:layout_height="match_parent">
//<com.optima.base.widget.ScrollerEnableViewPager
//        android:id="@+id/viewPager_tab_save_history"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:layout_marginTop="40dp"/>
//<com.optima.gridding.widget.morepopuindicator.MorePopuIndicator
//        android:id="@+id/tab_indicator_save_history"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"/>
//</RelativeLayout>

//    private MorePopuIndicator mMorePopuIndicator;//顶部滑动
//    private TabPageIndicator mTabPageIndicator;
//    private ViewPager mViewPager;
//    private final List<String> mTitles = new ArrayList<>();
//    private final List<View> mPagerViews = new ArrayList<>();
//    private List<IndicatorContentData> mIndicatorContentDatas = new ArrayList<>();


// mMorePopuIndicator = (MorePopuIndicator) mRootView.findViewById(R.id.tab_indicator_save_history);
//         mTabPageIndicator = mMorePopuIndicator.getTabPageIndicator();
//         mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager_tab_save_history);

//mTitles.clear();
//        mPagerViews.clear();
//        mIndicatorContentDatas.clear();
//        //上报事件
//        mTitles.add(getString(R.string.task_history_title_report_event));
//        mPagerViews.add(mReportEventView);
//        addIndicatorContentData(getString(R.string.task_history_title_report_event), R.mipmap.ic_home_event_report);


//    /**
//     * 添加tab的更多菜单的数据
//     *
//     * @param title
//     * @param imgRes
//     */
//    private void addIndicatorContentData(String title, int imgRes) {
//        IndicatorContentData data = new IndicatorContentData();
//        data.setTitle(title);
//        data.setImg(imgRes);
//        mIndicatorContentDatas.add(data);
//    }

//  mMorePopuIndicator.setData(mIndicatorContentDatas, new IndicatoerMenuAdapter.OnItemClickCallBack() {
//@Override
//public void onClick(int index) {
//        if (index < mPagerViews.size()) {
//        if (mMorePopuIndicator != null) {
//        mMorePopuIndicator.moreClick();
//        }
//        mViewPager.setCurrentItem(index, true);
//        }
//        }
//        });
//        mViewPager.setAdapter(new ViewPagerAdapter(mPagerViews, mTitles, mContext));
//        //mTabPageIndicator.setPageChangeListener(this);
//        mTabPageIndicator.setViewPager(mViewPager);
//        mTabPageIndicator.setIndicatorMode(TabPageIndicator.IndicatorMode.MODE_NOWEIGHT_EXPAND_NOSAME);
//        mTabPageIndicator.setViewPager(mViewPager);
//        //mTabPageIndicator.setDividerColor(R.color.white);// 设置分割线的颜色
//        //mTabPageIndicator.setDividerPadding(DensityUtil.dip2px(0));
//        mTabPageIndicator.setIndicatorColor(Color.parseColor("#509AFF"));// 设置底部导航线的颜色
//        mTabPageIndicator.setTextColorSelected(Color.parseColor("#509AFF"));// 设置tab标题选中的颜色
//        mTabPageIndicator.setTextColor(Color.parseColor("#555555"));// 设置tab标题未被选中的颜色
//        mTabPageIndicator.setTextSize(DensityUtil.dip2px(14));// 设置字体大小