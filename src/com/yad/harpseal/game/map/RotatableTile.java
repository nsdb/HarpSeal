package com.yad.harpseal.game.map;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.GameLog;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.constant.TileType;

/**
 * 터치를 통해 회전시킬 수 있는 타일입니다. 회전 가능 여부는 게임 맵이 결정합니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class RotatableTile extends GameObject {
	
	private int mapX,mapY;
	private int type;
	private int direction;
	
	private TransHelper helper;
	private BitmapTexture tile;
	
	// * Base tile form (Direction.UP)
	// Bridge : │
	// Fork : ┴
	// Corner : └
	// Intersection : ┼

	public RotatableTile(Communicable con, int mapX, int mapY, int type) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		this.type=type;
		this.direction=new Random().nextInt(4)+1;
		this.helper=new TransHelper();
		Context context=(Context)con.get("context");
		switch(type) {
		case TileType.RT_BRIDGE: tile=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.vertical); break;
		case TileType.RT_CORNER: tile=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.curve); break;
		case TileType.RT_FORK: tile=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.way3); break;
		case TileType.RT_INTERSECTION: tile=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.cross); break;
		}
		tile.loadBitmap(con);
	}
	
	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(layer != Layer.TILE) return;
		if(ev.getType() != GameEvent.MOTION_CLICK) return;
		int pointX=(int)(ev.getX()-Screen.FIELD_MARGIN_LEFT)/Screen.TILE_LENGTH;
		int pointY=(int)(ev.getY()-Screen.FIELD_MARGIN_TOP)/Screen.TILE_LENGTH;
		if(pointX==mapX && pointY==mapY) {
			GameLog.debug("Test : "+ev.getX()+","+ev.getY());
			if(con.send("rotatableCheck/"+mapX+"/"+mapY)==1) {
				direction=Direction.clockwise(direction);
				ev.process();
			}
		}
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.TILE) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		float angle=0;
		switch(direction) {
		case Direction.UP: angle=0; break;
		case Direction.RIGHT: angle=270; break;
		case Direction.DOWN: angle=180; break;
		case Direction.LEFT: angle=90; break;
		default: GameLog.error("Invalid direction : "+direction); break;
		}
		
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		gl.glRotatef(angle, 0, 0, 1);
		tile.draw(gl);
		gl.glRotatef(-angle, 0, 0, 1);
		helper.rollback(gl);
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("type")) return type;
		else if(name.equals("direction")) return direction;
		else if(name.equals("isLoaded")) return tile.isLoaded();
		else return super.get(name);
	}
}
