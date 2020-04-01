package com.lotus.ourhome.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.content.SharedPreferencesCompat;

import com.lotus.ourhome.app.App;
import com.lotus.ourhome.app.Constants;
import com.lotus.ourhome.model.bean.UserBean;

public class CookieUtil {
    private static UserBean currentUser;

    /**
     * 保存默认显示的账本Id
     */
    public static void setDefaultShowLedger(String ledgerId) {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        cache.put(Constants.SP_DEFAULT_SHOW_LEDGER, ledgerId);
    }

    public static String getDefaultShowLedger() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        return (String)cache.get(Constants.SP_DEFAULT_SHOW_LEDGER, false);
    }

    /**
     * 是否自动记住密码
     */
    public static boolean getRememberPsw() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        return (boolean)cache.get(Constants.SP_REMEMBER_PSW, false);
    }

    public static void setRememberPsw(boolean auth) {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        cache.put(Constants.SP_REMEMBER_PSW, auth);
    }

    /**
     * 是否自动登录
     */
    public static void setAutoLogin(boolean auth) {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        cache.put(Constants.SP_AUTO_LOGIN, auth);
    }

    public static boolean getAutoLogin() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        return (boolean)cache.get(Constants.SP_AUTO_LOGIN, false);
    }

    /**
     * 是否已登录
     */
    public static void setAuth(boolean auth) {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        cache.put(Constants.SP_IS_AUTH, auth);
    }

    public static boolean getAuth() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        return (boolean)cache.get(Constants.SP_IS_AUTH, false);
    }

    /**
     * 保存用户信息
     */
    public static boolean saveUserInfo(UserBean user) {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        boolean result = cache.saveObj(user);
        if (result) {
            currentUser = user;
        }
        return result;
    }

    public static UserBean getUserInfo() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        if (currentUser == null) {
            currentUser = cache.getObj(UserBean.class);
        }
        return currentUser;
    }

    /**
     * 清除cookie
     */
    public static void clearCookie() {
        CacheUtil cache = CacheUtil.build(App.getInstance());
        setAuth(false);
        setDefaultShowLedger("");
        setRememberPsw(false);
        setAutoLogin(false);
        cache.cleaAll();
        currentUser = null;
    }

    public static void put(String key, Object obj) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
       // SharedPreferences sp = App.getInstance().getSharedPreferences(Constant.SHAREDPREFERNCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof String) {
            editor.putString(key, (String) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            editor.putBoolean(key, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            editor.putFloat(key, ((Float) obj).floatValue());
        } else if (obj instanceof Long) {
            editor.putLong(key, ((Long) obj).longValue());
        } else {
            editor.putString(key, obj.toString());
        }
        SharedPreferencesCompat.EditorCompat editorCompat = SharedPreferencesCompat.EditorCompat.getInstance();
        editorCompat.apply(editor);
    }

    public static long get(String key, long defaultObject) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return sp.getLong(key, defaultObject);
    }

    public static String get(String key, String defaultObject) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return sp.getString(key, defaultObject);
    }
}