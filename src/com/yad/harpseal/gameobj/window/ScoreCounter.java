package com.yad.harpseal.gameobj.window;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class ScoreCounter extends GameObject {
	
	private int x,y;
	private Drawable step,fish;
	private int stepCount,fishCount,fishMax;
	
	private final static int MARGIN_TOP=30;
	private final static int MARGIN_LEFT=30;
	private final static int MARGIN_IN=15;
	private final static int MARGIN_IN2=30;
	private final static int IMAGE_SIZE=70;
	private final static int TEXT_SIZE=50;
	private final static int TEXT_LENGTH=90;

	public ScoreCounter(Communicable con) {
		super(con);
		this.x=MARGIN_TOP;
		this.y=MARGIN_LEFT;
		Resources res=(Resources)con.get("resources");
		step=res.getDrawable(R.drawable.sample_step);
		fish=res.getDrawable(R.drawable.sample_fish);
		this.stepCount=0;
		this.fishCount=0;
		this.fishMax=0;
	}

	@Override
	public void playGame(int ms) {
		stepCount=(Integer)con.get("stepCount");
		fishCount=(Integer)con.get("fishCount");
		fishMax=(Integer)con.get("fishMax");
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_WINDOW) return;
		p.reset();
		
		// step image
		int cX=x+IMAGE_SIZE/2;
		int cY=y+IMAGE_SIZE/2;
		int rX=IMAGE_SIZE/2;
		int rY=step.getIntrinsicHeight()*IMAGE_SIZE/step.getIntrinsicWidth()/2;
		step.setBounds(cX-rX,cY-rY,cX+rX,cY+rY);
		step.draw(c);
		
		// step text
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFFFFFFF);
		p.setTextSize(TEXT_SIZE);
		p.setTextAlign(Align.RIGHT);
		c.drawText(String.valueOf(stepCount), x+IMAGE_SIZE+MARGIN_IN+TEXT_LENGTH, y+IMAGE_SIZE/2+TEXT_SIZE/3, p);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xFF000000);
		p.setStrokeWidth(2);
		c.drawText(String.valueOf(stepCount), x+IMAGE_SIZE+MARGIN_IN+TEXT_LENGTH, y+IMAGE_SIZE/2+TEXT_SIZE/3, p);
		
		// fish image
		cX=x+IMAGE_SIZE/2+MARGIN_IN+TEXT_LENGTH+MARGIN_IN2+IMAGE_SIZE;
		cY=y+IMAGE_SIZE/2;
		rX=IMAGE_SIZE/2;
		rY=fish.getIntrinsicHeight()*IMAGE_SIZE/fish.getIntrinsicWidth()/2;
		fish.setBounds(cX-rX,cY-rY,cX+rX,cY+rY);
		fish.draw(c);
		
		// fish text
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFFFFFFF);
		p.setTextSize(TEXT_SIZE);
		p.setTextAlign(Align.RIGHT);
		c.drawText(String.valueOf(fishCount)+"/"+String.valueOf(fishMax), x+(IMAGE_SIZE+MARGIN_IN+TEXT_LENGTH)*2+MARGIN_IN2, y+IMAGE_SIZE/2+TEXT_SIZE/3, p);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xFF000000);
		p.setStrokeWidth(2);
		c.drawText(String.valueOf(fishCount)+"/"+String.valueOf(fishMax), x+(IMAGE_SIZE+MARGIN_IN+TEXT_LENGTH)*2+MARGIN_IN2, y+IMAGE_SIZE/2+TEXT_SIZE/3, p);
		
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
