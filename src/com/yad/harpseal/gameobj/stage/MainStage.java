package com.yad.harpseal.gameobj.stage;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.SampleField;
import com.yad.harpseal.gameobj.SampleUI;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class MainStage extends GameObject {
	
	SampleField field;
	SampleUI ui;
	
	public MainStage(Communicable con) {
		super(con);
		field=new SampleField(this);
		ui=new SampleUI(this);
	}

	@Override
	public void playGame(int ms) {
		field.playGame(ms);
		ui.playGame(ms);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		field.receiveMotion(ev, layer);
		ui.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		field.drawScreen(c, p, layer);
		ui.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		field.restoreData();
		ui.restoreData();
	}

	@Override
	public int send(String msg) {
		return con.send(msg);
	}

	@Override
	public Object get(String name) {
		return con.get(name);
	}

}
