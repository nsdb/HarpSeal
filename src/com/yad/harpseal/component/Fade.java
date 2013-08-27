package com.yad.harpseal.component;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.Rectangle;
import com.nsdb.engine.util.Communicable;
import com.yad.harpseal.constant.Screen;

public class Fade extends GameObject {
	
	private int type;
	public final static int OUT=10;
	public final static int IN=20;
	
	private int color;
	public final static int BLACK=0;
	public final static int WHITE=1;
	
	private int time;
	private int timeMax;
	
	private Rectangle bg;
	
	public Fade(Communicable con,int type,int color,int fadeTime) {
		super(con);
		this.type=type;
		this.color=color;
		this.time=0;
		this.timeMax=fadeTime;
		bg=new Rectangle(Screen.WIDTH,Screen.HEIGHT);
	}
	
	@Override
	public void playGame(int ms) {
		time+=ms;
		if(time>timeMax) con.send("fadeEnd");
	}
	
	@Override
	public void drawScreen(GL10 gl,int layer) {
		if(layer != Layer.SCREEN) return;
		
		float alpha=(float)Math.min(time,timeMax)/timeMax;
		if(type==OUT) alpha=1-alpha;
		
		switch(color) {
		case BLACK: bg.setColor(0, 0, 0, alpha); break;
		case WHITE: bg.setColor(1, 1, 1, alpha); break;
		}
		
		bg.draw(gl);
	}

}
