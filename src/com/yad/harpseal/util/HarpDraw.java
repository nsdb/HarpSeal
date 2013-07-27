package com.yad.harpseal.util;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class HarpDraw {
	
	private Drawable drawable;
	private RectF bounds;
	
	public final static int ALIGN_TOPLEFT=0;
	public final static int ALIGN_TOPCEN=1;
	public final static int ALIGN_TOPRIGHT=2;
	public final static int ALIGN_MIDLEFT=3;
	public final static int ALIGN_MIDCEN=4;
	public final static int ALIGN_MIDRIGHT=5;
	public final static int ALIGN_BOTLEFT=6;
	public final static int ALIGN_BOTCEN=7;
	public final static int ALIGN_BOTRIGHT=8;
	
	public HarpDraw(Drawable d) {
		this.drawable=d;
		this.bounds=new RectF();
		// null check
		d.getBounds();
	}
	
	public void setBase(int align,float x,float y,float size) {
		float width=drawable.getIntrinsicWidth();
		float height=drawable.getIntrinsicHeight();
		
		// size change
		if(width<height) {
			width=width*size/height;
			height=size;
		} else {
			height=height*size/width;
			width=size;
		}
		
		// width
		switch(align%3) {
		case 0: bounds.left=x+(size-width)/2; break;
		case 1: bounds.left=x-width/2; break;
		case 2: bounds.left=x-width-(size-width)/2; break;
		}
		bounds.right=bounds.left+width;
		
		// height
		switch(align/3) {
		case 0: bounds.top=y+(size-height)/2; break;
		case 1: bounds.top=y-height/2; break;
		case 2: bounds.top=y-height-(size-height)/2; break;
		}
		bounds.bottom=bounds.top+height;
		
		// actual set
		confirm();
	}
	
	public void setAlpha(int alpha) {
		drawable.setAlpha(alpha);
	}
	
	public void drawOn(Canvas c) {
		drawable.draw(c);
	}
	
	private void confirm() {
		Rect rect=new Rect();
		rect.top=Math.round(bounds.top);
		rect.left=Math.round(bounds.left);
		rect.right=Math.round(bounds.right);
		rect.bottom=Math.round(bounds.bottom);
		drawable.setBounds(rect);
	}

}
