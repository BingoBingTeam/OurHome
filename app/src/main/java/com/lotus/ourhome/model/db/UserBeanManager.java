package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.UserBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户表
 */
public class UserBeanManager extends BaseDataManager {
    private static final String TAG = UserBeanManager.class.getSimpleName();

    public UserBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveUserBean(UserBean userBean) {
        ContentValues values = new ContentValues();
        values.put(UserBean.ID, userBean.getId());
        values.put(UserBean.NAME, userBean.getName());
        values.put(UserBean.PASSWORD, userBean.getPassword());
        values.put(UserBean.APP_PASSWORD, userBean.getAppPassword());
        values.put(UserBean.PHONE_NUMBER, userBean.getPhoneNumber());
        values.put(UserBean.CREATE_TIME, userBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(UserBean.TABLE_NAME, null, values) != -1;
    }

    public UserBean getUserById(String id) {
        String selection = UserBean.ID + "=?";
        String[] selectionArgs = {id};
        return getUser(selection, selectionArgs, null, null, null, null);
    }

    public UserBean getUserByNameAndPsw(String name, String password) {
        String selection = UserBean.NAME + "=?" + " and " + UserBean.PASSWORD + "=?";
        String[] selectionArgs = {name, password};
        return getUser(selection, selectionArgs, null, null, null, null);
    }

    public UserBean getUser(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<UserBean> userBeanList = new ArrayList<UserBean>();

        Cursor cursor = queryUserBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            userBeanList.add(getUserBean(UserBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return userBeanList.size() > 0 ? userBeanList.get(0) : null;
    }

    public Cursor queryUserBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(UserBean.TABLE_NAME, UserBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public UserBean getUserBean(String[] clounms, Cursor cursor) {
        UserBean userBean = new UserBean();
        if (ArrayUtils.contains(clounms, UserBean.ID)) {
            userBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(UserBean.ID)));
        }
        if (ArrayUtils.contains(clounms, UserBean.NAME)) {
            userBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, UserBean.PASSWORD)) {
            userBean.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(UserBean.PASSWORD)));
        }
        if (ArrayUtils.contains(clounms, UserBean.APP_PASSWORD)) {
            userBean.setAppPassword(cursor.getString(cursor.getColumnIndexOrThrow(UserBean.APP_PASSWORD)));
        }
        if (ArrayUtils.contains(clounms, UserBean.PHONE_NUMBER)) {
            userBean.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(UserBean.PHONE_NUMBER)));
        }
        if (ArrayUtils.contains(clounms, UserBean.CREATE_TIME)) {
            userBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(UserBean.CREATE_TIME)));
        }
        return userBean;
    }

}
