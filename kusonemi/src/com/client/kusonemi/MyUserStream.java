package com.client.kusonemi;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class MyUserStream implements UserStreamListener {

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStatus(Status arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onException(Exception arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onBlock(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDirectMessage(DirectMessage arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onFavoritedRetweet(User source, User target,
			Status favoritedRetweeet) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onFollow(User source, User followedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onFriendList(long[] friendIds) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onQuotedTweet(User source, User target, Status quotingTweet) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onRetweetedRetweet(User source, User target,
			Status retweetedStatus) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnblock(User source, User unblockedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnfollow(User source, User unfollowedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserDeletion(long deletedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListCreation(User listOwner, UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListDeletion(User listOwner, UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListMemberAddition(User addedMember, User listOwner,
			UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListMemberDeletion(User deletedMember, User listOwner,
			UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListSubscription(User subscriber, User listOwner,
			UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListUnsubscription(User subscriber, User listOwner,
			UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListUpdate(User listOwner, UserList list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserProfileUpdate(User updatedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserSuspension(long suspendedUser) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
