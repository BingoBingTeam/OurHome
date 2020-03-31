package com.lotus.ourhome.component;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;


public class InitializeService extends IntentService {

    private static final String ACTION_INIT = "initApplication";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                initApplication();
            }
        }
    }

    private void initApplication() {
        //初始化错误收集
       // x.Ext.init(this); // xUtils

//        if (Constants.DEBUG) {
//           // x.Ext.setDebug(false);// xUtils
//            // 程序崩溃日志
//            CrashHandler crashHandler = CrashHandler.getInstance();
//            crashHandler.init(getApplicationContext());
//        }


        //初始化内存泄漏检测
//        LeakCanary.install(App.getInstance());

        //初始化过度绘制检测
//        BlockCanary.install(getApplicationContext(), new AppBlockCanaryContext()).start();

    }

}
