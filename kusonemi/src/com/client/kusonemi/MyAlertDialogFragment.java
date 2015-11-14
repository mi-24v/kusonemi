package com.client.kusonemi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class MyAlertDialogFragment extends DialogFragment {

	/*titleにはダイアログのタイトル
	 * textにはダイアログの本文(これだけは空にできない)
	 * okbutton, nobutton, nuturalbuttonには、
	 * ダイアログにボタンを1つ足すとき、ボタン部のテキストが入る。
	 * typeはボタンやタイトルの有無を指定
	 * idはボタンリスナの挙動を指定
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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Dialog dlog = null;
		int text = getArguments().getInt("text");
		int type = getArguments().getInt("type");

		if(text == 0){
			Log.e(getTag(), "Invalid Arguments");
			return null;
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
