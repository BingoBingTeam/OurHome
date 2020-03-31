package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.FamilyMemberBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class FamilyMemberBeanManager extends BaseDataManager {
    private static final String TAG = FamilyMemberBeanManager.class.getSimpleName();

    public FamilyMemberBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveFamilyMember(FamilyMemberBean familyMemberBean) {
        ContentValues values = new ContentValues();
        values.put(FamilyMemberBean.ID, familyMemberBean.getId());
        values.put(FamilyMemberBean.USER_ID, familyMemberBean.getUserId());
        values.put(FamilyMemberBean.NAME, familyMemberBean.getName());
        values.put(FamilyMemberBean.REMARK, familyMemberBean.getRemark());
        values.put(FamilyMemberBean.CREATE_TIME, familyMemberBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(FamilyMemberBean.TABLE_NAME, null, values) != -1;
    }

    public FamilyMemberBean getFamilyMemberById(String id) {
        String selection = FamilyMemberBean.ID + "=?";
        String[] selectionArgs = {id};
        List<FamilyMemberBean> familyMemberBeanList = getFamilyMemberList(selection, selectionArgs, null, null, null, null);
        return familyMemberBeanList.size() > 0 ? familyMemberBeanList.get(0) : null;
    }

    public List<FamilyMemberBean> getFamilyMemberByUserId(String userId) {
        String selection = FamilyMemberBean.USER_ID + "=?" ;
        String[] selectionArgs = {userId};
        return getFamilyMemberList(selection, selectionArgs, null, null, null, null);
    }

    public List<FamilyMemberBean> getFamilyMemberList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<FamilyMemberBean> familyMemberBeanList = new ArrayList<FamilyMemberBean>();

        Cursor cursor = queryFamilyMemberBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            familyMemberBeanList.add(getFamilyMemberBean(FamilyMemberBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return familyMemberBeanList;
    }

    public Cursor queryFamilyMemberBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(FamilyMemberBean.TABLE_NAME, FamilyMemberBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public FamilyMemberBean getFamilyMemberBean(String[] clounms, Cursor cursor) {
        FamilyMemberBean familyMemberBean = new FamilyMemberBean();
        if (ArrayUtils.contains(clounms, FamilyMemberBean.ID)) {
            familyMemberBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(FamilyMemberBean.ID)));
        }
        if (ArrayUtils.contains(clounms, FamilyMemberBean.NAME)) {
            familyMemberBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(FamilyMemberBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, FamilyMemberBean.USER_ID)) {
            familyMemberBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(FamilyMemberBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, FamilyMemberBean.CREATE_TIME)) {
            familyMemberBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(FamilyMemberBean.CREATE_TIME)));
        }
        if (ArrayUtils.contains(clounms, FamilyMemberBean.REMARK)) {
            familyMemberBean.setRemark(cursor.getString(cursor.getColumnIndexOrThrow(FamilyMemberBean.REMARK)));
        }
        return familyMemberBean;
    }

}

