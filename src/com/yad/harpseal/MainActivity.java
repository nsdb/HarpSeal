package com.yad.harpseal;

import com.yad.harpseal.controller.GameController;

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
		System.out.println("MainActivity created");
		
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
		System.out.println("MainActivity resumed");
		thread.restart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// game pause
		System.out.println("MainActivity paused");				
		thread.pause();
		
		// game end
		if(this.isFinishing()) {
			System.out.println("MainActivity destroyed");
			thread.end();
		}
	}
	
	// touch event
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("MainActivity received TouchEvent");
		thread.pushEvent(event.getAction(),event.getX(),event.getY());
		return true;
	}
	
}
