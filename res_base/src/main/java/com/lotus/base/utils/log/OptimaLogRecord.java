package com.lotus.base.utils.log;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class OptimaLogRecord {

	/** 文本中打印日志开关 */
	private static boolean DEBUG = true;

	private static final String ROOT_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Optima/ZHDD";

	private static final String LOG_FOLDER_PATH = ROOT_FOLDER_PATH + File.separator + "Log";
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static void setPrintEnable(boolean enable){
		DEBUG = enable;
	}

	/**
	 * debug日志
	 * @param content
	 */
	public static void d(String methodName, String content) {
		if (DEBUG) {
			content = "D/" + methodName + " -> " + content;
			coordinate(LOG_FOLDER_PATH, content);
		}
	}

	/**
	 * 错误日志
	 * @param content
	 */
	public static void e(String methodName, String content) {
		if (DEBUG) {
			content = "E/" +methodName + " -> " + content;
			coordinate(LOG_FOLDER_PATH, content);
		}
	}

	/**
	 * 错误日志
	 * @param content
	 */
	public static void i(String methodName, String content) {
		if (DEBUG) {
			content = "I/" +methodName + " -> " + content;
			coordinate(LOG_FOLDER_PATH, content);
		}
	}

	/**
	 * 将字符串写入Log日志中
	 * @param content
	 */
	private static void coordinate(String folderPath, String content) {

		content = timeFormat.format(new Date()) + " : " + content + "\n";

		File folder = new File(folderPath);
		File file = new File(folderPath, dateFormat.format(new Date()) + ".txt");

		try {
			if (!folder.exists()) {
				folder.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(file, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除15天前的所有log文件
	 */
	public static void cleanLog() {

		AsyncTask<Object, Integer, Boolean> asyncTask = new AsyncTask<Object, Integer, Boolean>() {

			private int mFileCount = 0;

			private long mStorageCount = 0;

			private long mFileTime = 0;

			@Override
			protected Boolean doInBackground(Object... arg0) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -15);
				calendar.set(Calendar.HOUR, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);

				mFileTime = calendar.getTimeInMillis();

				File rootFolder = new File(ROOT_FOLDER_PATH);
				if (rootFolder.exists() && rootFolder.isDirectory()) {
					File[] childFile = rootFolder.listFiles();

					if (childFile != null && childFile.length > 0) {
						for (File file : childFile) {
							recursionCleanLog(file);
						}
					}
				}

				return null;
			}

			private void recursionCleanLog(File file) {

				if (file == null) {
					return;
				}

				if (file.isDirectory()) {
					File[] childFile = file.listFiles();

					if (childFile != null && childFile.length > 0) {
						for (File child : childFile) {
							recursionCleanLog(child);
						}
					}
				} else {
					String fileName = file.getName();
					String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
					if ("txt".equals(suffix) && file.lastModified() < mFileTime) {
						mFileCount++;
						mStorageCount += file.length();
						file.delete();
					}
				}

			}

		};
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}