package com.yad.harpseal.constant;

/**
 * 방향 상수와 사용을 편리하게 하기 위한 메서드들이 들어있는 클래스입니다.
 */
public class Direction {

	public final static int NONE=0;
	public final static int UP=1;
	public final static int DOWN=2;
	public final static int LEFT=3;
	public final static int RIGHT=4;
	
	public static int clockwise(int direction) {
		switch(direction) {
		case UP: return RIGHT;
		case DOWN: return LEFT;
		case LEFT: return UP;
		case RIGHT: return DOWN;
		default: return direction;
		}
	}
	
	public static int clockwiseR(int direction) {
		switch(direction) {
		case UP: return LEFT;
		case DOWN: return RIGHT;
		case LEFT: return DOWN;
		case RIGHT: return UP;
		default: return direction;
		}
	}
	
	public static int reverse(int direction) {
		switch(direction) {
		case UP: return DOWN;
		case DOWN: return UP;
		case LEFT: return RIGHT;
		case RIGHT: return LEFT;
		default: return direction;
		}
	}

}
