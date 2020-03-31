package com.lotus.ourhome.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lotus.ourhome.R;
import com.lotus.ourhome.ui.user.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;

public class WelcomeActivity extends SupportActivity {

    private final String TAG = WelcomeActivity.class.getSimpleName();

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.layout_point)
    LinearLayout layoutPoint;
    @BindView(R.id.btn_finish)
    TextView btnFinish;

    private String[] mImages = {"http://attachments.gfan.com/forum/201702/14/181956vo22v9t9uzvvtoxb.jpg"
            , "http://b-ssl.duitang.com/uploads/blog/201411/01/20141101043018_t2YeG.jpeg",
            "http://attachments.gfan.com/forum/201702/14/181953avdaqd7tsw1vdat1.jpg"};

    private int mCurrentImageIndex = 0;

    private static final int DELAY_MILLIS_TIME = 1500;
    private static final int HANDLER_TO_LOGIN = 1;
    private static final int HANDLER_TO_NEXT_IMAGE = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_TO_LOGIN:
                    jumpToLogin();
                    break;
                case HANDLER_TO_NEXT_IMAGE:
                    mCurrentImageIndex++;
                    viewPager.setCurrentItem(mCurrentImageIndex, true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);// 软键盘影响沉
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.app_transparent));
        initView();
    }

    private void initView() {
        ViewPagerAdapter myAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(myAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setLayoutPoint(position);
                mCurrentImageIndex = position;
                if (position == mImages.length - 1) {//已显示最后一张图片，则自动跳转登录页
                    mHandler.sendEmptyMessageDelayed(HANDLER_TO_LOGIN, DELAY_MILLIS_TIME);
                } else {
                    mHandler.sendEmptyMessageDelayed(HANDLER_TO_NEXT_IMAGE, DELAY_MILLIS_TIME);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setLayoutPoint(mCurrentImageIndex);
        mHandler.sendEmptyMessageDelayed(HANDLER_TO_NEXT_IMAGE, DELAY_MILLIS_TIME);
    }

    /**
     * 设置小圆点布局
     *
     * @param position
     */
    private void setLayoutPoint(int position) {
        layoutPoint.removeAllViews();
        for (int i = 0; i < mImages.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置ImageView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(params);
            //设置小圆点样式
            if (position == i) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.point_selected));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.point_default));
            }
            layoutPoint.addView(imageView);
        }
    }

    private void jumpToLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        jumpToLogin();
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private Context mContext;

        public ViewPagerAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(mImages[position]).into(imageView);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToLogin();
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
//        App.getInstance().exitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
