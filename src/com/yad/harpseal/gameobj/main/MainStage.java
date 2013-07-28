package com.yad.harpseal.gameobj.main;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class MainStage extends GameObject {
	
	private int time;
	private int stageGroup = 0;

	public MainStage(Communicable con) {
		super(con);
		this.time=0;
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		//click
		int pointX = (int)(ev.getX());
		int pointY = (int)(ev.getY());
		if(ev.getType() != HarpEvent.MOTION_CLICK) return;

		if (stageGroup == 0) {
			if ( (pointX >= 100 && pointX <= 200) && (pointY >= 400 && pointY <= 500 ) ) {
				stageGroup = 1;
				ev.process();
				return;
			}
		}
		else {
			if ( ((pointX >= 100 && pointX <= 400) && (pointY >= 400 && pointY <= 500)) ) {
				con.send("gameStart/"+stageGroup+"/"+1);
			}
		}
	}

	// 0.015초마다 자동 호출 
	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_SCREEN) return;
		p.reset();

		if (stageGroup == 1) {
			// level에서 클릭해서 들어간 경우
			p.setColor(0xFFda70d6);
			c.drawRect(Screen.SCREEN_X/3f, Screen.SCREEN_Y*4/9f, Screen.SCREEN_X*2/3f, Screen.SCREEN_Y*5/9f, p);
			p.setColor(0xFFffffff);
			c.drawText("START", Screen.SCREEN_X/2, Screen.SCREEN_Y/2, p);
		}
		else {
			// 첫 화면일 경우 
			p.setColor(0xFFffe4e1);
			c.drawRect(Screen.SCREEN_X*1/6f, Screen.SCREEN_Y*5/9f, Screen.SCREEN_X*2/6f, Screen.SCREEN_Y*4/9f, p);
			c.drawRect(Screen.SCREEN_X*25/60f, Screen.SCREEN_Y*5/9f, Screen.SCREEN_X*35/60f, Screen.SCREEN_Y*4/9f, p);
			c.drawRect(Screen.SCREEN_X*4/6f, Screen.SCREEN_Y*5/9f, Screen.SCREEN_X*5/6f, Screen.SCREEN_Y*4/9f, p);
		}
		
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
