package com.yad.harpseal.game;

import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameLog;
import com.yad.harpseal.R;
import com.yad.harpseal.component.Fade;
import com.yad.harpseal.game.map.GameMap;

/**
 * 실제 게임이 진행되는 게임 스테이지입니다. 스테이지 그룹, 번호, 이동 수, 물고기 수 등의 스테이지 정보를 기록, 관리합니다.
 * 또한 타일, 캐릭터, 배경 등이 묶여있는 게임 맵, 조이스틱, 점수 표시기, 일시정지 버튼, 일시정지 창, 결과창 등의 게임 진행에 관련된 객체들도 생성, 관리합니다.<br>
 * actionName 멤버를 통해 현재 게임 스테이지의 상태를 확인할 수 있습니다.<br>
 * playable 멤버를 통해 현재 게임 진행 가능 여부를 확인할 수 있습니다.
 */
public class GameStage extends ManagerGameObject {
	
	// stage status
	private int stageGroup;
	private int stageNumber;
	private int stepCount;
	private int fishCount;
	private final static int FISH_MAX=3;

	// game objects
	private GameMap map;
	private Joystick stick;
	private ScoreCounter counter;
	private PauseBtn pb;
	private PauseWindow pw;
	private SuccessWindow sw;
	
	// stage action
	private int actionName;
	private boolean playable;
	private final static int ACT_START=1;
	private final static int ACT_PLAY=2;
	private final static int ACT_PAUSED=3;
	private final static int ACT_RESTART=4;
	private final static int ACT_SUCCESS=5;
	private final static int ACT_NEXT=6;
	private final static int ACT_END=7;
	
	// drawable
	private Fade fade;
	private final static int FADE_TIME=500;
	
	public GameStage(Communicable con, int stageGroup, int stageNumber) {
		super(con);
		
		// stage status
		this.stageGroup=stageGroup;
		this.stageNumber=stageNumber;
		this.stepCount=0;
		this.fishCount=0;
		////
		
		// game objects
		map=new GameMap(this,stageGroup,stageNumber);
		stick=new Joystick(this);
		counter=new ScoreCounter(this);
		pb=new PauseBtn(this);
		pw=new PauseWindow(this);
		sw=new SuccessWindow(this);
		startControl(map);
		startControl(stick);
		startControl(counter);
		startControl(pb);
		startControl(pw);
		startControl(sw);
		
		// stage action
		changeAction(ACT_START);

		// music
		con.send("playSound/"+R.raw.sample_bgm);
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("stickAction")) {
			map.send("movePlayer/"+msgs[1]);
			return 1;
		}
		else if(msgs[0].equals("playerStepped")) {
			stepCount+=1;
			return 1;
		}
		else if(msgs[0].equals("fishEaten")) {
			fishCount+=1;
			if(fishCount>FISH_MAX)
				GameLog.danger("FishCount value is more than FISH_MAX value!");
			return 1;
		}
		else if(msgs[0].equals("playerReached")) {
			changeAction(ACT_SUCCESS);
			sw.send("show");
			return 1;
		}
		else if(msgs[0].equals("gamePause")) {
			changeAction(ACT_PAUSED);
			pw.send("show");
			return 1;
		}
		else if(msgs[0].equals("gameResume")) {
			changeAction(ACT_PLAY);
			pw.send("hide");
			return 1;
		}
		else if(msgs[0].equals("gameRestart")) {
			changeAction(ACT_RESTART);
			return 1;
		}
		else if(msgs[0].equals("gameToNext")) {
			changeAction(ACT_NEXT);
			return 1;
		}
		else if(msgs[0].equals("gameEnd")) {
			changeAction(ACT_END);
			return 1;
		}
		else if(msgs[0].equals("fadeEnd")) {
			switch(actionName) {
			case ACT_START: changeAction(ACT_PLAY); break;
			case ACT_RESTART: restart(); break;
			case ACT_NEXT: toNext(); break;
			case ACT_END: con.send("gameEnded"); break;
			}
			return 1;
		}
		else return super.send(msg);
	}

	@Override
	public Object get(String name) {
		if(name.equals("stepCount"))
			return stepCount;
		else if(name.equals("fishCount"))
			return fishCount;
		else if(name.equals("fishMax"))
			return FISH_MAX;
		else if(name.equals("playable"))
			return playable;
		else if(name.equals("isLoaded"))
			return (Boolean)map.get("isLoaded") &&
					(Boolean)stick.get("isLoaded") &&
					(Boolean)counter.get("isLoaded") &&
					(Boolean)pb.get("isLoaded") &&
					(Boolean)pw.get("isLoaded") &&
					(Boolean)sw.get("isLoaded");
		else
			return super.get(name);
	}
	
	
	//// private
	
	private void changeAction(int actNa) {
		GameLog.info("Stage action changed : "+actionName+" -> "+actNa);
		actionName=actNa;
		
		// playable check
		switch(actionName) {
		case ACT_START: case ACT_PLAY:
			playable=true;
			stick.send("unlock");
			break;
		case ACT_PAUSED: case ACT_RESTART: case ACT_END: case ACT_SUCCESS: case ACT_NEXT:
			playable=false;
			stick.send("lock");
			break;
		default:
			GameLog.error("Invalid action name : "+actionName);
			playable=true;
			break;
		}
		
		// fade effect
		if(fade!=null) stopControl(fade);
		switch(actionName) {
		case ACT_START: fade=new Fade(this, Fade.OUT, Fade.WHITE, FADE_TIME); break;
		case ACT_RESTART: case ACT_NEXT: fade=new Fade(this, Fade.IN, Fade.WHITE, FADE_TIME); break;
		case ACT_END: fade=new Fade(this, Fade.IN, Fade.BLACK, FADE_TIME); break;
		}
		if(fade!=null) startControl(fade);
	}
	
	private void restart() {
		map.send("reset");
		pw.send("reset");
		sw.send("reset");
		stepCount=0;
		fishCount=0;
		changeAction(ACT_START);	
	}
	
	private void toNext() {
		GameLog.info("Next stage Loading...");
		stageNumber+=1;
		map.send("changeToNext");

		pw.send("reset");
		sw.send("reset");
		stepCount=0;
		fishCount=0;
		changeAction(ACT_START);		
	}
	
	
	
}
