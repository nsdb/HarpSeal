package com.yad.harpseal;

import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.util.Communicable;
import com.yad.harpseal.game.GameStage;
import com.yad.harpseal.main.MainStage;

/**
 * 맨 처음 실행되는 게임 객체입니다. 메인 스테이지와 게임 스테이지를 관리합니다.
 */
public class RootObject extends ManagerGameObject {

	private GameObject stage;
	private boolean stageLoaded;
	private int time;
	private final static int MIN_INTERVAL=500;

	public RootObject(Communicable con) {
		super(con);
		stage=new MainStage(this);
		stageLoaded=false;
		time=0;
	}
	
	@Override
	public void playGame(int ms) {
		super.playGame(ms);
		time+=ms;
		if(!stageLoaded && (Boolean)stage.get("isLoaded")==true && time>MIN_INTERVAL) {
			stageLoaded=true;
			startControl(stage);
		}
		
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("gameStart")) {
			stopControl(stage);
			stage=new GameStage(this,Integer.parseInt(msgs[1]),Integer.parseInt(msgs[2]));
			stageLoaded=false;
			time=0;
			return 1;
		}
		else if(msgs[0].equals("gameEnded")) {
			stopControl(stage);
			stage=new MainStage(this);
			stageLoaded=false;
			time=0;
			return 1;
		}
		else return super.send(msg);
		
	}

}
