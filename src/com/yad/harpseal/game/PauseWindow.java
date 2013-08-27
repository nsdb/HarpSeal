package com.yad.harpseal.game;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Align;
import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.glcomp.LinedRectangle;
import com.nsdb.engine.glcomp.Rectangle;
import com.nsdb.engine.glcomp.StringTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.component.CircleButton;
import com.yad.harpseal.constant.Screen;

/**
 * 게임 일시정지시 화면에 뜨는 일시정지 윈도우입니다.<br>
 * 눌린 버튼에 따라 게임 스테이지에게 게임 재시작, 게임 재개, 옵션 변경 등의 동작을 요청합니다.
 */
public class PauseWindow extends ManagerGameObject {
	
	private float cx,cy;
	private boolean show;
	private boolean restarting;
	
	private TransHelper helper;
	private Rectangle fade;
	private LinedRectangle window;
	private StringTexture title;
	private CircleButton restart,resume,option;
	
	private final static float WINDOW_WIDTH=500;
	private final static float WINDOW_HEIGHT=300;
	private final static float TXT_RX=0;
	private final static float TXT_RY=-60;
	private final static float TXT_SIZE=50;
	private final static float BTN_RX=150;
	private final static float BTN_RY=40;
	private final static float BTN_RADIUS=50;
	private final static float BTN_TXT_SIZE=20;

	public PauseWindow(Communicable con) {
		super(con);
		show=false;
		restarting=false;
		cx=Screen.WIDTH/2;
		cy=Screen.HEIGHT/2;
		helper=new TransHelper();
		fade=new Rectangle(Screen.WIDTH,Screen.HEIGHT);
		fade.setColor(0, 0, 0, 0.5f);
		window=new LinedRectangle(WINDOW_WIDTH,WINDOW_HEIGHT,5);
		window.setLineColor(0, 0, 0, 1);
		window.setColor(0.5f, 0.5f, 0.75f, 1);
		title=new StringTexture("일시정지중...",TXT_SIZE,Align.CENTER);
		title.setColor(1, 1, 1, 1);
		title.loadString(this);
		restart=new CircleButton(this, "restart", "재시작", cx-BTN_RX, cy+BTN_RY, BTN_RADIUS, BTN_TXT_SIZE);
		resume=new CircleButton(this, "resume", "진행", cx, cy+BTN_RY, BTN_RADIUS, BTN_TXT_SIZE);
		option=new CircleButton(this, "option", "옵션", cx+BTN_RX, cy+BTN_RY, BTN_RADIUS, BTN_TXT_SIZE);
		startControl(restart);
		startControl(resume);
		startControl(option);
	}

	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(!show) return;
		if(restarting) return;
		if(layer != Layer.WINDOW) return;
		super.receiveMotion(ev, layer);
		if(!ev.isProcessed()) {
			float rx=ev.getX()-cx;
			float ry=ev.getY()-cy;
			if(rx<WINDOW_WIDTH/2 && rx>-WINDOW_WIDTH/2 && ry<WINDOW_HEIGHT/2 && ry>-WINDOW_HEIGHT/2)
				ev.process();
		}
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(!show) return;
		if(layer != Layer.WINDOW) return;
		fade.draw(gl);
		helper.setBasePoint(gl, cx, cy, Screen.WIDTH, Screen.HEIGHT);
		window.draw(gl);
		helper.translate(gl, TXT_RX, TXT_RY);
		title.draw(gl);
		helper.rollback(gl);
		super.drawScreen(gl, layer);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("show")) {
			show=true;
			return 1;
		}
		else if(msgs[0].equals("hide")) {
			show=false;
			return 1;
		}
		else if(msgs[0].equals("reset")) {
			show=false;
			restarting=false;
			return 1;
		}
		else if(msgs[0].equals("clicked")) {
			if(msgs[1].equals("restart")) {
				con.send("gameRestart");
				restarting=true;
				return 1;
			} else if(msgs[1].equals("resume")) {
				con.send("gameResume");
				return 1;
			} else if(msgs[1].equals("option")) {
				// TODO
				return 1;
			} else return super.send(msg);
		}
		else return super.send(msg);
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return title.isLoaded() &&
					(Boolean)restart.get("isLoaded") &&
					(Boolean)resume.get("isLoaded") &&
					(Boolean)option.get("isLoaded");
		else
			return super.get(name);
	}

}
