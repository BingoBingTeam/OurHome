package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.GoodsTypeBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class GoodsTypeBeanManager   extends BaseDataManager {
    private static final String TAG = GoodsTypeBeanManager.class.getSimpleName();

    public GoodsTypeBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveGoodsType(GoodsTypeBean goodsTypeBean) {
        ContentValues values = new ContentValues();
        values.put(GoodsTypeBean.ID, goodsTypeBean.getId());
        values.put(GoodsTypeBean.USER_ID, goodsTypeBean.getUserId());
        values.put(GoodsTypeBean.NAME, goodsTypeBean.getName());
        values.put(GoodsTypeBean.ICON, goodsTypeBean.getIcon());
        values.put(GoodsTypeBean.CREATE_TIME, goodsTypeBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(GoodsTypeBean.TABLE_NAME, null, values) != -1;
    }

    public GoodsTypeBean getGoodsTypeById(String id) {
        String selection = GoodsTypeBean.ID + "=?";
        String[] selectionArgs = {id};
        List<GoodsTypeBean> familyMemberBeanList = getGoodsTypeList(selection, selectionArgs, null, null, null, null);
        return familyMemberBeanList.size() > 0 ? familyMemberBeanList.get(0) : null;
    }

    public List<GoodsTypeBean> getGoodsTypeListByUserId(String userId) {
        String selection = GoodsTypeBean.USER_ID + "=?" ;
        String[] selectionArgs = {userId};
        return getGoodsTypeList(selection, selectionArgs, null, null, null, null);
    }

    public List<GoodsTypeBean> getGoodsTypeList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<GoodsTypeBean> familyMemberBeanList = new ArrayList<GoodsTypeBean>();

        Cursor cursor = queryGoodsTypeBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            familyMemberBeanList.add(getGoodsTypeBean(GoodsTypeBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return familyMemberBeanList;
    }

    public Cursor queryGoodsTypeBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(GoodsTypeBean.TABLE_NAME, GoodsTypeBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public GoodsTypeBean getGoodsTypeBean(String[] clounms, Cursor cursor) {
        GoodsTypeBean goodsTypeBean = new GoodsTypeBean();
        if (ArrayUtils.contains(clounms, GoodsTypeBean.ID)) {
            goodsTypeBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsTypeBean.ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsTypeBean.NAME)) {
            goodsTypeBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(GoodsTypeBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, GoodsTypeBean.ICON)) {
            goodsTypeBean.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(GoodsTypeBean.ICON)));
        }
        if (ArrayUtils.contains(clounms, GoodsTypeBean.USER_ID)) {
            goodsTypeBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsTypeBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsTypeBean.CREATE_TIME)) {
            goodsTypeBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(GoodsTypeBean.CREATE_TIME)));
        }
        return goodsTypeBean;
    }

}
