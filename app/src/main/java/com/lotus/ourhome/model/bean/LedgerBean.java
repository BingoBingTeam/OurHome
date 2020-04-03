package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;
import com.lotus.ourhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 账本表
 */
public class LedgerBean  extends BaseBean{

    public static final String DEFAULT_LEDGER = "日常";//添加默认的日常账本

    //=======================================================================================================
    public static final String USER_ID = "user_id";//登陆者id
    public static final String COLOR = "color";//账本封面颜色

    public static final String TABLE_NAME = "ledger";
    public static final String[] TABLE_COLUMN = {ID, NAME,USER_ID,COLOR, CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + NAME + " text, "
            + USER_ID + " text, "
            + COLOR + " text, "
            + CREATE_TIME + " integer default 0"
            + ")";

    //=======================================================================================================
    private String userId;
    private String color;

    public static String createId(String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(System.currentTimeMillis());
        builder.append(BillBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    //=======================================================================================================
    public static final int DEFAULT_NO_SELECTED_TYPE_COLOR = R.color.app_main;
    public static List<Integer> getDefaultColorList(){
        List<Integer> list = new ArrayList<>();
        list.add(R.color.app_main);
        list.add(R.color.red);
        list.add(R.color.HotPink);
        list.add(R.color.Green);
        list.add(R.color.DarkCyan);
        list.add(R.color.Cyan);
        list.add(R.color.DarkSlateBlue);
        list.add(R.color.OliveDrab);
        list.add(R.color.MediumSlateBlue);
        list.add(R.color.LightSkyBlue);
        list.add(R.color.SaddleBrown);
        list.add(R.color.Sienna);
        list.add(R.color.PaleTurquoise);
        list.add(R.color.Plum);
        list.add(R.color.DarkSalmon);
        list.add(R.color.PaleGoldenRod);
        list.add(R.color.Salmon);
        list.add(R.color.Gold);
        return list;
    }
}
