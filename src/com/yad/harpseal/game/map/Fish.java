package com.yad.harpseal.game.map;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.constant.Screen;

/**
 * 플레이어가 먹을 수 있는 물고기입니다. 먹힘 여부는 게임 맵이 결정합니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class Fish extends GameObject {

	private int mapX,mapY;

	private TransHelper helper;
	private BitmapTexture fish;

	public Fish(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		helper=new TransHelper();
		Context context=(Context)con.get("context");
		fish=new BitmapTexture(context, Screen.TILE_LENGTH/1.2f, R.drawable.crown_fish);
		fish.loadBitmap(con);
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.CHARACTER) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		
		// draw
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		fish.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("eaten")) {
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("isLoaded")) return fish.isLoaded();
		else return super.get(name);
	}

}
