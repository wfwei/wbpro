package mine.weibo.repost;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import mine.util.Tools;
import mine.util.io.DB4Repost;
import mine.weibo.global.AccessTokenAndKey;
import mine.weibo.global.Global;
import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.Trend;
import weibo4j.Trends;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

public class RepostStatus implements Runnable {

	private Weibo weibo;
	private String accessKey_default = "fbc99ff096d635f6e9de501c7646a81a",
			accessToken_default = "127a920f7d4e886443042ee694062949";
	int rateCount = 0;
	String searchKeywords = "有奖转发 iphone";
	String targetUser = "我就爱讲段子";
	long weiboUserId = 2638714490L;
	boolean newday = false;
	Calendar c = Calendar.getInstance();

	private static String[][] accessTokenAndKey = AccessTokenAndKey
			.getAccessTokenAndKey();
	private static HashSet<Long> repostedStatus = DB4Repost
			.getRepostedStatus("repost_status_table");
	private static HashMap<Long, String> usersToAt = null;

	RepostStatus(String accessToken, String accessKey) {
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		this.accessKey_default = accessKey;
		this.accessToken_default = accessToken;
		weibo = new Weibo();
		weibo.setToken(accessToken_default, accessKey_default);

	}

	private void unFollowOthers(int num, int cursor, int count) {
		int numOfUnfollowed = 0;
		try {
			List<User> lu = weibo.getFriendsStatuses(
					String.valueOf(weiboUserId), cursor, count);
			rateCheck();
			for (User u : lu) {
				// TODO 无条件删除
				if (true) {
					try {
						weibo.destroyFriendshipByScreenName(u.getScreenName());
						rateCheck();
						numOfUnfollowed++;
						mine.weibo.global.Global.gout.println("unFollowed a user：\t"
								+ numOfUnfollowed);
						if (numOfUnfollowed >= num)
							break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (numOfUnfollowed < num / 1.5) {
				unFollowOthers(num / 2, cursor + count, count);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

	private void followOthers(HashMap<Long, String> usersToAt) {
		for (String userName : usersToAt.values()) {
			try {
				weibo.createFriendshipByScreenName(userName);
				rateCheck();
				mine.weibo.global.Global.gout.println("\t\tFollowed a user：\t");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void repostStatus(String str, int mode) {
		try {
			Paging paging = new Paging();
			int page = 1, statusesPerPage = 50;
			paging.count(statusesPerPage);
			paging.setPage(page);
			List<Status> statuses = null;
			String prewords = null;

			if (weiboUserId < 0) {
				weiboUserId = weibo.verifyCredentials().getId();
				rateCheck();
			}
			/**
			 * to be implemented prewords
			 * */
			switch (mode) {
			case 0:
				String keywords = str;
				statuses = weibo.getTrendStatus(keywords, paging);
				prewords = "每日一转，祝自己和你们好运吧 ^ ^ ";
				break;
			case 1:
				String userId = str;
				statuses = weibo.getUserTimeline(userId, paging);
				prewords = "from @" + targetUser + " 和你们分享哦 [可爱] ";
				break;
			case 2:
				List<Trends> trendsDaily = weibo.getTrendsDaily(0);
				Trend[] ts = trendsDaily.get(0).getTrends();
				int n = Tools.myrand(0, ts.length);
				Trend t = ts[n];
				String hotword = t.getQuery();
				statuses = weibo.getTrendStatus(hotword, paging);
				prewords = "#每日热门#之 " + t.getName() + " ~~";
				break;
			default:
				break;
			}
			rateCheck();

			for (Status st : statuses) {
				Status retweetedStatus = st.getRetweeted_status();
				if (mode != 1 && null != retweetedStatus) {
					st = retweetedStatus;
				}
				long statusId = st.getId();
				if (mode != 1 && !st.getUser().isVerified())
					continue;
				if (repostedStatus.contains(statusId))
					continue;
				String words = getWords(prewords);
				weibo.repost(String.valueOf(statusId), words);
				rateCheck();
				mine.weibo.global.Global.gout.println("转发了一条微博\t" + statusId + "\t"
						+ words);
				DB4Repost.insertRepostedStatus("repost_status_table", statusId,
						st.getCreatedAt(), weiboUserId);
				followOthers(usersToAt);
				updateUser2At();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<Long, String> retrieveUser2At(int num) {
		usersToAt = DB4Repost.getNUserToAt("user2at_table", num, 0);
		return usersToAt;
	}

	private void updateUser2At() {
		for (Long userId : usersToAt.keySet()) {
			DB4Repost.updateUserToAt("user2at_table", userId);
		}
	}

	private String getWords(String preWords) {
		String words = preWords;
		usersToAt = retrieveUser2At(3);
		for (String userName : usersToAt.values()) {
			words += " @" + userName;
		}
		if (words.length() > 139) {
			return words.substring(0, 138);
		}
		return words;
	}

	private void sleep(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void rateCheck() {
		rateCount++;
		if (newday) {
			rateCount = 0;
			newday = false;
		}
		if (rateCount > 136) {
			c = Calendar.getInstance();
			int leftMinutes = 60 - c.get(Calendar.MINUTE) + 5;
			try {
				Global.gout.println(weibo.verifyCredentials().getName()
						+ " 进入睡眠");
				Thread.sleep(leftMinutes * 60 * 1000);
				rateCount = 0;
				Global.gout.println(weibo.verifyCredentials().getName()
						+ " 醒来，历时 ：\t" + leftMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (WeiboException e) {
				e.printStackTrace();
			}
		} else {
			Global.gout.println("rateCheck...睡眠一分钟\n" + c.getTime().toString());
			sleep(1);
		}
	}

	private void scheduling() {
		int hour = 0;
		while (true) {
			c = Calendar.getInstance();
			hour = c.get(Calendar.HOUR_OF_DAY);
			int interval1 = 0, interval2 = 0;
			if (hour >= 9 && hour < 23) {
				interval1 = Tools.myrand(5, 15);
				interval2 = Tools.myrand(5, 15);
				repostStatus("热点", 2);
				sleep(interval1);
				unFollowOthers(15, 100, 40);
				repostStatus("2557255825", 1);// 我就爱讲段子
				sleep(interval2);
				unFollowOthers(15, 250, 40);
				repostStatus("有奖转发 iphone", 0);
				sleep(200 - interval1 - interval2 - 20);
			} else {
				if (hour < 9)
					sleep(hour * 60);
				else
					sleep(9 * 60);
				newday = true;
			}
			Global.gout.println("\n\n\t\t\t\t\tcycle    " + c.getTime().toString());
		}
	}

	public static void main(String[] args) {

		Global.initAll();
		Thread thread1 = new Thread(new RepostStatus(accessTokenAndKey[6][0],
				accessTokenAndKey[6][1]));
		thread1.start();

	}

	public void run() {
		try {
			scheduling();
		} catch (Exception e) {
			Global.gout.println(e.toString());
		}
	}
}
