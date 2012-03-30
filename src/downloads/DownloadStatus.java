package downloads;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.Weibo;
import global.AccessTokenAndKey;
import global.Global;
import mysql.DB4Downloads;

public class DownloadStatus implements Runnable {

	private Weibo weibo;
	private String accessKey_default = "fbc99ff096d635f6e9de501c7646a81a",
			accessToken_default = "127a920f7d4e886443042ee694062949";
	private static String[][] accessTokenAndKey = AccessTokenAndKey
			.getAccessTokenAndKey();
	private String targetUser = "冷笑话精选";
	private int currentPage = 0;
	// TODO did not really use the two
	private String fromDate = null, toDate = null;

	private SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateformat_ym = new SimpleDateFormat("yyyy-MM");

	int rateCount = 0;

	DownloadStatus(String accessToken, String accessKey, String targetUser) {
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		this.accessKey_default = accessKey;
		this.accessToken_default = accessToken;
		weibo = new Weibo();
		weibo.setToken(accessToken_default, accessKey_default);
		this.targetUser = targetUser;
		init();
	}

	private void init() {
		currentPage = DB4Downloads.getPage("record_table", targetUser);
		fromDate = DB4Downloads.getFromDate("record_table", targetUser);
		toDate = DB4Downloads.getToDate("record_table", targetUser);
	}

	private void getStatus(int page) {
		try {
			int statusesPerPage = 20;
			Paging paging = new Paging();
			paging.count(statusesPerPage);
			while (true) {
				paging.setPage(page);
				List<Status> statuses = weibo.getUserTimeline(targetUser,
						paging);
				rateCheck();
				int count = 0;
				for (Status st : statuses) {
					Status retweetedStatus = st.getRetweeted_status();
					// no reposted status
					if (retweetedStatus != null)
						continue;

					if (!DB4Downloads.Status_fetched.contains(st.getId())) {
						count++;
						long statusId = st.getId();
						long userId = st.getUser().getId();
						String created_at = dateformat
								.format(st.getCreatedAt());
						String text = st.getText();
						String original_pic = st.getOriginal_pic();
						String date_ym = dateformat_ym
								.format(st.getCreatedAt());
						int pointIndex = original_pic.lastIndexOf('.');
						String filetype;
						if (pointIndex > -1)
							filetype = original_pic.substring(pointIndex);
						else
							filetype = ".jpg";
						String name_pic = Long.toString(statusId) + filetype;
						DownPic.download(original_pic, date_ym, name_pic);
						// save in database
						DB4Downloads.insertStatus("status_info_table",
								statusId, created_at, text, original_pic,
								userId);
						Global.gout.println(targetUser + "   添加了第\t" + count
								+ "\t个status \t" + statusId);

						if (page % 4 == 3) {
							Global.gout.println(targetUser + " 当前的page数:\t"
									+ page + "\t save records paras");
							DB4Downloads.updatePage("record_table", targetUser,
									page);
							if (created_at.compareTo(fromDate) < 0)
								fromDate = DB4Downloads.setFromDate("record_table",
										targetUser, created_at);
							if (created_at.compareTo(toDate) > 0)
								toDate = DB4Downloads.setToDate("record_table",
										targetUser, created_at);
						}
					}
				}
				
				// TODO 小于10的时候才停止
				if (statuses.size() < statusesPerPage-10)
					break;
				else
					page++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Global.gout.println(targetUser + " 当前的page数:\t" + page);
		DB4Downloads.updatePage("record_table", targetUser, page);
		DB4Downloads.setFromDate("record_table", targetUser, fromDate);
		DB4Downloads.setToDate("record_table", targetUser, toDate);
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
		if (rateCount > 136) {
			int leftMinutes = 60 - new Date().getMinutes() + 5;
			try {
				Global.gout.println(targetUser + " 进入睡眠");
				Thread.sleep(leftMinutes * 60 * 1000);
				rateCount = 0;
				Global.gout.println(targetUser + " 醒来，历时 ：\t" + leftMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			sleep(1);
		}
	}

	public static void main(String[] args) {
		Global.initAll();
		Thread thread1 = new Thread(new DownloadStatus(accessTokenAndKey[4][0],
				accessTokenAndKey[4][1], "冷笑话精选"));
		Thread thread2 = new Thread(new DownloadStatus(accessTokenAndKey[5][0],
				accessTokenAndKey[5][1], "冷笑话搞笑幽默"));

		thread1.start();
		thread2.start();
	}

	public void run() {
		try {
			getStatus(currentPage);
		} catch (Exception e) {
			Global.gout.println(e.toString());
		}
	}
}
