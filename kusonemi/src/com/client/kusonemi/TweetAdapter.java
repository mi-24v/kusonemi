package com.client.kusonemi;

import com.loopj.android.image.SmartImageView;

import twitter4j.Status;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Status> {
	private LayoutInflater mli;

	TweetAdapter(Context ct){
		super(ct, android.R.layout.simple_list_item_1);
		mli = (LayoutInflater)ct.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View contentview, ViewGroup parent){
		//Viewインスタンスにモノが入ってたら再利用する
		if(contentview == null){
			contentview = mli.inflate(R.layout.listitem_tl, null);
		}

		//tweetステータス
		Status item = getItem(position);

		//スクリーンネーム
		TextView user = (TextView)contentview.findViewById(R.id.username);
		user.setText(item.getUser().getName());

		//@ネーム
		TextView name = (TextView)contentview.findViewById(R.id.name);
		name.setText("/@" + item.getUser().getScreenName());

		//本文
		TextView text = (TextView)contentview.findViewById(R.id.text);
		text.setText(item.getText());

		//アイコン
		SmartImageView icon =
				(SmartImageView)contentview.findViewById(R.id.icon);
		icon.setImageUrl(item.getUser().getProfileImageURL());

		//via
		TextView via = (TextView)contentview.findViewById(R.id.via);
		via.setText("via " + item.getSource().replaceAll("<.+?>", ""));

		//時刻
		TextView time = (TextView)contentview.findViewById(R.id.time);
		time.setText(item.getCreatedAt().toString());

		return contentview;
	}

}
