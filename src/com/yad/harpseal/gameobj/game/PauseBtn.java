package com.yad.harpseal.gameobj.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Func;
import com.yad.harpseal.util.HarpEvent;

public class PauseBtn extends GameObject {
	
	private float btnX,btnY;
	
	private final float BTN_MARGIN=15;
	private final float BTN_RADIUS=30;
	private final float RANGE_ACTIVE=50;

	public PauseBtn(Communicable con) {
		super(con);
		btnX=Screen.SCREEN_X-BTN_MARGIN-BTN_RADIUS;
		btnY=BTN_MARGIN+BTN_RADIUS;
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;
		if(ev.getType() != HarpEvent.MOTION_CLICK) return;
		if(Func.distan(btnX, btnY, ev.getX(), ev.getY()) > RANGE_ACTIVE) return;
		con.send("paused");
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;
		p.reset();
		
		// circle
		p.setStyle(Paint.Style.FILL);
		p.setColor(0x20BBBBBB);
		c.drawCircle(btnX,btnY,BTN_RADIUS,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0x40000000);
		c.drawCircle(btnX,btnY,BTN_RADIUS,p);
		
		// rect 2
		p.setStyle(Paint.Style.FILL);
		p.setColor(0x20DDDDDD);
		c.drawRect(btnX-BTN_RADIUS*0.5f, btnY-BTN_RADIUS*0.5f, btnX-BTN_RADIUS*0.15f, btnY+BTN_RADIUS*0.5f, p);
		c.drawRect(btnX+BTN_RADIUS*0.15f, btnY-BTN_RADIUS*0.5f, btnX+BTN_RADIUS*0.5f, btnY+BTN_RADIUS*0.5f, p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0x40000000);
		c.drawRect(btnX-BTN_RADIUS*0.5f, btnY-BTN_RADIUS*0.5f, btnX-BTN_RADIUS*0.15f, btnY+BTN_RADIUS*0.5f, p);
		c.drawRect(btnX+BTN_RADIUS*0.15f, btnY-BTN_RADIUS*0.5f, btnX+BTN_RADIUS*0.5f, btnY+BTN_RADIUS*0.5f, p);
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
