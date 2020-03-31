package com.lotus.base.utils.json;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.List;

/**
 * json转换类
 * @author 向蕙蜻
 */
public class JsonUtil {

	ObjectMapper mObjectMapper;
	JsonFactory mJsonFactory;
	static JsonUtil mJacksonUtil;

	public static JsonUtil getInstance() {
		if (mJacksonUtil == null) {
			mJacksonUtil = new JsonUtil();
		}
		return mJacksonUtil;
	}

	private JsonUtil() {
		mObjectMapper = new ObjectMapper();
		mObjectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);//（禁止SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS）
		mJsonFactory = mObjectMapper.getJsonFactory();
	}

	/**
	 * 将json字符串数据，转换成cls类实例
	 */
	public <T extends Object> T jsonToObject(String json, Class<T> cls) throws JsonParseException, IOException {
		return mObjectMapper.readValue(json, cls);
	}

	/**
	 * 将实例对象，转化成json字符串
	 */
	public String objectToJson(Object obj) {
		try {
			return mObjectMapper.writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * json转list
	 */
	public <T extends Object> List<T> jsonToList(String json, Class<T> cls) {
		List<T> list = null;
		JavaType javaType = mObjectMapper.getTypeFactory().constructParametricType(List.class, cls);
		try {
			list = mObjectMapper.readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * list转json
	 * @param <T>list 数据
	 */
	public <T extends Object> String listToJson(List<T> list) {
		String memberStr = null;
		try {
			memberStr = mObjectMapper.writeValueAsString(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return memberStr;
	}

}
