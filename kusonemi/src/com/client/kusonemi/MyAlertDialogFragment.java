package com.client.kusonemi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class MyAlertDialogFragment extends DialogFragment {

	private MyAlertDialogFragment(){}

	/**
	 * @param title ダイアログのタイトル
	 * @param text ダイアログの本文(これだけは空にできない)
	 * @param okbutton okボタンに表示するテキスト
	 * @param nobutton noボタンに表示するテキスト
	 * @param nuturalbutton naturalボタンに表示するテキスト
	 * @param type ボタンやタイトルの有無を指定
	 * @param id ボタンリスナの挙動を指定
	 * */
	public static MyAlertDialogFragment newInstance(int title, int text
			,int okbutton, int nobutton, int nuturalbutton, int type, int id){
		MyAlertDialogFragment frg = new MyAlertDialogFragment();
		Bundle naiyou = new Bundle();
		naiyou.putInt("title", title);
		naiyou.putInt("text", text);
		naiyou.putInt("okbutton", okbutton);
		naiyou.putInt("nobutton", nobutton);
		naiyou.putInt("nuturalbutton", nuturalbutton);
		naiyou.putInt("type", type);
		naiyou.putInt("id", id);
		frg.setArguments(naiyou);
		return frg;
	}

	/**
	 * @param title ダイアログタイトル
	 * @param text メッセージ
	 * @param type ダイアログレイアウトタイプ
	 * @param id ボタンのコールバックリスナの挙動
	 * */
	public static MyAlertDialogFragment newInstance(String title,String text,int type,int id){
		MyAlertDialogFragment frg = new MyAlertDialogFragment();
		Bundle prop = new Bundle();
		prop.putString("title", title);
		prop.putString("msg", text);
		prop.putInt("type", type);
		return frg;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Dialog dlog = null;
		int text = getArguments().getInt("text");
		int type = getArguments().getInt("type");

		if(text == 0){
			String msg = getArguments().getString("msg");
			if (msg == null) {
				Log.e(getTag(), "Invalid Arguments");
				return null;
			}else{
				switch(type){
				case 1:
					dlog = new AlertDialog.Builder(getActivity())
					.setMessage(msg)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which){
							((MainActivity)getActivity()).
							ondialogPositiveClick(getArguments().getInt("id"));
						}
					})
					.create();
					break;
				}
			}
		}else{
			switch(type){
			case 1://okのみボタンあり、タイトルナシ
				dlog = new AlertDialog.Builder(getActivity())
				.setMessage(text)
				.setPositiveButton(getArguments().getInt("okbutton"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO 自動生成されたメソッド・スタブ
								((MainActivity)getActivity()).
								ondialogPositiveClick(getArguments().getInt("id"));
							}
						})
				.create();
				break;
			default://textのみ
				dlog = new AlertDialog.Builder(getActivity())
				.setMessage(text)
				.create();
			}
		}

		return dlog;
	}

}
