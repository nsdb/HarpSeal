package com.yad.harpseal.game.map;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.LinedCircle;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.constant.Screen;

/**
 * 플레이어가 도달해야 하는 도착지점입니다. 도착 여부는 게임 맵이 결정합니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class GoalFlag extends GameObject {

	private int mapX,mapY;

	private TransHelper helper;
	private LinedCircle seal;

	public GoalFlag(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		helper=new TransHelper();
		seal=new LinedCircle(Screen.TILE_LENGTH*0.3f,32,5);
		seal.setLineColor(0.75f, 0.3f, 0.3f, 1);
		seal.setColor(0.9f, 0.3f, 0.3f, 1);
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.CHARACTER) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		
		// draw
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		seal.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else return super.get(name);
	}

}
