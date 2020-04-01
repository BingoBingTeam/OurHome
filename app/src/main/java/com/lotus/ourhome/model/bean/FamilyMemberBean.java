package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;

/**
 * 家庭成员表
 */
public class FamilyMemberBean extends BaseBean {

    public static final String USER_ID = "user_id";//登陆者id
    public static final String REMARK = "remark";//备注

    public static final String TABLE_NAME = "family_member";
    public static final String[] TABLE_COLUMN = {ID, USER_ID, NAME, CREATE_TIME, REMARK};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + USER_ID + " text, "
            + NAME + " text, "
            + REMARK + " text, "
            + CREATE_TIME + " integer default 0 "
            + ")";

    //=======================================================================================================
    private String userId;
    private String remark;

    public static String createId(String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(System.currentTimeMillis());
        builder.append(UserBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
