package com.yad.harpseal.gameobj;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.util.Communicable;

public class SampleUI extends GameObject {

	int chunkNum;
	float touchX,touchY;

	public SampleUI(Communicable con) {
		super(con);
		chunkNum=con.send("loadChunk/"+R.raw.sample_chunk);
		touchX=-1;
		touchY=-1;
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(int type, float x, float y) {
		if(type==MotionEvent.ACTION_DOWN) {
			touchX=x;
			touchY=y;
			con.send("playChunk/"+chunkNum);
		}		
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;

		int screenY=(Integer)con.get("screenY");

		// point box
		p.setColor(0xFFFFFF00);
		c.drawRect(touchX-5,touchY-5,touchX+5,touchY+5,p);

		// point text
		p.setColor(0xFF000000);
		p.setTextSize(30);
		p.setTextAlign(Align.LEFT);
		c.drawText("TouchX : "+touchX+" / TouchY : "+touchY,15,screenY-15,p);
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
