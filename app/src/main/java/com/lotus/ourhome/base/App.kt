package com.lotus.ourhome.base

import android.app.Application
import com.lotus.ourhome.utils.CrashHandler

class App:Application() {

    companion object{
        lateinit var instance:App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashHandler.getInstance(this)
    }

    /**
     * 退出登录
     */
    fun logOut(){

    }

    /**
     * 退出程序
     */
    fun exit(){
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }
}