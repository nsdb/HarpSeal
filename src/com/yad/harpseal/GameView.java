package com.yad.harpseal;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	public GameView(Context context) {
		super(context);
		System.out.println("View class created");
		
		// holder hash will not be changed until this class(SurfaceView) is newly created.
		SurfaceHolder holder=getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("Surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("Surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("Surface destroyed");
	}

}
