/*
 * @Title ThreadDAOImpl.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-8 上午11:00:38
 * @version 1.0
 */
package com.Service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.alex.administrator.neverignore.MainActivity;

import java.util.ArrayList;
import java.util.List;

/** 
 * 数据访问接口实现
 * @author Yann
 * @date 2015-8-8 上午11:00:38
 */
public class ThreadDAOImpl implements ThreadDAO
{
	Context context;
	private DBHelper mHelper = null;

	public ThreadDAOImpl(Context context)
	{

		mHelper = new DBHelper(context);
		if (mHelper==null)
		Toast.makeText(MainActivity.mMainActivity, "null1", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void insertThread(Threadinfo threadInfo)
	{
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.execSQL("insert into thread_info(thread_id,url,start,end,finished,length,name,pause) values(?,?,?,?,?,?,?,?)",
				new Object[]{threadInfo.getId(), threadInfo.getUrl(),
				threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished(),threadInfo.getLength(),
						threadInfo.getName(),threadInfo.getPause()});
		db.close();
	}

	@Override
	public void updateThread_length(int thread_id, int length){
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.execSQL("update thread_info set end = ? where thread_id = ?",
				new Object[]{length, thread_id});
		db.execSQL("update thread_info set length = ? where thread_id = ?",
				new Object[]{length, thread_id});
		db.close();
	}

	@Override
	public void deleteThread(String url, int thread_id)
	{
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.execSQL("delete from thread_info where url = ? and thread_id = ?",
				new Object[]{url, thread_id});
		db.close();
	}

	@Override
	public void updateThread(String url, int thread_id, int finished)
	{
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
				new Object[]{finished, url, thread_id});
		db.close();
	}

	public void updateThread_state( int thread_id, int pause)
	{
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.execSQL("update thread_info set pause = ? where thread_id = ?",
				new Object[]{pause, thread_id});
		db.close();
	}

	@Override
	public List<Threadinfo> getAll(){
		List<Threadinfo> list = new ArrayList<Threadinfo>();
		SQLiteDatabase db=mHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select * from thread_info",null);
		while (cursor.moveToNext())
		{
			Threadinfo threadInfo = new Threadinfo(0,null,0,0,0,0,null,0);
			threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			threadInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
			threadInfo.setLength(cursor.getInt(cursor.getColumnIndex("length")));
			threadInfo.setPause(cursor.getInt(cursor.getColumnIndex("pause")));
			list.add(threadInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	@Override
	public List<Threadinfo> getThreads(String url)
	{
		List<Threadinfo> list = new ArrayList<Threadinfo>();
		SQLiteDatabase db=mHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
		while (cursor.moveToNext())
		{
			Threadinfo threadInfo = new Threadinfo(0,null,0,0,0,0,null,0);
			threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			threadInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
			threadInfo.setLength(cursor.getInt(cursor.getColumnIndex("length")));
			threadInfo.setPause(cursor.getInt(cursor.getColumnIndex("pause")));
			list.add(threadInfo);
		}
		cursor.close();
		db.close();
		return list;
	}


	@Override
	public boolean isExists(String url, int thread_id)
	{
		SQLiteDatabase db=mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id + ""});
		boolean exists = cursor.moveToNext();
		cursor.close();
		db.close();
		return exists;
	}
	@Override
	public int GetThread_state(int thread_id){
		SQLiteDatabase db=mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from thread_info where thread_id = ?", new String[]{thread_id + ""});
		cursor.moveToNext();
		int i=cursor.getInt(cursor.getColumnIndex("pause"));
		cursor.close();
		db.close();
		return i;
	}
}
