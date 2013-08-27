package com.yad.harpseal.game;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.LinedCircle;
import com.nsdb.engine.glcomp.LinedRectangle;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.Func;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.constant.Screen;

/**
 * 게임의 일시정지에 사용되는 일시정지 버튼입니다. 눌릴 경우 게임스테이지에게 게임 일시정지를 요청합니다.<br>
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class PauseBtn extends GameObject {
	
	private float btnX,btnY;
	
	private TransHelper helper;
	private LinedCircle bg;
	private LinedRectangle rect;
	
	private final float BTN_MARGIN=15;
	private final float BTN_RADIUS=30;
	private final float RANGE_ACTIVE=50;
	private final float RECT_WIDTH=12;
	private final float RECT_MARGIN=8;
	private final float RECT_HEIGHT=32;

	public PauseBtn(Communicable con) {
		super(con);
		btnX=Screen.WIDTH-BTN_MARGIN-BTN_RADIUS;
		btnY=BTN_MARGIN+BTN_RADIUS;
		helper=new TransHelper();
		bg=new LinedCircle(BTN_RADIUS,16,5);
		bg.setLineColor(0, 0, 0, 0.4f);
		bg.setColor(0.75f, 0.75f, 0.75f, 0.2f);
		rect=new LinedRectangle(RECT_WIDTH,RECT_HEIGHT,5);
		rect.setLineColor(0, 0, 0, 0.4f);
		rect.setColor(0.9f, 0.9f, 0.9f, 0.2f);
	}

	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(layer != Layer.WINDOW) return;
		if(ev.getType() != GameEvent.MOTION_CLICK) return;
		if(Func.distan(btnX, btnY, ev.getX(), ev.getY()) > RANGE_ACTIVE) return;
		con.send("gamePause");
		ev.process();
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(layer != Layer.WINDOW) return;
		
		helper.setBasePoint(gl, btnX, btnY, Screen.WIDTH, Screen.HEIGHT);
		bg.draw(gl);
		helper.translate(gl, -(RECT_MARGIN+RECT_WIDTH)*0.5f, 0);
		rect.draw(gl);
		helper.translate(gl, (RECT_MARGIN+RECT_WIDTH), 0);
		rect.draw(gl);
		helper.rollback(gl);
	}

}
