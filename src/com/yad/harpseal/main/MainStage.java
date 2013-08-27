package com.yad.harpseal.main;

import javax.microedition.khronos.opengles.GL10;

import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.util.Communicable;
import com.yad.harpseal.R;
import com.yad.harpseal.component.Fade;
import com.yad.harpseal.component.ImageButton;
import com.yad.harpseal.constant.Screen;

/**
 * 맨 처음 화면에 보이는 스테이지인 메인 스테이지입니다. 게임 시작, 옵션 설정 등을 할 수 있습니다.(아직 안되지만)
 */
public class MainStage extends ManagerGameObject {
	
	private int state;
	private final static int STATE_NORMAL=1;
	private final static int STATE_START=2;
	
	private BitmapTexture bg;
	private ImageButton start,option;
	private Fade fade;
	private final static int FADE_TIME=500;
	
	public MainStage(Communicable con) {
		super(con);
		state=STATE_NORMAL;
		bg=new BitmapTexture(Screen.WIDTH,Screen.HEIGHT,R.drawable.main);
		bg.loadBitmap(con);
		start=new ImageButton(this, "start", Screen.WIDTH*0.25f, Screen.HEIGHT*0.9f, Screen.WIDTH*0.4f, R.drawable.button_start);
		option=new ImageButton(this, "option", Screen.WIDTH*0.75f, Screen.HEIGHT*0.9f, Screen.WIDTH*0.4f, R.drawable.button_option);
		fade=new Fade(this, Fade.OUT, Fade.BLACK, FADE_TIME);
		startControl(start);
		startControl(option);
		startControl(fade);
		con.send("playSound/"+R.raw.sample_opening);
	}

	@Override
	public void drawScreen(GL10 gl,int layer) {
		super.drawScreen(gl, layer);
		if(layer != Layer.BACKGROUND) return;
		bg.draw(gl);
	}
	
	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("clicked")) {
			
			if(state==STATE_START) return 1;
			if(msgs[1].equals("start")) {
				
				if(fade!=null) stopControl(fade);
				fade=new Fade(this, Fade.IN, Fade.BLACK, FADE_TIME);
				startControl(fade);
				state=STATE_START;
				return 1;
				
			} else if(msgs[1].equals("option")) {
				
				// TODO
				return 1;
				
			} else return super.send(msg);
		}
		else if(msgs[0].equals("fadeEnd")) {
			
			if(state==STATE_START) {
				con.send("gameStart/1/1");
				return 1;
			}
			stopControl(fade);
			fade=null;
			return 1;
		}
		else return super.send(msg);
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return bg.isLoaded() &&
					(Boolean)start.get("isLoaded") &&
					(Boolean)option.get("isLoaded");
		else
			return super.get(name);
	}

}
