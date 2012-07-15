package mine.util.io;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import mine.weibo.global.Global;

public class DB4All {

	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://10.214.52.12/db4wb";
	private static String user = "root";
	private static String password = "wangfengwei";
	private static Connection con = null;

	private static HashSet<Long> Status_fetched = new HashSet<Long>();
	private static HashSet<Long> users_fetched = new HashSet<Long>();

	public static void initDB() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			setTimeout(57600);//16小时wait_timeout&interactive_timeout
			initParas();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setTimeout(int time) {
		Connection con = getCon();
		CallableStatement cs = null;
		try {
			// 可以直接传入参数
			// cs = conn.prepareCall("{call sp1(1)}");

			// 也可以用问号代替
			cs = con.prepareCall("{call set_timeout(?)}");
			// 设置第一个输入参数的值为110
			cs.setInt(1, time);

			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Global.gout.println("更改了mysql的wait_timeout和interactive_timeout \t"+time);
			try {
				if (cs != null) {
					cs.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void initParas() {
		long userId, statusId;
		String queryStatusIds = "select status_id from status_info_table";
		String queryUserIds = "select user_id from user_info_table";
		ResultSet result;
		int count = 0;
		try {
			Statement stmt = con.createStatement();

			result = stmt.executeQuery(queryStatusIds);
			while (result.next()) {
				statusId = result.getLong(1);
				Status_fetched.add(statusId);
				count++;
			}

			result = stmt.executeQuery(queryUserIds);
			while (result.next()) {
				userId = result.getLong(1);
				users_fetched.add(userId);
				count++;
			}

			// 几十兆数据
			if (count > 1000000) {
				throw new Exception("too much datas for memory");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashSet<Long> getStatus_fetched() {
		return Status_fetched;
	}

	public static void addStatus(long statusId) {
		Status_fetched.add(statusId);
	}

	public static HashSet<Long> getUsers_fetched() {
		return users_fetched;
	}

	public static void addUser(long userId) {
		users_fetched.add(userId);
	}

	public static Connection getCon() {
		if (null == con)
			initDB();
		return con;
	}

	public static void closeDB() {
		try {
			if (con == null)
				return;
			if (false == con.isClosed())
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		initDB();
		
	}
}
