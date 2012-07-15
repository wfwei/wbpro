package mine.weibo.downloads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;

public class DownPic {
	public static Logger logger = Logger.getLogger(DownPic.class);

	public static boolean download(String picUrl, String date, String name) {
		File dir = new File("D:/weiboData/" + date + "/");

		if (!dir.isDirectory())
			dir.mkdirs();
		String path = "D:/weiboData/" + date + "/" + name;
		
		return _download(picUrl, path);
	}

	private static boolean _download(String picUrl, String path) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(
					new URL(picUrl).openStream());
			File img = new File(path);
			out = new BufferedOutputStream(
					new FileOutputStream(img));
			byte[] buf = new byte[2048];
			int length = in.read(buf);
			while (length != -1) {
				out.write(buf, 0, length);
				length = in.read(buf);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}

	
	public static void main(String[] args) {
		download("http://www.google.com.hk/intl/zh-CN/images/logo_cn.gif",
				"2012-2", "google.jpg");
	}
}
