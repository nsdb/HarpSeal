package com.yad.harpseal.util;

public class Func {

	public static float distan(float x1,float y1,float x2,float y2) {
		return (float)Math.sqrt( Math.pow(x1-x2,2)+Math.pow(y1-y2,2) );
	}
	
	public static float limit(float value,float min,float max) {
		return Math.min(Math.max(value,min),max);
	}
}
