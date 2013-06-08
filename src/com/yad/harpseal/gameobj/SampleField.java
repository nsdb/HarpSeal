package com.yad.harpseal.gameobj;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class SampleField extends GameObject {

	public SampleField(Communicable con) {
		super(con);
		
		con.send("playSound/"+R.raw.sample_bgm);
		
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev,int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_FIELD) return;
		int screenX=Screen.SCREEN_X;
		int screenY=Screen.SCREEN_Y;
		p.reset();

		// outline
		p.setColor(0xFFFFFFFF);
		c.drawRect(0,0,screenX,screenY,p);

		// background
		p.setColor(0xFF888888);
		c.drawRect(10,10,screenX-10,screenY-10,p);

		// center point
		p.setColor(0xFF000000);
		c.drawRect(screenX/2-10,screenY/2-10,screenX/2+10,screenY/2+10,p);

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
		return null;
	}

}
