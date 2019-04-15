package com.zongrong.web;

/**
 * Verify function point in test case.
 * @author zongrong_liang
 *
 */
public class Assert {
	
	private static void generateScreenShotPic(boolean result){
		if(!result)
			Suite.createScreenShot(Suite.casePath);
	}

	public static void assertEquals(String expected,String actual){
		boolean result=false;
		if (expected == null && actual == null)
			result=true;
		if (expected != null && expected.equals(actual))
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(boolean expected,boolean actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(long expected,long actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(byte expected,byte actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(char expected,char actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(short expected,short actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertEquals(int expected,int actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	
	public static void assertSame(Object expected, Object actual){
		boolean result=false;
		if (expected == actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertNotSame(Object expected, Object actual){
		boolean result=false;
		if (expected != actual)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	
	public static void assertTrue(boolean result){
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);		
	}
	public static void assertFalse(boolean result){
		Suite.resultGen.addVerifyPoint(!result);
		generateScreenShotPic(!result);
	}
	
	public static void assertNull(Object object){
		boolean result=false;
		if (object == null)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	public static void assertNotNull(Object object){
		boolean result=false;
		if (object != null)
			result=true;
		Suite.resultGen.addVerifyPoint(result);
		generateScreenShotPic(result);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
