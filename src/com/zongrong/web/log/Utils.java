package com.zongrong.web.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 
 * @author zongrong_liang
 *
 */
public class Utils {

	public static void setFile(File file) {
		try {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
			file = null;
		}
	}
	public static void appendStringToFile(String filename, String sContents,boolean isNeedLineSeparator) {
		try {
			FileWriter out = new FileWriter(filename, true); //tells FileWriter
			out.write(System.getProperty("line.separator") + sContents);
			if(isNeedLineSeparator)
				out.write(System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	public static String getCurrentDateTime(){
		final Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String tt = sdf.format(cal.getTime());
		return tt;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getCurrentDateTime());
	}

}

