package com.yad.harpseal.gameobj.stage;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.SampleField;
import com.yad.harpseal.gameobj.character.GoalFlag;
import com.yad.harpseal.gameobj.character.PlayerSeal;
import com.yad.harpseal.gameobj.tile.NormalTile;
import com.yad.harpseal.gameobj.window.Joystick;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GameStage extends GameObject {
	
	// sample map structure
	private final static String[] tileSample= {
		"11000",
		"01000",
		"01110",
		"01010",
		"01011",
		"00001"
	};
	private final static String[] charSample= {
		"10000",
		"00000",
		"00000",
		"00000",
		"00000",
		"00002"
	};
	
	// map
	private String[] tileString;
	private String[] charString;
	private int mapWidth,mapHeight;
	private ArrayList<GameObject> tiles;
	private ArrayList<GameObject> characters;
	
	// field
	SampleField field;
	
	// user interface
	Joystick stick;
	
	public GameStage(Communicable con) {
		super(con);
		field=new SampleField(this);
		stick=new Joystick(this);
		
		// map vaildity check... later
		tileString=tileSample;
		charString=charSample;
		////
		
		// read map
		mapWidth=tileString[0].length();
		mapHeight=tileString.length;
		tiles=new ArrayList<GameObject>();
		characters=new ArrayList<GameObject>();
		for(int y=0;y<mapHeight;y++) {
			for(int x=0;x<mapWidth;x++) {
				// create tiles
				switch(tileString[y].charAt(x)) {
				case '0': break;
				case '1': tiles.add(new NormalTile(this,x,y)); break;
				default: HarpLog.danger("Invalid tile type"); break;
				}
				// create characters
				switch(charString[y].charAt(x)) {
				case '0': break;
				case '1': characters.add(new PlayerSeal(this,x,y)); break;
				case '2': characters.add(new GoalFlag(this,x,y)); break;
				default: HarpLog.danger("Invalid character type"); break;
				}
			}
		}
	}

	@Override
	public void playGame(int ms) {
		for(GameObject o : tiles)
			o.playGame(ms);
		for(GameObject o : characters)
			o.playGame(ms);
		field.playGame(ms);
		stick.playGame(ms);
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		for(GameObject o : tiles) {
			o.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
		}
		for(GameObject o : characters) {
			o.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
		}
		field.receiveMotion(ev, layer);
		if(ev.isProcessed()) return;
		stick.receiveMotion(ev, layer);
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		for(GameObject o : tiles)
			o.drawScreen(c, p, layer);
		for(GameObject o : characters)
			o.drawScreen(c, p, layer);
		field.drawScreen(c, p, layer);
		stick.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		for(GameObject o : tiles)
			o.restoreData();
		for(GameObject o : characters)
			o.restoreData();
		field.restoreData();
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