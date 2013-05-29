package com.yad.harpseal.gameobj.util;

import android.graphics.Rect;

import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public abstract class HarpButton extends GameObject {
	
	Rect box;

	public HarpButton(Communicable con,Rect box) {
		super(con);
		this.box=box;
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object get(String name) {
		if(name.equals("box")) return box;
		else {
			HarpLog.error("HarpButton received invalid name of get() : "+name);
			return null;
		}
	}

}
