package com.yad.harpseal.gameobj.game.map;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class PlayerSeal extends GameObject {

	private int mapX,mapY;

	private int moveDirection;
	private int moveTime;
	private final static int MOVE_VALUE=400; 

	public PlayerSeal(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		this.moveDirection=Direction.NONE;
		this.moveTime=0;
	}

	@Override
	public void playGame(int ms) {
		
		// movement
		if(moveDirection != Direction.NONE) {
			moveTime+=ms;
			if(moveTime>MOVE_VALUE) {
				moveDirection=Direction.NONE;
				moveTime=0;
				con.send("moved/PlayerSeal/"+mapX+"/"+mapY);
			}
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_CHARACTER) return;		
		p.reset();
		float baseX=Screen.FIELD_MARGIN_LEFT+mapX*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+mapY*Screen.TILE_LENGTH;
		float tLen=Screen.TILE_LENGTH;
		
		// movement
		switch(moveDirection) {
		case Direction.UP: baseY-=tLen*moveTime/MOVE_VALUE-tLen; break;
		case Direction.LEFT: baseX-=tLen*moveTime/MOVE_VALUE-tLen; break;
		case Direction.RIGHT: baseX+=tLen*moveTime/MOVE_VALUE-tLen; break;
		case Direction.DOWN: baseY+=tLen*moveTime/MOVE_VALUE-tLen; break;
		}
		
		// draw seal
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFDDDDEE);
		c.drawCircle(baseX+tLen/2,baseY+tLen/2,tLen/3,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0xFFAAAABB);
		c.drawCircle(baseX+tLen/2,baseY+tLen/2,tLen/3,p);
		p.setColor(0xFF111122);
		c.drawLine(baseX+tLen*0.35f,baseY+tLen*0.35f,baseX+tLen*0.35f,baseY+tLen*0.45f,p);
		c.drawLine(baseX+tLen*0.65f,baseY+tLen*0.35f,baseX+tLen*0.65f,baseY+tLen*0.45f,p);
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		// movement
		if(msgs[0].equals("move") && moveDirection==Direction.NONE) {
			moveDirection=Integer.parseInt(msgs[1]);
			moveTime=0;
			switch(moveDirection) {
			case Direction.UP: mapY-=1; break;
			case Direction.LEFT: mapX-=1; break;
			case Direction.RIGHT: mapX+=1; break;
			case Direction.DOWN: mapY+=1; break;
			}
		}
		return 0;
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("moveDirection")) return moveDirection;
		else if(name.equals("moveTime")) return moveTime;
		else if(name.equals("moveValue")) return MOVE_VALUE;
		else {
			HarpLog.error("PlayerSeal received invalid name of get() : "+name);
			return null;
		}
	}

}
