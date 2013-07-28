package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.game.map.GameMap;
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
	private TheArcticOcean field;
	private GameCamera camera;
	private Joystick stick;
	private ScoreCounter counter;
	private PauseBtn pb;
	private PauseWindow pw;
	private SuccessWindow sw;
	
	// stage action
	private int actionName;
	private int actionTime;
	private boolean playable;
	private final static int ACT_STARTING_FADEOUT=1;
	private final static int ACT_PLAYING=2;
	private final static int ACT_PAUSED=3;
	private final static int ACT_RESTARTING_FADEIN=4;
	private final static int ACT_RESTARTING_FADEOUT=5;
	private final static int ACT_SUCCESS=6;
	private final static int ACT_NEXT_FADEIN=7;
	private final static int ACT_NEXT_FADEOUT=8;
	private final static int ACT_ENDING_FADEIN=9;
	private final static int FADE_TIME=300;
	

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
		field=new TheArcticOcean(this, (Integer)map.get("width"), (Integer)map.get("height"));
		camera=new GameCamera( this, (Integer)map.get("width"), (Integer)map.get("height"),
				(Integer)map.get("playerX"), (Integer)map.get("playerY") );
		stick=new Joystick(this);
		counter=new ScoreCounter(this);
		pb=new PauseBtn(this);
		pw=new PauseWindow(this);
		sw=new SuccessWindow(this);
		
		// stage action
		changeAction(ACT_STARTING_FADEOUT);
	}

	@Override
	public void playGame(int ms) {

		// objects
		map.playGame(ms);
		field.playGame(ms);
		camera.playGame(ms);
		stick.playGame(ms);
		counter.playGame(ms);
		pb.playGame(ms);
		pw.playGame(ms);
		sw.playGame(ms);
		
		// controller
		actionTime+=ms;
		switch(actionName) {
		case ACT_STARTING_FADEOUT:
			if(actionTime>=FADE_TIME) changeAction(ACT_PLAYING);
			break;
		case ACT_ENDING_FADEIN:
			if(actionTime>=FADE_TIME) con.send("gameEnded");
			break;
		case ACT_RESTARTING_FADEIN:
			if(actionTime>=FADE_TIME) restart();
			break;
		case ACT_NEXT_FADEIN:
			if(actionTime>=FADE_TIME) toNext();
			break;
		case ACT_RESTARTING_FADEOUT: case ACT_NEXT_FADEOUT:
			if(actionTime>=FADE_TIME) changeAction(ACT_PLAYING);
			break;
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {

		// camera
		if(layer==Layer.LAYER_FIELD) ev.setCamera( (Float)camera.get("cameraX"), (Float)camera.get("cameraY") );
		else if(layer==Layer.LAYER_WINDOW) ev.setCamera(0, 0);
		
		// objects
		if(playable) {
			map.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
			field.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
			camera.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
			stick.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
			counter.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
			pb.receiveMotion(ev, layer);			
			if(ev.isProcessed()) return;
		}
		pw.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		sw.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		
		// camera
		if(layer==Layer.LAYER_FIELD) c.translate( -(Float)camera.get("cameraX"), -(Float)camera.get("cameraY") );
		else if(layer==Layer.LAYER_WINDOW) c.translate( (Float)camera.get("cameraX"), (Float)camera.get("cameraY") );
		
		// objects
		map.drawScreen(c, p, layer);
		field.drawScreen(c, p, layer);
		camera.drawScreen(c, p, layer);
		stick.drawScreen(c, p, layer);
		counter.drawScreen(c, p, layer);
		pb.drawScreen(c, p, layer);
		pw.drawScreen(c, p, layer);
		sw.drawScreen(c, p, layer);
		
		// screen effect
		p.reset();
		int alpha;
		if(layer != Layer.LAYER_SCREEN) return;
		switch(actionName) {
		case ACT_STARTING_FADEOUT: case ACT_RESTARTING_FADEOUT: case ACT_NEXT_FADEOUT:
			alpha=Math.round( (float)(FADE_TIME-actionTime)/FADE_TIME*0xFF ) << 24;
			p.setColor(alpha | 0xFFFFFF);
			c.drawRect(c.getClipBounds(), p);
			break;
		case ACT_ENDING_FADEIN:
			alpha=Math.round( (float)actionTime/FADE_TIME*0xFF ) << 24;
			p.setColor(alpha | 0x000000);
			c.drawRect(c.getClipBounds(), p);
			break;
		case ACT_RESTARTING_FADEIN: case ACT_NEXT_FADEIN:
			alpha=Math.round( (float)actionTime/FADE_TIME*0xFF ) << 24;
			p.setColor(alpha | 0xFFFFFF);
			c.drawRect(c.getClipBounds(), p);
			break;
		}
	}

	@Override
	public void restoreData() {
		map.restoreData();
		field.restoreData();
		stick.restoreData();
		counter.restoreData();
		pb.restoreData();
		pw.restoreData();
		sw.restoreData();
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
			changeAction(ACT_SUCCESS);
			sw.send("show");
			return 1;
		}
		else if(msgs[0].equals("scroll")) {
			return camera.send(msg);
		}
		else if(msgs[0].equals("gamePause")) {
			changeAction(ACT_PAUSED);
			pw.send("show");
			return 1;
		}
		else if(msgs[0].equals("gameResume")) {
			changeAction(ACT_PLAYING);
			pw.send("hide");
			return 1;
		}
		else if(msgs[0].equals("gameRestart")) {
			changeAction(ACT_RESTARTING_FADEIN);
			return 1;
		}
		else if(msgs[0].equals("gameToNext")) {
			changeAction(ACT_NEXT_FADEIN);
			return 1;
		}
		else if(msgs[0].equals("gameEnd")) {
			changeAction(ACT_ENDING_FADEIN);
			return 1;
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
		
		// playable check
		switch(actionName) {
		case ACT_STARTING_FADEOUT: case ACT_PLAYING: case ACT_RESTARTING_FADEOUT: case ACT_NEXT_FADEOUT:
			playable=true;
			break;
		case ACT_PAUSED: case ACT_RESTARTING_FADEIN: case ACT_ENDING_FADEIN: case ACT_SUCCESS: case ACT_NEXT_FADEIN:
			playable=false;
			stick.send("reset");
			break;
		default:
			HarpLog.error("Invalid action name : "+actionName);
			playable=true;
			break;
		}
	}
	
	private void restart() {
		map.send("reset");
		pw.send("reset");
		sw.send("reset");
		camera.send("focus/"+(Integer)map.get("playerX")+"/"+(Integer)map.get("playerY"));
		stepCount=0;
		fishCount=0;
		changeAction(ACT_RESTARTING_FADEOUT);	
	}
	
	private void toNext() {
		HarpLog.info("Next stage Loading...");
		stageNumber+=1;
		map.send("changeToNext");
		field.send("update/"+(Integer)map.get("width")+"/"+(Integer)map.get("height"));
		camera.send("update/"+(Integer)map.get("width")+"/"+(Integer)map.get("height"));

		pw.send("reset");
		sw.send("reset");
		camera.send("focus/"+(Integer)map.get("playerX")+"/"+(Integer)map.get("playerY"));
		stepCount=0;
		fishCount=0;
		changeAction(ACT_NEXT_FADEOUT);		
	}
	
	
	
}
