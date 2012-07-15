package mine.util.io;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import mine.weibo.global.Global;

public class DB4Repost {

	private static Connection con = DB4All.getCon();

	private static HashSet<Long> repostedStatus = new HashSet<Long>();
	private static HashMap<Long, String> usersToAt = new HashMap<Long, String>();

	public static HashSet<Long> getRepostedStatus(String tableName) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			String query = "select status_id from " + tableName;
			ResultSet result = stmt.executeQuery(query);
			while (result.next()) {
				repostedStatus.add(result.getLong(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return repostedStatus;
	}

	public static void insertRepostedStatus(String tableName, long statusId,
			Date date, long weiboUserId) {
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		repostedStatus.add(statusId);
		try {
			Statement stmt = con.createStatement();
			String insert = "insert into " + tableName + " values ( "
					+ statusId + ", '" + dateformat.format(date) + "',"
					+ weiboUserId + " )";
			stmt.executeUpdate(insert);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static HashMap<Long, String> getNUserToAt(String tableName,
			int numOfUsersToAt, int atCount) {
		usersToAt.clear();
		Statement stmt;
		try {
			stmt = con.createStatement();
			String query = "select user_id,user_name from " + tableName
					+ " where at_count =" + atCount + " limit 0,"
					+ numOfUsersToAt;
			ResultSet result = stmt.executeQuery(query);
			if (!result.next()) {
				Global.gout
						.println("available user to @ is abused !   now at_count ="
								+ atCount);
				atCount++;
				getNUserToAt(tableName, numOfUsersToAt, atCount);
			} else {
				usersToAt.put(result.getLong(1), result.getString(2));
			}
			while (result.next()) {
				usersToAt.put(result.getLong(1), result.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersToAt;
	}

	public static void updateUserToAt(String tableName, Long userId) {
		try {
			Statement stmt = con.createStatement();
			long value = 1;
			String getAtCount = "select at_count from " + tableName
					+ " where user_id = " + userId;
			ResultSet rs = stmt.executeQuery(getAtCount);
			if (rs.next()) {
				value += rs.getLong(1);
			}
			String updateUser = "UPDATE " + tableName + " SET at_count = "
					+ value + " where user_id = " + userId;
			stmt.executeUpdate(updateUser);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
