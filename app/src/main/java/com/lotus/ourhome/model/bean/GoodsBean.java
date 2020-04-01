package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;

/**
 * 物品表
 */
public class GoodsBean extends BaseBean{
    public static final String USER_ID = "user_id";//登陆者id
    public static final String TYPE = "type";//物品类别
    public static final String FAMILY_MEMBER_ID = "family_member_id";//家庭成员id，即谁买的或得到的
    public static final String SAVE_PLACE_ID = "save_place_id";//保存地址Id
    public static final String OWN_TIME = "own_time";//拥有的时间，即购买或得到的时间
    public static final String EXPIRATION_TIME = "expiration_time";//保质截止时间
    public static final String EXPIRATION_DURATION = "expiration_duration";//保质时长
    public static final String IMAGE_PATH = "image_path";//图片地址
    public static final String REMARK = "remark";//备注

    public static final String TABLE_NAME = "goods";
    public static final String[] TABLE_COLUMN = {ID,USER_ID,NAME,TYPE,REMARK,FAMILY_MEMBER_ID,SAVE_PLACE_ID,OWN_TIME,EXPIRATION_TIME,EXPIRATION_DURATION,IMAGE_PATH,CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + USER_ID + " text, "
            + NAME + " text, "
            + TYPE + " text, "
            + REMARK + " text, "
            + FAMILY_MEMBER_ID + " text, "
            + SAVE_PLACE_ID + " text, "
            + OWN_TIME + " text, "
            + EXPIRATION_TIME  + " integer default 0, "
            + EXPIRATION_DURATION + " text, "
            + CREATE_TIME + " integer default 0"
            + ")";
    //=======================================================================================================
    private String userId;
    private String type;
    private String familyMemberId;
    private String savePlace;
    private long ownTime;
    private long expirationTime;
    private String expirationDuration;
    private String imagePath;
    private String remark;

    public static String createId(String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(System.currentTimeMillis());
        builder.append(UserBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFamilyMemberId() {
        return familyMemberId;
    }

    public void setFamilyMemberId(String familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public String getSavePlace() {
        return savePlace;
    }

    public void setSavePlace(String savePlace) {
        this.savePlace = savePlace;
    }

    public long getOwnTime() {
        return ownTime;
    }

    public void setOwnTime(long ownTime) {
        this.ownTime = ownTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getExpirationDuration() {
        return expirationDuration;
    }

    public void setExpirationDuration(String expirationDuration) {
        this.expirationDuration = expirationDuration;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
