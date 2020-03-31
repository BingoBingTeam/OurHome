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

    public static final  String TYPE = "type";//区别是收入、还是支出
    public static final  String USE_TYPE = "use_type";//用作什么的钱，或者来源是什么
    public static final  String ICON = "icon";
    public static final  String USER_ID = "user_id";

    public static final String TABLE_NAME = "bill_type";
    public static final String[] TABLE_COLUMN = {ID,NAME,ICON,USE_TYPE,CREATE_TIME,USER_ID};
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " text primary key, "
            + NAME + " text, "
            + TYPE + " text, "
            + USE_TYPE + " text, "
            + ICON + " text, "
            + USER_ID + " text, "
            + CREATE_TIME + " integer default 0"
            + ")";
    //=======================================================================================================
    private String type;
    private String useType;
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

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
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

}
