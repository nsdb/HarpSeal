package com.yad.harpseal.util;

public class HarpEvent {
	private int type;
	private float x,y;
	private boolean processed;
	
	public HarpEvent(int type,float x,float y) {
		this.type=type;
		this.x=x;
		this.y=y;
		this.processed=false;
	}
	
	public void regulate(float scaleRate,float transHeight) {
		x/=scaleRate;
		y/=scaleRate;
		y-=transHeight;
	}
	
	
	public int getType() { return type; }
	public float getX() { return x; }
	public float getY() { return y; }
	
	
	public void process() {
		if(processed==true) {
			HarpLog.danger("Event is already processed");
		}
		processed=true;
	}
	
	public boolean isProcessed() { return processed; }

}
