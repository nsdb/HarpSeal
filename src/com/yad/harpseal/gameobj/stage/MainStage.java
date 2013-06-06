package com.yad.harpseal.gameobj.stage;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.Joystick;
import com.yad.harpseal.gameobj.SampleField;
import com.yad.harpseal.gameobj.SampleUI;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;

public class MainStage extends GameObject {
	
	SampleField field;
	SampleUI ui;
	Joystick stick;
	
	public MainStage(Communicable con) {
		super(con);
		field=new SampleField(this);
		ui=new SampleUI(this);
		stick=new Joystick(this);
	}

	@Override
	public void playGame(int ms) {
		field.playGame(ms);
		ui.playGame(ms);
		stick.playGame(ms);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		field.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		ui.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		stick.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		field.drawScreen(c, p, layer);
		ui.drawScreen(c, p, layer);
		stick.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		field.restoreData();
		ui.restoreData();
		stick.restoreData();
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
