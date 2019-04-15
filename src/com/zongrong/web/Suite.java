package com.zongrong.web;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import com.zongrong.web.annotation.After;
import com.zongrong.web.annotation.AfterClass;
import com.zongrong.web.annotation.Before;
import com.zongrong.web.annotation.BeforeClass;
import com.zongrong.web.annotation.Parameters;
import com.zongrong.web.annotation.Test;
import com.zongrong.web.data.Config;
import com.zongrong.web.log.LogGenerator;
import com.zongrong.web.log.ResultGenerator;

/**
 * Run all of the cases
 * @author zongrong_liang
 *
 */
public class Suite implements CaseService{

	public ArrayList<Class<?>> caseClassList = new ArrayList<Class<?>>();
	public static ResultGenerator resultGen=null;
	public static LogGenerator logger=LogGenerator.getLogger();
	//if depends multi-method,the methods will be separated from ";"
	public static String caseDependSeparator=";";
	public static String beforeClassMethod=null;
	public static String beforeMethod=null;
	public static String afterClassMethod=null;
	public static String afterMethod=null;
	//for screen shot picture.
	public static String casePath=null;

	@SuppressWarnings("rawtypes")
	public void addTest(Class classPath) {
		caseClassList.add(classPath);
	}

	/**
	 * Get the test cases(with annotation "@Test").
	 * @param caseClass
	 * @return
	 */
	private ArrayList<String> getTestCaseList(Class<?> caseClass){
		ArrayList<String> result = new ArrayList<String>();
		Method methodList[] = caseClass.getDeclaredMethods();
		for (int i = 0; i < methodList.length; i++) {
			Method method = methodList[i];
			String methodName=method.getName();
			if (method.isAnnotationPresent(Test.class)) {
				Test test = (Test) method.getAnnotation(Test.class);
				String depends=test.depends();
				if(depends!=null && !depends.trim().equals("")){					
					String[] ds=depends.split(caseDependSeparator);
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
				if(!result.contains(methodName)){
					result.add(methodName);
				}				
			}
		}
		
		return result;
	}
	/**
	 * Run the cases for all the test class.
	 */
	public void run() {
		try {
			for(int i=0;i<this.caseClassList.size();i++){				
				Class<?> caseCls=caseClassList.get(i);
				clear();
				checkAnnotationMethods(caseCls);
				resultGen=ResultGenerator.getLogger(ResultGenerator.resultPath+"/"+caseCls.getName()+".txt");
				if(!this.isTestAnnotationExist(caseCls)){
					String path=caseCls.toString().split(" ")[1].trim();
					resultGen.error(path+" : There no test case");
					logger.error("There is no test case in the class: "+path);
				}else{
					Class<?> cls = Class.forName(caseCls.getName());
					Object obj = cls.newInstance();   
					ArrayList<String> caseList=getTestCaseList(cls);
					
					//beforeClass
					boolean isBeforeClassSucc=true;
					if(beforeClassMethod!=null){
						isBeforeClassSucc=runBeforeClass(caseCls, obj);
					}				
					resultGen.beginClass(caseCls);
					int index=0;
					for (int j = 0; (j < caseList.size()) && isBeforeClassSucc; j++) {
						Method method = getMethod(cls,caseList.get(j));
						String methodName=method.getName();
						index++;
						casePath=caseCls.getName()+"_"+method.getName();
						//before
						boolean beforeRes=true;
						if(beforeMethod!=null){
							beforeRes=runBefore(caseCls, obj);
						}
						if(!beforeRes){
							casePath=null;
							if(afterMethod!=null){
								runAfter(caseCls, obj);
							}
							if(index==1){
								break;
							}
						}
						
						//test case method
						resultGen.beginCase(caseCls,methodName);
						boolean isOver=runTestCase(obj,method);
						if(!isOver){
							createScreenShot(casePath);	
						}
						String caseResult=resultGen.endCase(isOver)?"passed":"failed";
						
						logger.info("Finish to run case"+caseCls.getName()+": "+methodName+" :"+caseResult);
						//after
						if(afterMethod!=null){
							runAfter(caseCls, obj);
						}
						casePath=null;
					}
					//afterClass
					if(afterClassMethod!=null){
						runAfterClass(caseCls, obj);
					}				
					resultGen.resultOver();
				}
			}
			resultGen.resultAllOver();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Check whether there is "@Test" annotation and public method in case class.
	 * @param caseClass
	 * @return
	 */
	public boolean isTestAnnotationExist(Class<?> caseClass){
		boolean result=false;
		Method methodList[] = caseClass.getDeclaredMethods();
		try {
			for (Method method:methodList) {
				String modifier= Modifier.toString(method.getModifiers()).trim();
				if (method.isAnnotationPresent(Test.class) && modifier.startsWith("public")) {
					result=true;
					break;
				}
			}			
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void clear(){
		beforeClassMethod=null;
		beforeMethod=null;
		afterClassMethod=null;
		afterMethod=null;
	}
	
	public static Method getMethod(Class<?> cls,String methodName){
		Method result=null;
		Method[] methods=cls.getDeclaredMethods();
		for(Method method:methods){
			if(method.getName().equals(methodName)){
				result=method;
				break;
			}
		}
		return result;
	}
	
	public static void checkAnnotationMethods(Class<?> caseClass){
		try {
			Method[] methods = caseClass.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(BeforeClass.class)) {
					beforeClassMethod=method.getName();
				}
				if (method.isAnnotationPresent(Before.class)) {
					beforeMethod=method.getName();
				}
				if (method.isAnnotationPresent(AfterClass.class)) {
					afterClassMethod=method.getName();
				}
				if (method.isAnnotationPresent(After.class)) {
					afterMethod=method.getName();
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean runBeforeClass(Class<?> caseCls,Object obj){
		boolean result=true;		
		try{
			logger.info("Start to run "+caseCls.getName()+": "+beforeClassMethod);
			Method method = caseCls.getMethod(beforeClassMethod) ;
			method.invoke(obj);
		}catch(Exception e){
			result=false;
			logger.error(e);
		}
		if(result)
			logger.info("Finish to run "+caseCls.getName()+": "+beforeClassMethod);
		
		return result;
	}
	
	public static boolean runBefore(Class<?> caseCls,Object obj){
		boolean result=true;
		try{
			logger.info("Start to run "+caseCls.getName()+": "+beforeMethod);
			Method methodBefore = caseCls.getMethod(beforeMethod) ;
			methodBefore.invoke(obj);
		}catch(Exception e){
			result=false;
			logger.error(e);			
		}
		if(result)
			logger.info("Finish to run "+caseCls.getName()+": "+beforeMethod);	
		
		return result;
	}
	
	public static boolean runTestCase(final Object obj,final Method method){
		boolean isOver=true;		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<Boolean> callable=new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return  exeCase(obj, method);
			}
			
		};
		Future<Boolean> future=executor.submit(callable);
		long timeOut=Long.parseLong(Config.getProperty("case_run_timeout"));
		try {
			isOver = (Boolean)future.get(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			//������������������
			isOver=false;
			logger.error(e);
		} catch (ExecutionException e) {
			//Execution������
			isOver=false;
			logger.error(e);
		} catch (TimeoutException e) {
			//������������������������
			isOver=false;
			logger.error(e);
		}
		executor.shutdownNow();		
		
		return isOver;
	}
	
	private static boolean exeCase(Object obj,Method method){
		boolean isOver=true;
		try{
			Class<?> caseCls=obj.getClass();
			logger.info("Start to run case "+caseCls.getName()+": "+method.getName());
			
			Test test = (Test) method.getAnnotation(Test.class);
			String parameter=test.parameter();
			if(parameter==null || parameter.trim().equals("")){
				method.invoke(obj);	
			}else{
				/**
				 * Resolve @Parameter and @Test(parameter="...")
				 */
				Method paraMethod = caseCls.getMethod(parameter) ;
				if(paraMethod.isAnnotationPresent(Parameters.class)){
					Object[][] dataObject=(Object[][])paraMethod.invoke(obj);
					for(int i=0;i<dataObject.length;i++){
						method.invoke(obj,dataObject[i]);	
					}
				}else{
					isOver=false;
					logger.error("There is no annotation '@Parameter' for '"+parameter+"'");
				}
				
			}
			
		}catch(Exception e){
			isOver=false;
			logger.error(e);
		}
		return isOver;
	}
	
	public static void runAfter(Class<?> caseCls,Object obj){
		boolean result=true;
		try{
			logger.info("Start to run "+caseCls.getName()+": "+afterMethod);
			Method methodAfter = caseCls.getMethod(afterMethod) ;
			methodAfter.invoke(obj);
			
		}catch(Exception e){
			result=false;
			logger.error(e);
		}
		if(result)
			logger.info("Finish to run "+caseCls.getName()+": "+afterMethod);		
	}
	
	public static void runAfterClass(Class<?> caseCls,Object obj){
		boolean result=true;
		try{
			logger.info("Start to run "+caseCls.getName()+": "+afterClassMethod);
			Method method = caseCls.getMethod(afterClassMethod) ;
			method.invoke(obj);
		}catch(Exception e){
			result=false;
			logger.error(e);
		}
		if(result)
			logger.info("Finish to run "+caseCls.getName()+": "+afterClassMethod);		
	}
	
	/**
	 * Create screen shot picture when there is exception, or verifying failed.
	 * @param casePath
	 */
	public static void createScreenShot(String casePath){
		try{
			Robot robot = new Robot();
			Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
			int width = (int) d.getWidth();
			int height = (int) d.getHeight();
			Image image = robot.createScreenCapture(new Rectangle(0, 0, width, height));
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.createGraphics();
			g.drawImage(image, 0, 0, width, height, null);
			//save screen shot.
			String picType="png";
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String picName=casePath+"_"+df.format(new Date())+"."+picType;
			ImageIO.write(bi, picType, new File(Config.getProperty("logDir")+"/"+picName));
		}catch(IOException e){
			e.printStackTrace();
		}catch(AWTException e){
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Suite s=new Suite();
		s.run();
	}
}

