package com.yad.harpseal.gameobj.game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.constant.TileType;
import com.yad.harpseal.gameobj.GameObject;
import com.yad.harpseal.gameobj.game.map.BrakingTile;
import com.yad.harpseal.gameobj.game.map.Fish;
import com.yad.harpseal.gameobj.game.map.GoalFlag;
import com.yad.harpseal.gameobj.game.map.NormalTile;
import com.yad.harpseal.gameobj.game.map.PlayerSeal;
import com.yad.harpseal.gameobj.game.map.RotatableTile;
import com.yad.harpseal.util.Communicable;
import com.yad.harpseal.util.HarpEvent;
import com.yad.harpseal.util.HarpLog;

public class GameMap extends GameObject {

	// map status
	private int stageGroup;
	private int stageNumber;
	private int mapWidth,mapHeight;
	private String[] tileString;
	private String[] charString;
	
	// map objects
	private ArrayList<GameObject> tiles;
	private ArrayList<GameObject> characters;
	private Queue<GameObject> sentenced;
	private PlayerSeal player;	// also exists in 'characters', sometimes null.
	
	public GameMap(Communicable con, int stageGroup, int stageNumber) {
		super(con);
		tiles=new ArrayList<GameObject>();
		characters=new ArrayList<GameObject>();
		sentenced=new LinkedList<GameObject>();

		this.stageGroup=stageGroup;
		this.stageNumber=stageNumber;
		readMap();
		buildMap();
	}
	

	@Override
	public void playGame(int ms) {
		for(GameObject o : tiles)
			o.playGame(ms);
		for(GameObject o : characters)
			o.playGame(ms);

		tiles.remove(sentenced.peek());
		characters.remove(sentenced.poll());
	}

	@Override
	public void receiveMotion(HarpEvent ev, int layer) {
		for(GameObject o : tiles) {
			o.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
		}
		for(GameObject o : characters) {
			o.receiveMotion(ev, layer);
			if(ev.isProcessed()) return;
		}
	}

	@Override
	public void drawScreen(Canvas c, Paint p, int layer) {
		for(GameObject o : tiles)
			o.drawScreen(c, p, layer);
		for(GameObject o : characters)
			o.drawScreen(c, p, layer);
	}

	@Override
	public void restoreData() {
		for(GameObject o : tiles)
			o.restoreData();
		for(GameObject o : characters)
			o.restoreData();
	}

	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("movePlayer")) {
			
			if(player==null) return 0;
			else if(movableCheck(player,Integer.parseInt(msgs[1]))==true) {
				breakTileStart( (Integer)player.get("mapX"), (Integer)player.get("mapY") );
				player.send("move/"+msgs[1]);
				return 1;
			} else return 0;
		}
		else if(msgs[0].equals("rotatableCheck")) {
			
			if(rotatableCheck(Integer.parseInt(msgs[1]),Integer.parseInt(msgs[2]))==true) return 1;
			else return 0;
		}
		else if(msgs[0].equals("moved")) {
			
			if(msgs[1].equals("PlayerSeal")) {
				con.send("playerStepped");
				if(eatFish(Integer.parseInt(msgs[2]), Integer.parseInt(msgs[3]))==true)
					con.send("fishEaten");
				if(goalCheck(Integer.parseInt(msgs[2]), Integer.parseInt(msgs[3]))==true)
					con.send("playerReached");
			}
			return 1;
		}
		else if(msgs[0].equals("broken")) {
			breakTileEnd( Integer.parseInt(msgs[1]), Integer.parseInt(msgs[2]) );
			return 1;
		}
		else if(msgs[0].equals("reset")) {
			clearMap();
			buildMap();
			return 1;
		}
		else if(msgs[0].equals("changeToNext")) {
			stageNumber+=1;
			clearMap();
			readMap();
			buildMap();
			return 1;
		}
		else return con.send(msg);
		
	}

	@Override
	public Object get(String name) {

		if(name.equals("playerX")) {
			if(player==null) return 0;
			else return (Integer)player.get("mapX")*Screen.TILE_LENGTH+Screen.TILE_LENGTH/2+Screen.FIELD_MARGIN_LEFT;
		}
		else if(name.equals("playerY")) {
			if(player==null) return 0;
			else return (Integer)player.get("mapY")*Screen.TILE_LENGTH+Screen.TILE_LENGTH/2+Screen.FIELD_MARGIN_TOP;
		}
		else if(name.equals("width")) {
			return (Integer)mapWidth*Screen.TILE_LENGTH+Screen.FIELD_MARGIN_LEFT*2;
		}
		else if(name.equals("height")) {
			return (Integer)mapHeight*Screen.TILE_LENGTH+Screen.FIELD_MARGIN_TOP*2;	
		}
		else return con.get(name);
	}
	
	//// private - map setting
	
	private void readMap() {
		tileString=null;
		charString=null;
		
		AssetManager am=(AssetManager)con.get("assetManager");		
		try {

			// open file
			InputStream is=am.open("stage"+stageGroup+".txt");
			InputStreamReader isr=new InputStreamReader(is,"utf-8");
			BufferedReader br=new BufferedReader(isr);
			String str=null;
			
			// find map string matching stage number
			str=br.readLine();
			while(!str.equals("#"+stageNumber)) {
				str=br.readLine();
				if(str==null) break;
			}
			if(!str.equals("#"+stageNumber)) {
				HarpLog.error("Failed to find map string matching stage number : "+stageGroup+", "+stageNumber);
				readTempMap();
				return;
			}
			
			// read map
			mapWidth=Integer.parseInt( br.readLine() );
			mapHeight=Integer.parseInt( br.readLine() );
			tileString=new String[mapHeight];
			for(int i=0;i<mapHeight;i++) {
				str=br.readLine();
				if(str.length() != mapWidth) {
					HarpLog.error("File struct error - tileString : "+stageGroup+", "+stageNumber);
					readTempMap();
					return;
				}
				tileString[i]=str;
			}
			charString=new String[mapHeight];
			for(int i=0;i<mapHeight;i++) {
				str=br.readLine();
				if(str.length() != mapWidth) {
					HarpLog.error("File struct error - charString : "+stageGroup+", "+stageNumber);
					readTempMap();
					return;
				}
				charString[i]=str;				
			}
			HarpLog.info("Success read map! : "+stageGroup+", "+stageNumber);
			
		} catch(Exception e) {
			e.printStackTrace();
			HarpLog.error("Failed to read map : "+stageGroup+", "+stageNumber);
		}
		
	}
	
	private void buildMap() {

		// tiles, characters
		for(int y=0;y<mapHeight;y++) {
			for(int x=0;x<mapWidth;x++) {
				
				// create tiles
				switch(tileString[y].charAt(x)) {
				case '0': break;
				case '1': tiles.add(new NormalTile(this,x,y)); break;
				case '2': tiles.add(new RotatableTile(this,x,y,TileType.RT_BRIDGE)); break;
				case '3': tiles.add(new RotatableTile(this,x,y,TileType.RT_CORNER)); break;
				case '4': tiles.add(new RotatableTile(this,x,y,TileType.RT_FORK)); break;
				case '5': tiles.add(new RotatableTile(this,x,y,TileType.RT_INTERSECTION)); break;
				case '6': tiles.add(new BrakingTile(this,x,y)); break;
				default: HarpLog.danger("Invalid tile type"); break;
				}
				
				// create characters
				switch(charString[y].charAt(x)) {
				case '0': break;
				case '1':
					player=new PlayerSeal(this,x,y);
					characters.add(player);
					break;
					
				case '2': characters.add(new GoalFlag(this,x,y)); break;
				case '3': characters.add(new Fish(this,x,y)); break;
				default: HarpLog.danger("Invalid character type"); break;
				}
			}
		}
	}
	
	private void clearMap() {
		
		for(GameObject o : tiles)
			o.restoreData();
		for(GameObject o : characters)
			o.restoreData();
		tiles.clear();
		characters.clear();
		player=null;
	}
	
	private void readTempMap() {
		mapWidth=5;
		mapHeight=6;
		tileString=new String[mapHeight];
		tileString[0]="00000";
		tileString[1]="60006";
		tileString[2]="00000";
		tileString[3]="00100";
		tileString[4]="00000";
		tileString[5]="60006";
		charString=new String[mapHeight];
		charString[0]="00000";
		charString[1]="30003";
		charString[2]="00000";
		charString[3]="00100";
		charString[4]="00000";
		charString[5]="20003";
	}
	
	//// private - tile, character action
	
	private boolean movableCheck(GameObject target,int direction) {
		
		// action check
		if((Integer)target.get("moveDirection")!=Direction.NONE) return false;
		
		// start point check
		int mapX=(Integer)target.get("mapX");
		int mapY=(Integer)target.get("mapY");
		for(GameObject o : tiles) {
			if( (Integer)o.get("mapX")==mapX &&
				(Integer)o.get("mapY")==mapY) {
				
				// tile type
				if(o.getClass()==NormalTile.class)
					continue;
				else if(o.getClass()==RotatableTile.class) {
					int tileType=(Integer)o.get("type");
					int tileDirection=(Integer)o.get("direction");
					if(direction==tileDirection) continue;
					switch(tileType) {
					case TileType.RT_BRIDGE: if(direction!=Direction.reverse(tileDirection)) return false; break;
					case TileType.RT_CORNER: if(direction!=Direction.clockwise(tileDirection)) return false; break;
					case TileType.RT_FORK: if(direction!=Direction.clockwise(tileDirection) && direction!=Direction.clockwiseR(tileDirection)) return false; break;
					case TileType.RT_INTERSECTION: break;
					default: HarpLog.error("Invalid Tile Type : "+tileType); return false;
					}
				} else if(o.getClass()==BrakingTile.class) {
					if((Boolean)o.get("braking")==true) return false;
					else break;
				}
			}
		}
		
		// destination check
		switch(direction) {
		case Direction.UP: mapY-=1; break;
		case Direction.LEFT: mapX-=1; break;
		case Direction.RIGHT: mapX+=1; break;
		case Direction.DOWN: mapY+=1; break;
		default: HarpLog.error("Invalid Direction : "+direction); return false;
		}
		for(GameObject o : tiles) {
			if( (Integer)o.get("mapX")==mapX &&
				(Integer)o.get("mapY")==mapY) {

				// tile type
				if(o.getClass()==NormalTile.class)
					return true;
				else if(o.getClass()==RotatableTile.class) {
					int tileType=(Integer)o.get("type");
					int tileDirection=(Integer)o.get("direction");
					if(direction==Direction.reverse(tileDirection)) return true;
					switch(tileType) {
					case TileType.RT_BRIDGE: return (direction==tileDirection);
					case TileType.RT_CORNER: return (direction==Direction.clockwiseR(tileDirection));
					case TileType.RT_FORK: return (direction==Direction.clockwiseR(tileDirection) || direction==Direction.clockwise(tileDirection));
					case TileType.RT_INTERSECTION: return true;
					default: HarpLog.error("Invalid Tile Type : "+tileType); return false;
					}
				} else if(o.getClass()==BrakingTile.class) {
					return (Boolean)o.get("braking")==false;
				}
				
			}
		}
		return false;
	}
	
	private boolean rotatableCheck(int x,int y) {
		
		for(GameObject o : characters)
			if(o.getClass()==PlayerSeal.class && (Integer)o.get("mapX")==x && (Integer)o.get("mapY")==y)
				return false;
		return true;
	}
	
	private void breakTileStart(int x,int y) {
		
		for(GameObject o : tiles) {
			if(o.getClass()==BrakingTile.class && (Integer)o.get("mapX")==x && (Integer)o.get("mapY")==y) {
				o.send("break");
				break;
			}
		}	
	}

	private boolean eatFish(int x,int y) {

		for(GameObject o : characters) {
			if(o.getClass()==Fish.class && (Integer)o.get("mapX")==x && (Integer)o.get("mapY")==y) {
				if(o.send("eaten")==1) {
					sentenceObject(o);
					return true;
				}
				else return false;
			}
		}
		return false;
	}
	
	private boolean goalCheck(int x,int y) {
		
		for(GameObject o : characters) {
			if(o.getClass()==GoalFlag.class && (Integer)o.get("mapX")==x && (Integer)o.get("mapY")==y) {
				return true;
			}
		}
		return false;
	}
	
	private void breakTileEnd(int x,int y) {
		
		for(GameObject o : tiles) {
			if(o.getClass()==BrakingTile.class && (Integer)o.get("mapX")==x && (Integer)o.get("mapY")==y) {
				sentenceObject(o);
				break;
			}
		}
	}
	
	// private - sentence
	
	private void sentenceObject(GameObject o) {
		o.restoreData();
		sentenced.add(o);
	}
	
}
