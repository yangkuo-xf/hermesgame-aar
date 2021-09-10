package com.hermesgamesdk.gamebox.db;

import java.util.ArrayList;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hermesgamesdk.gamebox.entity.ThreadInfo;

/**
 * 數據庫增刪改查的實現類
 *
 */
public class ThreadDAOImple implements ThreadDAO {

	private DBHelper dbHelper = null;

	public ThreadDAOImple(Context context) {
		super();
		this.dbHelper = DBHelper.getInstance(context);
	}

	// 插入綫程
	@Override
	public synchronized void insertThread(ThreadInfo info) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("url", info.getUrl());
		values.put("start", info.getStart());
		values.put("end", info.getEnd());
		values.put("finished", info.getFinished());
		values.put("isDownFinish", 0);
		values.put("finishTime", 0);
		values.put("packageName", info.getPackageName());
		values.put("savePath", info.getSavePath());
		values.put("icon", info.getIcon());
		values.put("showName", info.getShowName());

		db.insert("thread_info", null, values);
	}

	// 刪除綫程
	@Override
	public synchronized void deleteThread(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete("thread_info", "url = ?", new String[] { url});
	}

	public synchronized void updateThreadSuccess(String url,int finished,String savePath) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		long time=System.currentTimeMillis()/1000;
		String timeunix=String.valueOf(time);
		String lastChangeTime = timeunix;
		db.execSQL("update thread_info set isDownFinish = 1,savePath = ?,lastChangeTime = "+lastChangeTime+",finishTime = "+timeunix+" where url = ?",
				new Object[]{savePath,url});
	}

	// 更新綫程
	@Override
	public synchronized void updateThread(String url, String savePath, int finished) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		long time=System.currentTimeMillis()/1000;
		String lastChangeTime=String.valueOf(time);
		db.execSQL("update thread_info set finished = ?, savePath = ?,lastChangeTime = "+lastChangeTime+" where url = ?",
				new Object[]{finished,savePath, url});
	}

	// 查詢綫程
	@Override
	public List<ThreadInfo> queryThreads(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		Cursor cursor = db.query("thread_info", null, "url = ?", new String[] { url }, null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			thread.setPackName(cursor.getString(cursor.getColumnIndex("packageName")));
			thread.setSavePath(cursor.getString(cursor.getColumnIndex("savePath")));
			thread.setIsDownloaded(cursor.getInt(cursor.getColumnIndex("isDownFinish")));
			thread.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			thread.setShowName(cursor.getString(cursor.getColumnIndex("showName")));
			list.add(thread);
		}


		cursor.close();
		return list;
	}

	public List<ThreadInfo> queryThreadEnd(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		Cursor cursor = db.query("thread_info", null, "url = ? and isDownFinish = 1", new String[] { url }, null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			thread.setPackName(cursor.getString(cursor.getColumnIndex("packageName")));
			thread.setSavePath(cursor.getString(cursor.getColumnIndex("savePath")));
			thread.setIsDownloaded(cursor.getInt(cursor.getColumnIndex("isDownFinish")));
			thread.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			thread.setShowName(cursor.getString(cursor.getColumnIndex("showName")));
			list.add(thread);
		}


		cursor.close();
		return list;
	}

	public List<ThreadInfo> queryThreadUnEnd() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		Cursor cursor = db.query("thread_info", null, " isDownFinish = 0", null, null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			thread.setPackName(cursor.getString(cursor.getColumnIndex("packageName")));
			thread.setSavePath(cursor.getString(cursor.getColumnIndex("savePath")));
			thread.setIsDownloaded(cursor.getInt(cursor.getColumnIndex("isDownFinish")));
			thread.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			thread.setShowName(cursor.getString(cursor.getColumnIndex("showName")));
			list.add(thread);
		}


		cursor.close();
		return list;
	}

	public List<ThreadInfo> getRuningDownTask(){

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		Cursor cursor = db.query("thread_info", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setId(cursor.getInt(cursor.getColumnIndex("id")));
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			thread.setPackName(cursor.getString(cursor.getColumnIndex("packageName")));
			thread.setSavePath(cursor.getString(cursor.getColumnIndex("savePath")));
			thread.setIsDownloaded(cursor.getInt(cursor.getColumnIndex("isDownFinish")));
			thread.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			thread.setShowName(cursor.getString(cursor.getColumnIndex("showName")));
			thread.setLastChangeTime(cursor.getInt(cursor.getColumnIndex("lastChangeTime")));
			list.add(thread);
		}

		return list;
	}

	public void deleteThreadById(String id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete("thread_info", "id = ?", new String[] { id});
	}

	// 判斷綫程是否爲空
	@Override
	public boolean isExists(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("thread_info", null, "url = ?", new String[] { url},
				null, null, null);
		boolean exists = cursor.moveToNext();

		cursor.close();
		return exists;
	}

}
