package com.lotus.base.utils.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * sqlite 工具
 * 
 * @author 刘洋
 *
 */
public class SqliteUtils {

	/**
	 * 判断某表中是否存在某列
	 * 
	 * @param db
	 * @param tableName
	 * @param columnName
	 * @return 没找到表或者字段返回false
	 */
	public static boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
		if (db != null && db.isOpen()) {

			String sql = "select * from sqlite_master where name=? and sql like ?;";
			String[] selectionArgs = { tableName, "%" + columnName + "%" };
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			int count = cursor.getCount();
			cursor.close();
			return count > 0;
		}
		return false;
	}

	/**
	 * 查询某个表的表结构sql语句
	 * 
	 * @param db
	 * @param tableName
	 * @return 没找到表返回null
	 */
	public static String getTableSQL(SQLiteDatabase db, String tableName) {
		String tabelSQL = null;

		if (db != null && db.isOpen()) {
			String sql = "select * from sqlite_master where name=?; ";
			String[] selectionArgs = { tableName };
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				tabelSQL = cursor.getString(cursor.getColumnIndexOrThrow("sql"));
			}
			cursor.close();
		}
		return tabelSQL;
	}

}
