package com.yad.harpseal.gameobj.character;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GoalFlag extends GameObject {

	private int mapX,mapY;

	public GoalFlag(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
	}

	@Override
	public void playGame(int ms) {
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
		
		// draw seal
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFF8888FF);
		c.drawRect(baseX+tLen*0.4f,baseY+tLen*0.3f,baseX+tLen*0.8f,baseY+tLen*0.6f,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0xFF333355);
		c.drawRect(baseX+tLen*0.4f,baseY+tLen*0.3f,baseX+tLen*0.8f,baseY+tLen*0.6f,p);
		c.drawLine(baseX+tLen*0.4f,baseY+tLen*0.25f,baseX+tLen*0.4f,baseY+tLen*0.8f,p);
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		return 0;
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else {
			HarpLog.error("GoalFlag received invalid name of get() : "+name);
			return null;
		}
	}

}
