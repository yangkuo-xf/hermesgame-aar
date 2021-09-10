package com.hermesgamesdk.gamebox.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 數據庫幫助類
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "download.db";
	private static final int VERSION = 8;
	private static final String SQL_CREATE = "create table thread_info(id integer primary key autoincrement, "
			+ "url text, start integer, end integer, finished integer,isDownFinish integer,finishTime INT,savePath text,packageName VARCHAR( 255 ),icon VARCHAR( 255 ),showName VARCHAR( 60 ),lastChangeTime INT)";

	private static final String SQL_DROP = "drop table if exists thread_info";
	private static DBHelper sHelper = null;



	private DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/**
	 * 使用单例模式获取DBHelper
	 */
	public static DBHelper getInstance(Context context) {
		if (sHelper == null) {
			sHelper = new DBHelper(context);
		}
		return sHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREATE);
	}

}
