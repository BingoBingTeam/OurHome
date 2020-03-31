package com.lotus.base.constants;

import android.os.Environment;

public class BaseConstants {

	public static boolean DEBUG = true;

	public static final String BASE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Lotus/OurHome";
	public static final String BASE_HIDEN_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.Lotus/OurHome";//隐藏文件夹
	public static final String IMAGE_FOLDER = BASE_HIDEN_FOLDER + "/image";//
	public static final String VIDEO_FOLDER = BASE_HIDEN_FOLDER + "/video";//
	public static final String AUDIO_FOLDER = BASE_HIDEN_FOLDER + "/audio";//
	public static final String CACHE_FOLDER = BASE_HIDEN_FOLDER + "/cache";//
	public static final String APP_FOLDER = BASE_FOLDER + "/app";
	public static final String LOG_FOLDER = BASE_FOLDER + "/log";
	public static final String PDF_FOLDER = BASE_FOLDER + "/pdf";
	public static final String OTHER_FOLDER = BASE_FOLDER + "/other";
	public static final String TEXT_FOLDER = BASE_FOLDER + "/text";

	/** 广播私有权限 */
	public static final String BROADCASE_PERMISSION = "com.lotus.ourhome.broadcase.permission";

}
