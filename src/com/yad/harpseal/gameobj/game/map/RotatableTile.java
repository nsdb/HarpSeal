package com.yad.harpseal.gameobj.game.map;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.constant.TileType;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class RotatableTile extends NormalTile {
	
	private int type;
	private int direction;
	
	// * Base tile form (Direction.UP)
	// Bridge : │
	// Fork : ┴
	// Corner : └
	// Intersection : ┼

	public RotatableTile(Communicable con, int mapX, int mapY, int type) {
		super(con, mapX, mapY);
		this.type=type;
		this.direction=new Random().nextInt(4)+1;
		switch(type) {
		case TileType.RT_BRIDGE: break;
		case TileType.RT_CORNER: break;
		case TileType.RT_FORK: break;
		case TileType.RT_INTERSECTION: break;
		default: HarpLog.error("Invalid Tile Type : "+type);
		}
	}
	
	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		if(layer != Layer.LAYER_FIELD) return;
		if(ev.getType() != HarpEvent.MOTION_CLICK) return;
		int pointX=(int)(ev.getX()-Screen.FIELD_MARGIN_LEFT)/Screen.TILE_LENGTH;
		int pointY=(int)(ev.getY()-Screen.FIELD_MARGIN_TOP)/Screen.TILE_LENGTH;
		if(pointX==mapX && pointY==mapY) {
			if(con.send("rotatableCheck/"+mapX+"/"+mapY)==1) {
				direction=Direction.clockwise(direction);
				ev.process();
			}
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		if(layer != Layer.LAYER_TILE) return;		
		p.reset();
		int baseX=Screen.FIELD_MARGIN_LEFT+mapX*Screen.TILE_LENGTH;
		int baseY=Screen.FIELD_MARGIN_TOP+mapY*Screen.TILE_LENGTH;
		int tLen=Screen.TILE_LENGTH;
		int tHeight=Screen.TILE_HEIGHT;
		
		// draw tile
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xFFEEEEFF);
		switch(type) {
		case TileType.RT_BRIDGE:
			if(direction==Direction.UP || direction==Direction.DOWN)
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen,p);
			else
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
			break;
		case TileType.RT_CORNER:
			if(direction==Direction.UP) {
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen*0.7f,p);
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
			} else if(direction==Direction.RIGHT) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen,p);
			} else if(direction==Direction.DOWN) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen,p);
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen*0.7f,p);
			} else {
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen*0.7f,p);
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen*0.7f,p);				
			}
			break;
		case TileType.RT_FORK:
			if(direction==Direction.UP) {
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen*0.7f,p);
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
			} else if(direction==Direction.RIGHT) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen,p);
			} else if(direction==Direction.DOWN) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen,p);
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
			} else {
				c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen*0.7f,baseY+tLen*0.7f,p);
				c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen,p);
			}
			break;
		case TileType.RT_INTERSECTION:
			c.drawRect(baseX,baseY+tLen*0.3f,baseX+tLen,baseY+tLen*0.7f,p);
			c.drawRect(baseX+tLen*0.3f,baseY,baseX+tLen*0.7f,baseY+tLen,p);
			break;
		}
		p.setColor(0xFFAAAABB);
		switch(type) {
		case TileType.RT_BRIDGE:
			if(direction==Direction.UP || direction==Direction.DOWN)
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
			else
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
			break;
		case TileType.RT_CORNER:
			if(direction==Direction.UP) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
			} else if(direction==Direction.RIGHT) {
				c.drawRect(baseX+tLen*0.7f,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
			} else if(direction==Direction.DOWN) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen*0.3f,baseY+tLen*0.7f+tHeight,p);
			} else {
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen*0.7f,baseY+tLen*0.7f+tHeight,p);
			}
			break;
		case TileType.RT_FORK:
			if(direction==Direction.UP) {
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
			} else if(direction==Direction.RIGHT) {
				c.drawRect(baseX+tLen*0.7f,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
			} else if(direction==Direction.DOWN) {
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
				c.drawRect(baseX+tLen*0.7f,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen*0.3f,baseY+tLen*0.7f+tHeight,p);
			} else {
				c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen*0.3f,baseY+tLen*0.7f+tHeight,p);
				c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
			}
			break;
		case TileType.RT_INTERSECTION:
			c.drawRect(baseX+tLen*0.3f,baseY+tLen,baseX+tLen*0.7f,baseY+tLen+tHeight,p);
			c.drawRect(baseX+tLen*0.7f,baseY+tLen*0.7f,baseX+tLen,baseY+tLen*0.7f+tHeight,p);
			c.drawRect(baseX,baseY+tLen*0.7f,baseX+tLen*0.3f,baseY+tLen*0.7f+tHeight,p);
			break;
		
		}
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("type")) return type;
		else if(name.equals("direction")) return direction;
		else return super.get(name);
	}
}
