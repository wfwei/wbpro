package downloads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class DownPic {

	// private void _download(String picUrl, String path) throws HttpException,
	// IOException {
	// HttpClient client = new HttpClient();
	// GetMethod get = new GetMethod(picUrl);
	// client.executeMethod(get);
	// File storeFile = new File(path);
	// FileOutputStream fileOutputStream = new FileOutputStream(storeFile);
	// FileOutputStream output = fileOutputStream;
	//
	// output.write(get.getResponseBody());
	// output.close();
	//
	// }
	//
	// public boolean download(String picUrl, String date, String name) {
	// boolean success = false;
	// File dir = new File("D:/weiboData/" + date + "/");
	// try {
	// if (!dir.isDirectory())
	// dir.mkdirs();
	// String path = "D:/weiboData/" + date + "/" + name;
	// _download(picUrl, path);
	// success = true;
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return success;
	// }

	public static void main(String[] args) {
		//
		// new DownPic()
		// .download(
		// "http://img5.pcpop.com/ProductImages/Leader/0/446/000446471.jpg",
		// "20110208","2008sohu.jpg");

		download("http://www.google.com.hk/intl/zh-CN/images/logo_cn.gif",
				"2012-2", "google.jpg");
	}

	public static boolean download(String picUrl, String date, String name) {
		boolean success = false;
		File dir = new File("D:/weiboData/" + date + "/");

		if (!dir.isDirectory())
			dir.mkdirs();
		String path = "D:/weiboData/" + date + "/" + name;
		_download(picUrl, path);
		success = true;

		return success;
	}

	private static void _download(String picUrl, String path) {
		try {
			BufferedInputStream in = new BufferedInputStream(
					new URL(picUrl).openStream());
			File img = new File(path);
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(img));
			byte[] buf = new byte[2048];
			int length = in.read(buf);
			while (length != -1) {
				out.write(buf, 0, length);
				length = in.read(buf);
			}
			in.close();
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
