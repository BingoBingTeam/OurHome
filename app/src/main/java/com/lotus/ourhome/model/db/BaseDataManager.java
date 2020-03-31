package com.lotus.ourhome.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lotus.ourhome.util.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lotus.ourhome.model.db.SQLiteHelper.DATABASE_NAME;


public abstract class BaseDataManager {
    private static final String TAG = BaseDataManager.class.getSimpleName();

    protected SQLiteDatabase sqLiteDatabase;

    protected SQLiteHelper dataBase;

    protected Context mContext;

    public BaseDataManager() {
    }

    public BaseDataManager(Context context) {
        this.mContext = context;
        int version = AppUtil.getCurrentVersionCode(context);
        dataBase = SQLiteHelper.getInstance(context, version);
    }

    protected void open() {
        if (sqLiteDatabase == null || sqLiteDatabase.isOpen() == false) {
            sqLiteDatabase = dataBase.getWritableDatabase();
            //使用ContentProvider时，开始时数据库文件不存在，但getWritableDatabase返回的db不为空，所以操作数据库会crash  start
            File dataFile = mContext.getDatabasePath(DATABASE_NAME);
            if (!dataFile.exists()){
                Log.i(TAG,mContext.getPackageName() + "  getDatabasePath(DATABASE_NAME) = " + dataFile.exists());
                sqLiteDatabase.close();
                sqLiteDatabase = dataBase.getWritableDatabase();
            }
            //使用ContentProvider时，开始时数据库文件不存在，但getWritableDatabase返回的db不为空，所以操作数据库会crash  end
        }
    }

    /**遍历数据库中所有表名*/
    public List<String> getTables(){
        List<String> tables = new ArrayList<>();
        open();
        Cursor cursor = sqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            tables.add(name);
        }
        cursor.close();
        return tables;
    }

    /**
     * 检查某表列是否存在
     * @param tableName 表名
     * @param columnName 列名
     * @return
     */
    public boolean checkColumnExist(String tableName
            , String columnName) {
        open();
        boolean result = false ;
        Cursor cursor = null ;
        try{
            //查询一行
            cursor = sqLiteDatabase.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
                    , null );
            if (cursor != null){
                result = cursor.getColumnIndex(columnName) != -1 ;
            }else{
                return false;
            }

        }catch (Exception e){
            Log.e(TAG,"checkColumnExists..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
        return result ;
    }

    /**
     * 重置数据库
     */
    public void resetDatabase() {

        // 删所有表数据
        open();
        for (String tableName : SQLiteHelper.TABLE_NAME_ARRAY) {
            int result = sqLiteDatabase.delete(tableName, null, null);
            Log.i(TAG, "clean table " + tableName + ", result=" + result);
        }
        dataBase.initData(sqLiteDatabase);
    }

}
