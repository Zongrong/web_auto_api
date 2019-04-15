package com.zongrong.web.log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.zongrong.web.data.Config;

/**
 * Log file generator,to create log.txt file.
 * @author zongrong_liang
 *
 */
public class LogGenerator {
	//for log.txt file
	public static String logPath=Config.getProperty("logDir");	
	private String LOG_FILE_NAME="log.txt";
	private static LogGenerator logger=null;
	private static File logAllFile = null;
		
	public LogGenerator() {
		logAllFile=new File(logPath+"/"+LOG_FILE_NAME);
		Utils.setFile(logAllFile);	
	}
	/**
	 * For log file logger.
	 * @return
	 */
	public static LogGenerator getLogger() {
		if(logger==null){
			logger=new LogGenerator();
		}
		
		return logger;		  
	}
		
	
	public static LogGenerator getLogger(Object c) {	
		return logger;
	}
			
	public void info(String content){
		String conts=Utils.getCurrentDateTime()+" "+content;
		Utils.appendStringToFile(logAllFile.getAbsolutePath(), conts,false);
		System.out.println(conts);
	}
	
	public void error(Exception e){		
		String content= Utils.getCurrentDateTime()+" "+getErrorInfoFromException(e);
		Utils.appendStringToFile(logAllFile.getAbsolutePath(), content,false);
		System.err.println(content);
	}
	
	public void error(String content){
		String conts=Utils.getCurrentDateTime()+" "+content;
		Utils.appendStringToFile(logAllFile.getAbsolutePath(), conts,false);
		System.err.println(conts);
	}
	
	public static String getErrorInfoFromException(Exception e) {   
	    try {   
	        StringWriter sw = new StringWriter();   
	        PrintWriter pw = new PrintWriter(sw);   
	        e.printStackTrace(pw);
	        return sw.toString() ; 
	    } catch (Exception e2) {   
	        return "bad getErrorInfoFromException";   
	    }   
	}   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
