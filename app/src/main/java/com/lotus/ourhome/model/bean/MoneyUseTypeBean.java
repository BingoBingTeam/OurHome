package com.lotus.ourhome.model.bean;

import android.content.Context;

import com.lotus.base.utils.string.StringUtil;
import com.lotus.ourhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱的来源或使用来别
 */
public class MoneyUseTypeBean extends BaseBean{

    public static final  String TYPE = "type";//区别是属于收入、还是支出的
    public static final  String ICON = "icon";//图标
    public static final  String USER_ID = "user_id";//登录用户id

    public static final String TABLE_NAME = "bill_type";
    public static final String[] TABLE_COLUMN = {ID,NAME,ICON,CREATE_TIME,USER_ID};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + NAME + " text, "
            + TYPE + " text, "
            + ICON + " text, "
            + USER_ID + " text, "
            + CREATE_TIME + " integer default 0"
            + ")";
    //=======================================================================================================
    private String type;
    private String icon;
    private String userId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    //=======================================================================================================
    public static final int DEFAULT_NO_SELECTED_TYPE_ICON = -1;
    public static List<Integer> getMoneyUserTypeIconList(String type){
        List<Integer> list = new ArrayList<>();
        list.add(R.mipmap.ic_saving_pot);
        list.add(R.mipmap.ic_money_packet);
        list.add(R.mipmap.ic_money);
        list.add(R.mipmap.ic_mark);
        list.add(R.mipmap.ic_red_packet);
        list.add(R.mipmap.ic_wallet);
        list.add(R.mipmap.ic_love);
        list.add(R.mipmap.ic_collect);
        if(BillBean.TYPE_EXPENSES.equals(type)){
            list.add(R.mipmap.ic_traffic);
            list.add(R.mipmap.ic_take_out);
            list.add(R.mipmap.ic_snacks);
            list.add(R.mipmap.ic_repair);
            list.add(R.mipmap.ic_play);
            list.add(R.mipmap.ic_phone);
            list.add(R.mipmap.ic_pet);
            list.add(R.mipmap.ic_medical);
            list.add(R.mipmap.ic_make_up);
            list.add(R.mipmap.ic_learn);
            list.add(R.mipmap.ic_home);
            list.add(R.mipmap.ic_gift);
            list.add(R.mipmap.ic_general_goods);
            list.add(R.mipmap.ic_game);
            list.add(R.mipmap.ic_furniture);
            list.add(R.mipmap.ic_fruit);
            list.add(R.mipmap.ic_food);
            list.add(R.mipmap.ic_fitness);
            list.add(R.mipmap.ic_eat);
            list.add(R.mipmap.ic_costume);
            list.add(R.mipmap.ic_communication);
            list.add(R.mipmap.ic_child);
            list.add(R.mipmap.ic_car);
            list.add(R.mipmap.ic_bring_up);
            list.add(R.mipmap.ic_beauty);
            list.add(R.mipmap.ic_bank_card);
            list.add(R.mipmap.ic_wallet);
            list.add(R.mipmap.ic_kitchen);
            list.add(R.mipmap.ic_bedroom);
            list.add(R.mipmap.ic_balcony);
            list.add(R.mipmap.ic_wc);
        }
        return list;
    }

    public static List<String> getMoneyUserTypeNameList(String type){
        List<String> list = new ArrayList<>();
        list.add("存钱");
        list.add("预支");
        list.add("一般");
        list.add("标记");
        list.add("红包");
        list.add("钱包");
        list.add("日常");
        list.add("通用");
        if(BillBean.TYPE_EXPENSES.equals(type)){
            list.add("交通");
            list.add("外卖");
            list.add("零食");
            list.add("维修");
            list.add("娱乐");
            list.add("电子设备");
            list.add("宠物");
            list.add("医疗");
            list.add("护肤");
            list.add("学习");
            list.add("房子");
            list.add("礼物");
            list.add("日用品");
            list.add("游戏");
            list.add("家具");
            list.add("水果");
            list.add("食材");
            list.add("健身");
            list.add("用餐");
            list.add("服饰");
            list.add("通讯");
            list.add("孩子");
            list.add("车子");
            list.add("育儿");
            list.add("丽人");
            list.add("银行");
            list.add("钱包");
            list.add("厨房");
            list.add("卧室");
            list.add("阳台");
            list.add("卫生间");
        }
        return list;
    }

}
