package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Func;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GameCamera extends GameObject {

	private float cameraX,cameraY;
	private float cameraWidth,cameraHeight;

	private int cameraMode;
	private float targetX,targetY;
	
	private final static int CAM_MENUAL=1;
	private final static int CAM_TARGET=2;
	private final static float TARGET_MOTION_MIN=1f;
	private final static float TARGET_MOTION_SPD=0.075f;

	public GameCamera(Communicable con, int width, int height, int x, int y) {
		super(con);
		this.cameraX=0;
		this.cameraY=0;
		this.cameraWidth=width;
		this.cameraHeight=height;
		
		this.cameraMode=CAM_MENUAL;
		this.targetX=0;
		this.targetY=0;
		setCameraPoint(x,y,false);
	}

	// In fact, something is wrong.. -_-
	@Override
	public void playGame(int ms) {
		
		if(cameraMode != CAM_TARGET) return;
		
		if(targetX!=cameraX || targetY!=cameraY) {
			float moveX=(targetX-cameraX)*TARGET_MOTION_SPD*ms/15;
			float moveY=(targetY-cameraY)*TARGET_MOTION_SPD*ms/15;
			float minPer=Func.distan(0, 0, moveX, moveY)/TARGET_MOTION_MIN*ms/15;
			if(minPer<1) {
				moveX/=minPer;
				moveY/=minPer;
			}
			
			if(Func.distan(targetX,targetY,cameraX,cameraY)<TARGET_MOTION_MIN*ms/15) {
				cameraX=targetX;
				cameraY=targetY;
			}
			else {
				cameraX+=moveX;
				cameraY+=moveY;
			}
		}
		if(targetX==cameraX && targetY==cameraY) {
			cameraMode=CAM_MENUAL;
			HarpLog.info("Camera mode change : "+cameraMode);
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("playerMoved")) {
			int x=Integer.parseInt(msgs[1]);
			int y=Integer.parseInt(msgs[2]);
			if(autoTargetingCheck(x,y)==true)
				setCameraPoint(x,y,true);
			return 1;
		}
		else if(msgs[0].equals("scroll")) {
			cameraMode=CAM_MENUAL;
			cameraX+=Float.parseFloat(msgs[1]);
			cameraY+=Float.parseFloat(msgs[2]);
			regulateCamera();
			return 1;
		}
		else if(msgs[0].equals("focus")) {
			setCameraPoint(Integer.parseInt(msgs[1]),Integer.parseInt(msgs[2]),true);
			return 1;
		}
		else return 0;
	}

	@Override
	public Object get(String name) {
		if(name.equals("cameraX"))
			return cameraX;
		else if(name.equals("cameraY"))
			return cameraY;
		else return null;
	}
	
	// private

	private void setCameraPoint(float x, float y, boolean animate) {
		if(animate) {
			cameraMode=CAM_TARGET;
			targetX=x-Screen.SCREEN_X/2;
			targetY=y-Screen.SCREEN_Y/2;
			targetX=Func.limit(targetX, 0, cameraWidth-Screen.SCREEN_X);
			targetY=Func.limit(targetY, 0,  cameraHeight-Screen.SCREEN_Y);
			HarpLog.info("Camera mode change : "+cameraMode);
		} else {
			cameraX=x-Screen.SCREEN_X/2;
			cameraY=y-Screen.SCREEN_Y/2;
			regulateCamera();
		}
	}
	
	private void regulateCamera() { 
		cameraX=Func.limit(cameraX, 0, cameraWidth-Screen.SCREEN_X);
		cameraY=Func.limit(cameraY, 0,  cameraHeight-Screen.SCREEN_Y);
	}
	
	private boolean autoTargetingCheck(float x, float y) {
		return (x < cameraX+Screen.TILE_LENGTH || x > cameraX+Screen.SCREEN_X-Screen.TILE_LENGTH ||
				y < cameraY+Screen.TILE_LENGTH || y > cameraY+Screen.SCREEN_Y-Screen.TILE_LENGTH);			
	}
	
}
