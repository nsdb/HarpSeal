package com.yad.harpseal.component;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Align;
import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.LinedCircle;
import com.nsdb.engine.glcomp.StringTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.Func;
import com.nsdb.engine.util.GameEvent;
import com.yad.harpseal.constant.Screen;

/**
 * 임시로 사용하기 좋은 원형 버튼입니다.
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class CircleButton extends GameObject {

	private String id;
	private String name;
	private float x,y;
	
	private LinedCircle bg;
	private StringTexture txt;

	public CircleButton(Communicable con,String id,String name,float x,float y,float cSize,float fontSize) {
		super(con);
		this.id=id;
		this.name=name;
		this.x=x;
		this.y=y;
		bg=new LinedCircle(cSize,32,5);
		bg.setColor(1, 1, 1, 1);
		bg.setLineColor(0, 0, 0, 1);
		txt=new StringTexture(this.name,fontSize,Align.CENTER);
		txt.setColor(0, 0, 0, 1);
		txt.loadString(this);
	}
	
	@Override
	public void receiveMotion(GameEvent ev,int layer) {
		if(layer != Layer.WINDOW) return;
		if(ev.getType() != GameEvent.MOTION_CLICK) return;
		float rx=ev.getX()-x;
		float ry=ev.getY()-y;
		if(Func.distan(0, 0, rx, ry) > bg.getRadius()) return;
		con.send("clicked/"+id);
		ev.process();
	}
	
	@Override
	public void drawScreen(GL10 gl,int layer) {
		if(layer != Layer.WINDOW) return;
		gl.glTranslatef(x-Screen.WIDTH/2, Screen.HEIGHT/2-y, 0);
		bg.draw(gl);
		txt.draw(gl);
		gl.glTranslatef(Screen.WIDTH/2-x, y-Screen.HEIGHT/2, 0);
	}
	
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return txt.isLoaded();
		else
			return super.get(name);
	}
}