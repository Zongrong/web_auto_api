package com.zongrong.web.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Get test data to verify test point.
 * @author zongrong_liang
 *
 */
public class ParaData {

	private static Properties sysObj = null;
	private static Properties propObj = null;
	private static String sysPropertyFile = "data/system.properties";

	public static void initPropertiesFile(String propertiesFile) {
		propObj = getPropertiesInstance(propertiesFile);
		if(sysObj==null)
			sysObj=getSystemPropertiesInstance();
	}

	/**
	 * return system properties Instance
	 * @throws IOException 
	 */
	private static Properties getSystemPropertiesInstance(){
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(sysPropertyFile));					
			sysObj =  new Properties();
			sysObj.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sysObj;
	}
	
	/**
	 * return properties Instance
	 * @throws IOException 
	 */
	private static Properties getPropertiesInstance(String  propertiesFilePath){
		try {
			InputStream in = new BufferedInputStream(new FileInputStream( propertiesFilePath));					
			propObj =  new Properties();
			propObj.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return propObj;
	}

	public static String getSysData(String key) {
		if(sysObj==null)
			sysObj=getSystemPropertiesInstance();
		String value = sysObj.getProperty(key);
		try {
			if(value!=null)
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public static String getData(String key) {
		String value = null;
		if(propObj!=null){
			value = propObj.getProperty(key);
			try {
				if(value!=null)
					value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return value;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}

