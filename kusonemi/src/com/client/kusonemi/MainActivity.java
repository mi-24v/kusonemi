package com.client.kusonemi;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.ConfigurationContext;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
	//OAuth用変数
	private OAuthAuthorization mOAuth;
	private RequestToken mReq;
	public static final String CALLBACK_URL = "callback://kusonemi";

	//Token保存用key
	public static final String OAUTH_DATA = "oauth_data";
	public static final String TOKEN = "token";
	public static final String TOKEN_SECRET = "token_secret";

	//Viewアイテム
	private ActionBarDrawerToggle mDrawerToggle;
	private MyFragmentPagerAdapter pageradapter;

	//ListView(Draerに使用)用の配列とAdapter
	private List<String> drawerdata;
	private ArrayAdapter<String> drawerlist;

	//Twitter4j系
	private String token,tokenSecret,ScreenName;
	private Twitter twitter;
	private User MyUser;

	//UserStreamを有効化するかどうかのbool
	public static boolean StreamEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setDrawer();
		setPager();
		setDrawerListview();
		loadToken();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds
		//items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.StreamEnabled).setChecked(StreamEnabled);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// イベントをActionBarDrawerToggleに通知
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }

		int id = item.getItemId();
		switch(id){
		case R.id.action_settings:
			Intent it = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(it);
			break;
		case R.id.StreamEnabled:
			item.setChecked(!item.isChecked());
			StreamEnabled = item.isChecked();
			//・Stream使用Fragmentを探索
			//・ひっか掛かったFragmentでstart(stop)Stream的なものを呼ぶ
			for(int i = 0; i < pageradapter.getCount(); i++){
				String classname =
						this.pageradapter.
						getSection(i).getFragmentClassName();
				if(classname == TimeLineFragment.class.getName()){
					TimeLineFragment tlfrg = (TimeLineFragment)
					this.pageradapter.instantiateFragment(i);
					if(StreamEnabled){
						tlfrg.startStream(this, false);
					}else{
						tlfrg.stopStream(this);
					}
				}else{
					continue;
				}
				//else ifすれば探索対象を増やすことが可なはず
			}
			break;
		case R.id.Screenlock://画面消灯無効化
			item.setChecked(!item.isChecked());
			if(item.isChecked()){
				this.getWindow().addFlags(WindowManager.LayoutParams
						.FLAG_KEEP_SCREEN_ON);
			}else{
				this.getWindow().clearFlags(WindowManager.LayoutParams
						.FLAG_KEEP_SCREEN_ON);
			}
			break;
		case R.id.acount_add:
			this.startOAuth();
			break;
		case R.id.acount_delete:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	//戻るキーを制御
	@Override
	public void onBackPressed(){
		//drawerが開いてれば閉じる
		DrawerLayout mDrawer =
				(DrawerLayout)findViewById(R.id.drawer_layout);
		if(mDrawer.isDrawerOpen(Gravity.LEFT)){
			mDrawer.closeDrawer(Gravity.LEFT);
		}else{
			showtoast("長押しで終了");
		}
		//superを呼ぶと通常処理
	}

	//Backキー長押しで終了させる
	@Override
	public boolean onKeyLongPress(int KeyCode, KeyEvent e){
		if(KeyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return  true;
		}
		return super.onKeyLongPress(KeyCode, e);
	}

	private void setDrawer(){
		//ToolBarをセット
		Toolbar tb = (Toolbar)findViewById(R.id.tool_bar);
		tb.setTitle(R.string.app_name);
		setSupportActionBar(tb);
		//ActionBarから戻るできるようにする???
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//Drawer設定
		DrawerLayout mdl = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mdl,
							R.string.drawer_open, R.string.drawer_close);
		mdl.setDrawerListener(mDrawerToggle);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}

	private void setPager(){
		ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
		pageradapter = new MyFragmentPagerAdapter
				(getApplicationContext(), getSupportFragmentManager());
		vp.setAdapter(pageradapter);
	}

	private void setDrawerListview(){
		ListView drawerview = (ListView)findViewById(R.id.drawerlist);
		drawerdata = new ArrayList<String>();
		drawerlist = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, drawerdata);
		drawerview.setAdapter(drawerlist);
	}

	public void StreamStateChange(boolean sw){
		StreamEnabled = sw;
	}

	public void ondialogPositiveClick(int type){
		switch(type){
		case 1:
			startOAuth();
			break;
		default://なにもしない
			return;
		}
	}

	private void setTweet(){
		Button tweetbtn = (Button)findViewById(R.id.DoTweet);
		tweetbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				EditText et = (EditText)
						findViewById(R.id.TweetArea);
				if (!et.getText().toString().isEmpty()) {
					new TweetTask().execute(et.getText().toString());
					et.setText(null);
				}else{
					showtoast("ﾂｲｰﾄが空です");
				}
			}
		});
	}

	private void startOAuth(){
		showtoast("ページを用意しています...");
		new OAuthTask().execute();
	}

	private void loadToken(){
		String[] rawToken;
		SharedPreferences pc = getSharedPreferences(
				getPackageName(), MODE_PRIVATE);
		MySQLer ms = new MySQLer(getApplicationContext(), OAUTH_DATA, null);
		//Default UserのTokenを取得
		rawToken = ms.loadToken(pc.getString("defaultAccountpos", "1"));
		if(rawToken != null){
			token = rawToken[1];
			tokenSecret = rawToken[2];
			ScreenName = rawToken[3];

			settwitter();
			addTL();
			setTweet();
		}else{
			showdialog(0, R.string.login_dialog, R.string.ok,0,0, 1,1,false);
		}
	}

	private void settwitter(){
		ConfigurationBuilder bd = new ConfigurationBuilder();
		bd.setOAuthConsumerKey(getString(R.string.consumer_key));
		bd.setOAuthConsumerSecret(getString(R.string.consumer_secret));
		bd.setOAuthAccessToken(token);
		bd.setOAuthAccessTokenSecret(tokenSecret);
		TwitterFactory tf = new TwitterFactory(bd.build());
		twitter = tf.getInstance();
		AsyncTask<Twitter, Void, User> t = new AsyncTask<Twitter, Void, User>(){
			private String errormsg;
			protected User doInBackground(Twitter... params){
				User user;
				try {
					user = params[0].verifyCredentials();
				} catch (TwitterException e) {
					e.printStackTrace();
					errormsg = e.getErrorMessage();
					return null;
				} catch(Exception e){
					e.printStackTrace();
					errormsg = e.getMessage();
					return null;
				}
				return user;
			}
			protected void onPostExecute(User result){
				if(result == null){
					showtoast(errormsg);
				}else{
					MyUser = result;
					DrawerLayout mdl = (DrawerLayout)findViewById(R.id.drawer_layout);
					mdl.setBackgroundColor(Integer.parseInt(MyUser.getProfileBackgroundColor(),16));
				}
			}
		};
		t.execute(twitter);
	}

	private void addTL(){
		if (twitter != null && token != null && tokenSecret != null) {
			String Title = "Home " + ScreenName;
			Bundle bundle = new Bundle();
			drawerlist.add(Title);
			bundle.putString("token", token);
			bundle.putString("tokensecret", tokenSecret);
			pageradapter.addSection(Title,
					TimeLineFragment.class, bundle);
			pageradapter.instantiateItem((ViewGroup) getWindow().getDecorView()
					.findViewById(R.id.viewpager), pageradapter
					.getCount() - 1);
			Log.d(getLocalClassName(), pageradapter.getSection(0).getTitle());
		}
	}

	private void showtoast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	private void showdialog(int title, int text, int okbutton
			,int nobutton, int naturalbutton
				,int type, int id, boolean cancelable){
		DialogFragment df = MyAlertDialogFragment.
				newInstance(title, text, okbutton,
						nobutton, naturalbutton, type, id);
		if(!cancelable){
			df.setCancelable(false);
		}
		df.show(getFragmentManager(), getPackageName());
	}

	private void showdialog(String title, String text, int type, int id, boolean cancelable){
		DialogFragment df = MyAlertDialogFragment.newInstance(title, text, type, id);
		if(!cancelable){
			df.setCancelable(false);
		}
		df.show(getFragmentManager(), getPackageName());
	}

	@Override
	protected void onNewIntent(Intent it){
		if (it != null) {
			new TokenTask().execute(it);
		}
	}

	public class TweetTask extends AsyncTask<String, Void, String>{
		private final int CausedByNetworkIssue = -1;
		private final int ErrorMessageNotAvailable = -2;
		private final String SUCCESS = "success";

		/**
		 * @param Tweet Text
		 **/
		@Override
		protected String doInBackground(String... params){
			Log.d(getLocalClassName(), "tweet is "+params[0]);
			try{
				twitter.updateStatus(params[0]);
			}catch(TwitterException e){
				e.printStackTrace();
				//エラーコード取得
				int errcode;
				if(e.isCausedByNetworkIssue()){
					errcode = CausedByNetworkIssue;
				}else if(!e.isErrorMessageAvailable()){
					errcode = ErrorMessageNotAvailable;
				}else{
					errcode = e.getErrorCode();
				}
				//エラーコード解析
				switch(errcode){
				case 185:
					return "ﾂｲｰﾄ規制";
				case 187:
					return "ﾂｲｰﾄ重複";
				case CausedByNetworkIssue:
					return "ネットワーク接続なし";
				case ErrorMessageNotAvailable:
					return "不明なエラー";
				default:
					return "エラー";
				}
			}
			return SUCCESS;
		}

		@Override
		protected void onPostExecute(String result){
			if(result == SUCCESS){
				showtoast("ﾂｲｰﾄしました");
			}else{
				showtoast("ﾂｲｰﾄ失敗(" + result + ")");
			}

		}
	}

	public class OAuthTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params){
			twitter4j.conf.Configuration cf =
					ConfigurationContext.getInstance();

			mOAuth = new OAuthAuthorization(cf);
			mOAuth.setOAuthConsumer(getString(R.string.consumer_key)
					, getString(R.string.consumer_secret));

			try{
				mReq = mOAuth.getOAuthRequestToken(CALLBACK_URL);
			}catch(TwitterException e){
				e.printStackTrace();
				return "ページ取得失敗";
			}

			String url;
			url = mReq.getAuthorizationURL();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return null;
		}

		@Override
		protected void onPostExecute(String result){
			if(result != null){
				showtoast(result);
			}
		}
	}

	public class TokenTask extends AsyncTask<Intent, Void, String>{
		final String SUCCESS = "succeed";
		@Override
		protected String doInBackground(Intent... params){
			Intent intent = params[0];
			Uri uri = intent.getData();
			AccessToken atoken = null;
			if(uri != null && uri.toString().startsWith(CALLBACK_URL)){
				String verifier = uri.getQueryParameter("oauth_verifier");
				try{
					if (verifier != null) {
						atoken = mOAuth.getOAuthAccessToken(mReq, verifier);
					}
				}catch(TwitterException e){
					e.printStackTrace();
					Log.e(getLocalClassName(),
							"failed to get OAuth Token");
					return "認証エラー";
				}
			}

			if (atoken != null) {
				//Tokenをset
				token = atoken.getToken();
				tokenSecret = atoken.getTokenSecret();
				ScreenName = atoken.getScreenName();
				settwitter();
				//TokenをSave
				try {
					MySQLer sql = new MySQLer(getApplicationContext(),
							OAUTH_DATA, null);
					sql.saveToken(
							"@" + twitter.verifyCredentials().getScreenName(),
							String.valueOf(twitter.verifyCredentials().getId()),
							atoken.getToken(), atoken.getTokenSecret());
				} catch (TwitterException e) {
					e.printStackTrace();
					return "認証情報の保存に失敗しました...";
				}
				return SUCCESS;
			}else return "認証コードがありません";
		}

		@Override
		protected void onPostExecute(String result){
			if(result == SUCCESS){
				showtoast("認証成功！");
				addTL();
			}else{
				showtoast("認証失敗(" + result + ")" );
			}
		}
	}
}
