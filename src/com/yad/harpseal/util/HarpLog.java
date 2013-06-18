package com.yad.harpseal.util;

import java.util.Calendar;

public class HarpLog {
	
	private static Calendar cal;
	
	private static final boolean DEBUG=false;
	
	public static void init(String type) {
		cal=Calendar.getInstance();
		info("Logger inited");
	}
	
	public static void debug(String msg) {
		if(DEBUG) log("debug",msg);
	}

	public static void info(String msg) {
		log("info",msg);
	}
	
	public static void danger(String msg) {
		log("danger",msg);
	}
	
	public static void error(String msg) {
		log("error",msg);
	}
	
	public static void fetal(String msg) {
		log("fetal",msg);
	}

	private static void log(String level,String msg) {
		String result="["+level+"]["+getCurrentTimeString()+"] "+msg;
		System.out.println(result);
	}

	private static String getCurrentTimeString() {
		cal.setTimeInMillis(System.currentTimeMillis());
		return ""+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+
				cal.get(Calendar.SECOND)+":"+cal.get(Calendar.MILLISECOND);
	}
	
}
