package com.client.kusonemi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class MySQLer {
	private SQLiteDatabase sdb;
	private SQLiteHelper hlpr;
	private MyConceal conceal;
	private String db_name;

	MySQLer(Context ct, String table_name, String sql){
		db_name = table_name;
		if(sql == null){
			if(db_name == MainActivity.OAUTH_DATA){
				hlpr = new SQLiteHelper(ct, MainActivity.OAUTH_DATA
					, "create table " + MainActivity.OAUTH_DATA
							+ " ( _id integer primary key autoincrement,"
							+ " screenName text,"
							+ " userid text not null,"
							+ " token blob not null,"
							+ " tokensecret blob not null );");
			}
		}else{
			hlpr = new SQLiteHelper(ct, db_name, sql);
		}

		try{
			 sdb = hlpr.getWritableDatabase();
			 conceal = new MyConceal(ct);
		}catch(SQLiteException e){
			 Log.w("kusonemi", "database open error");
			 e.printStackTrace();
			 return;
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			Log.e("kusonemi", "conseal is not available");
			e.printStackTrace();
			return;
		}
	}

	public void  saveToken(String userscreeenname,String userID,
			String token, String tokensecret){
		byte[] cScreenname = conceal.Encrypting(userscreeenname);
		byte[] cuserID = conceal.Encrypting(userID);
		byte[] ctoken = conceal.Encrypting(token);
		byte[] ctokensecret = conceal.Encrypting(tokensecret);

		ContentValues val = new ContentValues();
		//各フィールドをセット
		val.put("screenName", cScreenname);
		val.put("userid", cuserID);
		val.put("token", ctoken);
		val.put("tokensecret", ctokensecret);

		long id = sdb.insert(db_name, null, val);
		//エラー時に-1が返るのを処理
		if(id < 0){
			Log.e(db_name, "insert error at database inserting");
		}
	}

	//配列の中身
	//0番:userid
	//1番:token
	//2番:tokensecret
	//3番:screenName
	public String[] loadToken(String id){
		String[] rawToken = null;
		Cursor cr = sdb.rawQuery("select * from "
				+ db_name + " where _id = ?"
				, new String[]{id});
		if(cr.moveToFirst()){
			cr.moveToFirst();
			Log.d("kusonemi", "got Colomn is "
					+ cr.getColumnCount());
			rawToken = new String[4];
			rawToken[0] = conceal.Decrypting
					(cr.getBlob(cr.getColumnIndex("userid")));
			rawToken[1] = conceal.Decrypting
					(cr.getBlob(cr.getColumnIndex("token")));
			rawToken[2] = conceal.Decrypting
					(cr.getBlob(cr.getColumnIndex("tokensecret")));
			rawToken[3] = conceal.Decrypting(cr.getBlob
					(cr.getColumnIndex("screenName")));
		}
		cr.close();

		return rawToken;
	}

}
