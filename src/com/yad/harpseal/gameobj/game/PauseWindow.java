package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Func;
import com.yad.harpseal.util.HarpEvent;

public class PauseWindow extends GameObject {
	
	private float cx,cy;
	private boolean show;
	private boolean restarting;
	
	private final static float WINDOW_WIDTH=500;
	private final static float WINDOW_HEIGHT=300;
	private final static float TXT_RX=0;
	private final static float TXT_RY=-60;
	private final static float TXT_SIZE=50;
	private final static float BTN_RX_INTERVAL=150;
	private final static float BTN_RY=40;
	private final static float BTN_RADIUS=50;
	private final static float BTN_INNERTXT_SIZE=20;

	public PauseWindow(Communicable con) {
		super(con);
		show=false;
		restarting=false;
		cx=Screen.SCREEN_X/2;
		cy=Screen.SCREEN_Y/2;
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(!show) return;
		if(layer != Layer.LAYER_WINDOW) return;
		if(restarting) return;
		
		if(ev.getType()==HarpEvent.MOTION_CLICK) {
			
			float x=cx-BTN_RX_INTERVAL;
			float y=cy+BTN_RY;
			if(Func.distan(x, y, ev.getX(), ev.getY())<=BTN_RADIUS) {
				con.send("gameRestart");
				restarting=true;
				ev.process();
			}
			x+=BTN_RX_INTERVAL;
			if(Func.distan(x, y, ev.getX(), ev.getY())<=BTN_RADIUS) {
				con.send("gameResume");
				ev.process();
			}
			x+=BTN_RX_INTERVAL;
			if(Func.distan(x, y, ev.getX(), ev.getY())<=BTN_RADIUS) {
				// TODO option
				ev.process();
			}
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(!show) return;
		if(layer != Layer.LAYER_WINDOW) return;
		p.reset();
		
		// screen
		p.setStyle(Paint.Style.FILL);
		p.setColor(0x80000000);
		c.drawRect(c.getClipBounds(), p);
		
		// window rect
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFAAAAFF);
		c.drawRect(cx-WINDOW_WIDTH/2,cy-WINDOW_HEIGHT/2,cx+WINDOW_WIDTH/2,cy+WINDOW_HEIGHT/2,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0xFF000000);
		c.drawRect(cx-WINDOW_WIDTH/2,cy-WINDOW_HEIGHT/2,cx+WINDOW_WIDTH/2,cy+WINDOW_HEIGHT/2,p);
		
		// pause text
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFDDDDFF);
		p.setTextAlign(Align.CENTER);
		p.setTextSize(TXT_SIZE);
		c.drawText("일시정지중...", cx+TXT_RX, cy+TXT_RY, p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(2);
		p.setColor(0xFF000000);
		c.drawText("일시정지중...", cx+TXT_RX, cy+TXT_RY, p);
		
		// buttons
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFDDDDFF);
		c.drawCircle(cx-BTN_RX_INTERVAL, cy+BTN_RY, BTN_RADIUS, p);
		c.drawCircle(cx, cy+BTN_RY, BTN_RADIUS, p);
		c.drawCircle(cx+BTN_RX_INTERVAL, cy+BTN_RY, BTN_RADIUS, p);
		p.setColor(0xFF000000);
		p.setTextAlign(Align.CENTER);
		p.setTextSize(BTN_INNERTXT_SIZE);
		c.drawText("재시작",cx-BTN_RX_INTERVAL, cy+BTN_RY+BTN_INNERTXT_SIZE/3, p);
		c.drawText("진행",cx, cy+BTN_RY+BTN_INNERTXT_SIZE/3, p);
		c.drawText("옵션",cx+BTN_RX_INTERVAL, cy+BTN_RY+BTN_INNERTXT_SIZE/3, p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0xFF000000);
		c.drawCircle(cx-BTN_RX_INTERVAL, cy+BTN_RY, BTN_RADIUS, p);
		c.drawCircle(cx, cy+BTN_RY, BTN_RADIUS, p);
		c.drawCircle(cx+BTN_RX_INTERVAL, cy+BTN_RY, BTN_RADIUS, p);
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("show")) {
			show=true;
			return 1;
		}
		else if(msgs[0].equals("hide")) {
			show=false;
			return 1;
		}
		else if(msgs[0].equals("reset")) {
			show=false;
			restarting=false;
			return 1;
		}
		else return 0;
	}

	@Override
	public Object get(String name) {
		return null;
	}

}
