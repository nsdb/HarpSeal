package com.yad.harpseal.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.yad.harpseal.gameobj.stage.MainStage;
import com.yad.harpseal.util.HarpEvent;

public class GameController extends GameControllerBase {

	MainStage main;

	public GameController(Context context,SurfaceHolder holder) {
		super(context,holder);
		main=new MainStage(this);
	}

	@Override
	public void playGame(int ms) {
		main.playGame(ms);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		main.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		main.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		main.restoreData();
	}

}
