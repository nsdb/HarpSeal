package com.yad.harpseal.game.map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;

import com.nsdb.engine.core.GameObject;
import com.nsdb.engine.core.ManagerGameObject;
import com.nsdb.engine.util.Communicable;
import com.nsdb.engine.util.GameLog;
import com.yad.harpseal.constant.Direction;
import com.yad.harpseal.constant.Screen;
import com.yad.harpseal.constant.TileType;

/**
 * 게임에서 나타나는 모든 타일, 캐릭터, 배경, 카메라 등을 생성, 관리하는 객체입니다.<br>
 * 처음 생성되거나 게임 스테이지로부터 메시지를 받으면, 스테이지에 해당하는 파일을 불러와서 읽어오고, 알맞는 타일, 캐릭터들을 생성합니다.<br>
 * 또한 모든 게임 내 객체들의 행동은 이 객체에게 가능 여부를 체크한 다음에 진행됩니다.
 */
public class GameMap extends ManagerGameObject {

	// map status
	private int stageGroup;
	private int stageNumber;
	private int mapWidth,mapHeight;
	private String[] tileString;
	private String[] charString;
	private float width,height;
	
	// map objects
	private ArrayList<GameObject> tiles;
	private ArrayList<GameObject> characters;
	private PlayerSeal player;	// also exists in 'characters', sometimes null.	
	private TheArcticOcean field;
	private GameCamera camera;
	
	public GameMap(Communicable con, int stageGroup, int stageNumber) {
		super(con);
		tiles=new ArrayList<GameObject>();
		characters=new ArrayList<GameObject>();

		this.stageGroup=stageGroup;
		this.stageNumber=stageNumber;
		readMap();
		buildMap();		
		
		field=new TheArcticOcean(this, width, height);
		camera=new GameCamera( this, width, height, getPlayerX(), getPlayerY() );
		startControl(field);
		startControl(camera);
	}
	
	@Override
	public void playGame(int ms) {
		super.playGame(ms);
		setTransAll( (Float)camera.get("cameraX"), (Float)camera.get("cameraY") );
	}
	
	@Override
	public int send(String msg) {
		String[] msgs=msg.split("/");
		
		if(msgs[0].equals("movePlayer")) {
			
			if(player==null) return 0;
			else if(characterMovableCheck(player,Integer.parseInt(msgs[1]))==true) {
				breakTileStart( (Integer)player.get("mapX"), (Integer)player.get("mapY") );
				player.send("move/"+msgs[1]);
				camera.send("playerMoved/"+getPlayerX()+"/"+getPlayerY());
				return 1;
			} else return 0;
		}
		else if(msgs[0].equals("scroll")) {
			if((Boolean)con.get("playable")==false) return 0;
			return camera.send(msg);
		}
		else if(msgs[0].equals("rotatableCheck")) {
			
			if((Boolean)con.get("playable")==false) return 0;
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
			camera.send("focus/"+getPlayerX()+"/"+getPlayerY());
			return 1;
		}
		else if(msgs[0].equals("changeToNext")) {
			stageNumber+=1;
			clearMap();
			readMap();
			buildMap();
			field.send("update/"+width+"/"+height);
			camera.send("update/"+width+"/"+height);
			camera.send("focus/"+getPlayerX()+"/"+getPlayerY());
			return 1;
		}
		else return super.send(msg);
		
	}
	
	@Override
	public Object get(String name) {
		if(name.equals("isLoaded")) {
			for(int i=0;i<tiles.size();i++)
				if((Boolean)tiles.get(i).get("isLoaded")==false)
					return false;
			for(int i=0;i<characters.size();i++)
				if((Boolean)characters.get(i).get("isLoaded")==false)
					return false;
			return (Boolean)field.get("isLoaded") &&
					(Boolean)camera.get("isLoaded");
		}
		else
			return super.get(name);
	}
	

	
	//// private - map setting
	
	private void readMap() {
		tileString=null;
		charString=null;
		
		Context context=(Context)con.get("context");
		AssetManager am=context.getAssets();		
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
			if(str==null) {
				GameLog.error("Failed to find map string matching stage number : "+stageGroup+", "+stageNumber);
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
					GameLog.error("File struct error - tileString : "+stageGroup+", "+stageNumber);
					readTempMap();
					return;
				}
				tileString[i]=str;
			}
			charString=new String[mapHeight];
			for(int i=0;i<mapHeight;i++) {
				str=br.readLine();
				if(str.length() != mapWidth) {
					GameLog.error("File struct error - charString : "+stageGroup+", "+stageNumber);
					readTempMap();
					return;
				}
				charString[i]=str;				
			}
			width=(Integer)mapWidth*Screen.TILE_LENGTH+Screen.FIELD_MARGIN_LEFT*2;
			height=(Integer)mapHeight*Screen.TILE_LENGTH+Screen.FIELD_MARGIN_TOP*2;
			GameLog.info("Success read map! : "+stageGroup+", "+stageNumber);
			
		} catch(Exception e) {
			e.printStackTrace();
			GameLog.error("Failed to read map : "+stageGroup+", "+stageNumber);
		}
		
	}
	
	private void buildMap() {

		GameObject obj=null;
		// tiles, characters
		for(int y=0;y<mapHeight;y++) {
			for(int x=0;x<mapWidth;x++) {
				
				// create tiles
				switch(tileString[y].charAt(x)) {
				case '0': obj=null; break;
				case '1': obj=new NormalTile(this,x,y); break;
				case '2': obj=new RotatableTile(this,x,y,TileType.RT_BRIDGE); break;
				case '3': obj=new RotatableTile(this,x,y,TileType.RT_CORNER); break;
				case '4': obj=new RotatableTile(this,x,y,TileType.RT_FORK); break;
				case '5': obj=new RotatableTile(this,x,y,TileType.RT_INTERSECTION); break;
				case '6': obj=new BrakingTile(this,x,y); break;
				default: GameLog.danger("Invalid tile type"); break;
				}
				if(obj != null) {
					startControl(obj);
					tiles.add(obj);
				}
				
				
				// create characters
				switch(charString[y].charAt(x)) {
				case '0': obj=null; break;
				case '1':
					player=new PlayerSeal(this,x,y);
					obj=player;
					break;
					
				case '2': obj=new GoalFlag(this,x,y); break;
				case '3': obj=new Fish(this,x,y); break;
				default: GameLog.danger("Invalid character type"); break;
				}
				if(obj != null) {
					startControl(obj);
					characters.add(obj);
				}
			}
		}
		
	}
	
	private void clearMap() {
		for(int i=0;i<tiles.size();i++)
			stopControl(tiles.get(i));
		for(int i=0;i<characters.size();i++)
			stopControl(characters.get(i));
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
	
	private float getPlayerX() {
		if(player==null) return 0;
		else return (Integer)player.get("mapX")*Screen.TILE_LENGTH+Screen.TILE_LENGTH/2+Screen.FIELD_MARGIN_LEFT;		
	}
	
	private float getPlayerY() {
		if(player==null) return 0;
		else return (Integer)player.get("mapY")*Screen.TILE_LENGTH+Screen.TILE_LENGTH/2+Screen.FIELD_MARGIN_TOP;		
	}
	
	private boolean characterMovableCheck(GameObject target,int direction) {
		
		// action check
		if((Integer)target.get("moveDirection")!=Direction.NONE) return false;
		
		// start point check
		int mapX=(Integer)target.get("mapX");
		int mapY=(Integer)target.get("mapY");
		for(GameObject o : tiles) {
			if( (Integer)o.get("mapX")==mapX && (Integer)o.get("mapY")==mapY) {
				if( tileBoardableCheck(o,direction)==false ) return false;
				else break;
			}
		}
		
		// destination check
		switch(direction) {
		case Direction.UP: mapY-=1; break;
		case Direction.LEFT: mapX-=1; break;
		case Direction.RIGHT: mapX+=1; break;
		case Direction.DOWN: mapY+=1; break;
		default: GameLog.error("Invalid Direction : "+direction); return false;
		}
		direction=Direction.reverse(direction);
		for(GameObject o : tiles) {
			if( (Integer)o.get("mapX")==mapX && (Integer)o.get("mapY")==mapY) {
				if( tileBoardableCheck(o,direction)==false ) return false;
				else return true;
			}
		}
		return false;
	}
	
	private boolean tileBoardableCheck(GameObject target,int direction) {
		
		if(target.getClass()==NormalTile.class)
			return true;
		
		else if(target.getClass()==RotatableTile.class) {
			int tileType=(Integer)target.get("type");
			int tileDirection=(Integer)target.get("direction");
			if(direction==tileDirection) return true;
			switch(tileType) {
			case TileType.RT_BRIDGE: return (direction==Direction.reverse(tileDirection));
			case TileType.RT_CORNER: return (direction==Direction.clockwise(tileDirection));
			case TileType.RT_FORK: return (direction==Direction.clockwise(tileDirection) || direction==Direction.clockwiseR(tileDirection));
			case TileType.RT_INTERSECTION: return true;
			default: GameLog.error("Invalid Tile Type : "+tileType); return false;
			}
			
		} else if(target.getClass()==BrakingTile.class) {
			if((Boolean)target.get("braking")==true) return false;
			else return true;
			
		} else {
			GameLog.error("Unknown tile class founded at tileMoveableCheck!");
			return false;
		}
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
					stopControl(o);
					characters.remove(o);
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
				stopControl(o);
				tiles.remove(o);
				break;
			}
		}
	}
	
}
