package com.lotus.base.utils.regex;

import android.content.Context;
import android.os.Environment;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * 191204
 */
public class VerifyUtil {

	/**
	 * 检查是否存在sd卡
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean checkSoftStage(Context mContext) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 判断是否存在SD卡
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 监测URL地址是否有效
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkURL(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection urlConn = (HttpURLConnection) u.openConnection();
			urlConn.connect();
			if (urlConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 判断是否为数字
	 * @param msg
	 * @return
	 */
	public static String isNaN(String msg) {
		Pattern pattern = Pattern.compile("^[+-]?\\d*[.]?\\d*$");
		Matcher isNum = pattern.matcher(msg);
		if (isNum.matches()) {
			return Double.parseDouble(msg) + "";
		} else {
			return "no";
		}
	}
	
	/**
	 * 验证手机号码
	 * @param mobileNum
	 * @return
	 */
	public static boolean isValidMobileNum(String mobileNum){
		Pattern pattern = Pattern.compile("^1[1-9]\\d{9}$");
		Matcher matcher = pattern.matcher(mobileNum);
		return matcher.matches();
	}
	
	/**
	 * 验证邮箱
	 * @param mail
	 * @return
	 */
	public static boolean isValidMail(String mail){
		Pattern pattern = Pattern.compile("^[A-Za-zd]+([-_.][A-Za-zd]+)*@([A-Za-zd]+[-.])+[A-Za-zd]{2,5}$");
		Matcher matcher = pattern.matcher(mail);
		return matcher.matches();
	}

	/**
	 * 验证字符串是否为空、null
	 */
	public static String verifyString(String str){
		if(null != str && str.length() != 0 ){
			String content = str.toLowerCase();
			if(!content.equals("null")){
				return content;
			}else {
				return "";
			}
		}else {
			return "";
		}
	}
}
