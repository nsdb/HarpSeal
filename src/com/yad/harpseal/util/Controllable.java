package com.yad.harpseal.util;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface Controllable {

	public void playGame(int ms);
	public void receiveMotion(HarpEvent ev,int layer);
	public void drawScreen(Canvas c,Paint p,int layer);
	public void restoreData();
}
