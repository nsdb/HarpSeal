package com.yad.harpseal;

import com.yad.harpseal.util.HarpLog;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	public GameView(Context context) {
		super(context);
		HarpLog.info("GameView created");
		
		// holder hash will not be changed until this class(SurfaceView) is newly created.
		SurfaceHolder holder=getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		HarpLog.info("Surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		HarpLog.info("Surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		HarpLog.info("Surface destroyed");
	}

}
