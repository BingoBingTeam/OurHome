package com.lotus.ourhome.util;

import android.content.Context;

import com.lotus.ourhome.R;

import org.apache.commons.lang.StringUtils;

import cn.pedant.SweetAlert.views.SweetAlertDialog;


public class DialogUtil {

    public static SweetAlertDialog getLoadingDialog(Context context, String title, String content){
        SweetAlertDialog mLoadingDataDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        mLoadingDataDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.app_main));
        mLoadingDataDialog.setCancelable(false);
        mLoadingDataDialog.setCanceledOnTouchOutside(false);
        mLoadingDataDialog.setTitleText(title);
        if(!StringUtils.isEmpty(content)){
            mLoadingDataDialog.setContentText(content);
        }
        return mLoadingDataDialog;
    }

    public static SweetAlertDialog getWarningDialog(Context context,String title,String content){
        SweetAlertDialog mWarningDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        mWarningDialog.setTitleText(title);
        mWarningDialog.setCancelText("取消");
        if(!StringUtils.isEmpty(content)){
            mWarningDialog.setContentText(content);
        }
        mWarningDialog.showCancelButton(true);
        mWarningDialog.setConfirmText("确定");
        return mWarningDialog;
    }
}
