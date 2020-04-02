package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.LedgerBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 账本表
 */
public class LedgerBeanManager extends BaseDataManager {
    private static final String TAG = LedgerBeanManager.class.getSimpleName();

    public LedgerBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveLedgerBean(LedgerBean ledgerBean) {
        ContentValues values = new ContentValues();
        values.put(LedgerBean.ID, ledgerBean.getId());
        values.put(LedgerBean.NAME, ledgerBean.getName());
        values.put(LedgerBean.COLOR, ledgerBean.getColor());
        values.put(LedgerBean.USER_ID, ledgerBean.getUserId());
        values.put(LedgerBean.CREATE_TIME, ledgerBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(LedgerBean.TABLE_NAME, null, values) != -1;
    }

    public LedgerBean getLedgerById(String id) {
        String selection = LedgerBean.ID + "=?";
        String[] selectionArgs = {id};
        List<LedgerBean> ledgerBeanList = getLedgerList(selection, selectionArgs, null, null, null, null);
        return ledgerBeanList.size() > 0 ? ledgerBeanList.get(0) : null;
    }

    public List<LedgerBean> getLedgerByUserId(String userId) {
        String selection = LedgerBean.USER_ID + "=?";
        String[] selectionArgs = {userId};
        return getLedgerList(selection, selectionArgs, null, null, null, null);
    }

    public List<LedgerBean> getLedgerList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<LedgerBean> userBeanList = new ArrayList<LedgerBean>();

        Cursor cursor = queryLedgerBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            userBeanList.add(getLedgerBean(LedgerBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return userBeanList;
    }

    public Cursor queryLedgerBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(LedgerBean.TABLE_NAME, LedgerBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public LedgerBean getLedgerBean(String[] clounms, Cursor cursor) {
        LedgerBean ledgerBean = new LedgerBean();
        if (ArrayUtils.contains(clounms, LedgerBean.ID)) {
            ledgerBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(LedgerBean.ID)));
        }
        if (ArrayUtils.contains(clounms, LedgerBean.NAME)) {
            ledgerBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(LedgerBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, LedgerBean.COLOR)) {
            ledgerBean.setColor(cursor.getString(cursor.getColumnIndexOrThrow(LedgerBean.COLOR)));
        }
        if (ArrayUtils.contains(clounms, LedgerBean.USER_ID)) {
            ledgerBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(LedgerBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, LedgerBean.CREATE_TIME)) {
            ledgerBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(LedgerBean.CREATE_TIME)));
        }
        return ledgerBean;
    }

}
