package com.lotus.ourhome.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang.ArrayUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 权限工具：
 * 判断是否有权限、请求权限
 */
public class PermissionUtil {

    public static final int ACTION_GRANTED_ALL_PERMISSION = 1;//获得所有权限

    public static final int ACTION_START_ACTIVITY_FOR_RESULT = 2;//startActivityForResult

    public static final int ACTION_REQUEST_PERMISSIONS = 3;//requestPermissions

    public static final int REQUEST_CODE_FOR_OVER_DRAW = 1000;//上层绘制权限检测

    public static final int REQUEST_CODE_FOR_MULTIPLE_PERMISSION = 10001;//权限检测

    private Activity mActivity;
    private Intent mIntent;
    private String[] mPermissionsArray = {};

    public PermissionUtil(Activity activity){
        this.mActivity = activity;
    }

    /**
     * 验证是否通过了所有的权限
     *
     * @return 通过了所有的权限 1；startActivityForResult 2；requestPermissions 3
     */
    public int checkPermissions() {
        boolean canDrawOverlays = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            AppOpsManager appOpsManager = (AppOpsManager) mActivity.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(),
                    mActivity.getPackageName());
            canDrawOverlays =  mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            canDrawOverlays = Settings.canDrawOverlays(mActivity);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canDrawOverlays){//上层绘制权限检测
                mIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mActivity.getPackageName()));
//                startActivityForResult(mIntent, REQUSET_CODE_OVER_DRAW);
                return ACTION_START_ACTIVITY_FOR_RESULT;
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {// 网络定位
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray,
                        Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {// 粗略定位
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {// 麦克风权限
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray, Manifest.permission.RECORD_AUDIO);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {// 读取手机状态
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray, Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// 读存储权限
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// 写存储权限
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {// 相机权限
                mPermissionsArray = (String[]) ArrayUtils.add(mPermissionsArray, Manifest.permission.CAMERA);
            }
            if (mPermissionsArray.length > 0) {
//                requestPermissions(mPermissionsArray, CODE_FOR_MULTIPLE_PERMISSION);
                return ACTION_REQUEST_PERMISSIONS;
            }
        }
        return ACTION_GRANTED_ALL_PERMISSION;
    }

    /**
     * 关闭页面
     * @param isExitApp 是否关闭程序
     */
    public void finishActivity(boolean isExitApp){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                mActivity.finish();
                if(isExitApp){
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }
        }, 2000);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public String[] getPermissionsArray() {
        return mPermissionsArray;
    }
}
