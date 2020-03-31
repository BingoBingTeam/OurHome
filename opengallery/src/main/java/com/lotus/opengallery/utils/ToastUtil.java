package com.lotus.opengallery.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(Context context,String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void shortShow(Context context,String msg) {
       Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context,String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
