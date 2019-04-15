package com.zongrong.web.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Get the config parameters in file config.properties
 * @author zongrong_liang
 *
 */
public class Config {
	
	private final static String CONFIG_FILE = "config.properties";

	private static Properties configObj = null;


	/**
	 * return properties Instance
	 */
	private static Properties getPropertiesInstance() {
		if (configObj == null) {
			try {
				InputStream in = new BufferedInputStream(new FileInputStream(CONFIG_FILE));
				configObj =  new Properties();
				configObj.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return configObj;
	}

	public static String getProperty(String key) {
		Properties prop = getPropertiesInstance();
		String value = prop.getProperty(key);
		try {
			if(value!=null)
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return value;
	}
	
}
