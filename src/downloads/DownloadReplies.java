package downloads;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import global.AccessTokenAndKey;
import global.Global;
import mysql.DB4Downloads;

public class DownloadReplies implements Runnable {

	private Weibo weibo;
	private String currentThreadIdentifier = null;
	private String accessKey_default = "fbc99ff096d635f6e9de501c7646a81a",
			accessToken_default = "127a920f7d4e886443042ee694062949";
	private SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	int rateCount = 0;

	private static String[][] accessTokenAndKey = AccessTokenAndKey
			.getAccessTokenAndKey();
	private static Queue<Long> statusNeedsReply = DB4Downloads
			.getStatusNeedsReply("status_info_table", 5);

	DownloadReplies(String accessToken, String accessKey) {
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		this.accessKey_default = accessKey;
		this.accessToken_default = accessToken;
		weibo = new Weibo();
		weibo.setToken(accessToken_default, accessKey_default);
		currentThreadIdentifier = accessKey.substring(0, 3);
	}

	private static long getStatus() {
		long statusId = -1;
		synchronized (statusNeedsReply) {
			statusId = statusNeedsReply.poll();
		}
		return statusId;
	}

	private void getStatusReply(int replyNum) {
		while (true) {
			Global.gout.println(currentThreadIdentifier + "\t进入临界区");
			long statusId = getStatus();
			if (statusId > 0) {
				Global.gout.println(currentThreadIdentifier + "\t获取statusId"
						+ statusId);
				_getStatusReply(statusId, replyNum);
			} else {
				Global.gout.println(currentThreadIdentifier + "\t获取statusId失败");
				break;
			}
		}
	}

	private void _getStatusReply(long statusId, int number) {
		int statusesPerPage = 20;
		Paging paging = new Paging();
		paging.count(statusesPerPage);
		int count = 0, page = 1;
		List<Status> statuses = null;
		while (true) {
			paging.setPage(page);
			try {
				statuses = weibo.getreposttimeline(String.valueOf(statusId),
						paging);
				rateCheck();
				for (Status st : statuses) {
					long replyId = st.getId();
					String text = st.getText();
					if (text.startsWith("转发微博") || text.length() < 2)
						continue;
					String created_at = dateformat.format(st.getCreatedAt());
					int weight = 1;
					DB4Downloads.insertStatusReply("status_reply_table",
							statusId, replyId, text, created_at, weight);
					DB4Downloads
							.insertUserInfo(st.getUser(), "user_info_table");
					count++;
					Global.gout.println(currentThreadIdentifier
							+ "\t添加status reply 成功，当前count值：\t" + count);
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}

			if (count >= number || statuses.size()<statusesPerPage)
				break;
			else
				page++;

		}
		DB4Downloads.updateStatusReplyNum("status_info_table", statusId, count);
		Global.gout.println(currentThreadIdentifier + "\t获取\t" + statusId
				+ "\t的reply数:\t" + number);
	}

	private void sleep(int minutes) {
		try {
			Global.gout.println(currentThreadIdentifier + "\t进入睡眠，时间为\t"+minutes);
			Thread.sleep(minutes * 60 * 1000);
			Global.gout.println(currentThreadIdentifier + "\t醒来，历时 ：\t"
					+ minutes);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void rateCheck() {
		rateCount++;
		if (rateCount > 136) {
			int leftMinutes = 60 - new Date().getMinutes() + 5;
			sleep(leftMinutes);
			rateCount = 0;
		} else {
			sleep(1);
		}
	}

	public static void main(String[] args) {
		Global.initAll();
		Thread thread1 = new Thread(new DownloadReplies(
				accessTokenAndKey[3][0], accessTokenAndKey[3][1]));
		Thread thread2 = new Thread(new DownloadReplies(
				accessTokenAndKey[2][0], accessTokenAndKey[2][1]));

		thread1.start();
		thread2.start();
	}

	public void run() {
		try {
			getStatusReply(34);
		} catch (Exception e) {
			Global.gout.println(e.toString());
		}
	}
}
