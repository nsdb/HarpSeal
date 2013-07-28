package com.yad.harpseal.gameobj.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpDraw;
import com.yad.harpseal.util.HarpEvent;

public class SuccessWindow extends GameObject {

	private HarpDraw step,fish;
	private int stepCount,fishCount,fishMax;
	
	private boolean show;
	private boolean restarting;
	
	private RectF restartRect, nextRect, mainRect;
	private final static int WINDOW_RY=-280;
	private final static int STEPTXT_SIZE=50;
	private final static int STEPTXT_MARGINBOT=30;
	private final static int FISHTXT_SIZE=50;
	private final static int FISHTXT_MARGINBOT=30;
	private final static int FISH_SIZE=150;
	private final static int FISH_BETWEEN=30;
	private final static int FISH_MARGINBOT=20;
	private final static int BUTTON1_X=200;
	private final static int BUTTON1_Y=75;
	private final static int BUTTON1_TXT=40;
	private final static int BUTTON1_MARGIN=100;
	private final static int BUTTON1_MARGINBOT=50;
	private final static int BUTTON2_X=400;
	private final static int BUTTON2_Y=75;
	private final static int BUTTON2_TXT=40;
	
	public SuccessWindow(Communicable con) {
		super(con);
		Resources res=(Resources)con.get("resources");
		this.step=new HarpDraw( res.getDrawable(R.drawable.sample_step) );
		this.fish=new HarpDraw( res.getDrawable(R.drawable.sample_fish) );
		this.stepCount=0;
		this.fishCount=0;
		this.fishMax=0;
		this.show=false;
		this.restarting=false;
		this.restartRect=new RectF(
				Screen.SCREEN_X/2-BUTTON1_X-BUTTON1_MARGIN/2,
				Screen.SCREEN_Y/2+WINDOW_RY+STEPTXT_SIZE+STEPTXT_MARGINBOT+FISHTXT_SIZE+FISHTXT_MARGINBOT+FISH_SIZE+FISH_MARGINBOT,
				0,0);
		restartRect.right=restartRect.left+BUTTON1_X;
		restartRect.bottom=restartRect.top+BUTTON1_Y;
		this.nextRect=new RectF(restartRect);
		nextRect.left+=BUTTON1_X+BUTTON1_MARGIN;
		nextRect.right+=BUTTON1_X+BUTTON1_MARGIN;
		this.mainRect=new RectF(restartRect);
		mainRect.left=(Screen.SCREEN_X-BUTTON2_X)/2;
		mainRect.top+=BUTTON1_Y+BUTTON1_MARGINBOT;
		mainRect.right=mainRect.left+BUTTON2_X;
		mainRect.bottom=mainRect.top+BUTTON2_Y;
	}

	@Override
	public void playGame(int ms) {
		stepCount=(Integer)con.get("stepCount");
		fishCount=(Integer)con.get("fishCount");
		fishMax=(Integer)con.get("fishMax");
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(!show) return;
		if(layer != Layer.LAYER_WINDOW) return;
		if(restarting) return;
		if(ev.getType() != HarpEvent.MOTION_CLICK) return;
		
		if(restartRect.contains(ev.getX(), ev.getY())) {
			con.send("gameRestart");
			restarting=true;
			ev.process();			
		}
		else if(nextRect.contains(ev.getX(), ev.getY())) {
			con.send("gameToNext");
			restarting=true;
			ev.process();
		}
		else if(mainRect.contains(ev.getX(), ev.getY())) {
			con.send("gameEnd");
			restarting=true;
			ev.process();
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(!show) return;
		if(layer != Layer.LAYER_WINDOW) return;
		p.reset();
		
		// screen
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFAAAAFF);
		c.drawRect(c.getClipBounds(), p);

		// step txt
		float cX=Screen.SCREEN_X/2;
		float cY=Screen.SCREEN_Y/2+WINDOW_RY;
		cY+=STEPTXT_SIZE;
		p.setTextAlign(Align.CENTER);
		p.setTextSize(FISHTXT_SIZE);
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFFFFFFF);
		c.drawText("이동 횟수 : "+stepCount, cX, cY, p);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xFF000000);
		p.setStrokeWidth(2);
		c.drawText("이동 횟수 : "+stepCount, cX, cY, p);
		cY+=STEPTXT_MARGINBOT;
		
		// fish txt
		cY+=FISHTXT_SIZE;
		p.setTextAlign(Align.CENTER);
		p.setTextSize(FISHTXT_SIZE);
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFFFFFFF);
		c.drawText("물고기 획득 수", cX, cY, p);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xFF000000);
		p.setStrokeWidth(2);
		c.drawText("물고기 획득 수", cX, cY, p);
		cY+=FISHTXT_MARGINBOT;
		
		// fish
		cX-=(fishMax*FISH_SIZE+(fishMax-1)*FISH_BETWEEN)/2;
		fish.setAlpha(255);
		for(int i=0;i<fishMax;i++) {
			if(i==fishCount) fish.setAlpha(64);
			fish.setBase(HarpDraw.ALIGN_TOPLEFT, cX, cY, FISH_SIZE);
			fish.drawOn(c);
			cX+=FISH_SIZE+FISH_BETWEEN;
		}
		fish.setAlpha(255);
		cX=Screen.SCREEN_X/2;
		cY+=FISH_SIZE+FISH_MARGINBOT;
		
		// buttons
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFDDDDFF);
		c.drawRect(restartRect, p);
		c.drawRect(nextRect, p);
		c.drawRect(mainRect, p);
		
		// button txt
		p.setColor(0xFF000000);
		p.setTextSize(BUTTON1_TXT);
		c.drawText("재시작", restartRect.centerX(), restartRect.centerY()+BUTTON1_TXT/3, p);
		c.drawText("다음", nextRect.centerX(), nextRect.centerY()+BUTTON1_TXT/3, p);
		p.setTextSize(BUTTON2_TXT);
		c.drawText("메인으로 돌아가기", mainRect.centerX(), mainRect.centerY()+BUTTON2_TXT/3, p);
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
