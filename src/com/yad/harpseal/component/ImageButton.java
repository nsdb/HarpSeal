package com.yad.harpseal.component;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameEvent;
import com.yad.harpseal.constant.Screen;

public class ImageButton extends GameObject {

	private String id;
	private float x,y;
	
	private BitmapTexture img;

	public ImageButton(Communicable con,String id,float x,float y,float width,float height,int bitmapID) {
		super(con);
		this.id=id;
		this.x=x;
		this.y=y;
		img=new BitmapTexture(width, height, bitmapID);
		img.loadBitmap(this);
	}
	
	public ImageButton(Communicable con,String id,float x,float y,float caseSize,int bitmapID) {
		super(con);
		this.id=id;
		this.x=x;
		this.y=y;
		Context context=(Context)con.get("context");
		img=new BitmapTexture(context, caseSize, bitmapID);
		img.loadBitmap(this);
	}
	
	@Override
	public void receiveMotion(GameEvent ev,int layer) {
		if(layer != Layer.WINDOW) return;
		if(ev.getType() != GameEvent.MOTION_CLICK) return;
		float rx=ev.getX()-x;
		float ry=ev.getY()-y;
		if(rx<-img.getWidth()/2 || rx>img.getWidth()/2) return;
		if(ry<-img.getHeight()/2 || ry>img.getHeight()/2) return;
		con.send("clicked/"+id);
		ev.process();
	}
	
	@Override
	public void drawScreen(GL10 gl,int layer) {
		if(layer != Layer.WINDOW) return;
		gl.glTranslatef(x-Screen.WIDTH/2, Screen.HEIGHT/2-y, 0);
		img.draw(gl);
		gl.glTranslatef(Screen.WIDTH/2-x, y-Screen.HEIGHT/2, 0);
	}
	
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return img.isLoaded();
		else
			return super.get(name);
	}

}
