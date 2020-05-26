package com.lotus.ourhome.base

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat

abstract class CheckPermissionActivity : Activity(){

    val CODE_FOR_MULTIPLE_PERMISSION = 1
    val REQUSET_CODE_OVER_DRAW = 1000

    fun checkPermission():Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!checkFloat(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.packageName)
                )
                startActivityForResult(intent, REQUSET_CODE_OVER_DRAW)
                return false
            }
            var permissionsMutableList = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) { // 相机
                permissionsMutableList.add(Manifest.permission.CAMERA)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) { // 读
                permissionsMutableList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) { // 写
                permissionsMutableList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) { // 读手机状态
                permissionsMutableList.add(Manifest.permission.READ_PHONE_STATE)
            }
            if(permissionsMutableList.size > 0){
                requestPermissions(permissionsMutableList.toTypedArray(),CODE_FOR_MULTIPLE_PERMISSION)
            }
        }
        return true
    }

    /**
     * 悬浮窗权限
     * @param context
     * @return
     */
    fun checkFloat(context: Context):Boolean{
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                var cls = Class.forName("android.content.Context")
                val declaredField = cls.getDeclaredField("APP_OPS_SERVICE")
                declaredField.isAccessible = true
                var obj: Any? = declaredField[cls] as? String ?: return false
                val str2 = obj as String
                obj = cls.getMethod("getSystemService", String::class.java)
                    .invoke(context, str2)
                cls = Class.forName("android.app.AppOpsManager")
                val declaredField2 = cls.getDeclaredField("MODE_ALLOWED")
                declaredField2.isAccessible = true
                val checkOp = cls.getMethod(
                    "checkOp", Integer.TYPE, Integer.TYPE,
                    String::class.java
                )
                val result = checkOp.invoke(
                    obj,
                    24,
                    Binder.getCallingUid(),
                    context.packageName
                ) as Int
                result == declaredField2.getInt(cls)
            } catch (e: Exception) {
                false
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appOpsMgr =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                        ?: return false
                val mode = appOpsMgr.checkOpNoThrow(
                    "android:system_alert_window",
                    Process.myUid(),
                    context.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            } else {
                Settings.canDrawOverlays(context)
            }
        }
    }
}