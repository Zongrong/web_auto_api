package com.zongrong.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.zongrong.web.annotation.Test;
import com.zongrong.web.data.Config;
import com.zongrong.web.log.ResultGenerator;

/**
 * The parent class for each test case��?
 * 1.Get webDriver object.
 * 2.Debug the cases for each test class��?
 * @author zongrong_liang
 *
 */
public abstract class TestBase implements CaseService{
	
	public ArrayList<String> caseList = new ArrayList<String>();
	
	private Class<? extends TestBase> caseClass=null;
	
	public TestBase(){
		caseClass=this.getClass();
		Suite.resultGen=ResultGenerator.getLogger(ResultGenerator.resultPath+"/"+caseClass.getName()+".txt");
	}
		
	public void addTestCase(String methodName){
		caseList.add(methodName);
	}
	
	/**
	 * New WebDriver object
	 * @return
	 */
	public static WebDriver getWebDriver(){
		WebDriver driver = null;
		String browserType=Config.getProperty("browserType");
		if(browserType.equalsIgnoreCase("IE")){
			System.setProperty("webdriver.ie.driver", Config.getProperty("IEDriver_path")); 
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();    
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);    
			driver = new InternetExplorerDriver(ieCapabilities);
			//driver = new InternetExplorerDriver();
						
		}else if(browserType.equalsIgnoreCase("Chrome")){
			System.setProperty("webdriver.chrome.driver", Config.getProperty("ChromeDriver_path"));
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setJavascriptEnabled(true);
			driver = new ChromeDriver();
			
		}else if(browserType.equalsIgnoreCase("htmlunit")){
			driver = new HtmlUnitDriver(true);
			
		}else{
			System.setProperty("webdriver.firefox.bin", Config.getProperty("firefox_path")); 
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            capabilities.setJavascriptEnabled(true);
			driver = new FirefoxDriver();
			
		}
		
		driver.manage().timeouts().implicitlyWait(Long.parseLong(Config.getProperty("web_element_timeout")), TimeUnit.SECONDS);
		
		return  driver;
	}
	
	/**
	 * Debug the cases in main() for test class.
	 */
	public void run(){
		try{
			Class<?> cls = Class.forName(caseClass.getName());
			if(!isCaseNameExist(cls)){
				return;
			}			
			if(!isTestAnnotationExist(cls)){
				return;
			}
			
			this.caseList=caseRunOrder(this.caseList);
			
			Object obj = cls.newInstance();   
			Suite.clear();
			Suite.checkAnnotationMethods(cls);
			//beforeClass
			boolean isBeforeClassSucc=true;
			Method method = null;
			if(Suite.beforeClassMethod!=null){
				method = cls.getMethod(Suite.beforeClassMethod) ;
				isBeforeClassSucc=Suite.runBeforeClass(cls, obj);
			}
			
			Suite.resultGen.beginClass(cls);			
			int index=0;
			for (int j = 0; (j < caseList.size()) && isBeforeClassSucc; j++) {
				index++;				
				//before
				boolean beforeRes=true;
				if(Suite.beforeMethod!=null){
					beforeRes=Suite.runBefore(cls, obj);
				}				
				if(!beforeRes){
					if(Suite.afterMethod!=null){
						Suite.runAfter(cls, obj);
					}
					if(index==1){
						break;
					}
				}
				
				method = Suite.getMethod(cls, caseList.get(j));
				String methodName=method.getName();
				if (method.isAnnotationPresent(Test.class)) {
					Suite.casePath=cls.getName()+"_"+method.getName();
					//test case method
					Suite.resultGen.beginCase(caseClass,methodName);
					boolean isOver=Suite.runTestCase(obj, method);
					if(!isOver){
						Suite.createScreenShot(Suite.casePath);	
					}
					
					String caseResult=Suite.resultGen.endCase(isOver)?"passed":"failed";					
					Suite.logger.info("Finish to run case"+cls.getName()+": "+methodName+" :"+caseResult);
				}
				//after
				if(Suite.afterMethod!=null){
					Suite.runAfter(cls, obj);
				}		
				Suite.casePath=null;
			}
			//afterClass
			if(Suite.afterClassMethod!=null){
				Suite.runAfterClass(cls, obj);
			}
			
			Suite.resultGen.resultOver();
			Suite.resultGen.resultAllOver();
			
		} catch (Exception e) {
			e.printStackTrace();			
		}		
	}
	

	/**
	 * Check whether there is the case name.
	 * @param caseClass
	 * @return
	 * @throws java.lang.NoSuchMethodException 
	 */
	public boolean isCaseNameExist(Class<?> caseClass) throws NoSuchMethodException{
		boolean result=true;
		for (int i = 0; i < caseList.size(); i++) {
			Method method=Suite.getMethod(caseClass,caseList.get(i)) ;
			if(method==null){
				result=false;
				throw new NoSuchMethodException("There is no test case: "+caseList.get(i));
			}
		}
		
		return result;
	}
	
	/**
	 * Check whether there is "@Test" annotation for all of the cases which need to run.
	 * @param caseClass
	 * @return
	 */
	public boolean isTestAnnotationExist(Class<?> caseClass){
		boolean result=true;
		String noTestStr=null;
		try {
			int index=0;
			for (int i = 0; i < caseList.size(); i++) {
				Method method = Suite.getMethod(caseClass,caseList.get(i)) ;
				if (!method.isAnnotationPresent(Test.class)) {
					index++;
					if(index==1){
						noTestStr=caseList.get(i);
						result=false;
					}else{
						noTestStr+=","+caseList.get(i);
					}
				}
			}			
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		
		if(!result){
			String cn="case";
			if(noTestStr!=null && noTestStr.contains(","))
				cn="cases";
			System.err.println("There is no '@Test' annotation for the "+cn+": "+noTestStr);
		}
		return result;
	}
	
	/**
	 * When there is @Test(depends="xxx;yyy") for one case,the order of cases executing will be changed. 
	 * If depends multi-method,the methods will be separated from ";"
	 * @param csList
	 * @return
	 */
	public ArrayList<String> caseRunOrder(ArrayList<String> csList){
		ArrayList<String> result = new ArrayList<String>();
		try{			
			for(int i=0;i<csList.size();i++){
				Method method=Suite.getMethod(caseClass,csList.get(i));
				Test test = (Test) method.getAnnotation(Test.class);
				String depends=test.depends();
				if(depends!=null && !depends.trim().equals("")){
					String[] ds=depends.split(Suite.caseDependSeparator);
					for(int k=0;k<ds.length;k++){
						ds[k]=ds[k].trim();
						if(!result.contains(ds[k])){
							result.add(ds[k]);							
						}else{
							int index=result.indexOf(ds[k]);
							result.remove(index);
							result.add(ds[k]);
						}
						
					}
					
				}
				if(!result.contains(csList.get(i))){
					result.add(csList.get(i));
				}				
			}
		}catch(SecurityException e){
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}

