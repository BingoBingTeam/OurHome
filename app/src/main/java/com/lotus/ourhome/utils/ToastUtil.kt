package com.lotus.ourhome.utils

import android.content.Context
import android.widget.Toast
import com.lotus.ourhome.base.App

object ToastUtil {
    fun showToast(context: Context,msg:String){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    fun showToast(msg:String){
        Toast.makeText(App.instance,msg,Toast.LENGTH_SHORT).show();
    }
}