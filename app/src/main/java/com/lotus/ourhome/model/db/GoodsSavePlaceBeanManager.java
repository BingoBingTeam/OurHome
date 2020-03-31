package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.GoodsSavePlaceBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class GoodsSavePlaceBeanManager  extends BaseDataManager {
    private static final String TAG = GoodsSavePlaceBeanManager.class.getSimpleName();

    public GoodsSavePlaceBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveGoodsSavePlaceBean(GoodsSavePlaceBean goodsSavePlaceBean) {
        ContentValues values = new ContentValues();
        values.put(GoodsSavePlaceBean.ID, goodsSavePlaceBean.getId());
        values.put(GoodsSavePlaceBean.USER_ID, goodsSavePlaceBean.getUserId());
        values.put(GoodsSavePlaceBean.NAME, goodsSavePlaceBean.getName());
        values.put(GoodsSavePlaceBean.CREATE_TIME, goodsSavePlaceBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(GoodsSavePlaceBean.TABLE_NAME, null, values) != -1;
    }

    public GoodsSavePlaceBean getFamilyMemberById(String id) {
        String selection = GoodsSavePlaceBean.ID + "=?";
        String[] selectionArgs = {id};
        List<GoodsSavePlaceBean> familyMemberBeanList = getGoodsSavePlaceList(selection, selectionArgs, null, null, null, null);
        return familyMemberBeanList.size() > 0 ? familyMemberBeanList.get(0) : null;
    }

    public List<GoodsSavePlaceBean> getGoodsSavePlaceListByUserId(String userId) {
        String selection = GoodsSavePlaceBean.USER_ID + "=?" ;
        String[] selectionArgs = {userId};
        return getGoodsSavePlaceList(selection, selectionArgs, null, null, null, null);
    }

    public List<GoodsSavePlaceBean> getGoodsSavePlaceList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<GoodsSavePlaceBean> familyMemberBeanList = new ArrayList<GoodsSavePlaceBean>();

        Cursor cursor = queryGoodsSavePlaceBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            familyMemberBeanList.add(getFamilyMemberBean(GoodsSavePlaceBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return familyMemberBeanList;
    }

    public Cursor queryGoodsSavePlaceBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(GoodsSavePlaceBean.TABLE_NAME, GoodsSavePlaceBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public GoodsSavePlaceBean getFamilyMemberBean(String[] clounms, Cursor cursor) {
        GoodsSavePlaceBean goodsSavePlaceBean = new GoodsSavePlaceBean();
        if (ArrayUtils.contains(clounms, GoodsSavePlaceBean.ID)) {
            goodsSavePlaceBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsSavePlaceBean.ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsSavePlaceBean.NAME)) {
            goodsSavePlaceBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(GoodsSavePlaceBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, GoodsSavePlaceBean.USER_ID)) {
            goodsSavePlaceBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsSavePlaceBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsSavePlaceBean.CREATE_TIME)) {
            goodsSavePlaceBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(GoodsSavePlaceBean.CREATE_TIME)));
        }
        return goodsSavePlaceBean;
    }

}
