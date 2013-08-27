package com.yad.harpseal.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Align;
import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.glcomp.VariableStringTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.constant.Screen;

/**
 * 현재 이동 수, 물고기 획득 수를 알려주는 윈도우입니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class ScoreCounter extends GameObject {
	
	private int x,y;
	private int stepCount,fishCount,fishMax;
	
	private TransHelper helper;
	private BitmapTexture step,fish;
	private VariableStringTexture number;
	
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
		Context context=(Context)con.get("context");
		this.stepCount=0;
		this.fishCount=0;
		this.fishMax=0;
		helper=new TransHelper();
		step=new BitmapTexture(context, IMAGE_SIZE, R.drawable.sample_step);
		step.loadBitmap(con);
		fish=new BitmapTexture(context, IMAGE_SIZE, R.drawable.crown_fish);
		fish.loadBitmap(con);
		number=new VariableStringTexture("0123456789/ ",TEXT_SIZE,Align.LEFT);
		number.loadString(con);
	}

	@Override
	public void playGame(int ms) {
		stepCount=(Integer)con.get("stepCount");
		fishCount=(Integer)con.get("fishCount");
		fishMax=(Integer)con.get("fishMax");
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.WINDOW) return;
		
		helper.setBasePoint(gl, x, y, Screen.WIDTH, Screen.HEIGHT);
		helper.translate(gl, IMAGE_SIZE/2, IMAGE_SIZE/2);
		step.draw(gl);
		helper.translate(gl, IMAGE_SIZE/2+MARGIN_IN, 0);
		number.setPrintString(""+stepCount);
		number.draw(gl);
		helper.translate(gl, TEXT_LENGTH+MARGIN_IN2+IMAGE_SIZE/2, 0);
		fish.draw(gl);
		helper.translate(gl, IMAGE_SIZE/2+MARGIN_IN, 0);
		number.setPrintString(""+fishCount+"/"+fishMax);
		number.draw(gl);
		helper.rollback(gl);
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return step.isLoaded() && fish.isLoaded();
		else
			return super.get(name);
	}

}
