package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;

/**
 * 用户表
 */
public class UserBean   extends BaseBean{
    public static final  String PASSWORD = "password";//登录密码
    public static final  String APP_PASSWORD = "app_password";//app密码锁
    public static final  String PHONE_NUMBER = "phone_number";//手机号

    public static final String TABLE_NAME = "user";
    public static final String[] TABLE_COLUMN = {ID,NAME,PASSWORD,APP_PASSWORD,PHONE_NUMBER,CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + NAME + " text, "
            + PASSWORD + " text, "
            + APP_PASSWORD + " text, "
            + PHONE_NUMBER + " text, "
            + CREATE_TIME + " integer default 0 "
            + ")";

    //=======================================================================================================
    private String password;
    private String appPassword;
    private String phoneNumber;

    public static String createId(String name, String phoneNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(phoneNumber);
        builder.append(System.currentTimeMillis());
        builder.append(UserBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
