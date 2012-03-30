package mine;

import java.util.HashSet;
import java.util.List;

import weibo4j.Paging;
import weibo4j.RateLimitStatus;
import weibo4j.Status;
import weibo4j.Tag;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * @author wfwei
 */
public class test {

	private String accessToken = "9e332f1bc1f4b2af99eaf707e15e8085";
	private String accessTokenSecret = "0d888e63ecae123b378dc674c496986a";
	private Weibo weibo;
	private int accessCount = 0, loopToken = 0;

	private String[][] accessTokenAndKey = new String[10][2];

	test() {

		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		try {

			// 初始化accessTokenAndKey
			accessTokenAndKey[0][0] = "9e332f1bc1f4b2af99eaf707e15e8085";
			accessTokenAndKey[0][1] = "0d888e63ecae123b378dc674c496986a";
			accessTokenAndKey[1][0] = "ca401a174dad293742fcdfa00a9095bf";
			accessTokenAndKey[1][1] = "78b36ce0d8685b0cfd6daecf849f085f";
			accessTokenAndKey[2][0] = "6bfef2c6f73ce025d257ad0a3381fdfa";
			accessTokenAndKey[2][1] = "6029cbc95b49a75eb61926027821f24a";
			accessTokenAndKey[3][0] = "1177fabe1ce07e1d7dad169f119371fe";
			accessTokenAndKey[3][1] = "b096cf839f0317ad1efb03b000faa49b";
			accessTokenAndKey[4][0] = "c777276d6e8f67668ec6f6ca6579f79b";
			accessTokenAndKey[4][1] = "773c5a5b91f75b6bbdd55b5366231009";
			accessTokenAndKey[5][0] = "d72761b6d746a25b0bded82b988e363e";
			accessTokenAndKey[5][1] = "e4fe36aad1d4c6702d236a819b0e20d1";
			accessTokenAndKey[6][0] = "127a920f7d4e886443042ee694062949";
			accessTokenAndKey[6][1] = "127a920f7d4e886443042ee694062949";

			// 得到weibo
			weibo = new Weibo();
			weibo.setToken(accessTokenAndKey[loopToken][0],
					accessTokenAndKey[loopToken][1]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// getUserTimeLine();
		// getTag();
		// getFav();
		// testAnalyzer();

		test t = new test();
		t.testRateLimit();
	}

	
	private void testRateLimit() throws Exception{
		weibo.setToken(accessTokenAndKey[4][0],
				accessTokenAndKey[4][1]);
		
		int statusesPerPage = 20;
		Paging paging = new Paging();
		paging.count(statusesPerPage);
		paging.setPage(1);
		
		List<Status> statuses = weibo.getUserTimeline("WeBless",
				paging);
		RateLimitStatus rls =  weibo.getRateLimitStatus();
		System.out.println(rls.toString());
		rls =  weibo.rateLimitStatus();
		System.out.println(rls.toString());
	
	}

	private void getTrends() {
		try {
			Paging paging = new Paging();
			int page = 1, statusesPerPage = 200, num = 0;
			paging.count(statusesPerPage);
			while (true) {
				paging.setPage(page);
				List<Status> statuses = weibo.getTrendStatus("转发 有机会 有奖",
						paging);
				int count = 0;
				for (Status st : statuses) {
					num++;

					System.out.println(st.toString());
					count++;
				}
				if (count < statusesPerPage || num > 500)
					break;
				else
					page++;// 翻页

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getTag() throws WeiboException {
		test s = new test();
		List<Tag> tags;
		Paging paging = new Paging();
		int perPage = 20, page = 1;
		paging.count(perPage);

		while (true) {
			paging.setPage(page);
			// tags = s.weibo.getTags("1757138607", paging);
			tags = s.weibo.getSuggestionsTags();
			int count = 0;
			for (Tag tag : tags) {
				System.out.println(tag.toString());
				count++;
			}
			if (count == perPage)
				page++;
			else
				break;
		}
	}

	/**
	 * tags{ 66693,第三方} tags{ 231102260003033770,Adworld} tags{
	 * 221102080003174162,洞察网络} tags{ 66694,数据调研} tags{ 8303,网络视频} tags{
	 * 8363,移动互联} tags{ 7066,互动营销} tags{ 1358,电子商务} tags{ 292,互联网} tags{
	 * 8291,社会化媒体}
	 * 
	 * */

	private static void getUserTimeLine() throws WeiboException {
		test s = new test();
		// User targetUser = s.weibo.verifyCredentials();
		List<Status> statuses;
		Status st;
		statuses = s.weibo.getUserTimeline("DCCI互联网数据中心");
		for (Status status : statuses) {
			System.out.println(status.toString());
		}
	}

	private void pagingTest() {
		HashSet<Long> statusCount = new HashSet<Long>();
		int pageSize = 10, page = 1;
		try {
			Paging paging = new Paging();
			paging.setCount(pageSize);
			while (true) {
				paging.setPage(page);
				List<Status> statuses = weibo.getTrendStatus("转发 有机会 有奖",
						paging);
				for (Status st : statuses) {
					statusCount.add(st.getId());
				}
				loopAccount();
				System.out.println("page = " + page + "    get status count："
						+ statuses.size() + "      statusCount 大小："
						+ statusCount.size());
				if (statuses.size() < pageSize) {
					break;
				} else
					page++;
				loopAccount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loopAccount() {
		accessCount++;
		if (accessCount == 140) {
			loopToken = (++loopToken) % 6;
			weibo.setToken(accessTokenAndKey[loopToken][0],
					accessTokenAndKey[loopToken][1]);
			accessCount = 0;
			System.out.println("轮换账户：" + loopToken);
		}
	}
}
