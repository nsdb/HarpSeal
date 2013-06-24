package com.yad.harpseal.gameobj.stage;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class MainStage extends GameObject {
	
	private int time;

	public MainStage(Communicable con) {
		super(con);
		this.time=0;
	}

	@Override
	public void playGame(int ms) {
		time+=ms;
		if(time>3000) {
			con.send("gameStart/"+1+"/"+1);
		}
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_SCREEN) return;
		p.reset();
		
		p.setTextAlign(Align.CENTER);
		p.setTextSize(30);
		p.setColor(0xFFFFFFFF);
		c.drawText("잠시 후 게임 화면으로 이동합니다", Screen.SCREEN_X/2, Screen.SCREEN_Y/2+15, p);

		if(time<500) {
			int alpha=Math.round( (float)(500-time)/500*0xFF ) << 24;
			p.setColor( alpha | 0x000000 );
			c.drawRect(0,0,Screen.SCREEN_X,Screen.SCREEN_Y,p);
		}
		if(time>2500) {
			int alpha=Math.round( (float)(time-2500)/500*0xFF ) << 24;
			p.setColor( alpha | 0xFFFFFF );
			c.drawRect(0,0,Screen.SCREEN_X,Screen.SCREEN_Y,p);
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
