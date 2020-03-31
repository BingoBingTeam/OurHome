package com.lotus.ourhome.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.GoodsBean;
import com.lotus.ourhome.model.bean.GoodsSavePlaceBean;
import com.lotus.ourhome.model.bean.GoodsTypeBean;
import com.lotus.ourhome.model.bean.LedgerBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.bean.UserBean;

/**
 * 数据库
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ourhome.db";

    private static SQLiteHelper instance = null;//单例对象

    //所有表名数组
    public static final String[] TABLE_NAME_ARRAY = {
            UserBean.TABLE_NAME,//用户表
            LedgerBean.TABLE_NAME,//账本表
            BillBean.TABLE_NAME,//账单表
            FamilyMemberBean.TABLE_NAME,//家庭成员表
            GoodsBean.TABLE_NAME,//物品表
            GoodsTypeBean.TABLE_NAME,//物品类别表
            GoodsSavePlaceBean.TABLE_NAME,//物品保存地址表
            MoneyUseTypeBean.TABLE_NAME//钱的来源或使用来别
    };

    public static synchronized SQLiteHelper getInstance(Context context, int version) {
        if (instance == null) {
            instance = new SQLiteHelper(context,DATABASE_NAME,null,version);
        }
        return instance;
    }

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserBean.CREATE_TABLE);
        db.execSQL(BillBean.CREATE_TABLE);
        db.execSQL(LedgerBean.CREATE_TABLE);
        db.execSQL(MoneyUseTypeBean.CREATE_TABLE);
        db.execSQL(GoodsBean.CREATE_TABLE);
        db.execSQL(GoodsTypeBean.CREATE_TABLE);
        db.execSQL(GoodsSavePlaceBean.CREATE_TABLE);
        db.execSQL(FamilyMemberBean.CREATE_TABLE);//家庭成员表
        initBaseData();
    }

    private void initBaseData(){
//        public static final String INITIALIZE_DATA_SELF = "INSERT INTO '" + TABLE_NAME + "' VALUES (1, '" + TYPE_CAUSE + "', '', '', 0);";

        //    db.execSQL(BasicdataIdentification.INITIALIZE_DATA_cause);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TAG", "update sqlite");
        int currentVersion = oldVersion;

        while (newVersion >= currentVersion){
            boolean isAddVersion = true;
            switch (currentVersion){
                case 10001:
                    upgrade_10101_to_10102(db);
                    currentVersion = 10102;
                    isAddVersion = false;
                    break;
            }
            if(isAddVersion){
                currentVersion++;
            }
        }
    }

    private void upgrade_10101_to_10102(SQLiteDatabase db) {
//        if (!SqliteUtils.checkColumnExist(db, Preregister.TABLE_NAME, Preregister.UNIT_NAME)) {
//            String sql_add_unit_name = "ALTER TABLE " + Preregister.TABLE_NAME + " ADD " + Preregister.UNIT_NAME + " TEXT;";
//            db.execSQL(sql_add_unit_name);
//        }
    }

    /**
     * 为数据库初始化数据
     *
     * @param db
     */
    public void initData(SQLiteDatabase db) {
        // 初始化数据库数据时
    }
}
