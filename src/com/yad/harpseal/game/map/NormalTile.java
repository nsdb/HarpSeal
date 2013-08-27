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
 * 플레이어가 밟고 건너갈 수 있는 타일입니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class NormalTile extends GameObject {
	
	private int mapX,mapY;
	
	private TransHelper helper;
	private BitmapTexture block;

	public NormalTile(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		helper=new TransHelper();
		Context context=(Context)con.get("context");
		block=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.block);
		block.loadBitmap(con);
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.TILE) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		block.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("isLoaded")) return block.isLoaded();
		else return super.get(name);
	}

}
