package com.yad.harpseal.gameobj.stage;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class MainStage extends GameObject {
	
	public MainStage(Communicable con) {
		super(con);
	}

	@Override
	public void playGame(int ms) {
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
	}

	@Override
	public void restoreData() {
	}

	@Override
	public int send(String msg) {
		return 0;
	}

	@Override
	public Object get(String name) {
		return null;
	}

}
