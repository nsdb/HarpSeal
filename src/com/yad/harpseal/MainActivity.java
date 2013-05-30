package com.yad.harpseal;

import com.yad.harpseal.controller.GameController;
import com.yad.harpseal.util.HarpLog;

import android.os.Bundle;
import android.view.MotionEvent;
import android.app.Activity;

public class MainActivity extends Activity {
	
	GameView view;
	GameController thread;

	// game start
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HarpLog.init("");
		HarpLog.info("MainActivity created");
		
		// init
		view=new GameView(getApplicationContext());
		thread=new GameController(this,view.getHolder());
		setContentView(view);
		thread.start();
		
	}
	
	// game restart
	@Override
	public void onResume() {
		super.onResume();
		HarpLog.info("MainActivity resumed");
		thread.restart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// game pause
		HarpLog.info("MainActivity paused");				
		thread.pause();
		
		// game end
		if(this.isFinishing()) {
			HarpLog.info("MainActivity destroyed");
			thread.end();
		}
	}
	
	// touch event
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		thread.pushEvent(event);
		return true;
	}
	
}
