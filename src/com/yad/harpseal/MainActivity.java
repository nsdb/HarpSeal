package com.yad.harpseal;

import com.nsdb.engine.GameActivity;
import com.yad.harpseal.constant.Screen;

import android.os.Bundle;

/**
 * 맨 처음 실행되는 액티비티입니다. 화면을 유지하고, 게임의 실행을 돕습니다.
 */
public class MainActivity extends GameActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setGameScreenValue(Screen.WIDTH,Screen.HEIGHT,Screen.HORIZONTAL);
		setRoot( new RootObject(getController()) );
		setStatusBarVisible(true);
		startGame();
	}
	
}
