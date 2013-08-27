package com.yad.harpseal.game.map;

import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.Func;
import com.nsdb.engine.util.GameLog;
import com.yad.harpseal.constant.Screen;

/**
 * 게임 맵에서 사용하는 카메라입니다. 모든 카메라에 관련된 동작을 담당합니다.<br>
 * 스크롤 가능 범위는 게임 맵에서 정해줍니다.<br>
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class GameCamera extends GameObject {

	private float cameraX,cameraY;
	private float cameraWidth,cameraHeight;

	private int cameraMode;
	private float targetX,targetY;
	
	private final static int CAM_MENUAL=1;
	private final static int CAM_TARGET=2;
	private final static float TARGET_MOTION_MIN=1f;
	private final static float TARGET_MOTION_SPD=0.075f;

	public GameCamera(Communicable con, float width, float height, float x, float y) {
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
		super.playGame(ms);
		
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
			GameLog.info("Camera mode change : "+cameraMode);
		}
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("playerMoved")) {
			float x=Float.parseFloat(msgs[1]);
			float y=Float.parseFloat(msgs[2]);
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
			setCameraPoint(Float.parseFloat(msgs[1]),Float.parseFloat(msgs[2]),false);
			return 1;
		}
		else if(msgs[0].equals("update")) {
			cameraWidth=Float.parseFloat(msgs[1]);
			cameraHeight=Float.parseFloat(msgs[2]);
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("cameraX"))
			return cameraX;
		else if(name.equals("cameraY"))
			return cameraY;
		else return super.get(name);
	}
	
	// private

	private void setCameraPoint(float x, float y, boolean animate) {
		if(animate) {
			cameraMode=CAM_TARGET;
			targetX=x-Screen.WIDTH/2;
			targetY=y-Screen.HEIGHT/2;
			targetX=Func.limit(targetX, 0, cameraWidth-Screen.WIDTH);
			targetY=Func.limit(targetY, 0,  cameraHeight-Screen.HEIGHT);
			GameLog.info("Camera mode change : "+cameraMode);
		} else {
			cameraX=x-Screen.WIDTH/2;
			cameraY=y-Screen.HEIGHT/2;
			regulateCamera();
		}
	}
	
	private void regulateCamera() { 
		cameraX=Func.limit(cameraX, 0, cameraWidth-Screen.WIDTH);
		cameraY=Func.limit(cameraY, 0,  cameraHeight-Screen.HEIGHT);
	}
	
	private boolean autoTargetingCheck(float x, float y) {
		return (x < cameraX+Screen.TILE_LENGTH || x > cameraX+Screen.WIDTH-Screen.TILE_LENGTH ||
				y < cameraY+Screen.TILE_LENGTH || y > cameraY+Screen.HEIGHT-Screen.TILE_LENGTH);			
	}
	
}
