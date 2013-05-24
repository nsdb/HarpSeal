package com.yad.harpseal.util;

public class HarpEvent {
	private int type;
	private float x,y;
	private boolean regulated;
	private boolean processed;
	
	public HarpEvent(int type,float x,float y) {
		this.type=type;
		this.x=x;
		this.y=y;
		this.regulated=false;
		this.processed=false;
	}
	
	public void regulate(float scaleRate,float transHeight) {
		if(regulated==true) {
			HarpLog.error("Event is already regulated");
		}
		x/=scaleRate;
		y/=scaleRate;
		y-=transHeight;
		regulated=true;
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
