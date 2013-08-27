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
 * 플레이어가 밟고 지나갈 경우 부숴지는 타일입니다. 파괴 여부는 게임 맵이 결정합니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class BrakingTile extends GameObject {
	
	private int mapX,mapY;
	private boolean braking;
	private int brakingTime;
	private final static int TIME_MAX=500;

	private TransHelper helper;
	private BitmapTexture crack;

	public BrakingTile(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		this.braking=false;
		this.brakingTime=0;
		helper=new TransHelper();
		Context context=(Context)con.get("context");
		crack=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.crack);
		crack.loadBitmap(con);
	}

	@Override
	public void playGame(int ms) {
		if(braking) {
			brakingTime+=ms;
			if(brakingTime>TIME_MAX) {
				brakingTime=TIME_MAX;
				con.send("broken/"+mapX+"/"+mapY);
			}
		}
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.TILE) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		float alpha=Math.max(0, (float)(TIME_MAX-brakingTime)/TIME_MAX);
		
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		crack.setColor(1, 1, 1, alpha);
		crack.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("break")) {
			if(braking==false) {
				braking=true;
				brakingTime=0;
			}
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("braking")) return braking;
		else if(name.equals("isLoaded")) return crack.isLoaded();
		else return super.get(name);
	}

}
