package com.yad.harpseal.gameobj.tile;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class NormalTile extends GameObject {
	
	private int mapX,mapY;

	public NormalTile(Communicable con,int mapX,int mapY) {
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
		if(layer != Layer.LAYER_TILE) return;		
		p.reset();
		int baseX=Screen.FIELD_MARGIN_LEFT+mapX*Screen.TILE_LENGTH;
		int baseY=Screen.FIELD_MARGIN_TOP+mapY*Screen.TILE_LENGTH;
		int tLen=Screen.TILE_LENGTH;
		int tHeight=Screen.TILE_HEIGHT;
		
		// draw tile
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFEEEEFF);
		c.drawRect(baseX,baseY,baseX+tLen,baseY+tLen,p);
		p.setColor(0xFFAAAABB);
		c.drawRect(baseX,baseY+tLen,baseX+tLen,baseY+tLen+tHeight,p);
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
			HarpLog.error("NormalTile received invalid name of get() : "+name);
			return null;
		}
	}

}
