package com.lotus.ourhome.widget;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.lotus.base.MessageEvent;
import com.lotus.base.constants.BaseConstants;
import com.lotus.base.utils.toasts.ToastUtil;
import com.lotus.ourhome.R;
import com.lotus.ourhome.app.App;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class RecordService extends Service {
	private static final String tag = RecordService.class.getSimpleName();
	protected static final boolean DEBUG = BaseConstants.DEBUG;

	protected Properties properties = null;
	protected Application mApplication;

	private static final int record_status_stop = 0;
	private static final int record_status_recording = 1;
	private static final int record_status_pause = 2;
	private static final int record_status_save = 3;

	/** 最大录音时长 */
	private static final int max_record_second = 3600;
//	private static final String suffix = ".amr";
	private static final String suffix = ".3gpp";

	private WindowManager mWindowManager;

	/** 正在录音悬浮图标 */
	private ImageView mRecordingIcon;
	private WindowManager.LayoutParams mRecordingIconLayoutParams;

	private RecordPanelDialog mRecordPanleDialog;

	private MediaRecorder mMediaRecorder;

	private SaveRecordAsync mSaveRecordAsync;

	/** 录音状态 */
	private int mRecordStatus;

	private long mBeginTime = 0;
	/** 录音总的时间 */
	private long mCountMillisecond = 0;
	/** 分段录制时的总时间 */
	private long mBeginSectionCountMillisecond = 0;

	private boolean isFromChat = false;

	private List<String> mRecordCacheFilePathList;

	private static final int handler_timeChange_changeView = 1;
	private static final int handler_volumeChange_changeView = 2;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case handler_timeChange_changeView:
				if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
					mRecordPanleDialog.setTime(mCountMillisecond);
				}
				break;

			case handler_volumeChange_changeView:

				if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
					mRecordPanleDialog.setVolumeResource((int) msg.obj);
				} else if (mRecordingIcon.getParent() != null) {
					mRecordingIcon.setImageResource((int) msg.obj);
				}

				break;
			}
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(tag, "onCreate");

		properties = new Properties();
		mApplication = (Application) App.getInstance();
		mRecordCacheFilePathList = new ArrayList<String>();
		createView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mRecordStatus != record_status_stop) {// 如果录音不是空闲，则保存录音
			saveRecord();
		}

		return new RecordBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		saveRecord();
		return true;
	}

	@Override
	public void onDestroy() {
		if (mRecordStatus != record_status_stop) {// 如果录音不是空闲，则保存录音
			saveRecord();
		}
		super.onDestroy();
	}

	public class RecordBinder extends Binder {
		public RecordService getService() {
			return RecordService.this;
		}
	}



	private Runnable mTimerRunnable = new Runnable() {

		@Override
		public void run() {
			if (mMediaRecorder != null && mRecordStatus == record_status_recording) {
				mCountMillisecond = mBeginSectionCountMillisecond + System.currentTimeMillis() - mBeginTime;
				mHandler.postDelayed(mTimerRunnable, 100);
				mHandler.sendEmptyMessage(handler_timeChange_changeView);

				if (isDiskAvailable() == false || mCountMillisecond >= max_record_second * 1000) {
					saveRecord();
				}

			}
		}
	};

	private Runnable mMicVolumeRunnable = new Runnable() {
		/* 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600, 当20 * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值 */
		private static final int BASE = 600;

		@Override
		public void run() {
			if (mMediaRecorder != null && mRecordStatus == record_status_recording) {
				int ratio = mMediaRecorder.getMaxAmplitude() / BASE;

				int db = 0;// 分贝
				if (ratio > 1) {
					db = (int) (20 * Math.log10(ratio));
				}

				int imageResource = R.drawable.media_record_microphone_volume_0;
				switch (db / 5) {
				case 0:
					imageResource = R.drawable.media_record_microphone_volume_0;
					break;
				case 1:
					imageResource = R.drawable.media_record_microphone_volume_1;
					break;
				case 2:
					imageResource = R.drawable.media_record_microphone_volume_2;
					break;
				case 3:
					imageResource = R.drawable.media_record_microphone_volume_3;
					break;
				case 4:
					imageResource = R.drawable.media_record_microphone_volume_4;
					break;
				case 5:
					imageResource = R.drawable.media_record_microphone_volume_5;
					break;
				}

				mHandler.postDelayed(mMicVolumeRunnable, 100);

				Message message = mHandler.obtainMessage(handler_volumeChange_changeView);
				message.obj = imageResource;
				mHandler.sendMessage(message);
			}
		}
	};

	/**
	 * 创建桌面悬浮窗口
	 */
	private void createView() {

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

//		mRecordingIconLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
//				WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//add zl 8.0新特性
			mRecordingIconLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		} else{
			mRecordingIconLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		}

		mRecordingIcon = new ImageView(this);
		mRecordingIcon.setImageResource(R.drawable.media_record_microphone_volume_0);
		mRecordingIcon.setBackgroundResource(R.drawable.bg_circle_selector);
		mRecordingIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showRecordPanel();
			}
		});
		// mRecordingIcon.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
		// int iconWidth = mRecordingIcon.getMeasuredWidth();
		// int iconHeight = mRecordingIcon.getMeasuredHeight();
		// mRecordingIconLayoutParams.type = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// mRecordingIconLayoutParams.format = PixelFormat.TRANSLUCENT;// 不设置这个弹出框的透明遮罩显示为黑色
		// mRecordingIconLayoutParams.width = iconWidth;
		// mRecordingIconLayoutParams.height = iconHeight;
		mRecordingIconLayoutParams.gravity = Gravity.CENTER;
		// mRecordingIconLayoutParams.x = getResources().getDisplayMetrics().widthPixels - iconWidth;
		// mRecordingIconLayoutParams.y = getResources().getDisplayMetrics().heightPixels - iconHeight - iconHeight / 2;
		mRecordingIcon.setOnTouchListener(new OnTouchListener() {
			float lastX, lastY;
			int oldOffsetX, oldOffsetY;
			int floatTag = 0;// 悬浮球 所需成员变量

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int action = event.getAction();
				float x = event.getX();
				float y = event.getY();
				if (floatTag == 0) {
					oldOffsetX = mRecordingIconLayoutParams.x; // 偏移量
					oldOffsetY = mRecordingIconLayoutParams.y; // 偏移量
				}
				if (action == MotionEvent.ACTION_DOWN) {
					lastX = x;
					lastY = y;
				} else if (action == MotionEvent.ACTION_MOVE) {
					mRecordingIconLayoutParams.x += (int) (x - lastX) / 3; // 减小偏移量,防止过度抖动
					mRecordingIconLayoutParams.y += (int) (y - lastY) / 3; // 减小偏移量,防止过度抖动
					floatTag = 1;
					mWindowManager.updateViewLayout(view, mRecordingIconLayoutParams);
				} else if (action == MotionEvent.ACTION_UP) {
					int newOffsetX = mRecordingIconLayoutParams.x;
					int newOffsetY = mRecordingIconLayoutParams.y;
					// 只要按钮一动位置不是很大,就认为是点击事件
					if (Math.abs(oldOffsetX - newOffsetX) <= 20 && Math.abs(oldOffsetY - newOffsetY) <= 20) {
						view.performClick();
					} else {
						floatTag = 0;
					}
				}
				return true;
			}
		});
	}

	/**
	 * 显示正在录音浮动图标
	 */
	private void showRecordIcon() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(this) == false) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return;
			}
		}

		if (mRecordStatus == record_status_stop) {
			Log.i(tag, "没有正在录音，不需要显示浮动图标");
			return;
		}
		mWindowManager.addView(mRecordingIcon, mRecordingIconLayoutParams);

		if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
			mRecordPanleDialog.dismiss();
		}
	}

	public void stopRecord() {
		saveRecord();
	}

	/**
	 * 显示录音dialog
	 */
	public void showRecordPanel() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(this) == false) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return;
			}
		}

		if (mRecordingIcon != null && mRecordingIcon.getParent() != null) {
			mWindowManager.removeViewImmediate(mRecordingIcon);
		}
		if (mRecordPanleDialog == null) {
			mRecordPanleDialog = new RecordPanelDialog(this);
		}
		if (mRecordPanleDialog.isShowing() == false) {
			mRecordPanleDialog.show();
		}

		if (mRecordPanleDialog != null) {//再次进入，将显示时间置为0 xhq add
			mRecordPanleDialog.setTime(0);
		}
	}

	/**
	 * 磁盘是否可用和剩余空间是否可以继续录制,
	 *
	 * @return 不可用，小于10M时返回false
	 */
	private boolean isDiskAvailable() {

//		if (org.xutils.common.util.FileUtil.isDiskAvailable() == false) {
//			Log.e(tag, "disk not available");
//			return false;
//		}
//		long diskAvailableSize = org.xutils.common.util.FileUtil.getDiskAvailableSize();

		long cacheSize = 0;
		for (String path : mRecordCacheFilePathList) {
			File file = new File(path);
			cacheSize += file.length();
		}

//		if (diskAvailableSize - cacheSize * 2 < 10 * 1024 * 1024) {// 剩余空间小于10M时
//			Log.i(tag, "剩余空间不足");
//			return false;
//		}

		return true;
	}

	private void startRecord() {
		if (mRecordStatus == record_status_recording || mRecordStatus == record_status_save) { return; }
		// 添加权限判断
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// 读存储权限||写存储权限
				ToastUtil.showToast(this,"没有存储读写权限");
				return;
			}
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
					) {// 麦克风权限
				ToastUtil.showToast(this,"需要麦克风权限");
				return;
			}
		}

		if (!isDiskAvailable()) {
			ToastUtil.showToast(this,"存储空间不足");
			return;
		}
		File folder = new File(BaseConstants.CACHE_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		try {
			String tempRecordCacheFilePath = BaseConstants.CACHE_FOLDER + "/record temp" + (mRecordCacheFilePathList.size() + 1) + suffix;

			mMediaRecorder = new MediaRecorder();
//			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//断点录音需要RAW_AMR
//			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// liuy update code 20180326 原来使用的是MediaRecorder.AudioSource.MIC，这里修改为CAMCORDER，防止在I7上出现usb麦崩溃问题
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//
			mMediaRecorder.setOutputFile(tempRecordCacheFilePath);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//			mMediaRecorder.setAudioEncodingBitRate(128000);
//			mMediaRecorder.setAudioSamplingRate(48000);
//			mMediaRecorder.setAudioChannels(2);

			mMediaRecorder.prepare();// 准备
			mMediaRecorder.start();// 开始

			mBeginTime = System.currentTimeMillis();
			mBeginSectionCountMillisecond = mCountMillisecond;
			mHandler.post(mTimerRunnable);// 启动计时器
			mHandler.post(mMicVolumeRunnable);// 启动音量检测

			if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
				mRecordPanleDialog.setRecording();// 变更界面
			}
			mRecordStatus = record_status_recording;// 修改全局状态

			mRecordCacheFilePathList.add(tempRecordCacheFilePath);// 添加缓存堆栈
		} catch (IllegalStateException e) {
			e.printStackTrace();
//			Log.e(tag, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
//			Log.e(tag, e.getMessage());
			ToastUtil.showToast(this,"启动录音失败");
		}

	}

	private void pauseRecord() {
		if (mRecordStatus != record_status_recording) { return; }

		if (mMediaRecorder != null) {
			try {
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				mMediaRecorder = null;
				resetRecord();
			}
		}
		mRecordStatus = record_status_pause;

		if (mBeginTime != 0) {
			mCountMillisecond = mBeginSectionCountMillisecond + (System.currentTimeMillis() - mBeginTime);
		}

		if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
			mRecordPanleDialog.setPause();
		}

		mBeginSectionCountMillisecond = 0;
		mBeginTime = 0;

	}

	private void resetRecord() {
		if (mRecordStatus == record_status_stop) return;

		pauseRecord();

		mRecordStatus = record_status_stop;

		mCountMillisecond = 0;
		mBeginSectionCountMillisecond = 0;
		mBeginTime = 0;
		if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
			mRecordPanleDialog.setTime(0);
			mRecordPanleDialog.setStop();
		}

		clearCache();
	}

	// private void stopRecord() {
	// if (mRecordStatus == record_status_stop)
	// return;
	// pauseRecord();// 先暂停
	//
	// mRecordStatus = record_status_stop;
	// }

	/**
	 * 保存，并发出通知
	 */
	public void saveRecord() {

		if (mRecordingIcon != null && mRecordingIcon.getParent() != null) {
			mWindowManager.removeViewImmediate(mRecordingIcon);
		}

		if (mRecordCacheFilePathList.size() > 0) {

			pauseRecord();

			mRecordStatus = record_status_save;

			mSaveRecordAsync = new SaveRecordAsync();
			mSaveRecordAsync.execute("");

			if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
				mRecordPanleDialog.setSave();
			}
		} else {
			if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
				mRecordPanleDialog.dismiss();
			}
		}

	}

	/**
	 * 清理缓存文件
	 */
	private void clearCache() {

		for (String path : mRecordCacheFilePathList) {
			File file = new File(path);
			file.delete();
		}

		mRecordCacheFilePathList.clear();
	}



	private class SaveRecordAsync extends AsyncTask<Object, Intent, String> {

		@Override
		protected String doInBackground(Object... arg0) {

			File voiceFile = new File(BaseConstants.AUDIO_FOLDER + "/" + System.currentTimeMillis() + suffix);

			FileOutputStream fileOutputStream = null;
			FileInputStream fileInputStream = null;
			try {
				File voiceFolder = new File(BaseConstants.AUDIO_FOLDER);
				if (voiceFolder.exists() == false) {
					boolean createFolder = voiceFolder.mkdirs();
					if (createFolder == false) {
						Log.i(tag, "创建文件夹失败");
						return "";
					}
				}

				if (voiceFile.exists() == false) {
					boolean createFile = voiceFile.createNewFile();
					if (createFile == false) {
						Log.i(tag, "创建录音合并文件失败");
						return "";
					}
				}
				fileOutputStream = new FileOutputStream(voiceFile);
				for (int i = 0; i < mRecordCacheFilePathList.size(); i++) {
					File file = new File(mRecordCacheFilePathList.get(i));
					fileInputStream = new FileInputStream(file);
					byte[] myByte = new byte[fileInputStream.available()];
					// 文件长度
					int length = myByte.length;

					if (i == 0) {
						while (fileInputStream.read(myByte) != -1) { // 头文件
							fileOutputStream.write(myByte, 0, length);
						}
					} else {
						while (fileInputStream.read(myByte) != -1) {// 之后的文件，去掉头文件就可以了
							fileOutputStream.write(myByte, 6, length - 6);
						}
					}
					fileOutputStream.flush();
					fileInputStream.close();
				}
				// 结束后关闭流
				fileOutputStream.close();

				clearCache();

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(tag, e.getMessage());
				return "";
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return voiceFile.getAbsolutePath();
		}

		@Override
		protected void onPostExecute(String filePath) {
			super.onPostExecute(filePath);

			if (!StringUtils.isEmpty(filePath)) {
				//通知新增附件
				MessageEvent messageEvent = new MessageEvent(MessageEvent.TYPE_ATTACHMENT_ADD);
				messageEvent.setContent(filePath);
				EventBus.getDefault().post(messageEvent);
			} else {
				ToastUtil.showToast(RecordService.this,"保存录音失败");
			}

			resetRecord();

			if (mRecordPanleDialog != null && mRecordPanleDialog.isShowing()) {
				mRecordPanleDialog.dismiss();
			}
		}
	}


	private class RecordPanelDialog extends Dialog {

		private View mPanelView;
		private TextView mTitleTextView;
		private TextView mTimeTextView;
		private ImageView mRunImageView;
		private ImageView mVolumeImageView;
		private Button mResetButton;
		private Button mStartButton;
		private Button mPauseButton;
		private Button mStopButton;

		private RotateAnimation mRotateAnimation;
		private AlphaAnimation mAlphaAnimation;

		private SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss:SS", Locale.CHINA);

		private long mLastClickTime = 0;


		public RecordPanelDialog(Context context) {
			super(context, R.style.simple_dialog);
			init(context);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//				getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//			} else {
//				getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
			} else {
				getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			}
			this.setContentView(mPanelView);
			this.setCanceledOnTouchOutside(true);
			this.setOnDismissListener(mOnDismissListener);
		}

		private void init(Context context) {
			mPanelView = LayoutInflater.from(context).inflate(R.layout.popup_audio_record, null);
			mTitleTextView = (TextView) mPanelView.findViewById(R.id.dialog_record_title_text);
			mRunImageView = (ImageView) mPanelView.findViewById(R.id.dialog_record_microphone_recording_image);
			mVolumeImageView = (ImageView) mPanelView.findViewById(R.id.dialog_record_microphone_volume_image);
			mTimeTextView = (TextView) mPanelView.findViewById(R.id.dialog_record_time_text);
			mResetButton = (Button) mPanelView.findViewById(R.id.dialog_record_reset_button);
			mStartButton = (Button) mPanelView.findViewById(R.id.dialog_record_record_button);
			mPauseButton = (Button) mPanelView.findViewById(R.id.dialog_record_pause_button);
			mStopButton = (Button) mPanelView.findViewById(R.id.dialog_record_close_button);

			mResetButton.setOnClickListener(mOnClickListener);
			mStartButton.setOnClickListener(mOnClickListener);
			mPauseButton.setOnClickListener(mOnClickListener);
			mStopButton.setOnClickListener(mOnClickListener);

			mRotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			mRotateAnimation.setDuration(1000);
			mRotateAnimation.setRepeatMode(Animation.RESTART);
			mRotateAnimation.setInterpolator(context, android.R.anim.linear_interpolator);
			mRotateAnimation.setRepeatCount(-1);

			mAlphaAnimation = new AlphaAnimation(1.0f, 0.1f);
			mAlphaAnimation.setDuration(500);
			mAlphaAnimation.setRepeatMode(Animation.RESTART);
			mAlphaAnimation.setRepeatCount(-1);
		}

		private View.OnClickListener mOnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (System.currentTimeMillis() - mLastClickTime < 1000) { // 小于1秒不处理
					return;
				}
				mLastClickTime = System.currentTimeMillis();


				if (view.equals(mResetButton)) {
					resetRecord();
				} else if (view.equals(mStartButton)) {
					startRecord();
				} else if (view.equals(mPauseButton)) {
					pauseRecord();
				} else if (view.equals(mStopButton)) {
					saveRecord();
				}
			}

		};

		private OnDismissListener mOnDismissListener = new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				if (mRecordStatus != record_status_stop) {
					showRecordIcon();
				}
			}
		};

		@Override
		public void show() {
			super.show();
			switch (mRecordStatus) {
			case record_status_pause:
				setPause();
				break;
			case record_status_recording:
				setRecording();
				break;
			case record_status_save:
				setSave();
				break;
			case record_status_stop:
				setStop();
				break;
			}

			if (mRecordStatus == record_status_save) {
				setCancelable(false);
				setCanceledOnTouchOutside(false);
			} else {
				setCanceledOnTouchOutside(true);
				setCancelable(true);
			}
		}

		/**
		 * 设置音量，取值为0~100
		 *
		 * @param imageResource
		 */
		public void setVolumeResource(int imageResource) {
			if (isShowing()) {
				mVolumeImageView.setImageResource(imageResource);
			}
		}

		public void setTime(long time) {
			if (isShowing()) {
				mTimeTextView.setText(timerFormat.format(new Date(mCountMillisecond)));
			}
		}

		public void setRecording() {
			if (isShowing()) {
				setCancelable(true);
				setCanceledOnTouchOutside(true);

				mTitleTextView.setText("录音");

				mRunImageView.setVisibility(View.VISIBLE);
				mRunImageView.startAnimation(mRotateAnimation);

				mTimeTextView.clearAnimation();

				mResetButton.setEnabled(true);
				mStartButton.setVisibility(View.GONE);
//				mPauseButton.setVisibility(View.VISIBLE);//屏蔽
				mStopButton.setVisibility(View.VISIBLE);
			}
		}

		public void setPause() {
			if (isShowing()) {

				setCancelable(false);
				setCanceledOnTouchOutside(false);

				mTitleTextView.setText("暂停录音");
				mRunImageView.setVisibility(View.GONE);
				mRunImageView.clearAnimation();

				mTimeTextView.startAnimation(mAlphaAnimation);

				mResetButton.setEnabled(true);
				mStartButton.setVisibility(View.VISIBLE);
				mPauseButton.setVisibility(View.GONE);
				mStopButton.setVisibility(View.GONE);
			}
		}

		public void setStop() {
			if (isShowing()) {
				setCancelable(true);
				setCanceledOnTouchOutside(true);

				mTitleTextView.setText("停止录音");

				mRunImageView.setVisibility(View.GONE);
				mRunImageView.clearAnimation();

				mResetButton.setEnabled(false);
				mStartButton.setVisibility(View.VISIBLE);
				mPauseButton.setVisibility(View.GONE);
				mStopButton.setVisibility(View.GONE);
			}
		}

		public void setSave() {
			if (isShowing()) {

				setCancelable(false);
				setCanceledOnTouchOutside(false);

				mTitleTextView.setText("保存录音");

				mRunImageView.setVisibility(View.GONE);
				mRunImageView.clearAnimation();

				mResetButton.setEnabled(false);
				mStartButton.setVisibility(View.VISIBLE);
				mPauseButton.setVisibility(View.GONE);
			}
		}
	}

}

//============== 使用 =========================
//	private RecordService mRecordService;//录音
//@Override
//public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//	EventBus.getDefault().register(this);
//	return mRootView;
//}
//@Override
//public void onActivityCreated(Bundle savedInstanceState) {
//	super.onActivityCreated(savedInstanceState);
//
//	Intent service = new Intent(mContext, RecordService.class);
//	mContext.bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
//}
//
//	//主线程接收消息
//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void onMessageEvent(MessageEvent messageEvent) {
//		if (messageEvent != null) {
//			if (MessageEvent.TYPE_ATTACHMENT_ADD.equals(messageEvent.getType())) {
//				Attachment addAttachment = messageEvent.getAttachment();
//				if (addAttachment != null) {
//					mAttachmentList.add(addAttachment);
//					mAttachmentDisplayAdapter.notifyDataSetChanged();
//				}
//			}
//		}
//	}
//
//	private ServiceConnection mServiceConnection = new ServiceConnection() {
//
//	@Override
//	public void onServiceDisconnected(ComponentName componentName) {
//		mRecordService = null;
//	}
//
//	@Override
//	public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//		mRecordService = ((RecordService.RecordBinder) iBinder).getService();
//		mRecordService.stopRecord();
//	}
//};
//
//@Override
//public void onDestroyView() {
//	super.onDestroyView();
//	EventBus.getDefault().unregister(this);
//	if (mRecordService != null && mContext != null) {
//		mRecordService.stopRecord();
//		mContext.unbindService(mServiceConnection);
//	}
//}