package com.client.kusonemi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static String SQL_SENTENCE = null;//sql文(create用)
	private static final int DB_VERSION = 4;
	private String DB_name;

	SQLiteHelper(Context ct, String db_name, String sql){
		super(ct, db_name, null, DB_VERSION);
		SQL_SENTENCE = sql;
		DB_name = db_name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		Log.i("com.client.kusonemi", "creating table...");
		db.execSQL(SQL_SENTENCE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL("drop table " + DB_name);
		db.execSQL(SQL_SENTENCE);
	}

}
