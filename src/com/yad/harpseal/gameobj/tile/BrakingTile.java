package com.yad.harpseal.gameobj.tile;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.util.Communicable;

public class BrakingTile extends NormalTile {
	
	private boolean braking;
	private int brakingTime;
	private final static int TIME_MAX=500;

	public BrakingTile(Communicable con,int mapX,int mapY) {
		super(con,mapX,mapY);
		this.braking=false;
		this.brakingTime=0;
	}

	@Override
	public void playGame(int ms) {
		if(braking) {
			brakingTime+=ms;
			if(brakingTime>TIME_MAX) {
				brakingTime=TIME_MAX;
				con.send("broken/"+mapX+"/"+mapY);
			}
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_TILE) return;		
		p.reset();
		int baseX=Screen.FIELD_MARGIN_LEFT+mapX*Screen.TILE_LENGTH;
		int baseY=Screen.FIELD_MARGIN_TOP+mapY*Screen.TILE_LENGTH;
		int tLen=Screen.TILE_LENGTH;
		int tHeight=Screen.TILE_HEIGHT;
		int alpha=Math.round( (float)(TIME_MAX-brakingTime)/TIME_MAX*0xFF ) << 24;
		
		// draw tile
		p.setStyle(Paint.Style.FILL);
		p.setColor( alpha | 0xEEEEFF);
		c.drawRect(baseX,baseY,baseX+tLen,baseY+tLen,p);
		p.setColor( alpha | 0xAAAABB);
		c.drawRect(baseX,baseY+tLen,baseX+tLen,baseY+tLen+tHeight,p);
		
		// crack
		p.setStyle(Paint.Style.STROKE);
		p.setColor( alpha | 0x8888AA);
		p.setStrokeWidth(2);
		c.drawRect(baseX+1, baseY+1, baseX+tLen-1, baseY+tLen-1, p);
		c.drawLine(baseX+tLen*0.3f, baseY, baseX+tLen*0.6f, baseY+tLen, p);
		c.drawLine(baseX+tLen*0.45f, baseY+tLen*0.5f, baseX+tLen, baseY+tLen*0.8f, p);
		c.drawLine(baseX+tLen*0.45f, baseY+tLen*0.5f, baseX+tLen*0.8f, baseY+tLen, p);
		c.drawLine(baseX+tLen*0.8f, baseY, baseX+tLen, baseY+tLen*0.2f, p);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("break")) {
			if(braking==false) {
				braking=true;
				brakingTime=0;
			}
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("braking")) return braking;
		else return super.get(name);
	}

}
