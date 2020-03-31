package com.lotus.base.utils.string;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Random;

/**
 * com.optima.util
 * LiuYang 2019/12/2 14:27
 */
public class StringUtil {
	static final String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final Random rnd = new Random();

	public static String randomString(Integer length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(str.charAt(rnd.nextInt(str.length())));
		}
		return sb.toString();
	}

	/**
	 * add pxf 得到name的一个字符的ascii值
	 * @param name
	 * @return
	 */
	public static int getNameFirstValue(String name) {
		if (TextUtils.isEmpty(name)) {
			return -1;
		}
		int nameFirstValue = -1;
		String firstStr;
		if (name.length() > 1) {
			firstStr = name.substring(0, 1);
		} else {
			firstStr = name;
		}
		if (firstStr.toCharArray()[0] <= 128) {
			nameFirstValue = firstStr.toCharArray()[0];
		} else {
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			String[] charArr;
			try {
				charArr = PinyinHelper.toHanyuPinyinStringArray((firstStr.toCharArray())[0], defaultFormat);
				if (charArr != null && charArr.length > 0) {
					nameFirstValue = charArr[0].toCharArray()[0];
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
				return nameFirstValue;
			}
		}
		return nameFirstValue;
	}
}
