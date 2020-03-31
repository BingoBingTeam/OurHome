package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;

/**
 * 账本表
 */
public class LedgerBean  extends BaseBean{

    public static final String USER_ID = "user_id";

    public static final String TABLE_NAME = "ledger";
    public static final String[] TABLE_COLUMN = {ID, NAME,USER_ID, CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + NAME + " text, "
            + USER_ID + " text, "
            + CREATE_TIME + " integer default 0"
            + ")";

    //=======================================================================================================
    private String userId;

    public static String createId(String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(System.currentTimeMillis());
        builder.append(BillBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
