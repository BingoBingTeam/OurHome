package com.lotus.ourhome.model.bean;

import com.lotus.base.utils.string.StringUtil;
import com.lotus.ourhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品类别表
 */
public class GoodsTypeBean extends BaseBean {
    public static final String ICON = "icon";//图标
    public static final String USER_ID = "user_id";//登陆者id

    public static final String TABLE_NAME = "goods_type";
    public static final String[] TABLE_COLUMN = {ID, USER_ID, NAME,ICON, CREATE_TIME};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + USER_ID + " text, "
            + ICON + " text, "
            + NAME + " text, "
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

    public static List<Integer> getGoodsTypeIconList(){
        List<Integer> list = new ArrayList<>();
        list.add(R.mipmap.ic_general_goods);
        list.add(R.mipmap.ic_snacks);
        list.add(R.mipmap.ic_phone);
        list.add(R.mipmap.ic_medical);
        list.add(R.mipmap.ic_make_up);
        list.add(R.mipmap.ic_learn);
        list.add(R.mipmap.ic_gift);
        list.add(R.mipmap.ic_game);
        list.add(R.mipmap.ic_furniture);
        list.add(R.mipmap.ic_fruit);
        list.add(R.mipmap.ic_food);
        list.add(R.mipmap.ic_fitness);
        list.add(R.mipmap.ic_costume);
        list.add(R.mipmap.ic_beauty);
        return list;
    }

    public static List<String> getGoodsTypeNameList(){
        List<String> list = new ArrayList<>();
        list.add("日用品");
        list.add("零食");
        list.add("电子设备");
        list.add("医疗");
        list.add("化妆");
        list.add("学习用品");
        list.add("礼物");
        list.add("游戏");
        list.add("家居");
        list.add("水果");
        list.add("食材");
        list.add("健身器材");
        list.add("服饰");
        list.add("丽人");
        return list;
    }

}
