package com.yad.harpseal.util;

public class HarpEvent {
	
	public final static int MOTION_DOWN=0;
	public final static int MOTION_DRAG=1;
	public final static int MOTION_UP=2;
	public final static int MOTION_CLICK=3;
	public final static int MOTION_LONGCLICK=4;
	
	private int type;
	private float x,y;
	private boolean regulated;
	private boolean processed;
	private float cameraX,cameraY;
	
	public HarpEvent(int type,float x,float y) {
		this.type=type;
		this.x=x;
		this.y=y;
		this.regulated=false;
		this.processed=false;
		this.cameraX=0;
		this.cameraY=0;
	}
	
	// Regulate event to app screen
	public void regulate(float scaleRate,float transHeight) {
		if(regulated) {
			HarpLog.error("Event is already regulated");
		}
		x/=scaleRate;
		y/=scaleRate;
		y-=transHeight;
		regulated=true;
	}
	
	// camera setting
	public void setCamera(float cx, float cy) {
		this.cameraX=cx;
		this.cameraY=cy;
	}
	
	// carve that it is processed
	public void process() {
		if(processed) {
			HarpLog.danger("Event is already processed");
		}
		processed=true;
	}
	
	// getter
	public int getType() {
		if(processed) {
			HarpLog.danger("Event is already processed");
		}
		return type;
	}
	public float getX() {
		if(processed) {
			HarpLog.danger("Event is already processed");
		}
		return x+cameraX;
	}
	public float getY() {
		if(processed) {
			HarpLog.danger("Event is already processed");
		}
		return y+cameraY;
	}	
	public boolean isProcessed() { return processed; }

}
