package com.lotus.ourhome.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.util.Log
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
class CrashHandler private constructor(// 程序的Context对象
    private val mContext: Context
) :
    Thread.UncaughtExceptionHandler {
    // 系统默认的UncaughtException处理类
    private val mDefaultHandler: Thread.UncaughtExceptionHandler?

    // 用来存储设备信息和异常信息
    private val infos: MutableMap<String, String> =
        HashMap()

    // 用于格式化日期,作为日志文件名的一部分
    private val formatter: DateFormat =
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(
        thread: Thread,
        ex: Throwable
    ) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : ", e)
            }
            // 退出程序
            Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //add pxf 同时打印出日志到控制台,方便debug调试
        Log.e("error", "Throwable", ex)
        // 使用Toast来显示异常信息
        object : Thread() {
            override fun run() {
                Looper.prepare()
                //				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop()
            }
        }.start()
        // 收集设备参数信息
        collectDeviceInfo(mContext)
        // 保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private fun collectDeviceInfo(ctx: Context) {
        try {
            val pm = ctx.packageManager
            val pi =
                pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName =
                    if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                infos["versionName"] = versionName
                infos["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(
                TAG,
                "an error occured when collect package info",
                e
            )
        }
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                infos[field.name] = field[null].toString()
                Log.d(
                    TAG,
                    field.name + " : " + field[null]
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "an error occured when collect crash info",
                    e
                )
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex: Throwable): String? {
        val sb = StringBuffer()
        for ((key, value) in infos) {
            sb.append("$key=$value\n")
        }
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)
        try {
            val timestamp = System.currentTimeMillis()
            val time = formatter.format(Date())
            val fileName = "crash-$time-$timestamp.txt"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path =
                    CRASH_FOLDER //"/sdcard/ZFT/log/crash/";
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val fos = FileOutputStream(path + fileName)
                fos.write(sb.toString().toByteArray())
                fos.close()
            }
            return fileName
        } catch (e: Exception) {
            Log.e(
                TAG,
                "an error occured while writing file...",
                e
            )
        }
        return null
    }

    companion object {
        val TAG = CrashHandler::class.java.simpleName
        const val APP_NAME = "ZFT"
        val CRASH_FOLDER = Environment.getExternalStorageDirectory()
            .path + "/Lotus/OurHome/log/crash/"

        // CrashHandler实例
        private var INSTANCE: CrashHandler? = null

        /** 获取CrashHandler实例 ,单例模式  */
        fun getInstance(applicationContext: Context): CrashHandler? {
            if (INSTANCE == null) {
                INSTANCE = CrashHandler(applicationContext)
            }
            return INSTANCE
        }
    }

    /** 保证只有一个CrashHandler实例  */
    init {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}