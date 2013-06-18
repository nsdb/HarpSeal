package com.yad.harpseal.gameobj.window;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.Func;

public class Joystick extends GameObject {
	
	private float padX,padY;
	private float stickX,stickY;
	private int activeDirection;
	private boolean grabbed;

	private final static int PAD_MARGIN=20;
	private final static int PAD_RADIUS=75;
	private final static int RANGE_ACTIVE=50;
	private final static int RANGE_MAX=60;
	private final static int STICK_RADIUS=50;
	
	public Joystick(Communicable con) {
		super(con);
		padX=Screen.SCREEN_X-PAD_RADIUS-PAD_MARGIN;
		padY=Screen.SCREEN_Y-PAD_RADIUS-PAD_MARGIN;
		stickX=0;
		stickY=0;
		activeDirection=Direction.NONE;
		grabbed=false;
	}

	@Override
	public void playGame(int ms) {
		if(activeDirection != Direction.NONE);
			con.send("stickAction/"+activeDirection);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;
		
		float distance=Func.distan(padX,padY,ev.getX(),ev.getY());
		// grab and stick point check
		switch(ev.getType()) {
		case HarpEvent.MOTION_DOWN:
			if(distance<=STICK_RADIUS) {
				grabbed=true;
				stickX=ev.getX()-padX;
				stickY=ev.getY()-padY;
				if(distance>RANGE_MAX) {
					stickX*=RANGE_MAX/distance;
					stickY*=RANGE_MAX/distance;
				}
				ev.process();
			}
			break;
		case HarpEvent.MOTION_DRAG:
			if(grabbed) {
				stickX=ev.getX()-padX;
				stickY=ev.getY()-padY;
				if(distance>RANGE_MAX) {
					stickX*=RANGE_MAX/distance;
					stickY*=RANGE_MAX/distance;
				}
				ev.process();
			}
			break;
		case HarpEvent.MOTION_UP:
			if(grabbed) {
				grabbed=false;
				stickX=0;
				stickY=0;
				ev.process();
			}
			break;
		}
		
		// stick direction check
		if(grabbed && distance>=RANGE_ACTIVE) {
			if(stickX>=Math.abs(stickY))
				activeDirection=Direction.RIGHT;
			else if(stickX<=stickY && stickX<=-stickY)
				activeDirection=Direction.LEFT;
			else if(Math.abs(stickX)<stickY)
				activeDirection=Direction.DOWN; 
			else
				activeDirection=Direction.UP;
		} else {
			activeDirection=Direction.NONE;
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {		
		if(layer != Layer.LAYER_WINDOW) return;
		p.reset();
		
		// pad circle
		p.setStyle(Paint.Style.FILL);
		p.setColor(0x20BBBBBB);
		c.drawCircle(padX,padY,PAD_RADIUS,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0x20DDDDDD);
		float is2=(float)(1/Math.sqrt(2));
		c.drawLine(padX,padY,padX+PAD_RADIUS*is2,padY+PAD_RADIUS*is2,p);
		c.drawLine(padX,padY,padX-PAD_RADIUS*is2,padY+PAD_RADIUS*is2,p);
		c.drawLine(padX,padY,padX+PAD_RADIUS*is2,padY-PAD_RADIUS*is2,p);
		c.drawLine(padX,padY,padX-PAD_RADIUS*is2,padY-PAD_RADIUS*is2,p);
		p.setColor(0x40000000);
		c.drawCircle(padX,padY,PAD_RADIUS,p);
		
		// stick
		p.setStyle(Paint.Style.FILL);
		p.setColor(0x40999999);
		c.drawCircle(padX+stickX,padY+stickY,STICK_RADIUS,p);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(0x40000000);
		c.drawCircle(padX+stickX,padY+stickY,STICK_RADIUS,p);
		
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
		if(name.equals("activeDirection"))
			return activeDirection;
		else
			return null;
	}

}
