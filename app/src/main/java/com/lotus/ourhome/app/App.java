package com.lotus.ourhome.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.lotus.ourhome.R;
import com.lotus.ourhome.component.CrashHandler;
import com.lotus.ourhome.component.InitializeService;
import com.lotus.ourhome.ui.user.activity.LoginActivity;
import com.lotus.ourhome.util.CookieUtil;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;


public class App extends Application {

    private static App instance;
    private final List<Activity> mActivityLists = new ArrayList<>();
    private int mRunActivityCount;//正在运行的activity数 用户验证程序的前后台
    private int mActivityStacks = 0;//当前存在的activity堆栈
    private Activity mLastActivity;

    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;
    public static float DIMEN_RATE = -1.0F;
    public static int DIMEN_DPI = -1;

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    public static synchronized App getInstance() {
        return instance;
    }

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Fragmentation.builder()
                // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(true) // 实际场景建议.debug(BuildConfig.DEBUG)
                /**
                 * 可以获取到{@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
                 * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
                 */
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                })
                .install();

        //初始化屏幕宽高
        getScreenSize();

        //初始化数据库
        //Realm.init(getApplicationContext());
        if (Constants.DEBUG) {
            // x.Ext.setDebug(false);// xUtils
            // 程序崩溃日志
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }

        //在子线程中完成其他初始化
        InitializeService.start(this);

        //管理Activity
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityStopped(Activity arg0) {

        }

        @Override
        public void onActivityStarted(Activity arg0) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            mLastActivity = activity;
            mRunActivityCount++;
        }

        @Override
        public void onActivityPaused(Activity arg0) {
            mRunActivityCount--;
        }

        @Override
        public void onActivityDestroyed(Activity arg0) {
            mActivityStacks--;
            if (mActivityLists.contains(arg0)) {
                mActivityLists.remove(arg0);
            }
        }

        @Override
        public void onActivityCreated(Activity arg0, Bundle arg1) {
            mActivityStacks++;
            if (!mActivityLists.contains(arg0)) {
                mActivityLists.add(arg0);
            }
        }
    };

    /**
     * 关闭程序
     */
    public void exitApp() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            if (mActivityLists != null) {
                synchronized (mActivityLists) {
                    for (Activity activity : mActivityLists) {
                        activity.finish();
                    }
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出登录，清楚保存的用户数据
     */
    public void logout(Activity activity){
        CookieUtil.setAuth(false);
        CookieUtil.clearCookie();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void getScreenSize() {
        WindowManager windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        DIMEN_RATE = dm.density / 1.0F;
        DIMEN_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        if(SCREEN_WIDTH > SCREEN_HEIGHT) {
            int t = SCREEN_HEIGHT;
            SCREEN_HEIGHT = SCREEN_WIDTH;
            SCREEN_WIDTH = t;
        }
    }

}
