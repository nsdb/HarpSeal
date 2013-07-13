package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GameStage extends GameObject {
	
	// stage status
	private int stageGroup;
	private int stageNumber;
	private int stepCount;
	private int fishCount;
	private final static int FISH_MAX=3;

	// game objects
	private GameMap map;
	private GameCamera camera;
	private Joystick stick;
	private ScoreCounter counter;
	
	// stage action
	private int actionName;
	private int actionTime;
	private final static int ACT_STARTING_FADEOUT=1;
	private final static int ACT_PLAYING=2;
	private final static int ACT_ENDING_FADEIN=3;
	

	public GameStage(Communicable con, int stageGroup, int stageNumber) {
		super(con);
		
		// stage status
		this.stageGroup=stageGroup;
		this.stageNumber=stageNumber;
		this.stepCount=0;
		this.fishCount=0;
		////
		
		// game objects
		map=new GameMap(this,stageGroup,stageNumber);
		camera=new GameCamera( this, (Integer)map.get("width"), (Integer)map.get("height"),
				(Integer)map.get("playerX"), (Integer)map.get("playerY") );
		stick=new Joystick(this);
		counter=new ScoreCounter(this);
		
		// stage action
		actionName=ACT_STARTING_FADEOUT;
		actionTime=0;
	}

	@Override
	public void playGame(int ms) {

		// objects
		map.playGame(ms);
		camera.playGame(ms);
		stick.playGame(ms);
		counter.playGame(ms);
		
		// controller
		actionTime+=ms;
		switch(actionName) {
		case ACT_STARTING_FADEOUT:
			if(actionTime>=500) changeAction(ACT_PLAYING);
			break;
		case ACT_ENDING_FADEIN:
			if(actionTime>=500) con.send("gameEnd");
			break;
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {

		// camera
		if(layer==Layer.LAYER_FIELD) ev.setCamera( (Float)camera.get("cameraX"), (Float)camera.get("cameraY") );
		else if(layer==Layer.LAYER_WINDOW) ev.setCamera(0, 0);
		
		// objects
		map.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		camera.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		stick.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		counter.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		
		// camera
		if(layer==Layer.LAYER_FIELD) c.translate( -(Float)camera.get("cameraX"), -(Float)camera.get("cameraY") );
		else if(layer==Layer.LAYER_WINDOW) c.translate( (Float)camera.get("cameraX"), (Float)camera.get("cameraY") );
		
		// objects
		map.drawScreen(c, p, layer);
		camera.drawScreen(c, p, layer);
		stick.drawScreen(c, p, layer);
		counter.drawScreen(c, p, layer);
		
		// controller
		p.reset();
		if(layer==Layer.LAYER_SCREEN && actionName==ACT_STARTING_FADEOUT) {
			int alpha=Math.round( (float)(500-actionTime)/500*0xFF ) << 24;
			p.setColor(alpha | 0xFFFFFF);
			c.drawRect(c.getClipBounds(), p);
		} else if(layer==Layer.LAYER_SCREEN && actionName==ACT_ENDING_FADEIN) {
			int alpha=Math.round( (float)actionTime/500*0xFF ) << 24;
			p.setColor(alpha | 0x000000);
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
			if(map.send("movePlayer/"+msgs[1])==1)
				camera.send("playerMoved/"+(Integer)map.get("playerX")+"/"+(Integer)map.get("playerY"));
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
			changeAction(ACT_ENDING_FADEIN);
			return 1;
		}
		else if(msgs[0].equals("scroll")) {
			return camera.send(msg);
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
	
	
	//// private
	
	private void changeAction(int actNa) {
		HarpLog.info("Stage action changed : "+actionName+" -> "+actNa);
		actionName=actNa;
		actionTime=0;
	}
	
	
	
}
