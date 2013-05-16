package com.yad.harpseal.gameobj;

import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.Controllable;
import com.yad.harpseal.util.HarpLog;

public abstract class GameObject implements Controllable,Communicable {

	protected final Communicable con;
	
	public GameObject(Communicable con) {
		this.con=con;
		HarpLog.debug("GameObject created : hash-"+this.hashCode());
	}
}
