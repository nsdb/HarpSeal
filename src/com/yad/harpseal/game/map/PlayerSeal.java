package com.yad.harpseal.game.map;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.glcomp.LinedCircle;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameLog;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Screen;

/**
 * 조이스틱을 통해 제어되는 플레이어입니다. 실제 이동 여부는 게임 맵이 결정합니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class PlayerSeal extends GameObject {

	private int mapX,mapY;

	private int moveDirection;
	private int moveTime;
	private final static int MOVE_TIME=400;
	
	private TransHelper helper;
	private LinedCircle seal;
	private BitmapTexture[] sprite;

	public PlayerSeal(Communicable con,int mapX,int mapY) {
		super(con);
		this.mapX=mapX;
		this.mapY=mapY;
		this.moveDirection=Direction.NONE;
		this.moveTime=0;
		helper=new TransHelper();
		seal=new LinedCircle(Screen.TILE_LENGTH*0.3f,32,5);
		seal.setLineColor(0.75f, 0.75f, 0.9f, 1);
		seal.setColor(0.9f, 0.9f, 0.95f, 1);
		Context context=(Context)con.get("context");
		sprite=new BitmapTexture[2];
		sprite[0]=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.seal01);
		sprite[0].loadBitmap(con);
		sprite[1]=new BitmapTexture(context, Screen.TILE_LENGTH, R.drawable.seal02);
		sprite[1].loadBitmap(con);
	}

	@Override
	public void playGame(int ms) {
		super.playGame(ms);
		
		// movement
		if(moveDirection != Direction.NONE) {
			moveTime+=ms;
			if(moveTime>MOVE_TIME) {
				moveDirection=Direction.NONE;
				moveTime=0;
				con.send("moved/PlayerSeal/"+mapX+"/"+mapY);
			}
		}
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.CHARACTER) return;		
		float baseX=Screen.FIELD_MARGIN_LEFT+(mapX+0.5f)*Screen.TILE_LENGTH;
		float baseY=Screen.FIELD_MARGIN_TOP+(mapY+0.5f)*Screen.TILE_LENGTH;
		float tLen=Screen.TILE_LENGTH;
		
		// movement
		switch(moveDirection) {
		case Direction.UP: baseY-=tLen*moveTime/MOVE_TIME-tLen; break;
		case Direction.LEFT: baseX-=tLen*moveTime/MOVE_TIME-tLen; break;
		case Direction.RIGHT: baseX+=tLen*moveTime/MOVE_TIME-tLen; break;
		case Direction.DOWN: baseY+=tLen*moveTime/MOVE_TIME-tLen; break;
		}

		// angle
		float angle=0;
		switch(moveDirection) {
		case Direction.UP: angle=180; break;
		case Direction.RIGHT: angle=90; break;
		case Direction.DOWN: angle=0; break;
		case Direction.LEFT: angle=270; break;
		case Direction.NONE: break;
		default: GameLog.error("Invalid direction : "+moveDirection); break;
		}
		
		
		// draw seal
		helper.setBasePoint(gl, baseX, baseY, Screen.WIDTH, Screen.HEIGHT);
		if(moveDirection==Direction.NONE)
			seal.draw(gl);
		else {
			gl.glRotatef(angle, 0, 0, 1);
			if(moveTime<MOVE_TIME/2) sprite[0].draw(gl);
			else sprite[1].draw(gl);
			gl.glRotatef(-angle, 0, 0, 1);
		}
		helper.rollback(gl);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		// movement
		if(msgs[0].equals("move")) {
			
			if(moveDirection!=Direction.NONE) return 0;
			moveDirection=Integer.parseInt(msgs[1]);
			moveTime=0;
			switch(moveDirection) {
			case Direction.UP: mapY-=1; break;
			case Direction.LEFT: mapX-=1; break;
			case Direction.RIGHT: mapX+=1; break;
			case Direction.DOWN: mapY+=1; break;
			default: GameLog.error("Invalid direction : "+moveDirection); return 0;
			}
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("mapX")) return mapX;
		else if(name.equals("mapY")) return mapY;
		else if(name.equals("moveDirection")) return moveDirection;
		else if(name.equals("moveTime")) return moveTime;
		else if(name.equals("moveValue")) return MOVE_TIME;
		else if(name.equals("isLoaded")) return sprite[0].isLoaded() && sprite[1].isLoaded();
		else return super.get(name);
	}

}
