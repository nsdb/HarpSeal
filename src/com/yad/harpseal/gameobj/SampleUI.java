package com.yad.harpseal.gameobj;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class SampleUI extends GameObject {

	int chunkNum;
	int type;
	float x,y;

	public SampleUI(Communicable con) {
		super(con);
		chunkNum=con.send("loadChunk/"+R.raw.sample_chunk);
		type=-1;
		x=-1;
		y=-1;
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev,int layer) {
		type=ev.getType();
		x=ev.getX();
		y=ev.getY();
		if(ev.getType()==HarpEvent.MOTION_CLICK) {
			con.send("playChunk/"+chunkNum);
		}		
		ev.process();
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;
		int screenY=(Integer)con.get("screenY");
		p.reset();

		// point box
		p.setColor(0xFFFFFF00);
		c.drawRect(x-5,y-5,x+5,y+5,p);

		// point text
		p.setColor(0xFFFFFFFF);
		p.setTextSize(30);
		p.setTextAlign(Align.LEFT);
		switch(type) {
		case HarpEvent.MOTION_DOWN: c.drawText("Action Down",15,screenY-45,p); break;
		case HarpEvent.MOTION_UP: c.drawText("Action Up",15,screenY-45,p); break;
		case HarpEvent.MOTION_DRAG: c.drawText("Action Drag",15,screenY-45,p); break;
		case HarpEvent.MOTION_CLICK: c.drawText("Action Click",15,screenY-45,p); break;
		case HarpEvent.MOTION_LONGCLICK: c.drawText("Action LongClick",15,screenY-45,p); break;
		default: c.drawText("Action Unknown",15,screenY-45,p); break;

		}
		c.drawText("X : "+x+" / Y : "+y,15,screenY-15,p);
	}

	@Override
	public void restoreData() {
		con.send("unloadChunk/"+chunkNum);
	}

	@Override
	public int send(String msg) {
		return 0;
	}

	@Override
	public Object get(String name) {
		return null;
	}


}
