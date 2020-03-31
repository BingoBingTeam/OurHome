package cn.pedant.SweetAlert.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.pedant.SweetAlert.R;
import cn.pedant.SweetAlert.views.SweetAlertDialog;

/**
 * types：SweetAlertDialog.NORMAL_TYPE, SweetAlertDialog.ERROR_TYPE,
 * SweetAlertDialog.SUCCESS_TYPE, SweetAlertDialog.WARNING_TYPE, SweetAlertDialog.CUSTOM_IMAGE_TYPE
 */
public class DialogUtils {

    public static final int DIALOG_WAIT_SECOND = 3;//对话框消失，等待倒计时

    /**
     * 返回一个普通提示框(提示语、取消按钮、确定按钮)
     * @param context
     * @param title 标题
     * @param content 提示语
     * @param confirmText 确定按钮文字，为空则表示无此按钮
     * @param confirmClickListener 确定事件回调
     * @param cancelText 取消按钮文字，为空则表示无此按钮
     * @param cancelClickListener 取消事件回调
     * @return
     */
    public static SweetAlertDialog getNormalDialog(Context context, String title, String content,
            String confirmText,SweetAlertDialog.OnSweetClickListener confirmClickListener,
                                                   String cancelText, SweetAlertDialog.OnSweetClickListener cancelClickListener) {

        SweetAlertDialog normalDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        normalDialog.setCancelable(true);
        normalDialog.setCanceledOnTouchOutside(false);
        normalDialog.setTitleText(title);
        normalDialog.setContentText(content);
        if(confirmText != null && confirmText.length() > 0){//确定按钮
            normalDialog.setConfirmText(confirmText);
            if (confirmClickListener != null) {
                normalDialog.setConfirmClickListener(confirmClickListener);
            }
        }else {
            normalDialog.showConfirmButton(false);
        }
        if(cancelText != null && cancelText.length() > 0){//取消按钮
            normalDialog.setCancelText(cancelText);
            if (cancelClickListener != null) {
                normalDialog.setCancelClickListener(cancelClickListener);
            }
        }else {
            normalDialog.showCancelButton(false);
        }
        return normalDialog;
    }


    /**
     * 返回一个警告提示框(提示语、取消按钮、确定按钮)
     * @param context
     * @param title 标题
     * @param content 提示语
     * @param confirmText 确定按钮文字，为空则表示无此按钮
     * @param confirmClickListener 确定事件回调
     * @param cancelText 取消按钮文字，为空则表示无此按钮
     * @param cancelClickListener 取消事件回调
     * @return
     */
    public static SweetAlertDialog getWarnDialog(Context context, String title, String content,
                                                   String confirmText,SweetAlertDialog.OnSweetClickListener confirmClickListener,
                                                   String cancelText, SweetAlertDialog.OnSweetClickListener cancelClickListener) {

        SweetAlertDialog normalDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        normalDialog.setCancelable(true);
        normalDialog.setCanceledOnTouchOutside(false);
        normalDialog.setTitleText(title);
        normalDialog.setContentText(content);
        if(confirmText != null && confirmText.length() > 0){//确定按钮
            normalDialog.setConfirmText(confirmText);
            if (confirmClickListener != null) {
                normalDialog.setConfirmClickListener(confirmClickListener);
            }
        }else {
            normalDialog.showConfirmButton(false);
        }
        if(cancelText != null && cancelText.length() > 0){//取消按钮
            normalDialog.setCancelText(cancelText);
            if (cancelClickListener != null) {
                normalDialog.setCancelClickListener(cancelClickListener);
            }
        }else {
            normalDialog.showCancelButton(false);
        }
        return normalDialog;
    }

    /**
     * 返回一个圆圈显示的进度提示框
     * @param context
     * @param title 标题
     * @param content 提示语
     * @param cancelText 取消按钮文字，为空则表示无按钮
     * @param cancelClickListener 取消事件回调
     * @return
     */
    public static SweetAlertDialog getSpinnerProgressDialog(Context context, String title, String content, String cancelText,
                                                            SweetAlertDialog.OnSweetClickListener cancelClickListener) {

        SweetAlertDialog progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getProgressHelper().setBarColor(Color.BLUE);
        progressDialog.setTitleText(title);
        progressDialog.setContentText(content);
        if(cancelText != null && cancelText.length() > 0){//取消按钮
            progressDialog.setCancelText(cancelText);
            if (cancelClickListener != null) {
                progressDialog.setCancelClickListener(cancelClickListener);
            }
        }else {
            progressDialog.showConfirmButton(false);
        }
        return progressDialog;
    }

    /**
     * 加载对话框（无按钮）
     *
     * @param context
     * @return
     */
    public static SweetAlertDialog getLoadingDialog(Context context) {
        String title = context.getString(R.string.loading);
        String cancelText = context.getString(R.string.cancel);
        return getSpinnerProgressDialog(context, title, null, cancelText, null);
    }

    /**
     * 加载对话框（有“取消”按钮）
     *
     * @param context
     * @return
     */
    public static SweetAlertDialog getLoadingDialog(Context context, SweetAlertDialog.OnSweetClickListener cancelClickListener) {
        String title = context.getString(R.string.loading);
        String content = null;
        String cancelText = context.getString(R.string.cancel);
        return getSpinnerProgressDialog(context, title, content, cancelText, cancelClickListener);
    }

    /**
     * 离开等待对话框
     * DialogUtils.showLeaveWatingDialog(getActivity(), mHandler.getLooper(), "已加入后台上传", BaseConstants.DIALOG_WAIT_SECOND,false);
     * @param activity
     * @param looper
     * @param message
     * @param waitSecond
     * @param isFinish 是否关闭页面
     */
    public static void showLeaveWatingDialog(final Activity activity, Looper looper, CharSequence message, int waitSecond, final boolean isFinish) {
        showLeaveWatingDialog(activity, looper, message, waitSecond, isFinish, null);
    }

    /**
     * 离开等待对话框
     *
     * @param activity
     * @param looper           主线程handler
     * @param message          提示信息
     * @param waitSecond       等待时长
     * @param isFinish         离开，是否关闭页面
     * @param onDialogCallBack 回调
     * @return
     */
    @SuppressLint("StringFormatMatches")
    public static void showLeaveWatingDialog(final Activity activity, Looper looper, CharSequence message,
                                             int waitSecond, final boolean isFinish, final OnDialogCallBack onDialogCallBack) {

        if (activity == null || looper == null) {
            throw new NullPointerException("activity is null");
        }

        if (waitSecond <= 0) {
            waitSecond = DIALOG_WAIT_SECOND;
        }

        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(message.toString());
        sweetAlertDialog.setConfirmText(activity.getString(R.string.dialog_leave_time_button, waitSecond));
        sweetAlertDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    if (isFinish) {
                        activity.finish();
                    } else {
                        if (activity.getFragmentManager().getBackStackEntryCount() > 0) {
                            activity.getFragmentManager().popBackStackImmediate();
                        } else {
                            activity.finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (onDialogCallBack != null) {
                    onDialogCallBack.onMiss();
                }
            }
        });

        final int temp = waitSecond;
        final int handler_decrease_time = 0x99999999;
        final Handler handler = new Handler(looper) {
            int countDown = temp;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                countDown--;
                if (countDown <= 0) {
                    removeCallbacksAndMessages(null);
                    sweetAlertDialog.cancel();
                } else {
                    sweetAlertDialog.setConfirmText(activity.getString(R.string.dialog_leave_time_button, countDown));
                    sendEmptyMessageDelayed(handler_decrease_time, 1000);
                }
            }

        };

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog, int buttonId) {
                handler.removeCallbacksAndMessages(null);
                sweetAlertDialog.cancel();
            }
        });

        sweetAlertDialog.show();
        handler.sendEmptyMessageDelayed(handler_decrease_time, 1000);
    }

    /**
     * 返回一个横向显示的进度提示框
     *
     * @param context
     * @param iconRes
     * @param title
     * @param onDialogClickListener
     * @return
     */
    public static ProgressDialog getHorizontalProgressDialog(Context context, int iconRes, String title,
                                                             DialogInterface.OnClickListener onDialogClickListener) {

        final ProgressDialog progressDialog = new ProgressDialog(context);

        if (onDialogClickListener == null) {
            onDialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.cancel();
                }
            };
        }

        progressDialog.setIcon(iconRes);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        // mDownloadProgressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setProgressNumberFormat("%1d kb/%2d kb");
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), onDialogClickListener);

        return progressDialog;
    }

    /**
     * 后台自动登录时，登录加载对话框
     * @param msg
     * @return
     */
    public static SweetAlertDialog getLoginLoadingDialog(final Activity activity, String msg,SweetAlertDialog.OnSweetClickListener cancelClickListener) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.BLUE);
        sweetAlertDialog.setTitleText(activity.getString(R.string.please_wait));
        sweetAlertDialog.setContentText(msg);
        sweetAlertDialog.setCancelText(activity.getString(R.string.cancel));
        sweetAlertDialog.setCancelClickListener(cancelClickListener);
        return sweetAlertDialog;
    }

    public interface OnDialogCallBack {
        void onMiss();
    }
}
