/**
 * Copyright(c)2012 Beijing PeaceMap Co. Ltd.
 * All right reserved.
 */
package com.lotus.base.utils.network;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 网络状态检查
 * @author： aokunsang
 * @date： 2012-12-18
 */
public class NetworkUtil {

	/** 没有连接网络 */
	public static final int NETWORK_NONE = -1;

	/** 移动网络 */
	public static final int NETWORK_MOBILE = 0;

	/** 无线网络 */
	public static final int NETWORK_WIFI = 1;

	public static int getNetWorkState(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
				return NETWORK_WIFI;
			} else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
				return NETWORK_MOBILE;
			}
		} else {
			return NETWORK_NONE;
		}
		return NETWORK_NONE;
	}

	public static interface Type {
		String _WIFI = "WIFI";
		String _GPRS = "GPRS";
		String _CMWAP = "CMWAP";
	}

	/**
	 * 监测当前时候是否有可用网络
	 * @param context
	 * @return 返回true，当前有可用网络 返回false，当前无可用网络
	 */
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取网络类型
	 * @param mContext
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getNetType(Context mContext) {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		State stategprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State statewifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (State.CONNECTED == statewifi && State.CONNECTED == stategprs) {
			return Type._WIFI;
		}
		if (State.CONNECTED != stategprs && State.CONNECTED == statewifi) {
			return Type._WIFI;
		}
		if (State.CONNECTED != statewifi && State.CONNECTED == stategprs) {
			Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
			try {
				String apn = "";
				ContentResolver cr = mContext.getContentResolver();
				Cursor cursor = cr.query(PREFERRED_APN_URI, new String[]{"_id", "apn", "type"}, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					if (cursor.isAfterLast()) {
						return Type._GPRS;
					}
					apn = cursor.getString(1);
					cursor.close();
				}
				if (apn.toUpperCase().equals("CMWAP")) {
					return Type._CMWAP;
				} else if (apn.toUpperCase().equals("CMNET")) {
					return Type._GPRS;
				} else {
					return Type._GPRS;
				}
			} catch (Exception ep) {
				ep.printStackTrace();
			}
		}
		return Type._GPRS;
	}

	/**
	 * 获取当前网络类型
	 * @param context
	 * @return
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		if (null == context) {
			return null;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo;
	}

	/**
	 * 获取当前网络类型 wifi,还是gprs 或者不可用
	 * @param context
	 * @return 0:不可用;
	 */
	public static int getNetworkType(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
			return networkInfo.getType();
		}

		return 0;
	}

	/**
	 * 网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isAvailable(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		if (networkInfo != null && networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 当前是否正在使用wifi
	 * @param context
	 * @return
	 */
	public static boolean isWiFi(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		if (networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 请在异步方法中进行验证是否可以访问外网
	 * @return
	 * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
	 */
	public static boolean ping() {
		InputStream input = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次
			// 读取ping的内容，可以不加
			input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			Log.i("NetworkUtil", "result content : " + stringBuffer.toString());

			int status = p.waitFor();// ping的状态
			if (status == 0) { // success
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
