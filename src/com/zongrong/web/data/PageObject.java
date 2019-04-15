package com.zongrong.web.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Get the elements object on web page.
 * @author zongrong_liang
 *
 */
public class PageObject{	
	
	private Properties propObj = null;
	private WebDriver driver=null;
	
	public PageObject(WebDriver driver,String dataFile){
		this.driver=driver;
		initPropertiesFile(dataFile);
	}
	public static enum With {
		id,
		xpath,
		name,
		className,
		cssSelector,
		linkText,
		partialLinkText,
		tagName,
	}
		
	private void initPropertiesFile(String propertiesFile) {
		propObj = getPropertiesInstance(propertiesFile);
	}

	/**
	 * return properties Instance
	 * @throws IOException 
	 */
	private Properties getPropertiesInstance(String  propertiesFilePath){
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
	
	private String getProperty(String key) {
		String value = propObj.getProperty(key);
		try {
			if(value!=null)
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return value;
	}
	/**
	 * Get the object of the page by key in properties file with xpath.
	 * @param key
	 * @return
	 */
	public WebElement getObjectByKey(String key){
		return this.getObjectByKey(With.xpath, key);
	}
	/**
	 * Get the object of the page by key in properties file.
	 * @param with
	 * @param key
	 * @return
	 */
	public WebElement getObjectByKey(With with,String key){
		String value=getProperty(key);
		return this.getObject(with, value);
	}
	/**
	 * Get the object of the page by xpath value.
	 * @param value
	 * @return
	 */
	public WebElement getObject(String value){
		return this.getObject(With.xpath, value);
	}
	/**
	 * Get the object of the page by value(xpath||id||name......).
	 * @param with
	 * @param value
	 * @return
	 */
	public WebElement getObject(With with,String value){
		WebElement obj=null;
		
		int waitTime=Integer.parseInt(Config.getProperty("web_element_wait"));		
		if(waitTime>0){
			try {			
				TimeUnit.MILLISECONDS.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try{
			switch(with){
			case id:
				obj=driver.findElement(By.id(value));
				break;
			case xpath:
				obj=driver.findElement(By.xpath(value));
				break;
			case name:
				obj=driver.findElement(By.name(value));
				break;
			case className:
				obj=driver.findElement(By.className(value));
				break;
			case cssSelector:
				obj=driver.findElement(By.cssSelector(value));
				break;
			case linkText:
				obj=driver.findElement(By.linkText(value));
				break;
			case partialLinkText:
				obj=driver.findElement(By.partialLinkText(value));
				break;
			case tagName:
				obj=driver.findElement(By.tagName(value));
				break;		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
				
		return obj;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
