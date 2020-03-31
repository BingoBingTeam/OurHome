package com.lotus.base.utils.time;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

	/**
	 * 根据时间戳返回日期或时分字符串
	 * @param timestamp
	 * @return
	 */
	public static String getDateOrTime(long timestamp, boolean useHanzi) {
		Calendar ca = Calendar.getInstance();
		int today = ca.get(Calendar.DAY_OF_MONTH);

		ca.setTimeInMillis(timestamp);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		int minute = ca.get(Calendar.MINUTE);

		if (today == day) {
			if (useHanzi) {
				return year + "年" + month + "月" + day + "日";
			} else {
				return year + "/" + month + "/" + day;
			}
		} else {
			if (useHanzi) {
				return hour + "时" + minute + "分";
			} else {
				return hour + ":" + minute;
			}
		}

	}

	public static int getMonth() {
		Calendar c = Calendar.getInstance();
//		int mYear = c.get(Calendar.YEAR); // 获取当前年份
		int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
//		int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
//		int mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
//		int mHour = c.get(Calendar.HOUR_OF_DAY);//时
//		int mMinute = c.get(Calendar.MINUTE);//分
		return mMonth;
	}

	public static int getYear() {
		Calendar c = Calendar.getInstance();//
		int mYear = c.get(Calendar.YEAR); // 获取当前年份
		return mYear;
	}

	/**
	 * 根据提供的年月日获取该月份的第一天
	 */
	public static long getSupportBeginDayofMonth(int monthOfYear) {
		Calendar cal = Calendar.getInstance();
		// 不加下面2行，就是取当前时间前一个月的第一天及最后一天
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date lastDate = cal.getTime();

		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDate = cal.getTime();
		return firstDate.getTime();
	}

	/**
	 * 根据提供的年月获取该月份的最后一天
	 */
	public static long getSupportEndDayofMonth(int monthOfYear) {
		Calendar cal = Calendar.getInstance();
		// 不加下面2行，就是取当前时间前一个月的第一天及最后一天
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date lastDate = cal.getTime();

		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDate = cal.getTime();
		return lastDate.getTime();
	}

	// 日期转化为大小写
	public static String dataToUpper(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		return numToUpper(year) + "年" + monthToUppder(month) + "月" + dayToUppder(day) + "日";
	}

	/***
	 * <b>function:</b> 将数字转化为大写
	 * @createDate 2010-5-27 上午10:28:12
	 * @param num 数字
	 * @return 转换后的大写数字
	 */
	public static String numToUpper(int num) {
		// String u[] = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
		//String u[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
		String u[] = {"○", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
		char[] str = String.valueOf(num).toCharArray();
		String rstr = "";
		for (int i = 0; i < str.length; i++) {
			rstr = rstr + u[Integer.parseInt(str[i] + "")];
		}
		return rstr;
	}

	/***
	 * <b>function:</b> 月转化为大写
	 * @createDate 2010-5-27 上午10:41:42
	 * @param month 月份
	 * @return 返回转换后大写月份
	 */
	public static String monthToUppder(int month) {
		if (month < 10) {
			return numToUpper(month);
		} else if (month == 10) {
			return "十";
		} else {
			return "十" + numToUpper(month - 10);
		}
	}

	/***
	 * <b>function:</b> 日转化为大写
	 * @createDate 2010-5-27 上午10:43:32
	 * @param day 日期
	 * @return 转换大写的日期格式
	 */
	public static String dayToUppder(int day) {
		if (day < 20) {
			return monthToUppder(day);
		} else {
			char[] str = String.valueOf(day).toCharArray();
			if (str[1] == '0') {
				return numToUpper(Integer.parseInt(str[0] + "")) + "十";
			} else {
				return numToUpper(Integer.parseInt(str[0] + "")) + "十" + numToUpper(Integer.parseInt(str[1] + ""));
			}
		}
	}

	/**
	 * 将秒数转换为日时分秒，
	 * @param second 传入的秒数
	 * @return 输出 1000天01小时07分09秒，或  01小时07分09秒
	 */
	public static String secondToTime(long second) {
		long days = second / 86400; //转换天数
		second = second % 86400;    //剩余秒数
		long hours = second / 3600; //转换小时
		second = second % 3600; //剩余秒数
		long minutes = second / 60; //转换分钟
		second = second % 60;   //剩余秒数
		if (days > 0) {
			return days + "天" + (hours < 10 ? "0" : "") + hours + "小时" + (minutes < 10 ? "0" : "") + minutes + "分" + (second < 10 ? "0" : "") + second + "秒";
		} else {
			return (hours < 10 ? "0" : "") + hours + "小时" + (minutes < 10 ? "0" : "") + minutes + "分" + (second < 10 ? "0" : "") + second + "秒";
		}
	}

	/**
	 * 判断时间是否在今天之内
	 * @param time 判断的时间
	 * @return
	 */
	public static boolean isInToday(long time) {
		long todayBegin = getTodayBeginTime();
		long todayEnd = getTodayEndTime();
		return (time > todayBegin) && (time < todayEnd);
	}

	/**
	 * 返回今天的开始时间
	 * @return
	 */
	public static long getTodayBeginTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 返回今天的结束时间
	 * @return
	 */
	public static long getTodayEndTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTimeInMillis();
	}
}
