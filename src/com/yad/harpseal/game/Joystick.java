package com.yad.harpseal.game;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.glcomp.LinedCircle;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.Func;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Screen;

/**
 * 플레이어 캐릭터의 조작에 사용되는 조이스틱 클래스입니다. 스틱이 기울면 게임 스테이지에게 지속적으로 자신의 상태를 보냅니다.<br>
 * lock/unlock 메시지를 통해 사용 가능 여부를 정할 수 있습니다. <br>
 * (GameStage의 playable이 false가 될 경우 함께 상태가 정해집니다)<br>
 * 소유한 게임 객체가 없으므로 super.playGame 등을 사용하지 않습니다.
 */
public class Joystick extends GameObject {
	
	private float padX,padY;
	private float grabX,grabY;
	private float stickX,stickY;
	private int activeDirection;
	private boolean grabbed;
	private boolean locked;
	
	private TransHelper helper;
	private LinedCircle pad;
	private LinedCircle stick;

	private final static int PAD_MARGIN=40;
	private final static int PAD_RADIUS=100;
	private final static int RANGE_GRAB=90;
	private final static int RANGE_ACTIVE=50;
	private final static int RANGE_MAX=70;
	private final static int STICK_RADIUS=80;
	
	public Joystick(Communicable con) {
		super(con);
		padX=Screen.WIDTH-PAD_RADIUS-PAD_MARGIN;
		padY=Screen.HEIGHT-PAD_RADIUS-PAD_MARGIN;
		grabX=0;
		grabY=0;
		stickX=0;
		stickY=0;
		activeDirection=Direction.NONE;
		grabbed=false;
		locked=false;
		helper=new TransHelper();
		pad=new LinedCircle(PAD_RADIUS,32,5);
		pad.setLineColor(0, 0, 0, 0.25f);
		pad.setColor(0.8f, 0.8f, 0.8f, 0.25f);
		stick=new LinedCircle(STICK_RADIUS,32,5);
		stick.setLineColor(0, 0, 0, 0.25f);
		stick.setColor(0.8f, 0.8f, 0.8f, 0.25f);
	}

	@Override
	public void playGame(int ms) {
		if(activeDirection != Direction.NONE)
			con.send("stickAction/"+activeDirection);
	}

	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(layer != Layer.WINDOW) return;
		if(locked) return;
		
		// grab and stick point check
		float distanFromPadCenter=Func.distan(padX,padY,ev.getX(),ev.getY());
		switch(ev.getType()) {
		case GameEvent.MOTION_DOWN:
			if(distanFromPadCenter<=RANGE_GRAB) {
				grabbed=true;
				grabX=ev.getX();
				grabY=ev.getY();
				stickX=0;
				stickY=0;
				ev.process();
			}
			break;
		case GameEvent.MOTION_DRAG:
			if(grabbed) {
				stickX=ev.getX()-grabX;
				stickY=ev.getY()-grabY;
				ev.process();
			}
			break;
		case GameEvent.MOTION_UP:
			if(grabbed) {
				grabbed=false;
				stickX=0;
				stickY=0;
				ev.process();
			}
			break;
		}
		
		// stick active and direction check
		float distanFromGrabPoint=Func.distan(0, 0, stickX, stickY);
		if(distanFromGrabPoint>RANGE_MAX) {
			stickX*=RANGE_MAX/distanFromGrabPoint;
			stickY*=RANGE_MAX/distanFromGrabPoint;
			distanFromGrabPoint=RANGE_MAX;
		}
		if(distanFromGrabPoint>=RANGE_ACTIVE) {
			if(stickX>=Math.abs(stickY))
				activeDirection=Direction.RIGHT;
			else if(stickX<=stickY && stickX<=-stickY)
				activeDirection=Direction.LEFT;
			else if(Math.abs(stickX)<stickY)
				activeDirection=Direction.DOWN; 
			else
				activeDirection=Direction.UP;
		} else {
			activeDirection=Direction.NONE;
		}
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {		
		if(layer != Layer.WINDOW) return;
		
		helper.setBasePoint(gl, padX, padY, Screen.WIDTH, Screen.HEIGHT);
		pad.draw(gl);
		helper.translate(gl, stickX, stickY);
		stick.draw(gl);
		helper.rollback(gl);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("lock")) {
			stickX=0;
			stickY=0;
			activeDirection=Direction.NONE;
			grabbed=false;
			locked=true;
			return 1;
		}
		else if(msgs[0].equals("unlock")) {
			locked=false;
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("activeDirection"))
			return activeDirection;
		else
			return super.get(name);
	}

}
