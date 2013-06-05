package com.yad.harpseal.controller;

import java.util.LinkedList;
import java.util.Queue;

import com.yad.harpseal.constant.Layer;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Controllable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public abstract class GameControllerBase extends Thread implements Controllable,Communicable {
	
	// common
	private Context context;
	
	// thread
	private boolean isPaused;
	private boolean isEnded;
	
	// game play
	private int period;
	private final static int PERIOD_BASE=15;
	
	// motion event
	private Queue<HarpEvent> event;
	private boolean pressed;
	private float pressX,pressY;
	private int pressTime;
	private final static int CLICK_RANGE=50;
	private final static int CLICK_TIME=500;
	private final static int LONGCLICK_TIME=1000;

	// drawing
	private SurfaceHolder holder;
	private Paint paint;
	private final static int SCREEN_X=600;
	private final static int SCREEN_Y=800;
	
	// sound
	private MediaPlayer mediaPlayer;
	private SoundPool soundPool;
	
	
	public GameControllerBase(Context context,SurfaceHolder holder) {
		this.context=context;
		this.isPaused=false;
		this.isEnded=false;
		this.period=PERIOD_BASE;
		this.event=new LinkedList<HarpEvent>();
		this.pressed=false;
		this.pressTime=0;
		this.holder=holder;
		this.paint=new Paint();
		this.mediaPlayer=new MediaPlayer();
		this.soundPool=new SoundPool(7,AudioManager.STREAM_MUSIC,0);
		HarpLog.info("GameController created");
	}
	
	@Override
	public final void start() {
		super.start();
		HarpLog.info("Game thread started");
	}

	@Override
	public final void run() {
		super.run();
		HarpLog.info("Game thread is running");
		
		// temp variable (Frequently changed)
		Canvas c=null;
		long fms,lms;
		float scaleRate;
		float transHeight;
		HarpEvent ev=null;
		
		while(!isEnded) {
			
			// do thread's work (including draw screen)
			while(!isPaused) {

				try {

					// timer start
					fms=System.currentTimeMillis();
					
					// game play
					playGame(period);
					
					// check screen is available
					c=holder.lockCanvas(null);
					if(c!=null) {
						
						// calculate scale, trans
						scaleRate=(float)c.getWidth()/SCREEN_X;
						transHeight=(float)(c.getHeight()-SCREEN_Y*scaleRate)*0.5f;
						
						// long click check
						if(pressed) {
							pressTime+=period;
							if(pressTime>=LONGCLICK_TIME) {
								event.add(new HarpEvent(HarpEvent.MOTION_LONGCLICK,pressX,pressY));
								HarpLog.debug("Action LongClick : "+ev.getX()+", "+ev.getY());
								pressed=false;
								pressTime=0;
							}
						}
						
						// process events
						while(event.peek()!=null) {
							ev=event.poll();
							ev.regulate(scaleRate, transHeight);
							for(int i=Layer.LAYER_SIZE-1;(i>0 && ev.isProcessed()==false);i--) {
								receiveMotion(ev,i);
							}
						}

						// drawing
						paint.reset();
						paint.setColor(0xFF000000);
						c.drawRect(0,0,c.getWidth(),c.getHeight(),paint);
						c.translate(0,transHeight);
						c.scale(scaleRate,scaleRate);
						for(int i=0;i<Layer.LAYER_SIZE;i++)
							drawScreen(c,paint,i);
						
						// restore canvas
						holder.unlockCanvasAndPost(c);
						c=null;
					}
					
					// timer end
					lms=System.currentTimeMillis();
					if(fms+period<lms) Thread.sleep(lms-fms-period);
					
					// time loss need to be processed
					
				} catch (InterruptedException e) {
				
					// print error
					e.printStackTrace();

					// time loss need to be processed but.. I think this exception cannot be called
					HarpLog.danger("Interrupted Exception caught! It makes time loss!");

				}
				
			}
			////
			
			// wait
			HarpLog.info("Game thread is waiting being restarted");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			////
		}

		// restore data
		HarpLog.info("Game thread ended");
		restoreData();
		mediaPlayer.release();
		soundPool.release();
	}
	
	public final void pause() {
		HarpLog.info("Game thread paused");
		isPaused=true;
		mediaPlayer.pause();
	}
	
	public final void restart() {
		HarpLog.info("Game thread restarted");
		isPaused=false;		
		mediaPlayer.start();
	}

	public final void end() {
		HarpLog.info("Game thread will be ended");
		isEnded=true;
	}
	
	public final void pushEvent(MotionEvent ev) {
		switch(ev.getAction()) {

		case MotionEvent.ACTION_DOWN:
			event.add(new HarpEvent(HarpEvent.MOTION_DOWN,ev.getX(),ev.getY()));
			HarpLog.debug("Action Down : "+ev.getX()+", "+ev.getY());
			pressed=true;
			pressX=ev.getX();
			pressY=ev.getY();
			pressTime=0;
			break;
			
		case MotionEvent.ACTION_UP:
			event.add(new HarpEvent(HarpEvent.MOTION_UP,ev.getX(),ev.getY()));
			HarpLog.debug("Action Up : "+ev.getX()+", "+ev.getY());
			if(pressed && pressTime<CLICK_TIME) {
				event.add(new HarpEvent(HarpEvent.MOTION_CLICK,ev.getX(),ev.getY()));
				HarpLog.debug("Action Click : "+ev.getX()+", "+ev.getY());
			}
			pressed=false;
			pressTime=0;
			break;
			
		case MotionEvent.ACTION_MOVE:
			event.add(new HarpEvent(HarpEvent.MOTION_DRAG,ev.getX(),ev.getY()));
			HarpLog.debug("Action Move : "+ev.getX()+", "+ev.getY());
			if( pressed && Math.max(Math.abs(ev.getX()-pressX),Math.abs(ev.getY()-pressY)) > CLICK_RANGE ) {
				HarpLog.debug("Click Range Out : "+(ev.getX()-pressX)+", "+(ev.getY()-pressY));
				pressed=false;
				pressTime=0;
			}
			break;
			
		}
	}
	
	@Override
	public int send(String msg) {
		HarpLog.debug("Controller received message : "+msg);
		
		String[] msgs=msg.split("/");
		if(msgs[0].equals("playSound")) {
			
			mediaPlayer.release();
			mediaPlayer=MediaPlayer.create(context,Integer.parseInt(msgs[1]));
			if(mediaPlayer!=null) {
				mediaPlayer.setLooping(true);
				mediaPlayer.start();
				return 1;
			}
			else return 0;
			
		} else if(msgs[0].equals("resetSound")) {
			
			mediaPlayer.reset();
			return 1;
			
		} else if(msgs[0].equals("loadChunk")) {
			
			return soundPool.load(context,Integer.parseInt(msgs[1]),0);
			
		} else if(msgs[0].equals("unloadChunk")) {
			
			if(soundPool.unload(Integer.parseInt(msgs[1]))==true)
				return 1;
			else
				return 0;
			
		} else if(msgs[0].equals("playChunk")) {
			
			if( soundPool.play(Integer.parseInt(msgs[1]),1f,1f,0,0,1f) != 0 )
				return 1;
			else
				return 0;
		}
		
		HarpLog.error("Controller couldn't understand message : "+msg);
		return 0;
		
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("screenX")) return SCREEN_X;
		else if(name.equals("screenY")) return SCREEN_Y;
		else {
			HarpLog.error("Controller received invalid name of get() : "+name);
			return null;
		}
	}
	
}
