package com.yad.harpseal.gameobj.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.yad.harpseal.R;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpDraw;
import com.yad.harpseal.util.HarpEvent;

public class ScoreCounter extends GameObject {
	
	private int x,y;
	private HarpDraw step,fish;
	private int stepCount,fishCount,fishMax;
	
	private final static int MARGIN_TOP=10;
	private final static int MARGIN_LEFT=10;
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
		step=new HarpDraw( res.getDrawable(R.drawable.sample_step) );
		fish=new HarpDraw( res.getDrawable(R.drawable.sample_fish) );
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
		step.setValue(HarpDraw.ALIGN_TOPLEFT, x, y, IMAGE_SIZE);
		step.drawOn(c);
		
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
		fish.setValue(HarpDraw.ALIGN_TOPLEFT, x+IMAGE_SIZE+MARGIN_IN+TEXT_LENGTH+MARGIN_IN2, y, IMAGE_SIZE);
		fish.drawOn(c);
		
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
