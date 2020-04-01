package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;
import com.lotus.ourhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品保存地址表
 */
public class GoodsSavePlaceBean extends BaseBean{
    public static final  String USER_ID = "user_id";//登陆者id
    public static final String ICON = "icon";//图标

    public static final String TABLE_NAME = "goods_save_place";
    public static final String[] TABLE_COLUMN = {ID,USER_ID,NAME,ICON,CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + USER_ID + " text, "
            + NAME + " text, "
            + ICON + " text, "
            + CREATE_TIME + " integer default 0 "
            + ")";
    //=======================================================================================================
    private String userId;
    private String icon;

    public static String createId(String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId);
        builder.append(System.currentTimeMillis());
        builder.append(UserBean.class.getName());
        builder.append(Math.random() * 1000000);
        return builder.hashCode() + StringUtil.randomString(6);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    //=======================================================================================================
    public static List<Integer> getGoodsPlaceTypeIconList(){
        List<Integer> list = new ArrayList<>();
            list.add(R.mipmap.ic_kitchen);
            list.add(R.mipmap.ic_bedroom);
            list.add(R.mipmap.ic_balcony);
            list.add(R.mipmap.ic_wc);
        return list;
    }

    public static List<String> getGoodsPlaceTypeNameList(){
        List<String> list = new ArrayList<>();
        list.add("厨房");
        list.add("卧室");
        list.add("阳台");
        list.add("卫生间");
        return list;
    }

}
