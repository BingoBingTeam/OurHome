package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.GoodsBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class GoodsBeanManager extends BaseDataManager {
    private static final String TAG = GoodsBeanManager.class.getSimpleName();

    public GoodsBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveGoods(GoodsBean goodsBean) {
        ContentValues values = new ContentValues();
        values.put(GoodsBean.ID, goodsBean.getId());
        values.put(GoodsBean.USER_ID, goodsBean.getUserId());
        values.put(GoodsBean.TYPE, goodsBean.getType());
        values.put(GoodsBean.FAMILY_MEMBER_ID, goodsBean.getFamilyMemberId());
        values.put(GoodsBean.SAVE_PLACE, goodsBean.getSavePlace());
        values.put(GoodsBean.OWN_TIME, goodsBean.getOwnTime());
        values.put(GoodsBean.EXPIRATION_TIME, goodsBean.getExpirationTime());
        values.put(GoodsBean.EXPIRATION_DURATION, goodsBean.getExpirationDuration());
        values.put(GoodsBean.IMAGE_PATH, goodsBean.getImagePath());
        values.put(GoodsBean.NAME, goodsBean.getName());
        values.put(GoodsBean.CREATE_TIME, goodsBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(GoodsBean.TABLE_NAME, null, values) != -1;
    }

    public GoodsBean getGoodsById(String id) {
        String selection = GoodsBean.ID + "=?";
        String[] selectionArgs = {id};
        List<GoodsBean> familyMemberBeanList = getGoodsList(selection, selectionArgs, null, null, null, null);
        return familyMemberBeanList.size() > 0 ? familyMemberBeanList.get(0) : null;
    }

    public List<GoodsBean> getGoodsListByType(String userId) {
        String selection = GoodsBean.USER_ID + "=?";
        String[] selectionArgs = {userId};
        return getGoodsList(selection, selectionArgs, null, null, null, null);
    }

    public List<GoodsBean> getGoodsList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<GoodsBean> familyMemberBeanList = new ArrayList<GoodsBean>();

        Cursor cursor = queryGoodsBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            familyMemberBeanList.add(getGoodsBean(GoodsBean.TABLE_COLUMN, cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return familyMemberBeanList;
    }

    public Cursor queryGoodsBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(GoodsBean.TABLE_NAME, GoodsBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public GoodsBean getGoodsBean(String[] clounms, Cursor cursor) {
        GoodsBean goodsBean = new GoodsBean();
        if (ArrayUtils.contains(clounms, GoodsBean.ID)) {
            goodsBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.NAME)) {
            goodsBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.USER_ID)) {
            goodsBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.CREATE_TIME)) {
            goodsBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(GoodsBean.CREATE_TIME)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.TYPE)) {
            goodsBean.setType(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.TYPE)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.FAMILY_MEMBER_ID)) {
            goodsBean.setFamilyMemberId(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.FAMILY_MEMBER_ID)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.SAVE_PLACE)) {
            goodsBean.setSavePlace(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.SAVE_PLACE)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.EXPIRATION_DURATION)) {
            goodsBean.setExpirationDuration(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.EXPIRATION_DURATION)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.IMAGE_PATH)) {
            goodsBean.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(GoodsBean.IMAGE_PATH)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.OWN_TIME)) {
            goodsBean.setOwnTime(cursor.getLong(cursor.getColumnIndexOrThrow(GoodsBean.OWN_TIME)));
        }
        if (ArrayUtils.contains(clounms, GoodsBean.EXPIRATION_TIME)) {
            goodsBean.setExpirationTime(cursor.getLong(cursor.getColumnIndexOrThrow(GoodsBean.EXPIRATION_TIME)));
        }
        return goodsBean;
    }

}
