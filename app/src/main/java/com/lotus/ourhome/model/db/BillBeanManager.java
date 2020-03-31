package com.lotus.ourhome.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 账单表
 */
public class BillBeanManager extends BaseDataManager {
    private static final String TAG = BillBeanManager.class.getSimpleName();

    public BillBeanManager(Context context) {
        super(context);
    }

    /**
     * 保存
     */
    public boolean saveBillBean(BillBean billBean) {
        ContentValues values = new ContentValues();
        values.put(BillBean.ID, billBean.getId());
        values.put(BillBean.LEDGER_ID, billBean.getLedgerId());
        values.put(BillBean.USER_ID, billBean.getUserId());
        values.put(BillBean.TYPE, billBean.getType());
        values.put(BillBean.NAME, billBean.getName());
        values.put(BillBean.MONEY, billBean.getMoney());
        values.put(BillBean.MONEY_USE_TYPE_ID, billBean.getMoneyUseTypeId());
        values.put(BillBean.REMARK, billBean.getRemark());
        values.put(BillBean.HAPPEN_PERSON, billBean.getHappenPerson());
        values.put(BillBean.HAPPEN_TIME, billBean.getHappenTime());
        values.put(BillBean.HAPPEN_TIME_YEAR, billBean.getHappenTimeYear());
        values.put(BillBean.HAPPEN_TIME_MONTH, billBean.getHappenTimeMonth());
        values.put(BillBean.HAPPEN_TIME_DAY, billBean.getHappenTimeDay());
        values.put(BillBean.CREATE_TIME, billBean.getCreateTime());
        open();
        return sqLiteDatabase.replace(BillBean.TABLE_NAME, null, values) != -1;
    }

    public List<BillBean> getBillList(String ledgerId) {
        String selection = BillBean.LEDGER_ID + "=?";
        String[] selectionArgs = {ledgerId};
        return getBillList(selection, selectionArgs, null, null, null, null);
    }

    public List<BillBean> getBillListByUserIdAndHappenTime(String userId, long useTime) {
        String selection = BillBean.USER_ID + "=?";
        String[] selectionArgs = {userId};

        if (useTime > 0) {
            selection += " and " + BillBean.HAPPEN_TIME + "=?";
            selectionArgs = (String[]) ArrayUtils.add(selectionArgs, useTime);
        }
        return getBillList(selection, selectionArgs, null, null, null, null);
    }

    public List<BillBean> getBillListToCurrentTime(String userId, long currentTime) {
        String selection = BillBean.USER_ID + "=?";
        String[] selectionArgs = {userId};

        if (currentTime > 0) {
            selection += " and " + BillBean.HAPPEN_TIME + "<=? order by " + BillBean.HAPPEN_TIME +" desc";
            selectionArgs = (String[]) ArrayUtils.add(selectionArgs, String.valueOf(currentTime));
        }
        return getBillList(selection, selectionArgs, null, null, null, null);
    }

    public List<BillBean> getBillList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<BillBean> billBeanList = new ArrayList<BillBean>();
        MoneyUseTypeBeanManager moneyUseTypeBeanManager = new MoneyUseTypeBeanManager(mContext);
        FamilyMemberBeanManager familyMemberBeanManager = new FamilyMemberBeanManager(mContext);
        Cursor cursor = queryBillBean(selection, selectionArgs, groupBy, having, orderBy, limit);
        while (cursor != null && cursor.moveToNext()) {
            BillBean billBean = getBillBean(BillBean.TABLE_COLUMN, cursor);

            MoneyUseTypeBean moneyUseTypeBean = moneyUseTypeBeanManager.getMoneyUseTypeById(billBean.getMoneyUseTypeId());
            FamilyMemberBean familyMemberBean = familyMemberBeanManager.getFamilyMemberById(billBean.getHappenPerson());

            billBean.setFamilyMemberBean(familyMemberBean);
            billBean.setMoneyUseTypeBean(moneyUseTypeBean);
            billBeanList.add(billBean);
        }
        if (cursor != null) {
            cursor.close();
        }
        return billBeanList;
    }

    public Cursor queryBillBean(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        open();
        return sqLiteDatabase.query(BillBean.TABLE_NAME, BillBean.TABLE_COLUMN, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public BillBean getBillBean(String[] clounms, Cursor cursor) {
        BillBean billBean = new BillBean();
        if (ArrayUtils.contains(clounms, BillBean.ID)) {
            billBean.setId(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.ID)));
        }
        if (ArrayUtils.contains(clounms, BillBean.USER_ID)) {
            billBean.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.USER_ID)));
        }
        if (ArrayUtils.contains(clounms, BillBean.LEDGER_ID)) {
            billBean.setLedgerId(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.LEDGER_ID)));
        }
        if (ArrayUtils.contains(clounms, BillBean.TYPE)) {
            billBean.setType(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.TYPE)));
        }
        if (ArrayUtils.contains(clounms, BillBean.NAME)) {
            billBean.setName(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.NAME)));
        }
        if (ArrayUtils.contains(clounms, BillBean.MONEY)) {
            billBean.setMoney(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.MONEY)));
        }
        if (ArrayUtils.contains(clounms, BillBean.MONEY_USE_TYPE_ID)) {
            billBean.setMoneyUseTypeId(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.MONEY_USE_TYPE_ID)));
        }
        if (ArrayUtils.contains(clounms, BillBean.REMARK)) {
            billBean.setRemark(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.REMARK)));
        }
        if (ArrayUtils.contains(clounms, BillBean.HAPPEN_PERSON)) {
            billBean.setHappenPerson(cursor.getString(cursor.getColumnIndexOrThrow(BillBean.HAPPEN_PERSON)));
        }
        if (ArrayUtils.contains(clounms, BillBean.HAPPEN_TIME)) {
            billBean.setHappenTime(cursor.getLong(cursor.getColumnIndexOrThrow(BillBean.HAPPEN_TIME)));
        }
        if (ArrayUtils.contains(clounms, BillBean.HAPPEN_TIME_YEAR)) {
            billBean.setHappenTimeYear(cursor.getInt(cursor.getColumnIndexOrThrow(BillBean.HAPPEN_TIME_YEAR)));
        }
        if (ArrayUtils.contains(clounms, BillBean.HAPPEN_TIME_MONTH)) {
            billBean.setHappenTimeMonth(cursor.getInt(cursor.getColumnIndexOrThrow(BillBean.HAPPEN_TIME_MONTH)));
        }
        if (ArrayUtils.contains(clounms, BillBean.HAPPEN_TIME_DAY)) {
            billBean.setHappenTimeDay(cursor.getInt(cursor.getColumnIndexOrThrow(BillBean.HAPPEN_TIME_DAY)));
        }
        if (ArrayUtils.contains(clounms, BillBean.CREATE_TIME)) {
            billBean.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(BillBean.CREATE_TIME)));
        }
        return billBean;
    }

}
