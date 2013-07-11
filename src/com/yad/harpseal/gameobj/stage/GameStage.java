package com.yad.harpseal.gameobj.stage;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.window.Joystick;
import com.yad.harpseal.gameobj.window.ScoreCounter;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Func;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GameStage extends GameObject {
	
	// map
	private int stageGroup;
	private int stageNumber;
	private GameMap map;

	// camera
	private float cameraX,cameraY;
	private float cameraWidth,cameraHeight;
	private int cameraMode;
	private float targetX,targetY;
	private final static int CAM_MENUAL=1;
	private final static int CAM_TARGET=2;
	private final static float TARGET_MOTION_MIN=1f;
	private final static float TARGET_MOTION_SPD=0.075f;

	// user interfaces
	private Joystick stick;
	private ScoreCounter counter;
	
	// stage action
	private int actionName;
	private int actionTime;
	private final static int ACT_STARTING_FADEOUT=1;
	private final static int ACT_PLAYING=2;
	
	// score data
	private int stepCount;
	private int fishCount;
	private final static int FISH_MAX=3;
	
	
	//// public method
	
	
	public GameStage(Communicable con, int stageGroup, int stageNumber) {
		super(con);
		
		// map
		this.stageGroup=stageGroup;
		this.stageNumber=stageNumber;
		map=new GameMap(this,stageGroup,stageNumber);
		////
		
		// user interface
		stick=new Joystick(this);
		counter=new ScoreCounter(this);
		
		// camera point init
		cameraX=0;
		cameraY=0;
		cameraWidth=(Integer)map.get("width");
		cameraHeight=(Integer)map.get("height");
		cameraMode=CAM_MENUAL;
		targetX=0;
		targetY=0;
		setCameraPoint( (Integer)map.get("playerX"), (Integer)map.get("playerY") );
		
		// stage init
		actionName=ACT_STARTING_FADEOUT;
		actionTime=0;
		
		// score data init
		stepCount=0;
		fishCount=0;
	}

	@Override
	public void playGame(int ms) {

		// objects
		map.playGame(ms);
		stick.playGame(ms);
		counter.playGame(ms);
		
		// camera
		animateCamera(ms);
		
		// controller
		actionTime+=ms;
		if(actionName==ACT_STARTING_FADEOUT && actionTime>=500) {
			actionName=ACT_PLAYING;
			actionTime=0;
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {

		// camera
		if(layer==Layer.LAYER_FIELD) ev.setCamera(cameraX, cameraY);
		else if(layer==Layer.LAYER_WINDOW) ev.setCamera(0, 0);
		
		// objects
		map.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		stick.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		counter.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		
		// camera
		if(layer==Layer.LAYER_FIELD) c.translate(-cameraX,-cameraY);
		else if(layer==Layer.LAYER_WINDOW) c.translate(cameraX,cameraY);
		
		// objects
		map.drawScreen(c, p, layer);
		stick.drawScreen(c, p, layer);
		counter.drawScreen(c, p, layer);
		
		// controller
		if(layer==Layer.LAYER_SCREEN && actionName==ACT_STARTING_FADEOUT) {
			p.reset();
			int alpha=Math.round( (float)(500-actionTime)/500*0xFF ) << 24;
			p.setColor(alpha | 0xFFFFFF);
			c.drawRect(c.getClipBounds(), p);
		}
	}

	@Override
	public void restoreData() {
		map.restoreData();
		stick.restoreData();
		counter.restoreData();
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("stickAction")) {
			map.send("movePlayer/"+msgs[1]);			
			if(autoCameraRegulateCheck( (Integer)map.get("playerX"), (Integer)map.get("playerY") ))
				setCameraPoint( (Integer)map.get("playerX"), (Integer)map.get("playerY") );
			return 1;
		}
		else if(msgs[0].equals("playerStepped")) {
			stepCount+=1;
			return 1;
		}
		else if(msgs[0].equals("fishEaten")) {
			fishCount+=1;
			if(fishCount>FISH_MAX)
				HarpLog.danger("FishCount value is more than FISH_MAX value!");
			return 1;
		}
		else if(msgs[0].equals("playerReached")) {
			// TODO game end processing
			return 1;
		}
		else if(msgs[0].equals("scroll")) {
			if(cameraMode==CAM_MENUAL) {
				cameraX+=Float.parseFloat(msgs[1]);
				cameraY+=Float.parseFloat(msgs[2]);
				regulateCamera();
				return 1;
			} else return 0;
		}
		else return con.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("stepCount"))
			return stepCount;
		else if(name.equals("fishCount"))
			return fishCount;
		else if(name.equals("fishMax"))
			return FISH_MAX;
		else
			return con.get(name);
	}
	
	
	//// private method (game play)
	
	
	
	private void setCameraPoint(float x,float y) {
		cameraMode=CAM_TARGET;
		targetX=x-Screen.SCREEN_X/2;
		targetY=y-Screen.SCREEN_Y/2;
		targetX=Func.limit(targetX, 0, cameraWidth-Screen.SCREEN_X);
		targetY=Func.limit(targetY, 0,  cameraHeight-Screen.SCREEN_Y);
		HarpLog.info("Camera mode change : "+cameraMode);
		regulateCamera();
	}
	
	private void regulateCamera() { 
		cameraX=Func.limit(cameraX, 0, cameraWidth-Screen.SCREEN_X);
		cameraY=Func.limit(cameraY, 0,  cameraHeight-Screen.SCREEN_Y);
	}
	
	// In fact, something is wrong.. -_-
	private void animateCamera(int ms) {
		
		switch(cameraMode) {
		
		case CAM_MENUAL:
			regulateCamera();
			break;
		
		case CAM_TARGET:
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
			break;
		
		}		
	}
	
	private boolean autoCameraRegulateCheck(float x, float y) {
		return (x < cameraX+Screen.TILE_LENGTH || x > cameraX+Screen.SCREEN_X-Screen.TILE_LENGTH ||
				y < cameraY+Screen.TILE_LENGTH || y > cameraY+Screen.SCREEN_Y-Screen.TILE_LENGTH);
			
	}
	
	
	
	
	
	
}
