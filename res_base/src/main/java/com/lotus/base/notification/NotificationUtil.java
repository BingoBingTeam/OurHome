package com.lotus.base.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

/**
 * com.optima.android.notification
 * LiuYang 2019-06-13 10:48
 */
public class NotificationUtil {

	private static final String TAG = "NotificationUtil";

	@SuppressLint("StaticFieldLeak")
	private static NotificationUtil mNotificationUtil;
	private final Context mContext;

	private Notification mNotification;
	private NotificationManager mNotificationManager;

	private final String DEFAULT_CHANNEL_ID = "com.optima.ggr";
	private final String DOWNLOAD_CHANNEL_ID = "com.optima.ggr.download";

	private final int mNotificationId = 9999;
	private String mContentInfo;
	private int mSmallIcon;
	private Bitmap mLargeIcon;
	private int mLockScreenVisibility = Notification.VISIBILITY_PUBLIC; //  VISIBILITY_PRIVATE = 0;  VISIBILITY_PRIVATE = 0; VISIBILITY_SECRET = -1;
	private String mContentTitle;
	private String mContentText;
	private String mSubText;
	private boolean mEnableLights = false;
	private int mLightColor = Color.RED;
	private PendingIntent mContentIntent;
	private boolean mOngoing = false;
	private boolean mEnableVibration = false;

	public static NotificationUtil getInstance(Context applicationContext) {
		if (mNotificationUtil == null) {
			mNotificationUtil = new NotificationUtil(applicationContext);
		}
		return mNotificationUtil;
	}

	private NotificationUtil(Context context) {
		if (context == null) {
			throw new ClassCastException("The context is null ");
		}
		mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void createNotification() {
		Notification.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, "默认常驻", NotificationManager.IMPORTANCE_DEFAULT);
			channel.enableLights(mEnableLights);
			channel.setLightColor(mLightColor);
			channel.setShowBadge(false);
			channel.setSound(null, null);
			channel.enableVibration(mEnableVibration);
			channel.setLockscreenVisibility(mLockScreenVisibility);
			channel.canBypassDnd();
			channel.setBypassDnd(true);

			mNotificationManager.createNotificationChannel(channel);

			builder = new Notification.Builder(mContext, DEFAULT_CHANNEL_ID);
		} else {
			builder = new Notification.Builder(mContext);
		}

		if (mContentIntent != null) {
			builder.setContentIntent(mContentIntent);
		}
		if (mSubText != null) {
			builder.setSubText(mSubText);
		}
		if (mContentTitle != null) {
			builder.setContentTitle(mContentTitle);
		}
		if (mContentInfo != null) {
			builder.setContentInfo(mContentInfo);
		}
		if (mContentTitle != null) {
			builder.setContentTitle(mContentTitle);
		}
		if (mContentText != null) {
			builder.setContentText(mContentText);
		}
		builder.setSmallIcon(mSmallIcon);
		if (mLargeIcon != null) {
			builder.setLargeIcon(mLargeIcon);
		}
		builder.setWhen(System.currentTimeMillis());
		builder.setOngoing(mOngoing);

		mNotification = builder.build();
	}

	/**
	 * 显示通知栏下载进度
	 * @param id      通知id
	 * @param process 当前进度
	 * @param process 总进度
	 */
	public void showDownload(int id, int process, int max) {
		Notification.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, "下载", NotificationManager.IMPORTANCE_HIGH);
			channel.enableLights(mEnableLights);
			channel.setLightColor(mLightColor);
			channel.setShowBadge(false);
			channel.setSound(null, null);
			channel.enableVibration(false);

			mNotificationManager.createNotificationChannel(channel);

			builder = new Notification.Builder(mContext, DOWNLOAD_CHANNEL_ID);
		} else {
			builder = new Notification.Builder(mContext);
		}
		builder.setAutoCancel(false);
		builder.setOnlyAlertOnce(true);
		builder.setContentTitle("正在下载升级包");
		builder.setContentText((process * 100 / max) + "%");
		builder.setProgress(max, process, false);
		builder.setSmallIcon(mSmallIcon);
		builder.setWhen(System.currentTimeMillis());
		builder.setOngoing(mOngoing);

		mNotificationManager.notify(id, builder.build());
	}

	public void cancelDownload(int id) {
		Log.i(TAG, "cancelDownload: ");
		mNotificationManager.cancel(id);
	}

	public void startForeground(Service service) {
		service.startForeground(mNotificationId, getNotification(true));
	}

	public void updateNotification() {
		mNotificationManager.notify(mNotificationId, getNotification(true));
	}

	public void cancelNotification() {
		mNotificationManager.cancel(mNotificationId);
	}

	public void cancelAllNotification() {
		mNotificationManager.cancelAll();
	}

	public Notification getNotification(boolean update) {
		if (mNotification == null || update) {
			createNotification();
		}
		return mNotification;
	}

	public void setSmallIcon(int smallIcon) {
		this.mSmallIcon = smallIcon;
	}

	public void setContentInfo(String montentInfo) {
		this.mContentInfo = mContentInfo;
	}

	public void setContentTitle(String mContentTitle) {
		this.mContentTitle = mContentTitle;
	}

	public String getContentText() {
		return mContentText;
	}

	public void setContentText(String mContentText) {
		this.mContentText = mContentText;
	}

	public void setEnableLights(boolean mEnableLights) {
		this.mEnableLights = mEnableLights;
	}

	public void setLockScreenVisibility(int lockscreenVisibility) {

		this.mLockScreenVisibility = lockscreenVisibility;
	}

	public void setSubText(String mSubText) {
		this.mSubText = mSubText;
	}

	public void setLightColor(int mLightColor) {
		this.mLightColor = mLightColor;
	}

	public void setLargeIcon(int largeIcon) {
		try {
			this.mLargeIcon = BitmapFactory.decodeResource(mContext.getResources(), largeIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setContentIntent(PendingIntent contentIntent) {
		if (contentIntent != null) {
			this.mContentIntent = contentIntent;
		}
	}

	public void setOngoing(boolean ongoing) {
		this.mOngoing = ongoing;
	}

	public void setEnableVibration(boolean enableVibration) {
		this.mEnableVibration = mEnableVibration;
	}
}
