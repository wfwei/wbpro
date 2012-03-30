package downloads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tmp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
//		System.out.print("2012".compareTo("44"));
//		// TODO Auto-generated method stub
//		 Date startTime = new Date();//当前时间
//	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
//	     String startStr = sdf.format(startTime);
//	     System.out.println(startStr);
	     
	     Calendar c = Calendar.getInstance();
	     
	     System.out.println(c.getTimeInMillis());
	     Thread.sleep(1000);
	     System.out.println(c.getTimeInMillis());
	     Thread.sleep(1000);
	     System.out.println(c.getTimeInMillis());
	     Thread.sleep(1000);
	     System.out.println(c.getTimeInMillis());
	}

}
