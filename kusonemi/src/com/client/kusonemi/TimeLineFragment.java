package com.client.kusonemi;

import java.util.List;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TimeLineFragment extends ListFragment {
	//setされるList
	private TweetAdapter tladapter;

	//PulldownでrefreshするView
	private static ListFragmentSwipeRefreshLayout Swiperefresh;

	//Twitterのインスタンス
	private Twitter twitter;
	private static TwitterStream stream;

	@Override
	public void onResume(){
		super.onResume();
		setTwitterInstance();
	}

	//認証済みのTwitterインスタンスを引数にとるgetInstance()
	//PagerAdapterからインスタンスを取得するため現時点で使用していない
	public static TimeLineFragment newInstance(Twitter twitter){
		TimeLineFragment frg = new TimeLineFragment();
		Bundle naiyou = new Bundle();
		naiyou.putSerializable("Twitter", twitter);
		frg.setArguments(naiyou);
		return frg;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		//set adapter
		tladapter = new TweetAdapter
				(getActivity().getApplicationContext());
		setListAdapter(tladapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container
			,Bundle savedInstanceState){
		return setSwiper(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stream.cleanUp();
	}

	private View setSwiper(LayoutInflater inflater, ViewGroup container
			,Bundle savedInstanceState){
		// Create the list fragment's content view by calling the super method
        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        // Now create a SwipeRefreshLayout to wrap the fragment's content view
        Swiperefresh = new ListFragmentSwipeRefreshLayout(container.getContext());

        // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
        // the SwipeRefreshLayout
        Swiperefresh.addView(listFragmentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Make sure that the SwipeRefreshLayout will fill the fragment
        Swiperefresh.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        //色
        Swiperefresh.setColorSchemeColors(Color.RED, Color.BLUE,
        		Color.GREEN, Color.YELLOW);

        //Listener
        Swiperefresh.setOnRefreshListener(new OnRefreshListener(){
        	@Override
        	public void onRefresh(){ReloadTimeLine();}
        });

        // Now return the SwipeRefreshLayout as this fragment's content view
        return Swiperefresh;
	}

	private void setTwitterInstance() throws IllegalArgumentException{
		String token,tokensecret;
		token = getArguments().getString("token");
		tokensecret = getArguments().getString("tokensecret");
		if(token == null || tokensecret == null){
			throw new IllegalArgumentException("OAuth token is invaild");
		}
		//TwitterとTwitterStreamのインスタンスを用意するだけ
		ConfigurationBuilder bd = new ConfigurationBuilder();
		bd.setOAuthConsumerKey(getString(R.string.consumer_key));
		bd.setOAuthConsumerSecret(getString(R.string.consumer_secret));
		bd.setOAuthAccessToken(token);
		bd.setOAuthAccessTokenSecret(tokensecret);
		twitter = new TwitterFactory(bd.build()).getInstance();


		ConfigurationBuilder bsd = new ConfigurationBuilder();
		bsd.setOAuthConsumerKey(getString(R.string.consumer_key));
		bsd.setOAuthConsumerSecret(getString(R.string.consumer_secret));
		bsd.setOAuthAccessToken(token);
		bsd.setOAuthAccessTokenSecret(tokensecret);
		synchronized (this) {
			stream = new TwitterStreamFactory(bsd.build()).getInstance();
			Log.d(getTag(),
					"stream object is : " + String.valueOf(stream));
			if(stream != null){
				this.notifyAll();
				}else{
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
		}
		this.startStream(null, true);
	}

	private void onStreamStateChange(boolean sw, MainActivity ac){
		if (ac == null) {
			try {
				MainActivity activity = (MainActivity) getActivity();
				Log.d(getTag(),
						"Acvtivity object = " + String.valueOf(activity));
				activity.StreamStateChange(sw);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}else{
			ac.StreamStateChange(sw);
		}
	}
	
	private void setTweetClickAction(){
		getListView().setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO 自動生成されたメソッド・スタブ
				Status item = tladapter.getItem(position);
				try {
					twitter.createFavorite(item.getId());
					showtoast("ふぁぼりました");
					view.setBackgroundColor(Color.GREEN);
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					showtoast("ふぁぼれませんでした("+e.getErrorMessage()+")");
				}
			}
		});
	}

	private void setListScrollListener(){
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {
				//ListViewがスクロール中かどうかの状態がiに渡る
				//i=1でスクロール中
				//そうでないならi=0
				///なにもしない
			}

			@Override
			public void onScroll(AbsListView absListView,
					int firstVisibleItem, int visibleItemCount,
					int totallItemCount) {
				Log.d(getTag(), "onScroll");
				if (totallItemCount != 0) {
					int firstitem = tladapter.getPosition(tladapter
							.getItem(0));
					if (firstitem != firstVisibleItem) {
						absListView.setSelection(firstitem);
					}
				}
			}
		});
	}

	//ActivityからStreamEnabled = trueか
	//PreferenceからAutoCreateUserStream = true
	//が渡っていないとStreamは接続されない
	public void startStream(MainActivity ac, boolean isFirst){
		if(!MainActivity.StreamEnabled &&
				PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getBoolean("AutoCreateUserStream", false)
				== false){
			return;
			}

	if (stream != null) {
			stream.addListener(new MyUserStream() {
				@Override
				public void onStatus(final twitter4j.Status arg){
					Log.d(getTag(), "onStatus");
					TweetAdapter adapter =
							(TweetAdapter)getListView().getAdapter();
					if (adapter.getPosition(arg) < 0) {
						adapter.insert(arg, 0);
						getListView().setSelectionAfterHeaderView();
						adapter.notifyDataSetChanged();
						//getListView().getAdapter().getView(
							//	0, getListView().getChildAt(0), getListView());
						removeOverStatus();
						//getListView().post(new Runnable(){
							//@Override
							//public void run(){
								Log.d(getTag(), "Listpos: "
										+ getListView().getFirstVisiblePosition());
							//}
						//});
						}
				}
			});
	}

		if (isFirst) {
			Swiperefresh.setRefreshing(true);
			 ReloadTimeLine();
		}
		onStreamStateChange(true, ac);


		 Swiperefresh.setEnabled(false);
		 stream.user();
	}


	public void stopStream(MainActivity ac){
		if (stream != null) {
			AsyncTask<TwitterStream, Void, Void> task =
					new AsyncTask<TwitterStream,Void,Void>(){
				@Override
				protected Void doInBackground(TwitterStream... params){
					params[0].cleanUp();
					return null;
				}
			};
			task.execute(stream);
		}
		onStreamStateChange(false, ac);
		TimeLineFragment.Swiperefresh.setEnabled(true);
	}

	public void ReloadTimeLine(){
		new ReloadTask().execute();
	}

	public class ReloadTask extends AsyncTask<Void, Void, List<Status>>{
		int ErrorCode = 0;
		private final int CausedByNetworkIssue = -1;
		private final int ErrorMessageNotAvailable = -2;
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params){
			try{
				return twitter.getHomeTimeline();
			}catch(TwitterException e){
				e.printStackTrace();
				if(e.isCausedByNetworkIssue()){
					ErrorCode = CausedByNetworkIssue;
				}else if(!e.isErrorMessageAvailable()){
					ErrorCode = ErrorMessageNotAvailable;
				}else{
				ErrorCode = e.getErrorCode();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> result){
			Swiperefresh.setRefreshing(false);
			if(result != null){
				if (tladapter.isEmpty()) {
					for (twitter4j.Status status : result) {
						tladapter.add(status);
					}
				}else{
					for(twitter4j.Status status : result){
						if(tladapter.getPosition(status) < 0){
							tladapter.insert(status, 0);
						}
					}
				}

				removeOverStatus();

				//先頭へ移動
				getListView().setSelection(0);
			}else{
				String errmsg;
				switch(ErrorCode){
				case 64://凍結されているアカウントからリクエストがきたら403と一緒に返します。
					errmsg = "(君の垢は凍結されている。)";
					break;
				case 88://APIリミット到達
					errmsg = "(API規制(˘ω˘))";
					break;
				case 131://不明なエラー
					errmsg = "(内部エラー)";
					break;
				case 130://容量オーバー???
					errmsg = "(Over capacity)";
					break;
				case CausedByNetworkIssue://オフライン
					errmsg = "(ﾈｯﾄにつながってないンゴ)";
					break;
				case ErrorMessageNotAvailable:
					errmsg = "(例外使用不可)";
					break;
				default:
					errmsg = "(エラー)";
				}
				showtoast("駄目みたいですね" + errmsg);
			}
		}
	}

	private void removeOverStatus(){
		//最大表示件数オーバーで消去作業
		//件数はPreferenceに設定から定義
		int max = Integer.parseInt(PreferenceManager.
				getDefaultSharedPreferences(getActivity())
				.getString("MaxListTimeline", "101"));
		if(tladapter.getCount() > max){
			while(tladapter.getCount() > max){
				tladapter.remove(tladapter.getItem(max));
			}
		}
	}

	private void showtoast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();;
	}

	//公式サンプルのコピペ()
	private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }

        /**
         * As mentioned above, we need to override this method to properly signal when a
         * 'swipe-to-refresh' is possible.
         *
         * @return true if the {@link android.widget.ListView} is visible and can scroll up.
         */
        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }
        }

    }

    /**
     * Utility method to check whether a {@link ListView} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }

}
