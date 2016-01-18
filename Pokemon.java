//Pokemon class
//Stores all fields and methods of individual Pokemon objects

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;


public class Pokemon{
	//creates a pokemon
	
	private String name, status;
	
	private double hp, atk, def, exp, totalExp;
	private int level, expGiven;
	private String item;
	private String expType;
	private String type1, type2;
	private String statusA = "none"; //status A = all basic status conditions - poison, burn, paralysis, sleep
	private String statusB = "none";	//status B = all other status effects - recharging/charging, protecting against a move, flinching
	private boolean confused = false; //confusion is separate because it can be stacked with other status effects
	private double acc = 1;
	
	private ArrayList<Move> moveSet;
	private ArrayList<Integer> moveLevels;
	private Move[] moves;
	private Move curMove;
	
	private double sAtk, sDef, sHP; //The amount of atk, def or hp gained per level; s stands for scaling
	private double cAtk, cDef, cHP; //current atk, def and hp
	private double maxHP, maxAtk, maxDef;
	
	int x, y, ox, oy, tx, ty;
	
	
	private String[] turns;
		
	private int d, a, frame;
	private Image curImg;
	private Image[][][] img;
	private Image[] menuPics;
	private Image[] threeDPics;
	
	
	private Image[] hurtimg = new Image[4]; //hurt animation
	private Image[] sleepimg = new Image[2]; //sleep animation
	private Image icon;	
	
	private static HashMap<String, Integer> dir = loadDir();
	private static HashMap<String, Integer> action = loadActions();
	
	static String[] directions = {"down", "up", "left", "right"};
	static String[] actions = {"idle", "walk", "atk", "specialatk"};
	private boolean hurt = false;

	private int pauseframe = 0;
	public int pauselength = 0;
	
	private boolean noAction = false; //true if no action can be performed
	private int[] len;
	
	private boolean enemy;
	private boolean actionperformed = false; //determines if a pokemon has performed an action
	private boolean actionset = false;
	
	private int turncounter = 0; //+1 at the end of everyturn
	private int statusturn = 0; //determines the turn when the user was affected by a particular status condition (only applies to statusA type effects and confusion)
	private int healturn = 0; //determines when the effect will heal, if it does at all (only sleep, confusion, paralysis and freeze)
	
	private boolean special; //player pokemon can use a move from its moveset (special attack)
	
	public Pokemon(String name, String hp, String atk, String def, String exp, String level, String expType, String expYield, String type1, String type2, boolean enemy, int x, int y, String s){
		
		this.name = name;
		
    	this.hp = Double.parseDouble(hp);
    	this.atk = Double.parseDouble(atk);
    	this.def = Double.parseDouble(def);
    	
    	maxHP = this.hp;
    	maxAtk = this.atk;
    	maxDef = this.def;
    	
    	this.exp = Double.parseDouble(exp);
 
    	this.level = Integer.parseInt(level);
    	this.expType = expType;
    	this.expGiven = Integer.parseInt(expYield);
		
		this.type1 = type1;
		this.type2 = type2;
		
		moveSet = new ArrayList<Move>();
		moveLevels = new ArrayList<Integer>();
		moves = new Move[4];
		newlevelExp();
		
		
		
		this.enemy = enemy;
		this.x=x;
		this.y=y;
		
		tx = this.x;
		ty = this.y;
		
		
		
		turns = new String[1];
		
		a = 0;
		d = 0;
		frame = 0;
		
		String[] lengths = s.split(" ");
		
		len = new int[4];
		
		for(int i=0; i<4; i++){
			len[i] = Integer.parseInt(lengths[i]);
		}
		
		img = new Image[4][4][5];
		
		for(int i=0; i<4; i++){
			
			for(int j=0; j<4; j++){
				
				for(int k=0; k<len[j]; k++){
					
					img[i][j][k] = new ImageIcon("sprites/"+name+"/"+name+"_"+directions[i]+"_"+actions[j]+"_"+k+".png").getImage();
					
				}
			}
		}
		
		for (int i=0; i<4; i++){
			hurtimg[i] = new ImageIcon("sprites/"+name+"/"+name+"_"+directions[i]+"_hurt_0.png").getImage();
		}
		
		
		
		menuPics = new Image[2];
		
		for(int i=0; i<2; i++){
			menuPics[i] = new ImageIcon(name+"/"+name+"_left_icon_"+i+".png").getImage();
		}
		
		threeDPics = new Image[Integer.parseInt(lengths[4])];
		
		for(int i=0; i<threeDPics.length; i++){
			threeDPics[i] = new ImageIcon("Sprites/"+name+"/tmp-"+i+".gif").getImage();
		}
		
		curImg = img[d][a][frame];
		icon = new ImageIcon("Sprites/"+name+"/"+name+"Icon.png").getImage();

		
	}
	
	public void setCurMove(int i){
		curMove = moves[i];
		special = true; //allows player to use a move from its moveset
	}
	public Move getCurMove(){
		return curMove;
	}
	public boolean performSpecial(){
		return special;
	}
	public void resetSpecial(){
		special = false;
	}
	
	public Image getMenuPic(){
		return menuPics[(frame/18)%2];
	}
	
	public Image getIcon(){
		return icon;
	}
	
	public static HashMap<String, Integer> loadDir(){
		
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		String[] s = {"down", "up", "left", "right"};
		for(int i=0; i<4; i++){
			
			tmp.put(s[i], i);
		}
		
		return tmp;
	}
	
	public static HashMap<String, Integer> loadActions(){
		
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		String[] s = {"idle", "walk", "atk", "specialatk"};
		for(int i=0; i<4; i++){
			
			tmp.put(s[i], i);
		}
		
		return tmp;
	}
	
	public void update(){

		//Image Stuff
		frame += 1;
		
		if (actions[a].equals("atk") || actions[a].equals("specialatk")){
			curImg = img[d][a][(frame/(60/len[a]))%len[a]]; //all attack animations last a total of 60 updates, so therefore, each frame lasts 60/(number of images) loops

		}
		else{

			curImg = img[d][a][(frame/30)%len[a]];
		}
		

		priorityupdate(); //hurt animation
		
		if (pauselength!=0){
			pauseframe+=1;
		}
		if (pauselength>=pauseframe && pauselength!=0){ //pauses, usually a short pause occurs at the end of every attack, or during a status debuff
									 //pauses also have priority over both hurt or action sprites
			curImg = img[d][0][0];
		}
		
		
	}
	
	public void priorityupdate(){
		
		if (hurt){
			if (frame >20 && frame<=40){
				curImg = hurtimg[Arrays.asList(directions).indexOf(getDir())];
			}
			else{
				curImg = img[d][0][0];
			}
			if (frame == 60){
				hurt = false;
	

			}

		}
	}

	
	public void setPause(int pause){ //sets the pause length
		pauselength = pause;
	}
	
	public int getPauseA(){
		return pauseframe;
	}
	
	public int getPauseB(){
		return pauselength;
	}
	
	public void resetPause(){
		pauseframe = 0;
		pauselength = 0;
	}
	
	public int getFrame(){
		return frame;
	}
	
	public void resetFrame(){
		frame = 0;
	}

	public void move(int dx, int dy) {
	
		x += 2*dx;
		y += 2*dy;
		
	}
	
	public void takeDamage(double dmg){
		cHP-=dmg;
	}
	public void heal(double hp){
		cHP+=hp;
	}
	
	public void setHurt(){

		hurt = true;
		frame = 0;
	}
	
	public boolean getHurt(){
		return hurt;
	}
	
	public void addTurn(String s){
		
		turns[0] = s;
	}
	
	public void finishTurn(){
		
		turns[0] = null;
	}
	
	public void createMoveSet(ArrayList<Move> mvs, ArrayList<Integer> lvls){
    	moveSet = mvs;
    	moveLevels = lvls;
    	setMoveset();
    }
    
    public void setNoAction(boolean b){

    	noAction = b;
    	frame = 0; //resets the frame if no action is performed
    }
    public boolean getNoAction(){
    	return noAction;
    }
    
//-------------------------------------------------------------------------------
	//exp and level up related methods
    public double expNeeded(double d){
    	
		if (expType.equals("fast")){
			return (int)(4*Math.pow(d, 3)/5);
		}
		else if (expType.equals("mediumfast")){
			return (int)(Math.pow(d, 3));
		}
		else if (expType.equals("mediumslow")){
			int temp = (int)(Math.pow(d, 3)*6/5-15*Math.pow(d,2)+100*d-140);
			if (temp<=1){
				temp = 0;
			}
			return temp;
		}
		else{
			int temp = (int)(Math.pow(d,3)*5/4);
			if (temp<=1){
				temp = 0;
			}
			return temp;
		}
	}
    
    public void newlevelExp(){ //sets exp to be total exp of level\

    	totalExp = expNeeded((double)level+1);
    	
    	exp = expNeeded((double)level);
    }

    public void setExp(double newexp){
    	exp = newexp;
	}
    public void addExp(double extra){
    	exp += extra;
    }
    public double getExpgiven(){
    	return expGiven;
    }
	
    public void levelUp(){
    	if (level!=100){
    		level++;
    		setStats();
    		newlevelExp();
    	}

    }
    
	public void setDir(String s){
		
		d = dir.get(s);
		
	}
	
	public void setDir(int i){
		
		d = i;
	}
	
	public void setAction(String s){
		
		if(action.get(s) != a){
			if (getAction().equals("atk") || getAction().equals("specialatk")){
				if (frame>59){
					a = action.get(s);
			
					frame = 0;
				}
			}
			else{
				a = action.get(s);
				
				frame = 0;
			}
		}
	}

//--------------------------------------------------------------------------------------------
	//creates the moveset of four moves of the pokemon
	
	public void setMoveset(){
    	if (moves[0]==null && moves[1]==null){ //checks if the four move set is empty (occurs when creating new pokemon)
    		int lvl = level;
    		while(!moveLevels.contains(lvl)){
    			lvl-=1;
    		}
    		int index = moveLevels.indexOf(lvl);
    		for (int i = 0; i<4; i++){
    			if (index-i<0){
    				break;
    			}
    			else{
    				moves[i] = moveSet.get(index-i);
    			}
    		}
    	}
    	else{ //fills in a moveslot with a newmove, occurs when not all four slots are empty (learning a new move through levelup)
    		//for allied pokemon only
    		for (int i=0; i<4; i++){
    			if (moves[i]==null){
    				int pos = moveLevels.indexOf(level);
    				moves[i] = moveSet.get(pos);
    				break;
    			}
    		}
    	}
    }
    
    public void removeMove(int pos){
    	moves[pos] = null;
    }
    
//--------------------------------------------------------------------------------------------
	//sets up the current stats (atk, def, and hp stats) of the pokemon
	
	public void setScaling (double hpscale, double atkscale, double defscale){
    	sHP = hpscale;
    	sAtk = atkscale;
    	sDef = defscale;
    	cHP = hp+sHP*level;
    	cAtk = atk+sAtk*level;
    	cDef = def+sDef*level; 
    	setStats();
    		
    }
    
	public void setStats(){ //level up
    	maxHP = hp+sHP*level;
    	maxAtk = atk+sAtk*level;
    	maxDef = def+sDef*level;
    	cHP += sHP;
    	cAtk += sAtk;
    	cDef += sDef;
    }
    
//--------------------------------------------------------------------------------------------
	//sets up coordinates of pokemon in dungeon
	
	public void setX(int i){
		x = i;
	}
	
	public void setY(int i){
		y = i;
	}
	
	public void setTX(int x){
		tx = x;
	}
	
	public void setTY(int y){
		ty = y;
	}
	
	public void setRefPos(int x, int y){		
		ox = x;
		oy = y;
	}
	
//--------------------------------------------------------------------------------------------
	//simple getters
	
	public String getName(){
		return name;
	}
	
	public String getAction(){
		return actions[a];
	}
	public String getDir(){
		return directions[d];
	}
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getTX(){	
		return tx;
	}
	
	public int getTY(){
		return ty;
	}
	
	public int getOX(){
		return ox;
	}
	
	public int getOY(){
		return oy;
	}
	public int getLevel(){
    	return level;
    }
    
    public double getCHp(){
    	return cHP;
    }
    
    public double getCAtk(){
    	return cAtk;
    }
   
    public double getCDef(){
    	return cDef;
    }
    
    public double getMaxHp(){
    	return maxHP;
    }
    
    public double getMaxAtk(){
    	return maxAtk;
    }
   
    public double getMaxDef(){
    	return maxDef;
    }
    
    public double getExp(){
    	return exp;
    }
    
    public double getTotalExp(){
    	return totalExp;
    }
    
    public double getAcc(){
    	return acc;
    }
    
    public String getType1(){
    	return type1;
    }
    public String getType2(){
    	return type2;
    }
   
    public Move[] getMoveSet(){
    	return moves;
    }
    public ArrayList<Integer> getMovelevels(){
    	return moveLevels;
    }
    public ArrayList<Move> getAllmoves(){
    	return moveSet;
    }
    
//--------------------------------------------------------------------------------------------
	//allows the attack defence or accuracy to be changed; always a result of a special feature of an attack i.e. iron defence increases cDef
	
	public void setCAtk(double change){
		cAtk+=change;
	}
	public void setCDef(double change){
		cDef+=change;
	}
	public void changeAcc(double newacc){
		acc = newacc;
	}
	
	
//--------------------------------------------------------------------------------------------
	//returns properties of the current image, as well as the current turn (which is the direction of the action)	
	
	public String[] getTurns(){
		return turns;	
	}
	
	public int getWidth(){
		return curImg.getWidth(null);		
	}
	
	public int getHeight(){
		return curImg.getHeight(null);
	}
	
	public Image getImage(){
		return curImg;
		
	}
	
	public Image get3DPic(){
		return threeDPics[(frame/2)%threeDPics.length];
	}
	
	public int get3DWidth(){
		return threeDPics[(frame/2)%threeDPics.length].getWidth(null);
	}
	
	public int get3DHeight(){
		return threeDPics[(frame/2)%threeDPics.length].getHeight(null);
	}
	
//--------------------------------------------------------------------------------------------	
	
	public boolean isEnemy(){ //true if the pokemon is an enemy pokemon
		return enemy;
	}

//--------------------------------------------------------------------------------------------
	
	//these methods determine if the pokemon has started to and/or finished to perform an action
	public void performed(boolean b){
		actionperformed = b; //true if pokemon has completed an action
		turncounter+=1;
		if (b == true){
			applyStatusA();
		}
	}
	
	public boolean getPerformed(){
		return actionperformed;
	}
	
	public void actionSet(boolean b){
		actionset = b; //true if pokemon has begun an action
		if (actionset == true){
		}
	}
	
	public boolean getActionset(){
		return actionset;
	}
	
//--------------------------------------------------------------------------------------------
	
	//status condition related methods
	public String getStatusA(){
		return statusA;
	}
	
	public String getStatusB(){
		return statusB;
	}
	
	public void clearStatusA(){
		statusA = "none";
		statusturn = 0;
	}
	public void clearStatusB(){
		statusB = "none";
	}
	
	public void setStatusA(String condition){
		if (statusA.equals("none")){
			statusA = condition;
			statusturn = turncounter;
			if (condition.equals("paralyzed")){
				healturn = turncounter+2+(int)(Math.random()*2); //paralysis heals in 2-3 turns in mystery dungeon
			}
			if (condition.equals("frozen")){
				healturn = turncounter+4+(int)(Math.random()*2); //freeze heals in 4-5 turns
			}
			if (condition.equals("asleep")){
				healturn = turncounter+3+(int)(Math.random()*4); //sleep heals in 3-6 turns
			}
		}
	}
	
	public void resetTurncounter(){
		turncounter = 0;
	}
	
		
	public void setStatusB(String effect){
		if (statusB.equals("none")){
			statusB = effect;
		}
	}

	
	
	
	
	public void applyStatusA(){ //returns an integer if the status is in effect. the integer represents how
		if (statusA.equals("burned")){
			if (turncounter%20 == statusturn%20){ //burn takes effect once every 20 moves
				cHP-=5;
			}
		}
		else if (statusA.equals("poisoned")){
			if (turncounter%10 == statusturn%10){
				cHP-=2;
			}
		}
		
		//remvoes the status condition at the end of a turn if an already defined number of turns has elapsed
		else if (statusA.equals("asleep")){
			if (turncounter-statusturn == healturn){ 
				clearStatusA();	
			}
		}
		else if (statusA.equals("paralyzed")){
			if (turncounter-statusturn == healturn){
				clearStatusA();
			}
		}
		else if (statusA.equals("frozen")){
			if (turncounter-statusturn == healturn){
				clearStatusA();
			}
		}
	}
	
	
	public void autoheal(){ //natural regeneration
		if (turncounter%10 == 0){ //pokemon naturally gain 1 hp every 10 actions performed (if they are not already full hp)
			if (!statusA.equals("poisoned")){ //poison causes pokemon to be 
				if (cHP+1<=maxHP){
					cHP+=1;
				}
			}
		}	
	}
}