package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class TheArcticOcean extends GameObject {
	
	private int width,height;

	private boolean scrolling;
	private float scrollX,scrollY;

	public TheArcticOcean(Communicable con, int width, int height) {
		super(con);
		this.width=width;
		this.height=height;
		this.scrolling=false;
		con.send("playSound/"+R.raw.sample_bgm);
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(layer != Layer.LAYER_FIELD) return;
		
		switch(ev.getType()) {
		case HarpEvent.MOTION_DOWN:
			scrolling=true;
			scrollX=ev.getOriginalX();
			scrollY=ev.getOriginalY();
			ev.process();
			break;
		case HarpEvent.MOTION_DRAG:
			if(scrolling) {
				con.send( "scroll/"+(scrollX-ev.getOriginalX())+"/"+(scrollY-ev.getOriginalY()) );
				scrollX=ev.getOriginalX();
				scrollY=ev.getOriginalY();
				ev.process();
			}
			break;
		case HarpEvent.MOTION_UP:
			scrolling=false;
			ev.process();
			break;
			
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_FIELD) return;
		p.reset();

		p.setColor(0xFF9999FF);
		c.drawRect(0, 0, width, height/3, p);
		p.setColor(0xFF6666FF);
		c.drawRect(0, height/3, width, height/3*2, p);
		p.setColor(0xFF3333FF);
		c.drawRect(0, height/3*2, width, height, p);
	}

	@Override
	public void restoreData() {
		con.send("resetSound");
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("update")) {
			width=Integer.parseInt(msgs[1]);
			height=Integer.parseInt(msgs[2]);
			return 1;
		}
		else return 0;
	}

	@Override
	public Object get(String name) {
		return null;
	}

}
