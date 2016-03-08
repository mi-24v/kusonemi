package com.client.kusonemi;

import java.util.List;

import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import twitter4j.TwitterException;

@SuppressWarnings("unused")
public class MentionsTimeLineFragment extends TimeLineFragment {
	private static ListFragmentSwipeRefreshLayout Swiperefresh;

	//OutofMemoryErrorでネイテイブ落ちするから親クラスとSwiperを分離できない
	//staticで実装してるのが多分悪い()
	/*@Override
	public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState){
		return this.setSwiper(inflater, container, savedInstanceState);
	};

	protected View setSwiper(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState){
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

        if(MainActivity.StreamEnabled || PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getBoolean("AutoCreateUserStream", false)){
        	Swiperefresh.setEnabled(false);
        }

        // Now return the SwipeRefreshLayout as this fragment's content view
        return Swiperefresh;
	};*/

	@Override
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
					if(arg.getInReplyToUserId() != userID){
						return;
						}
					Log.d(this.getClass().getName(), "onReplayStatus");
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

		if (isFirst && Swiperefresh != null) {
			Swiperefresh.setRefreshing(true);
			 ReloadTimeLine();
			 Swiperefresh.setEnabled(false);
		}
		onStreamStateChange(true, ac);

		 stream.user();
	}

	@Override
	public void ReloadTimeLine(){
		new MentionReloadTask().execute();
	}

	public class MentionReloadTask extends ReloadTask{

		@Override
		protected List<twitter4j.Status> doInBackground(Void... params){
			try{
				return twitter.getMentionsTimeline();
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
	}

}
