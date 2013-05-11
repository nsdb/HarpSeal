package com.yad.harpseal.gameobj;

import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Controllable;

public abstract class GameObject implements Controllable,Communicable {

	protected final Communicable con;
	
	public GameObject(Communicable con) {
		this.con=con;
	}
}
