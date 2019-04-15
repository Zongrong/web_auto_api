package com.zongrong.web.log;

import java.io.File;
import java.util.ArrayList;

import com.zongrong.web.data.Config;

/**
 * Result file generator,to create all.txt file and each result file in results dir.
 * @author zongrong_liang
 *
 */
public class ResultGenerator {
	public static String resultPath=Config.getProperty("resultDir");
	
	private static ResultGenerator logger=null;
	@SuppressWarnings("unused")
	private String caseClass=null;
	private String caseName=null;
	private ArrayList<Boolean> vfPointsList=new ArrayList<Boolean>();	
	private static File resultFile = null;
	public static String FILE_NAME;
	private static int failedNum=0;
	private static int passedNum=0;
	
	//for all.txt result file
	private static String ALL_FILE_NAME="all.txt";
	private static ResultGenerator logger_all=null;	
	private static File resultAllFile = null;
	private static int failedAllNum=0;
	private static int passedAllNum=0;
	private String divideLine="=======================================================";
	
	public ResultGenerator() {
		resultAllFile=new File(resultPath+"/"+ALL_FILE_NAME);
		Utils.setFile(resultAllFile);
	}
	public ResultGenerator(String file) {
		passedNum = 0;
		failedNum = 0;
		init(file);		
	}
	
	/**
	 * For result file logger.
	 * @param logFile
	 * @return
	 */
	public static ResultGenerator getLogger(String logFile) {
		if(logger_all==null){
			logger_all=new ResultGenerator();
		}		
		logger=new ResultGenerator(logFile);
		
		return logger;		  
	}
	
	private void init(String file) {		
		FILE_NAME = file;
		resultFile=new File(FILE_NAME);
		Utils.setFile(resultFile);
		Utils.appendStringToFile(resultFile.getAbsolutePath(), "Test started",true);
	}	

	public static ResultGenerator getLogger(Object c) {	
		return logger;
	}
	
	public void beginClass(Class<?> caseClass){
		Utils.appendStringToFile(resultAllFile.getAbsolutePath(), caseClass.getName(),true);
	}
	
	public void beginCase(Class<?> caseClass,String caseName){
		this.caseClass=caseClass.getName();
		this.caseName=caseName;
	}

	public void addVerifyPoint(boolean result){
		vfPointsList.add(result);
	}
	
	public boolean endCase(boolean isOver){
		boolean isPass=true;
		String prefix_case="\t"+caseName+" - ";
		String prefix_vf="\t"+"\t"+"verifyPoint - ";
		for(int i=0;i<this.vfPointsList.size();i++){
			boolean res=this.vfPointsList.get(i);
			if(res==false){
				isPass=false;
			}
		}
		isPass=isPass && isOver;
		if(isPass){
			passedNum++;
			passedAllNum++;
			Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_case+"passed",true);
			Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_case+"passed",true);
			for(int i=0;i<this.vfPointsList.size();i++){
				if(i==this.vfPointsList.size()-1){
					Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_vf+"passed",true);
					Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_vf+"passed",true);
				}else{
					Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_vf+"passed",false);
					Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_vf+"passed",false);
				}					
			}
		}else{
			failedNum++;
			failedAllNum++;
			Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_case+"failed",true);
			Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_case+"failed",true);
			for(int i=0;i<this.vfPointsList.size();i++){
				boolean isLastOne=false;
				if(i==this.vfPointsList.size()-1)
					isLastOne=true;
				if(this.vfPointsList.get(i)){
					Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_vf+"passed",isLastOne);
					Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_vf+"passed",isLastOne);
				}else{
					Utils.appendStringToFile(resultFile.getAbsolutePath(), prefix_vf+"failed",isLastOne);
					Utils.appendStringToFile(resultAllFile.getAbsolutePath(), prefix_vf+"failed",isLastOne);
				}				
			}
		}
		this.vfPointsList.clear();
		
		return isPass;
	}
	
	public void resultOver(){
		StringBuffer buff=new StringBuffer();
		buff.append("Total cases: "+(passedNum+failedNum)+",");
		buff.append("passed: "+passedNum+",");
		buff.append("failed: "+failedNum);
		Utils.appendStringToFile(resultFile.getAbsolutePath(), buff.toString(),true);
		Utils.appendStringToFile(resultAllFile.getAbsolutePath(), buff.toString(),true);
	}
	
	public void resultAllOver(){		
		Utils.appendStringToFile(resultAllFile.getAbsolutePath(), divideLine,true);
		StringBuffer buff=new StringBuffer();
		buff.append("TOTAL CASES: "+(passedAllNum+failedAllNum)+",");
		buff.append("passed: "+passedAllNum+",");
		buff.append("failed: "+failedAllNum);
		Utils.appendStringToFile(resultAllFile.getAbsolutePath(), buff.toString(),true);
	}
	
	public void error(String content){
		Utils.appendStringToFile(resultAllFile.getAbsolutePath(), content,true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
