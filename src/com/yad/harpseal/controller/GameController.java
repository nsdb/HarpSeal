package com.yad.harpseal.controller;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.yad.harpseal.gameobj.*;
import com.yad.harpseal.util.HarpEvent;

public class GameController extends GameControllerBase {

	SampleField field;
	ArrayList<SampleBox> boxes;
	SampleUI ui;
	
	public GameController(Context context,SurfaceHolder holder) {
		super(context,holder);
		field=new SampleField(this);
		boxes=new ArrayList<SampleBox>();
		for(int i=0;i<10;i++)
			boxes.add(new SampleBox(this));
		ui=new SampleUI(this);
	}

	@Override
	public void playGame(int ms) {
		
		field.playGame(ms);
		for(int i=0;i<boxes.size();i++)
			boxes.get(i).playGame(ms);
		ui.playGame(ms);
		
	}

	@Override
	public void receiveMotion(HarpEvent ev,int layer) {
		
		field.receiveMotion(ev,layer);
		if(ev.isProcessed()) return;
		for(int i=0;i<boxes.size();i++) {
			boxes.get(i).receiveMotion(ev,layer);
			if(ev.isProcessed()) return;
		}
		ui.receiveMotion(ev,layer);
		
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		
		field.drawScreen(c, p, layer);
		p.reset();
		for(int i=0;i<boxes.size();i++) {
			boxes.get(i).drawScreen(c, p, layer);
			p.reset();
		}
		ui.drawScreen(c, p, layer);
		p.reset();
		
	}

	@Override
	public void restoreData() {
		field.restoreData();
		for(int i=0;i<boxes.size();i++)
			boxes.get(i).restoreData();
		ui.restoreData();
	}

}
