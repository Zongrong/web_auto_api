package com.zongrong.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


/**
 * Provide some common usage.
 * @author zongrong_liang
 *
 */
public class TaskBase{

	/**
	 * Check whether one element is existed on the page.
	 * @param obj
	 * @return
	 */
	public boolean isElementExist(WebElement obj){
		boolean result=false;
		if(obj !=null && obj.isDisplayed())
			result=true;
		
		return result;
	}
	/**
	 * Check whether one element is enabled on the page.
	 * @param obj
	 * @return
	 */
	public boolean isElementEnabled(WebElement obj){
		boolean result=false;
		try {
			if(obj!=null)
				result=obj.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public static enum SelBy {
		index,
		value,
		visibleValue,
	}
	/**
	 * Select list operation:Select an option by index���value(option "value" property) or visible text value.
	 * @param obj
	 * @param by
	 * @param value
	 */
	public void select(WebElement obj,SelBy by,String value){
		try {
			if(obj!=null){
				Select selectObj = new Select(obj);
				switch(by){
				case index:
					selectObj.selectByIndex(Integer.parseInt(value));
					break;
				case value:
					selectObj.selectByValue(value);
					break;
				case visibleValue:
					selectObj.selectByVisibleText(value);
					break;
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}

