package mine.weibo.global;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import mine.util.io.DB4All;

public class Global {

	public static final PrintStream gout = System.out;
	private static File logFile = new File("log.txt");

	public static final long ONE_HOUR = 1 * 60 * 60 * 1000L;
	public static final long TWO_MINUTES = 2 * 60 * 1000L;

	public static void initAll() {

		// 将输出重新定位到log文件,并用console代替系统的输出
		PrintStream out;
		try {
			out = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(logFile)));
			System.setOut(out);

			// 
			DB4All.initDB();
			
			// 
			AccessTokenAndKey.init();

		} catch (Exception e) {
			gout.println("error in Global");
		}
	}
}
