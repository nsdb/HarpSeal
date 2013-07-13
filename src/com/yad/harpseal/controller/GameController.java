package com.yad.harpseal.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.game.GameStage;
import com.yad.harpseal.gameobj.main.MainStage;
import com.yad.harpseal.util.HarpEvent;

public class GameController extends GameControllerBase {

	GameObject stage;

	public GameController(Context context,SurfaceHolder holder) {
		super(context,holder);
		stage=new MainStage(this);
	}

	@Override
	public void playGame(int ms) {
		stage.playGame(ms);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		stage.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		stage.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		stage.restoreData();
	}
	
	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");

		if(msgs[0].equals("gameStart")) {
			stage.restoreData();
			stage=null;
			stage=new GameStage(this,Integer.parseInt(msgs[1]),Integer.parseInt(msgs[2]));
			return 1;
		}
		else if(msgs[0].equals("gameEnd")) {
			stage.restoreData();
			stage=null;
			stage=new MainStage(this);
			return 1;
		}
		else return super.send(msg);
		
	}

}
