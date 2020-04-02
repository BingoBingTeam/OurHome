package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;

/**
 * 账单表
 */
public class BillBean extends BaseBean{

    public static final String TYPE_INCOME = "income";
    public static final String TYPE_EXPENSES = "expenses";
//=======================================================================================================

    public static final String LEDGER_ID = "ledger_id";//所属账本
    public static final String USER_ID = "user_id";//所属用户
    public static final String TYPE = "type";//是收入还是支出
    public static final String MONEY = "money";
    public static final String MONEY_USE_TYPE_ID = "money_use_type_id";//对应MoneyUseTypeBean的id，即钱的来源或使用来别
    public static final String REMARK = "remark";//备注
    public static final String FAMILY_MEMBER_ID = "family_member_id";//使用者ID，即谁用的这笔钱
    public static final String HAPPEN_TIME = "happen_time";// 此笔消费或收入产生的时间
    public static final String HAPPEN_TIME_YEAR = "happen_time_year";//此笔消费或收入产生的时间 -年
    public static final String HAPPEN_TIME_MONTH = "happen_time_month";//此笔消费或收入产生的时间 -月
    public static final String HAPPEN_TIME_DAY = "happen_time_day";//此笔消费或收入产生的时间 -日

    public static final String TABLE_NAME = "bill";
    public static final String[] TABLE_COLUMN = {ID,LEDGER_ID,USER_ID,TYPE,NAME,MONEY,MONEY_USE_TYPE_ID,REMARK,FAMILY_MEMBER_ID,
            HAPPEN_TIME,HAPPEN_TIME_YEAR,HAPPEN_TIME_MONTH,HAPPEN_TIME_DAY,CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + LEDGER_ID + " text, "
            + USER_ID + " text, "
            + TYPE + " text, "
            + NAME + " text, "
            + MONEY + " text, "
            + MONEY_USE_TYPE_ID + " text, "
            + REMARK + " text, "
            + FAMILY_MEMBER_ID + " text, "
            + HAPPEN_TIME + " integer default 0, "
            + HAPPEN_TIME_YEAR + " integer default 0, "
            + HAPPEN_TIME_MONTH + " integer default 0, "
            + HAPPEN_TIME_DAY + " integer default 0, "
            + CREATE_TIME + " integer default 0 "
            + ")";

    //=======================================================================================================
    private String userId;
    private String ledgerId;
    private String type;//是收入还是支出
    private String money;
    private String moneyUseTypeId;
    private String remark;
    private String happenPerson;
    private long happenTime;
    private int happenTimeYear;
    private int happenTimeMonth;
    private int happenTimeDay;
    private LedgerBean ledgerBean;
    private MoneyUseTypeBean moneyUseTypeBean;
    private FamilyMemberBean familyMemberBean;

    public static String createId(String userId, String userName) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(userName);
        builder.append(System.currentTimeMillis());
        builder.append(BillBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public MoneyUseTypeBean getMoneyUseTypeBean() {
        return moneyUseTypeBean;
    }

    public void setMoneyUseTypeBean(MoneyUseTypeBean moneyUseTypeBean) {
        this.moneyUseTypeBean = moneyUseTypeBean;
    }

    public LedgerBean getLedgerBean() {
        return ledgerBean;
    }

    public void setLedgerBean(LedgerBean ledgerBean) {
        this.ledgerBean = ledgerBean;
    }

    public FamilyMemberBean getFamilyMemberBean() {
        return familyMemberBean;
    }

    public void setFamilyMemberBean(FamilyMemberBean familyMemberBean) {
        this.familyMemberBean = familyMemberBean;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHappenPerson() {
        return happenPerson;
    }

    public void setHappenPerson(String happenPerson) {
        this.happenPerson = happenPerson;
    }

    public long getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(long happenTime) {
        this.happenTime = happenTime;
    }

    public int getHappenTimeYear() {
        return happenTimeYear;
    }

    public void setHappenTimeYear(int happenTimeYear) {
        this.happenTimeYear = happenTimeYear;
    }

    public int getHappenTimeMonth() {
        return happenTimeMonth;
    }

    public void setHappenTimeMonth(int happenTimeMonth) {
        this.happenTimeMonth = happenTimeMonth;
    }

    public int getHappenTimeDay() {
        return happenTimeDay;
    }

    public void setHappenTimeDay(int happenTimeDay) {
        this.happenTimeDay = happenTimeDay;
    }

    public String getMoneyUseTypeId() {
        return moneyUseTypeId;
    }

    public void setMoneyUseTypeId(String moneyUseTypeId) {
        this.moneyUseTypeId = moneyUseTypeId;
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

}
