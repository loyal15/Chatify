package vsppsgv.chatify.im.common;

import android.os.Environment;

import com.gpit.android.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import vsppsgv.chatify.im.CIApp;


public class CIFileLog {
	
	private static final String FILE_NAME = "bombom.log";
	private static final String FORMAT_TIMESTAMP = "mm/dd/yyy hh:mm:ss";

	public static void logMessage(String msg) {
		
		if ( CIApp.predefinedDevModeOn() == false )
			return;
		
		Date today = new Date();
		String timestamp = Utils.getDateString(today.getTime(), FORMAT_TIMESTAMP);
		msg = timestamp + ":" + msg;
		
		File logFile = new File(Environment.getExternalStorageDirectory() + "/" + FILE_NAME);
		
		if ( !logFile.exists() ) {
			try {
				logFile.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(msg);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
