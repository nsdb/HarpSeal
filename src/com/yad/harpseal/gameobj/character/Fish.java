package com.yad.harpseal.gameobj.character;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class Fish extends GameObject {

	private int mapX,mapY;
	private Drawable image;

	public Fish(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		Resources res=(Resources)con.get("resources");
		image=res.getDrawable(R.drawable.sample_fish);
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
		
		int tLen=Screen.TILE_LENGTH;
		int cX=Screen.FIELD_MARGIN_LEFT+mapX*tLen+tLen/2;
		int cY=Screen.FIELD_MARGIN_TOP+mapY*tLen+tLen/2;
		int rX=tLen/3;
		int rY=image.getIntrinsicHeight()*tLen/image.getIntrinsicWidth()/3;
		
		image.setBounds(cX-rX,cY-rY,cX+rX,cY+rY);
		image.draw(c);
		
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("eaten")) {
			return 1;
		}
		else return 0;
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else {
			HarpLog.error("Fish received invalid name of get() : "+name);
			return null;
		}
	}

}
