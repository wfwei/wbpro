package mysql;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

import weibo4j.User;

public class DB4Downloads {

	private static Connection con = DB4All.getCon();
	public static HashSet<Long> users_fetched = DB4All.getUsers_fetched();
	public static HashSet<Long> Status_fetched = DB4All.getStatus_fetched();
	private static Queue<Long> statusNeedsReply = new java.util.LinkedList<Long>();

	/**
	 * 
	 * */
	public static void insertStatus(String tableName, long statusId,
			String created_at, String text, String original_pic, long userId) {
		try {
			Statement stmt = con.createStatement();
			// 添加当前用户信息到tableName中
			String insert_sql = "insert into " + tableName + " values ("
					+ statusId + ",'" + created_at + "','" + delComma(text) + "','"
					+ original_pic + "'," + userId + ",0)";
			stmt.executeUpdate(insert_sql);
			DB4All.addStatus(statusId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * table structure: user.getId() user.getScreenName() user.getName()
	 * user.getCity() user.getLocation() des user.getGender()
	 * user.getFollowersCount() user.getFriendsCount() user.getStatusesCount()
	 * user.getFavouritesCount() user.getCreatedAt().toGMTString() verified
	 * */
	public static void insertUserInfo(User user, String tableName) {
		long userId = user.getId();
		// 已经有了该用户的信息
		if (users_fetched.contains(userId))
			return;
		try {
			Statement stmt = con.createStatement();
			int verified = 1;
			if (user.isVerified() == false)
				verified = 0;

			// 去除'字符
			String des = delComma(user.getDescription());
			String insertUser = "insert into " + tableName + " values ( "
					+ user.getId() + ",'" + user.getScreenName() + "','"
					+ user.getName() + "', " + user.getCity() + " ,'"
					+ user.getLocation() + "','" + des + "','"
					+ user.getGender() + "', " + user.getFollowersCount()
					+ " , " + user.getFriendsCount() + " , "
					+ user.getStatusesCount() + " , "
					+ user.getFavouritesCount() + " , '"
					+ user.getCreatedAt().toGMTString() + "' , " + verified
					+ " )";
			stmt.executeUpdate(insertUser);
			DB4All.addUser(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getPage(String tableName, String targetUser) {
		Statement stmt;
		int page = 1;
		try {
			stmt = con.createStatement();
			String query = "select page from " + tableName
					+ " where user_name = '" + targetUser + "'";
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				page = result.getInt(1);
			} else {
				initRecordTable(tableName, targetUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return page;
	}

	private static void initRecordTable(String tableName, String targetUser) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			String insert = "insert into " + tableName + " values ('"
					+ targetUser + "',1,'4','1')";
			stmt.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getFromDate(String tableName, String targetUser) {
		Statement stmt;
		String fromDate = "3";// biger than any possible date
		try {
			stmt = con.createStatement();
			String query = "select from_date from " + tableName
					+ " where user_name = '" + targetUser + "'";
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				fromDate = result.getString(1);
			} else {
				initRecordTable(tableName, targetUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fromDate;
	}

	public static String getToDate(String tableName, String targetUser) {
		Statement stmt;

		String toDate = "1";// smaller than any date
		try {
			stmt = con.createStatement();
			String query = "select to_date from " + tableName
					+ " where user_name = '" + targetUser + "'";
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				toDate = result.getString(1);
			} else {
				initRecordTable(tableName, targetUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toDate;
	}

	public static String setFromDate(String tableName, String targetUser,
			String date) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			String update = "update " + tableName + " set from_date = '" + date
					+ "' where user_name = '" + targetUser + "'";
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String setToDate(String tableName, String targetUser,
			String date) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			String update = "update " + tableName + " set to_date = '" + date
					+ "' where user_name = '" + targetUser + "'";
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static int updatePage(String tableName, String targetUser, int page) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			String updateQuery = "update " + tableName + " set page = " + page
					+ " where user_name = '" + targetUser + "'";
			stmt.executeUpdate(updateQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return page;
	}

	public static Queue<Long> getStatusNeedsReply(String tableName,
			int minReplyNum) {
		Statement stmt;
		long statusId;
		statusNeedsReply.clear();
		try {
			stmt = con.createStatement();
			String query = "select status_id from " + tableName
					+ " where reply_num < " + minReplyNum;
			ResultSet result = stmt.executeQuery(query);
			while (result.next()) {
				statusId = result.getLong(1);
				statusNeedsReply.offer(statusId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statusNeedsReply;
	}

	public static void insertStatusReply(String tableName, long statusId,
			long replyId, String text, String created_at, int weight) {
		try {
			Statement stmt = con.createStatement();
			// 添加当前用户信息到tableName中
			String insert_sql = "insert into " + tableName + " values ("
					+ statusId + "," + replyId + ",'" + delComma(text) + "','"
					+ created_at + "'," + weight + ")";
			stmt.executeUpdate(insert_sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateStatusReplyNum(String tableName, long statusId,
			int count) {
		try {
			long replyNum = 0;
			Statement stmt = con.createStatement();
			String query = "select reply_num from " + tableName
					+ " where status_id = " + statusId;
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				replyNum = result.getLong(1);
			} else
				return;
			replyNum += count;
			String update = "update " + tableName + " set reply_num = "
					+ replyNum + " where status_id = " + statusId;
			stmt.executeUpdate(update);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// TODO WHY CHAGES ENCODING ?
	private static String delComma(String text) {
		byte[] bytes;
		try {
			bytes = text.getBytes("gbk");
			text = new String(bytes, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text.replace('\'', '’');
	}

	public static void main(String args[]) {
//		DB4All.initDB();
//		DB4All.addUser(1);
//		System.out.println(DB4All.getUsers_fetched().contains(1L));
//		System.out.println(users_fetched.contains(1L));
		DB4Downloads.updateStatusReplyNum("status_info_table", 3383270559497886L, 10);
		DB4Downloads.updateStatusReplyNum("status_info_table", 3383270559497886L, -10);

	}
}
