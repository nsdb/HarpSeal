package com.yad.harpseal.game.map;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.constant.Screen;

/**
 * 현재 게임의 배경을 나타내는 바다 객체입니다.<br>
 * 크기는 게임 맵에서 정해줍니다.<br>
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class TheArcticOcean extends GameObject {
	
	private float width,height;

	private boolean scrolling;
	private float scrollX,scrollY;
	
	private TransHelper helper;
	private BitmapTexture bg;

	public TheArcticOcean(Communicable con, float width, float height) {
		super(con);
		this.width=width;
		this.height=height;
		this.scrolling=false;
		helper=new TransHelper();
		bg=new BitmapTexture(width, height, R.drawable.background);
		bg.loadBitmap(con);
	}

	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(layer != Layer.BACKGROUND) {
			super.receiveMotion(ev, layer);
			return;
		}
		
		switch(ev.getType()) {
		case GameEvent.MOTION_DOWN:
			scrolling=true;
			scrollX=ev.getOriginalX();
			scrollY=ev.getOriginalY();
			ev.process();
			break;
		case GameEvent.MOTION_DRAG:
			if(scrolling) {
				con.send( "scroll/"+(scrollX-ev.getOriginalX())+"/"+(scrollY-ev.getOriginalY()) );
				scrollX=ev.getOriginalX();
				scrollY=ev.getOriginalY();
				ev.process();
			}
			break;
		case GameEvent.MOTION_UP:
			scrolling=false;
			ev.process();
			break;
			
		}
		super.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		super.drawScreen(gl, layer);
		if(layer != Layer.BACKGROUND) return;
		
		helper.setBasePoint(gl, width/2, height/2, Screen.WIDTH, Screen.HEIGHT);
		bg.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("update")) {
			width=Float.parseFloat(msgs[1]);
			height=Float.parseFloat(msgs[2]);
			bg=new BitmapTexture(width, height, R.drawable.background);
			bg.loadBitmap(con);
			return 1;
		}
		else return super.send(msg);
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return bg.isLoaded();
		else
			return super.get(name);
	}

}
