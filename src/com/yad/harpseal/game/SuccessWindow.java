package com.yad.harpseal.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.nsdb.engine.constant.Align;
import com.nsdb.engine.constant.Layer;
import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.glcomp.BitmapTexture;
import com.nsdb.engine.glcomp.Rectangle;
import com.nsdb.engine.glcomp.StringTexture;
import com.nsdb.engine.glcomp.VariableStringTexture;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameEvent;
import com.nsdb.engine.util.TransHelper;
import com.yad.harpseal.R;
import com.yad.harpseal.component.RectangleButton;
import com.yad.harpseal.constant.Screen;

/**
 * 한 스테이지를 끝냈을 때 보여지는 윈도우입니다.<br>
 * 눌린 버튼에 따라 게임 스테이지에게 게임 재시작, 다음 스테이지, 메인 매뉴로 이동 등을 요청할 수 있습니다.
 */
public class SuccessWindow extends ManagerGameObject {

	private int stepCount,fishCount,fishMax;	
	private boolean show;
	private boolean restarting;
	
	private TransHelper helper;
	private Rectangle bg;
	private VariableStringTexture stepTxt;
	private StringTexture fishTxt;
	private BitmapTexture fish;
	private RectangleButton restart,next,main;
	
	private final static int STEPTXT_SIZE=50;
	private final static int STEPTXT_MARGINBOT=30;
	private final static int FISHTXT_SIZE=50;
	private final static int FISHTXT_MARGINBOT=30;
	private final static int FISH_SIZE=150;
	private final static int FISH_BETWEEN=30;
	private final static int FISH_MARGINBOT=20;
	// center
	private final static int BUTTON1_X=200;
	private final static int BUTTON1_Y=75;
	private final static int BUTTON1_TXT=40;
	private final static int BUTTON1_MARGIN=100;
	private final static int BUTTON1_MARGINBOT=50;
	private final static int BUTTON2_X=400;
	private final static int BUTTON2_Y=75;
	private final static int BUTTON2_TXT=40;
	
	public SuccessWindow(Communicable con) {
		super(con);
		this.stepCount=0;
		this.fishCount=0;
		this.fishMax=0;
		this.show=false;
		this.restarting=false;
		helper=new TransHelper();
		bg=new Rectangle(Screen.WIDTH,Screen.HEIGHT);
		bg.setColor(0.8f, 0.8f, 1, 1);
		stepTxt=new VariableStringTexture("이동 횟수 : 1234567890",STEPTXT_SIZE,Align.CENTER);
		stepTxt.setColor(0, 0, 0, 1);
		stepTxt.loadString(con);
		fishTxt=new StringTexture("물고기 획득 수",FISHTXT_SIZE,Align.CENTER);
		fishTxt.setColor(0, 0, 0, 1);
		fishTxt.loadString(con);
		Context context=(Context)con.get("context");
		fish=new BitmapTexture(context, FISH_SIZE, R.drawable.crown_fish);
		fish.loadBitmap(con);
		restart=new RectangleButton(this, "restart", "재시작", Screen.WIDTH/2-(BUTTON1_MARGIN+BUTTON1_X)/2,
				Screen.HEIGHT/2+BUTTON1_Y/2, BUTTON1_X, BUTTON1_Y, BUTTON1_TXT);
		next=new RectangleButton(this, "next", "다음", Screen.WIDTH/2+(BUTTON1_MARGIN+BUTTON1_X)/2,
				Screen.HEIGHT/2+BUTTON1_Y/2, BUTTON1_X, BUTTON1_Y, BUTTON1_TXT);
		main=new RectangleButton(this, "main", "메인으로 돌아가기", Screen.WIDTH/2,
				Screen.HEIGHT/2+BUTTON1_Y+BUTTON1_MARGINBOT+BUTTON2_Y/2, BUTTON2_X, BUTTON2_Y, BUTTON2_TXT);
		startControl(restart);
		startControl(next);
		startControl(main);
	}

	@Override
	public void playGame(int ms) {
		super.playGame(ms);
		stepCount=(Integer)con.get("stepCount");
		fishCount=(Integer)con.get("fishCount");
		fishMax=(Integer)con.get("fishMax");
	}

	@Override
	public void receiveMotion(GameEvent ev, int layer) {
		if(!show) return;
		if(restarting) return;
		if(layer != Layer.WINDOW) return;
		super.receiveMotion(ev, layer);
		if(!ev.isProcessed()) ev.process();
	}

	@Override
	public void drawScreen(GL10 gl, int layer) {
		if(!show) return;
		if(layer != Layer.WINDOW) return;
		
		bg.draw(gl);
		helper.translate(gl, 0, -FISH_MARGINBOT-FISH_SIZE/2);
		float fishWidth=fishMax*FISH_SIZE+(fishMax-1)*FISH_BETWEEN;
		helper.translate(gl, -fishWidth/2, 0);
		for(int i=0;i<fishMax;i++) {
			helper.translate(gl, FISH_SIZE/2, 0);
			if(i<fishCount) fish.setColor(1, 1, 1, 1);
			else fish.setColor(1, 1, 1, 0.5f);
			fish.draw(gl);
			helper.translate(gl, FISH_SIZE/2+FISH_BETWEEN, 0);
		}
		helper.translate(gl, -FISH_BETWEEN-fishWidth/2, -FISH_SIZE/2-FISHTXT_MARGINBOT-FISHTXT_SIZE/2);
		fishTxt.draw(gl);
		helper.translate(gl, 0, -FISHTXT_SIZE/2-STEPTXT_MARGINBOT-STEPTXT_SIZE/2);
		stepTxt.setPrintString("이동 횟수 : "+stepCount);
		stepTxt.draw(gl);
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
			} else if(msgs[1].equals("next")) {
				con.send("gameToNext");
				restarting=true;
				return 1;
			} else if(msgs[1].equals("main")) {
				con.send("gameEnd");
				restarting=true;
				return 1;
			} else return super.send(msg);
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("isLoaded"))
			return fish.isLoaded() &&
					(Boolean)restart.get("isLoaded") &&
					(Boolean)next.get("isLoaded") &&
					(Boolean)main.get("isLoaded");
		else
			return super.get(name);
	}
}
