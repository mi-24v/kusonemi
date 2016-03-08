package com.client.kusonemi;

import com.loopj.android.image.SmartImageView;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

import com.beardedhen.androidbootstrap.AwesomeTextView;

public class TweetAdapter extends ArrayAdapter<Status> {
	public static final String LIKE_COLOR = "#E2264D";
	public static final String RT_COLOR = "#19CF86";

	private LayoutInflater mli;
	private Twitter twitter;

	@Deprecated
	TweetAdapter(Context ct){
		super(ct, android.R.layout.simple_list_item_1);
		mli = (LayoutInflater)ct.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	TweetAdapter(Context ct, Twitter t){
		super(ct, android.R.layout.simple_list_item_1);
		mli = (LayoutInflater)ct.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		twitter = t;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int position, View contentview, ViewGroup parent){
		ViewHolder holder;
		//Viewインスタンスにモノが入ってたら再利用する->けしてみる(毎回インスタンスをつくる)->だめでした(メモリリーク)
		if(contentview == null){
			contentview = mli.inflate(R.layout.listitem_tl, parent, false);
			holder = new ViewHolder(contentview);
			contentview.setTag(holder);
		}else{
			holder = (ViewHolder)contentview.getTag();
			if(!getItem(position).isRetweet()){
				holder.rtview.setVisibility(View.GONE);
			}
		}

		//スコープを効かせるためのゴリ押し
		final int lgray = holder.lightgray;

		//tweetステータス
		Status item = getItem(position);
		if(item.isRetweet()){
			String rticonurl = item.getUser().getProfileImageURL();
			String rtbyname = "@"+item.getUser().getScreenName();
			item = item.getRetweetedStatus();
			holder.rtview.setVisibility(View.VISIBLE);
			holder.itemContainer.setBackgroundColor(Color.rgb(0, 128, 255));

			//rt元アイコン
			holder.rticon.setImageUrl(rticonurl);

			//rt元ネーム
			holder.rtname.setText(rtbyname);

			//自分のRTならボタン色を変更
			if(item.isRetweetedByMe()){
				holder.retweetAction.setTextColor(Color.parseColor(RT_COLOR));
			}
		}else if(item.isFavorited()){
			holder.likeAction.setTextColor(Color.parseColor(LIKE_COLOR));
			contentview.setBackgroundColor(Color.GREEN);
		}else{
			holder.itemContainer.setBackgroundColor(Color.WHITE);
		}

		//likeする
		holder.likeAction.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				final Status s = getItem(position);
				final AwesomeTextView like = (AwesomeTextView)v;
				new AsyncTask<Twitter,Void,String>(){
					@Override
					protected String doInBackground(Twitter... params){
						try {
							if(!s.isFavorited()){
								twitter.createFavorite(s.getId());
								return "favorited";
							}else{
								twitter.destroyFavorite(s.getId());
								return "disfavorited";
							}
						} catch (TwitterException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return e.getErrorMessage();
						}
					}
					@Override
					protected void onPostExecute(String result){
						if(result != null){
							if(result.equals("favorited")){
								showtoast("ふぁぼりました");
								like.setTextColor(Color.parseColor(LIKE_COLOR));
							}else if(result.equals("disfavorited")){
								showtoast("あんふぁぼしました");
								like.setTextColor(lgray);
							}
						}else{
							showtoast("ふぁぼれませんでした("+result+")");
						}
					}
				}.execute(twitter);
			}
		});

		//retweetする
		holder.retweetAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Status s = getItem(position);
				final AwesomeTextView rt = (AwesomeTextView)v;
				new AsyncTask<Twitter,Void,String>(){
					@Override
					protected String doInBackground(Twitter... params){
						try {
							if(!s.isRetweetedByMe()){
								twitter.retweetStatus(s.getId());
								return "retweeted";
							}else{
								twitter.destroyStatus(s.getId());
								return "disretweeted";
							}
						} catch (TwitterException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return e.getErrorMessage();
						}
					}
					@Override
					protected void onPostExecute(String result){
						if(result != null){
							if(result.equals("retweeted")){
								showtoast("リツイートました");
								rt.setTextColor(Color.parseColor(RT_COLOR));
							}else if(result.equals("disretweeted")){
								showtoast("リツイートをけししました");
								rt.setTextColor(lgray);
							}
						}else{
							showtoast("リツイートできませんでした("+result+")");
						}
					}
				}.execute(twitter);
			}
		});

		//スクリーンネーム
		holder.user.setText(item.getUser().getName());

		//@ネーム
		holder.name.setText("/@" + item.getUser().getScreenName());

		//本文
		holder.text.setText(item.getText());

		//アイコン
		holder.icon.setImageUrl(item.getUser().getProfileImageURL());

		//via
		holder.via.setText("via " + item.getSource().replaceAll("<.+?>", ""));

		//時刻
		holder.time.setText(item.getCreatedAt().toString());

		return contentview;
	}

	private void showtoast(String text){
		Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show();
	}

	static class ViewHolder{
		//Android Bootstrap
		@Bind(R.id.like_action) AwesomeTextView likeAction;
		@Bind(R.id.retweet_action) AwesomeTextView retweetAction;
		@Bind(R.id.tlitem_container) RelativeLayout itemContainer;
		@Bind(R.id.retweetrow) LinearLayout rtview;
		@Bind(R.id.retweetbyicon) SmartImageView rticon;
		@Bind(R.id.retweet_2) TextView rtname;
		@Bind(R.id.username) TextView user;
		@Bind(R.id.name) TextView name;
		@Bind(R.id.text) TextView text;
		@Bind(R.id.icon) SmartImageView icon;
		@Bind(R.id.via) TextView via;
		@Bind(R.id.time) TextView time;
		@BindColor(R.color.lightgray) int lightgray;

		public ViewHolder(View v){
			ButterKnife.bind(this, v);

		}
	}

}
