package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.MoneyUseTypeBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class MoneyUseTypeBeanManager extends BaseDataManager {
    private static final String TAG = MoneyUseTypeBeanManager.class.getSimpleName();

    public MoneyUseTypeBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveMoneyUseTypeBean(MoneyUseTypeBean moneyUseTypeBean) {
        ContentValues values = new ContentValues();
        values.put(MoneyUseTypeBean.ID, moneyUseTypeBean.getId());
        values.put(MoneyUseTypeBean.NAME, moneyUseTypeBean.getName());
        values.put(MoneyUseTypeBean.ICON, moneyUseTypeBean.getIcon());
        values.put(MoneyUseTypeBean.USER_ID, moneyUseTypeBean.getUserId());
        values.put(MoneyUseTypeBean.TYPE, moneyUseTypeBean.getType());
        values.put(MoneyUseTypeBean.USE_TYPE, moneyUseTypeBean.getUseType());
        values.put(MoneyUseTypeBean.CREATE_TIME, moneyUseTypeBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(MoneyUseTypeBean.TABLE_NAME, null, values) != -1;
    }

    public MoneyUseTypeBean getMoneyUseTypeById(String id) {
        String selection = MoneyUseTypeBean.ID + "=?";
        String[] selectionArgs = {id};
        List<MoneyUseTypeBean> moneyUseTypeList = getMoneyUseTypeList(selection, selectionArgs, null, null, null, null);
        return moneyUseTypeList.size() > 0 ? moneyUseTypeList.get(0) : null;
    }


    public List<MoneyUseTypeBean> getMoneyUseTypeByUserId(String userId) {
        String selection = MoneyUseTypeBean.USER_ID + "=?";
        String[] selectionArgs = {userId};
        return getMoneyUseTypeList(selection, selectionArgs, null, null, null, null);
    }

    /**
     * 是属于支出的、还是消费的
     */
    public List<MoneyUseTypeBean> getMoneyUseTypeByUserIdAndType(String userId,String type) {
        String selection = MoneyUseTypeBean.USER_ID + "=?" + " and " + MoneyUseTypeBean.TYPE + "=?";
        String[] selectionArgs = {userId,type};
        return getMoneyUseTypeList(selection, selectionArgs, null, null, null, null);
    }

    public List<MoneyUseTypeBean> getMoneyUseTypeList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<MoneyUseTypeBean> moneyUseTypeBeanList = new ArrayList<MoneyUseTypeBean>();

        Cursor cursor = queryMoneyUseTypeBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            moneyUseTypeBeanList.add(getMoneyUseTypeBean(MoneyUseTypeBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return moneyUseTypeBeanList;
    }

    public Cursor queryMoneyUseTypeBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(MoneyUseTypeBean.TABLE_NAME, MoneyUseTypeBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public MoneyUseTypeBean getMoneyUseTypeBean(String[] clounms, Cursor cursor) {
        MoneyUseTypeBean moneyUseTypeBean = new MoneyUseTypeBean();
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.ID)) {
            moneyUseTypeBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.ID)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.NAME)) {
            moneyUseTypeBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.TYPE)) {
            moneyUseTypeBean.setType(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.TYPE)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.ICON)) {
            moneyUseTypeBean.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.ICON)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.USER_ID)) {
            moneyUseTypeBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.USE_TYPE)) {
            moneyUseTypeBean.setUseType(cursor.getString(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.USE_TYPE)));
        }
        if (ArrayUtils.contains(clounms, MoneyUseTypeBean.CREATE_TIME)) {
            moneyUseTypeBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(MoneyUseTypeBean.CREATE_TIME)));
        }
        return moneyUseTypeBean;
    }

}

