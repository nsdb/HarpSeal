package com.yad.harpseal.gameobj;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class SampleBox extends GameObject {

	int time;
	int squareColor;
	Rect squareRect;

	public SampleBox(Communicable con) {
		super(con);
		squareRect=new Rect();
	}

	@Override
	public void playGame(int ms) {

		time+=ms;
		if(time>=1000) {

			int screenX=(Integer)con.get("screenX");
			int screenY=(Integer)con.get("screenY");

			time-=1000;
			squareColor=0xFF000000 | (int)(Math.random()*0xFFFFFF);
			squareRect.left=(int)(Math.random()*(screenX-100));
			squareRect.top=(int)(Math.random()*(screenY-100));
			squareRect.right=squareRect.left+100;
			squareRect.bottom=squareRect.top+100;
		}
		
	}

	@Override
	public void receiveMotion(HarpEvent ev,int layer) {
	}
	
	@Override
	public void drawScreen(Canvas c,Paint p,int layer) {
		if(layer != Layer.LAYER_CHARACTER) return;
		p.setColor(squareColor);
		c.drawRect(squareRect,p);
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		return -1;
	}

	@Override
	public Object get(String name) {
		return null;
	}

}
