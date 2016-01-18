//Main class

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;

//Pokemon mystery dungeon is a game where the user takes on the role of a pokemon, and traverses through a dungeon
//in order to find the exit. Each floor of a dungeon is randomly generated, with corridors connecting rooms and enemy pokemon 
//that spawn in random rooms.  


public class Main extends JFrame implements ActionListener{
	//Main JFrame
	
	
	private GamePanel game;						//Panel with all game images
	private JPanel btm;
	private javax.swing.Timer myTimer;			//Game timer
	
	public Main(String name, String lvl, String exp, String dungeon){
		
		//Basic setup of screen
		//Positions and sizes are set for all buttons and images
		
		super("Pokemon Mystery Dungeon");
		
		setLayout(null);
		setSize(480,768);
		
		game = new GamePanel(name, lvl, exp, dungeon);				
		game.setSize(480, 768);
		game.setLocation(0,0);
		add(game);
		
		/*btm = new MenuPanel();
		btm.setSize(480, 384);
		btm.setLocation(0,384);
		add(btm);*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//Program will exit on close
		setVisible(true);									//Makes it visible
		
		myTimer = new javax.swing.Timer(17,this);			//Starts clock
		myTimer.start();
		
	}
	
	public void actionPerformed(ActionEvent e){
		
		game.update();
		if (game.exit){
			
			game.nextFloor();
			game.floor++;
			game.exit=false;
		}
		
		if (game.floor == game.numFloors){
			new MainMenu();
			setVisible(false);
			game.floor=1;
			
		}
			
	}
	
	public static void main(String[] args){
		
		new Main("rayquaza", "20", "-1", "Dungeon3");
		
	}
}

class GamePanel extends JPanel implements KeyListener{
	
	//main gamepanel where the action takes place
	
	private static ArrayList<Pokemon> allPokes; //arraylist of all pokemon on a floor
	
	private static ArrayList<Pokemon> enemies;
	private static ArrayList<Type> alltypes;
	
	private static ArrayList<Move> allMoves;
	private static ArrayList<String> moveList = new ArrayList<String>();
	
	private static String[] typenames = {"normal","fighting","flying","poison","ground","rock","bug","ghost","steel","fire","water","grass","electric","psychic","ice","dragon","dark"};
	
	private static Pokemon p; //player pokemon
	
	private Pokemon victim; //target pokemon of an  attack
	
	private Dungeon d;
	private int[][] grid;
	Image[] tiles;
	
	Image bg; 
		
	public static boolean transition = false;
	Image[] tranImg;
	int frame = 0;
	int alph = 0;
	
	public static ArrayList<Room> pokeRoom;
	public static Room tmp;
	//public static int posX;
	//public static int posY;
	
	public static boolean exit = false;
	public static int floor = 1;
	
	public static int numFloors;
	public static Item[] itemList;
	public static String[] enemyPoke;
	public static String[] pokeFloors;
	public static int numPoke;
	public static ArrayList<String> usable;
	public static int d1;//floor dimensions
	public static int d2; 
	public static int np1;//#of pokemon in each floor falls between np1 and np2
	public static int np2;
		
	private Camera cam;
	Font f;
	
	private boolean[] keys;
	int[] keysHeld;
	
	int menuActive, menuInd, atkInd, delayCount;
	String[] menuName;
	Image pokeTab = new ImageIcon("pokeTab.png").getImage();
	Image moveTab = new ImageIcon("moveTab.png").getImage();
	Image itemTab = new ImageIcon("itemTab.png").getImage();
	Image tabSelect = new ImageIcon("tabSelect.png").getImage();
	boolean battleOn, itemsOn, partyOn;
	
	private boolean framepause = false;
	private int framewait = 0; //number of frames before framepause is done
	
	private String[] directions = {"left", "right","up","down"};
	
	
	private boolean presskey;
	private boolean pressattack;

	private ArrayList<String> textque = new ArrayList<String>();
	
	private boolean selectingnewmove = false; //allows the user to enter the battle menu when choosing a move to delete
	private boolean pokefaint = false;
	
	
	public GamePanel(String name, String level, String exp, String dungeon){
		
		super();
		
		f = new Font("Pokemon Pixel Font", Font.PLAIN, 36);
		
		allPokes = new ArrayList<Pokemon>();
		enemies = new ArrayList<Pokemon>();
		alltypes = new ArrayList<Type>();
		allMoves = new ArrayList<Move>();
		moveList = new ArrayList<String>();
		
		loadTypes();
		loadMoves();
		
		

		tranImg = new Image[85];
		
		for(int i=0; i<85; i++){
			tranImg[i] = new ImageIcon("transition/transition"+i+".png").getImage();
		}
		
		readFile("tiny woods");
		
		
		d = new Dungeon(d1, d2);
	
		grid = d.getGrid();
		
		pokeRoom = d.getRooms();
		Room tmp = pokeRoom.get(0);
		
		for (int i=0; i<randInt(np1, np2); i++){
			placeEnemy(true);
		}
		
		int posX;
		int posY;
		while (true){
			boolean f = true;
			posX = randInt(tmp.getX1()+1, tmp.getX2()-1);
			posY = randInt(tmp.getY1()+1, tmp.getY2()-1);
			for (Pokemon poke: enemies){
				if (posX == poke.getY() && posY == poke.getX()){
					f = false;
				}
			}
			if (posX == d.getStairY() && posY == d.getStairX()){
				f = false;
			}
			if (f){
				break;
			}
		}
		
		if(exp.equals("-1")){
			
			createPoke("Pfiles/"+name,"46",false, posX*24, posY*24);
			p.setExp(p.getTotalExp()-1);
		}
		else{
			
			createPoke("Pfiles/"+name, level, false, posX*24, posY*24);
			p.setExp(Double.parseDouble(exp));

		}
		
		
		
		tiles = new Image[22];
		for(int i=0; i<22; i++){
			tiles[i] = new ImageIcon(dungeon+"/Tiles"+i+".png").getImage();
		}
		
		delayCount = 0;
		menuActive = -1;
		menuInd = 0;
		atkInd = 0;
		menuName = new String[]{"BATTLE","ITEMS","PARTY", "EXIT"};
		battleOn = false;
		itemsOn = false;
		
		bg = new ImageIcon("battleMenu.png").getImage();
		
		cam = new Camera(d, 480, 384);
		
		keys = new boolean[2000];
		keysHeld = new int[2000];
		Arrays.fill(keysHeld, -1);
		
		
		addKeyListener(this);
		this.setFocusable(true);
        this.grabFocus();
		
	}
	
	public void readFile(String fName){//reads files with all of the dungeon requirements/stats/pokemon, etc.
		//
		Scanner inFile = null;
		try{
			inFile = new Scanner(new BufferedReader(new FileReader("Dfiles/"+fName+".txt")));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			System.exit(0);
		}
		
		String[] dn = inFile.nextLine().split(" ");
		d1 = Integer.parseInt(dn[0]);
		d2 = Integer.parseInt(dn[1]);
		
		String[] np = inFile.nextLine().split(" ");
		np1=Integer.parseInt(np[0]);
		np2=Integer.parseInt(np[1]);
		
		String[] n = inFile.nextLine().split(" ");
		String[] coins = inFile.nextLine().split(" ");
		String st = n[0];
		numFloors = Integer.parseInt(n[1]);
		
		int numItems = Integer.parseInt(inFile.nextLine());
		itemList = new Item[numItems];
		
		for (int i=0; i<numItems; i++){
			Item tmp = new Item(inFile.nextLine());
			itemList[i] = tmp;
		}
		
		numPoke = Integer.parseInt(inFile.nextLine());

		pokeFloors = new String[numPoke];
		enemyPoke = new String[numPoke];
		
		for (int i=0; i<numPoke; i++){
			pokeFloors[i] = inFile.nextLine();
			enemyPoke[i] = inFile.nextLine();
		}
		
		usable = new ArrayList<String>();
		
		for (int i=0; i<numPoke; i++){
			String[] tmp = pokeFloors[i].split(" ");
			if (Integer.parseInt(tmp[0])<=floor && floor<=Integer.parseInt(tmp[1])){
				usable.add(enemyPoke[i]);
			}
		}
	}
	
	public void keyPressed(KeyEvent event){			
		//Checks to see if keys are pressed
    	int i = event.getKeyCode();
    	keys[i] = true;
    }
	
	public void keyReleased(KeyEvent evt){
		//Checks to see if keys are released
		int i = evt.getKeyCode();
		keys[i] = false;
		keysHeld[i] *= -1;

		
		
	}
	
	public void keyTyped(KeyEvent evt){} //Not used, but is part of implementing KeyListener
	
	public void update(){
	
		//BASIC MOVEMENT AND ATTACKING; UP DOWN LEFT RIGHT ARROW KEYS AND S BUTTON (FOR ATTACKS)
		resetActions();
		if (startedActions(allPokes) == false){ //allows the user to open the battle menu nothing has happened yet
			if (selectingnewmove == false){
				menuActive = keysHeld[KeyEvent.VK_SPACE];
			}
			
			getMenuActions();
		}
		

		if (menuActive == -1){
			if (p.performSpecial()){ //determines if a special attack has been selected

				p.setAction("specialatk");
				p.actionSet(true); //means pokemon has started to perform an action
				p.resetSpecial();
				textque.add("You used "+p.getCurMove().getName()+"!");

				if (pokemove(p, p.getDir()) == 2){
					String[] directions = {"up", "down", "left", "right"};
				    String[] reverse = {"down", "up", "right", "left"};
			
				    victim.setDir(reverse[Arrays.asList(directions).indexOf(p.getDir())]);
				    victim.setHurt();
					applySpecial(p.getCurMove(), p, victim);
				}
				else{
					if (p.getCurMove().getName().equals("swords-dance") || p.getCurMove().getName().equals("iron-defence")){
						applySpecial(p.getCurMove(), p, null);
						
					}
				}

			}
		}
		
		
		if (keys[KeyEvent.VK_ENTER]){
			// when selecting a new move, pressing enter allows the user to forward the textque
			
			if (selectingnewmove){
				menuActive = 1;
				keysHeld[KeyEvent.VK_SPACE] = 1;
				battleOn = true;
				
				if (textque.size()>3){
					for (int i=0; i<3; i++){
					textque.remove(0);
					}
				}
			}
		}


		if (menuActive == -1){
			if (p.getPauseB()==0){ //determines if paused (short pause in between attacks to make game smoother
				if (p.getActionset() == false && keys[KeyEvent.VK_A]==false && textque.size()==0){						
	
					//Basic player actions controlled by arrow keys and s key (for attacking)	
					if(keys[KeyEvent.VK_LEFT]){
						p.setDir("left");

						if (pokemove(p, "left") == 0){
							p.setTX(p.getX()-24); //TX, TY are where the pokemon are headed
							p.setTY(p.getY());
							
							determineEnemyActions(); //determines the actions enemies are about to perform
							
							p.actionSet(true); //actionSet(true) means that a pokemon has started an action
							p.addTurn("left");
							p.setAction("walk");
						}
		
						
					} else if(keys[KeyEvent.VK_RIGHT]){
						p.setDir("right");

						if (pokemove(p, "right") == 0){
							p.setTX(p.getX()+24);
							p.setTY(p.getY());
							
							
							p.actionSet(true);
							p.addTurn("right");
							
							p.setAction("walk");
						}
						
					} else if(keys[KeyEvent.VK_UP]){
						p.setDir("up");

						if (pokemove(p, "up") == 0){
							p.setTX(p.getX());
							p.setTY(p.getY()-24);
							
							p.actionSet(true);
							p.addTurn("up");
							p.setAction("walk");
						}
		
					} else if(keys[KeyEvent.VK_DOWN]){
						p.setDir("down");

						if (pokemove(p, "down") == 0){
							p.setTX(p.getX());
							p.setTY(p.getY()+24);
							
							p.actionSet(true);
							p.addTurn("down");
							p.setAction("walk");
	
						}
					} else if(keys[KeyEvent.VK_S] == true){ //basic attacking
						if (pressattack == false){
							pressattack = true;
							if (p.getActionset() == false){
								if (pokemove(p, p.getDir()) == 2){
									performbasicAtk(p, p.getDir());
									p.setAction("atk");
								}						
								else{
									p.setAction("atk");
								}
			
								p.actionSet(true);
								
							}
						}
					}
					else{
						pressattack = false;
						p.setAction("idle");
						
					}

				}
		
		
				if(p.getX() != p.getTX() || p.getY() != p.getTY()){
					//if the current position of the pokemon is not equal to the position it is planning on moving to, it can move toward the planned position
					if(p.getTurns()[0].equals("left")){
						p.move(-1, 0);
					}
					else if(p.getTurns()[0].equals("right")){
						p.move(1, 0);
					}
					else if(p.getTurns()[0].equals("down")){
						p.move(0, 1);
					}
					else if(p.getTurns()[0].equals("up")){
						p.move(0, -1);
					}	
				} else{
					if (p.getAction().equals("walk") && p.getActionset() == true){
	
						p.performed(true);
						p.finishTurn();
					}		
				}	
					
				
			
				if (p.getAction().equals("atk") || p.getAction().equals("specialatk")){
					if (p.getFrame() > 59){
						//attack animation = 60 frames
						if (pokefaint == true){
							setPause(40);
							pokefaint = false;
						}
						else{
							setPause(30);
							if (p.getAction().equals("specialatk")){
								setPause(40);
							}
						}
	
						p.performed(true);
	
					}			
				}		
						
				
				if (keys[KeyEvent.VK_A]){ //holding the a button allows the user to change direction without performing an action
					if (keys[KeyEvent.VK_UP]){
						p.setDir("up");
					}
					else if (keys[KeyEvent.VK_DOWN]){
						p.setDir("down");
					}
					else if (keys[KeyEvent.VK_LEFT]){
						p.setDir("left");
					}
					else if (keys[KeyEvent.VK_RIGHT]){
						p.setDir("right");
					}
				}
				
			}
			if (p.getPauseA() == p.getPauseB() && p.getPauseB()!=0){
				//pauses can be called at any time to stop the movement of the enemies; used to make the game flow smoother
				//if pauseA and pauseB are equal, the pause can be stopped, and movement can begin again
				p.resetPause();
				if (p.getAction().equals("atk") || p.getAction().equals("specialatk")){
					p.setAction("idle");
					p.resetFrame();
					p.actionSet(false);
				
				}
				if (selectingnewmove == false){ //selecting new move = unique case for texts
					textque.clear();
				}
				
			}
		}
		
		

		
		if (grid[p.getX()/24][p.getY()/24]==21){
			//21 on the grid is the stairs tile; allows pokemon to move to next floor
			resetActions();
			exit = true;
			transition = true;
		}
		
		resetActions(); //checks if the actions of all pokemon have been performed

		p.update();	
		
		p.autoheal(); //hp regen

		if (p.getAction().equals("walk")){
			if (p.getActionset()){
				determineEnemyActions(); //determines the actions of the enemies on the map
			}
		}
		else if (p.getAction().equals("atk") || p.getAction().equals("specialatk")){
			if (p.getPerformed()){
				determineEnemyActions();
			}
		}
		
	
		
		moveenemies();	//moves all enemies 
		
		resetActions();
		
		cam.update(p);
		repaint();
		

	}
	
	public void nextFloor(){
		//pokemon reaches next floor; all pokemon are reset except for the player pokemon
		
		d = new Dungeon(d1, d2);
		grid = d.getGrid();
		
		cam = new Camera(d, 480, 384);
		
		pokeRoom = d.getRooms();
		tmp = pokeRoom.get(0);		
		
		usable = new ArrayList<String>();
		
		for (int i=0; i<numPoke; i++){
			String[] tmp = pokeFloors[i].split(" ");
			if (Integer.parseInt(tmp[0])<=floor && floor<=Integer.parseInt(tmp[1])){
				usable.add(enemyPoke[i]);
			}
		}
		
		double oldexp = p.getExp();
		int level = p.getLevel();
		
		enemies.clear();
		allPokes.clear();
		
		placeEnemy(true);
		
		int posX;
		int posY;
		while (true){
			boolean f = true;
			posX = randInt(tmp.getX1()+1, tmp.getX2()-1);
			posY = randInt(tmp.getY1()+1, tmp.getY2()-1);
			for (Pokemon poke: enemies){
				if (posX == poke.getY() && posY == poke.getX()){
					f = false;
				}
			}
			if (posX == d.getStairY() && posY == d.getStairX()){
				f = false;
			}
			if (f){
				break;
			}
		}
		
		createPoke("Pfiles/rayquaza", ""+level, false, posX*24, posY*24);
		p.setExp(oldexp);

	}
	
	public void placeEnemy(boolean begin){ //spawns a random number of pokemon, each in a random room 
		
		while (true){
			int num = randInt(0, pokeRoom.size()-1);
			Room rm = pokeRoom.get(num);	
			boolean flag = true;
			if (!begin){
				if (rm.getY1()<p.getX()/24 && p.getX()/24<rm.getY2()){
					if (rm.getX1()<p.getY()/24 && p.getY()/24<rm.getX2()){
						flag=false;
					}
				}
			}
		
			int c=0;
			for (Pokemon poke: enemies){
				if (rm.getY1()<poke.getX()/24 && poke.getX()/24<rm.getY2()){
					if (rm.getX1()<poke.getY()/24 && poke.getY()/24<rm.getX2()){
						c++;
					}				
				}
			}
			if (c>2){
				flag=false;
			}
			if (flag){
				int posX;
				int posY;
				while (true){
					boolean f = true;
					posX = randInt(rm.getX1()+1, rm.getX2()-1);
					posY = randInt(rm.getY1()+1, rm.getY2()-1);
					if (!begin){
						if (posX == p.getY() && posY == p.getX()){
							f = false;
						}
					}
					
					for (Pokemon poke: enemies){
						if (posX == poke.getY() && posY == poke.getX()){
							f = false;
						}
					}
					if (posX == d.getStairY() && posY == d.getStairX()){
						f = false;
					}
					if (f){
						break;
					}
				}

				if (usable.size()>0){
					int n = randInt(0, usable.size()-1);
					String[] stuff = usable.get(n).split(" ");
					createPoke("Pfiles/"+stuff[0], ""+randInt(Integer.parseInt(stuff[1]), Integer.parseInt(stuff[2])),true,  posX*24, posY*24);
				}
			
			}
			if (flag){
				break;
			}
		}		
	}
	
	
	
	public void getMenuActions(){ //BATTLE MENU
	//determines the actions the user can take when using the menu, including exiting to main menu, viewing pokemon stats, and choosing a special attack
		
		if(!battleOn && !itemsOn && !partyOn){
			if(keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_DOWN]){
				delayCount++;
			}
		} 
		else if(battleOn && (keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_Z])){
				delayCount++;
		}
		else{
			delayCount = 0;
		}
		
		if(delayCount != 0){
			if(!battleOn && !itemsOn && !partyOn){
				if(keys[KeyEvent.VK_DOWN]){
					if(delayCount < 2){
						menuInd = (menuInd+1)%4;
					} 
					else if(delayCount%10 == 0){
						menuInd = (menuInd+1)%4;
					}
				} 
				else if(keys[KeyEvent.VK_UP]){
					if(delayCount < 2){
						menuInd--;
					} 
					else if(delayCount%10 == 0){
						menuInd--;
					}
					
					if(menuInd<0){
						menuInd = 3;
					}
				}
			}	
		}
		
		if (keys[KeyEvent.VK_Z] == false){
			presskey = false; 
		}
		
		if(menuActive == 1){ 
			if(keys[KeyEvent.VK_Z] == true && battleOn == false && partyOn == false && itemsOn == false){
				presskey = true;
				if(menuInd == 0){
					battleOn = true;
					itemsOn = false;
					partyOn = false;
				} else if (menuInd == 1){
					battleOn = false;
					itemsOn = true;
					partyOn = false;		
				} else if (menuInd == 2){
					battleOn = false;
					itemsOn = false;
					partyOn = true;
				} else{
					battleOn = false;
					itemsOn = false;
					partyOn = false;
					keysHeld[KeyEvent.VK_SPACE] = -1;
				}
			}
		}
		
		if (battleOn){ //battle menu screen, allows user to select a special move from the pokemons moveset
			
			if(delayCount != 0){
				if(keys[KeyEvent.VK_DOWN]){
					if(delayCount < 2){
						atkInd = (atkInd+1)%4;
					} 
					else if(delayCount%10 == 0){
						atkInd = (atkInd+1)%4;
					}
					
				} 
				else if(keys[KeyEvent.VK_UP]){
					if(delayCount < 2){
						atkInd--;
					} 
					else if(delayCount%10 == 0){
						atkInd--;
					}
					
					if(atkInd<0){
						atkInd = 3;
					}
				}
				else if(keys[KeyEvent.VK_RIGHT]){
					
					if(delayCount < 2){
						atkInd = (atkInd+2)%4;
					} 
					else if(delayCount%10 == 0){
						atkInd = (atkInd+2)%4;
					}
	
				}
				else if(keys[KeyEvent.VK_LEFT]){
					if(delayCount < 2){
						atkInd-=2;
					} 
					else if(delayCount%10 == 0){
						atkInd-=2;
					}	
					if(atkInd<0){
						atkInd += 4;
					}
				}
				else if (keys[KeyEvent.VK_Z]){
					if (presskey == false){
						
						if (delayCount == 2){

							presskey = true;
						
							//the battle menu screen can also appear when learning a new move 
							//when a pokemon knows 4 moves and can learn a new one, the screen appears to allow the user to select a move to delete (or not delete)
							if (selectingnewmove){
								battleOn = false;
								itemsOn = false;
								partyOn = false;
								menuActive = -1;

								keysHeld[KeyEvent.VK_SPACE] = -1;
								
								p.removeMove(atkInd);
								p.setMoveset();
								selectingnewmove = false;
								textque.clear();
								
								p.performed(false);
								for (Pokemon poke: allPokes){
									poke.actionSet(false);
								}
								
								
								
							}
							else{
								
								int lastmove = 3;
								for (int i=0; i<4; i++){
									if (p.getMoveSet()[i] == null){
										lastmove = i-1;
										break;
									}
										
								}
								if (atkInd<=lastmove){
									p.setCurMove(atkInd);
									p.getCurMove().lowerPP(); //-1 pp for usage of move
									battleOn = false;
									keysHeld[KeyEvent.VK_SPACE] = -1;
									menuActive = -1;
								}
							}
							
						}
					}
				}	
			}
		}
		
		if(keys[KeyEvent.VK_X]){
			battleOn = false;
			itemsOn = false;
			partyOn = false;
			if (selectingnewmove){
				selectingnewmove = false;
				textque.clear();
				menuActive = -1;
				keysHeld[KeyEvent.VK_SPACE] = -1;
				
				p.performed(false);
				for (Pokemon poke: allPokes){
					poke.actionSet(false);
				}
			}
			
			menuActive = -1;
		}
	}
	
	
	
	
	public void setPause(int frames){ //sets a pause where frames is the number of frames before the pause ends
		for (Pokemon poke: allPokes){
			poke.setPause(frames);
		}
	}	
	
	public void determineEnemyActions(){ //determines what actions the enemies are about to perform
		int num = enemies.size();
		int count = 0;
		for(Pokemon e: enemies){			
			
			if (e.getAction().equals("atk") || e.getAction().equals("specialatk") || moveable(e)==false){
				if (e.getActionset() == false){
					enemyaction(e);
					p.setHurt(); //player pokemon will be the target of an enemy attack, so it must be hurt
					break;
				}
			}
			else{
				count+=1;
			}
	
			if (count == num){
				for (Pokemon poke: enemies){
					if (poke.getActionset() == false){
						enemyaction(poke);
					}
				}
			}
				
		}

	}
	
	public void moveenemies(){
		//moves all enemies according to what action they have been set to perform. Attacking enemies take priority and move one by one, followed by moving pokemon (all move at the same time)
		
		int count = 0;
		
		//enemy move priority goes like this:
		//1. attacking enemies move first, and one by one in the order they appear in the arraylist enemies
		//2. enemies planning to move move at the same time
		
		boolean attackable = true;
		for (Pokemon e: enemies){ //if a single enemy is about to attack, the users move must be completed first

			if (e.getAction().equals("atk") || e.getAction().equals("specialatk")){
				attackable = false;
			}
		}
		if (p.getPerformed()){
			attackable = true;
			
		}
		
		
		
		if (p.getPauseA() == 0){
			if (attackable == true){
				for (Pokemon e: enemies){
					if (e.getPerformed() == false){
						if (e.getAction().equals("atk") || e.getAction().equals("specialatk")){
							count+=1;
							applymove(e);
							break; //allows attacks to take place one by one
						}
						if (e.getNoAction()){ //if it cant move
							count+=1;
							applymove(e);
							break;
						}
					}
					if (count == 0){ //meaning all attacking pokemon have finished attacking
						if (e.getAction().equals("walk")){
							applymove(e);
						}

	
						if (e.getAction().equals("idle")){
							applymove(e);

						}
					}
				}
			}
		}

	}
	
	public void enemyaction(Pokemon e){
		//determines the action of the pokemon
		if (moveable(e)){

			if (Math.abs(p.getTX()-e.getX())>=Math.abs(p.getTY()-e.getY())){

				if (p.getTX()<e.getX()){
					singleEnemyAction(e,"left");
				}
				if (p.getTX()>e.getX() && e.getActionset() == false){ 
					singleEnemyAction(e,"right");
				}
				if (p.getTY()<e.getY() && e.getActionset() == false){
					singleEnemyAction(e,"up");
				}
				if (p.getTY()>e.getY() && e.getActionset() == false){
					singleEnemyAction(e,"down");
				}
				if (e.getActionset() == false){
					singleEnemyAction(e, "none", true);
				}
			}
			else{
				if (p.getTY()<e.getY()){
					singleEnemyAction(e,"up");
				}
				if (p.getTY()>e.getY() && e.getActionset() == false){
					singleEnemyAction(e,"down");
				}
				if (p.getTX()<e.getX() && e.getActionset() == false){
					singleEnemyAction(e,"left");
				}
				if (p.getTX()>e.getX() && e.getActionset() == false){ 
					singleEnemyAction(e,"right");
				}
				if (e.getActionset() == false){
					singleEnemyAction(e, "none", true);
				}
			}		
			
		}
		else{
			setPause(50);
			e.setNoAction(true);
			e.actionSet(true);
		}
		
	}
	
	
	public void singleEnemyAction(Pokemon e, String dir){
		singleEnemyAction(e, dir, false);
	}
	
	public void singleEnemyAction(Pokemon e, String dir, boolean cannotmove){

		int x = xMove(dir);
		int y = yMove(dir);

		
		if (pokemove(e,dir) == 0){
				
			e.setDir(dir);
			e.setTX(e.getX()+x);
			e.setTY(e.getY()+y);
			e.addTurn(dir);
			e.setAction("walk"); //allows allied pokemon to move/attack again
			e.actionSet(true);
			
		}
		else if (pokemove(e, dir) == 2 && victim == p){

			e.setDir(dir);
			performenemybasicAtk(e, dir);	
			e.actionSet(true);
			
				
		}
		if (cannotmove){ //if there is nowhere for the enemy to possibly move to get closer to the player pokemon, then it will just move in another direction
					//if the path for an enemy pokemon is being obstructed by another enemy pokemon, it chooses a random direction to move in
			for (int i=0; i<4; i++){
				if (pokemove(e, directions[i]) == 0){

					e.setDir(directions[i]);
					e.setTX(e.getX()+xMove(directions[i]));
					e.setTY(e.getY()+yMove(directions[i]));
					e.addTurn(directions[i]);
					e.setAction("walk"); //allows allied pokemon to move/attack again
					e.actionSet(true);
					break;		
				}
			}
			//if (count == 4){ //if an enemy pokemon is completely stuck
			//	e.actionSet((true)
			//}

		}
	
	}
	
	public void applymove(Pokemon e){
		//if (e.getNoAction()){ //pokemon action inhibited by freeze, paralysis, sleep, flinch, or recharge
		//	setPause(50);
		//}
		if (e.getNoAction() == false){

			if(e.getX() != e.getTX() || e.getY() != e.getTY()){
				if(e.getTurns()[0].equals("left")){
					e.move(-1, 0);
				}
				else if(e.getTurns()[0].equals("right")){
					e.move(1, 0);
				}
				else if(e.getTurns()[0].equals("down")){
					e.move(0, 1);
				}
				else if(e.getTurns()[0].equals("up")){
					e.move(0, -1);
				}	
				
			}

			if (e.getAction().equals("walk") && e.getActionset()==true && e.getPerformed() == false){ 
				if (e.getX() == e.getTX() && e.getY() == e.getTY()){
					e.performed(true);
					e.finishTurn();
					
				}
			}
			
			if (e.getX() == e.getTX() && e.getY() == e.getTY()){
				if (p.getAction().equals("idle")){
					if (e.getAction().equals("walk")){
						e.setAction("idle");
					}
				}
			}
			if (e.getAction().equals("atk") || e.getAction().equals("specialatk")){
				if (e.getFrame() > 59){
					setPause(30);
					e.performed(true); //completes the turn and sets the framewait (amount of loops before the next turn can start)
					if (p.getAction().equals("specialatk")){
						setPause(40);
					}
					
					e.setAction("idle");	
				}
	
			}
		

		}
		e.update();
		
	}
	
	public void performenemybasicAtk(Pokemon poke, String dir){ //allows the enemy to perform an attack; sets an attack action for an enemy pokemon (atk or specialatk)
		

		if ((int)(Math.random()*3)>=1){ //enemies have a 2/3 chance of performing a special attack
			String[] directions = {"up", "down", "left", "right"};
		    String[] reverse = {"down", "up", "right", "left"};
	
		    victim.setDir(reverse[Arrays.asList(directions).indexOf(dir)]);
		    victim.setHurt(); //hurt animation
		    
		    int nummoves = 0; //nummoves = number of moves available
		    for (int i=0; i<4; i++){
		    	if (poke.getMoveSet()[i] == null){
		    		nummoves = i;
		    		break;
		    	}
		    }
		    //enemies are not affected by a pp limit when using specials
		    
		    int index = (int)(Math.random()*nummoves); //checks if the move still has pp left
			    
			poke.setAction("specialatk");
	
			textque.add(poke.getName()+" used "+poke.getMoveSet()[index].getName()+"!");
			
			applySpecial(poke.getMoveSet()[index], poke, victim);

		
		}
		else{
			performbasicAtk(poke, dir);
		}
	
	}
	
	public void performbasicAtk(Pokemon poke, String dir){
		//perform a basic attack (S button for player poke, 1/3 chance for enemy pokemon)
		
		double damage = 1;
		poke.setAction("atk");
		String[] directions = {"up", "down", "left", "right"};
	    String[] reverse = {"down", "up", "right", "left"};

	    victim.setDir(reverse[Arrays.asList(directions).indexOf(dir)]);
	    victim.setHurt(); //hurt animation
		damage = ((2*poke.getLevel()/250.0)*(poke.getCAtk()/victim.getCDef())*10+2);
		
		textque.add("Damage done: "+(int)damage);
		victim.takeDamage(damage);
		if (victim.getCHp()<1){
			if (victim.isEnemy()){

				enemies.remove(victim);
				allPokes.remove(victim);
				
				double expgain = (victim.getExpgiven()*victim.getLevel())/7; //experience formula
				poke.addExp(expgain);

				textque.add(victim.getName()+" fainted! You gained "+(int)expgain+" EXP");
				pokefaint = true;
				

			}

		}


		
	}
	
	public int xMove(String dir){ //takes a direction and returns the horizontal distance required to head to the next tile in direction dir
		int x = 0;
		if (dir.equals("left")){
			x = -24;
		}
		if (dir.equals("right")){
			x = 24;
		}
		return x;
	}
	
	public int yMove(String dir){
		int y = 0;
		if (dir.equals("up")){
			y = -24;
		}
		if (dir.equals("down")){
			y = 24;
		}
		return y;
	}
	
	public int pokemove(Pokemon pkmn,  String dir){ //determines if a pokemon can move in a specific direction
		int x = xMove(dir);
		int y = yMove(dir);
		
		if (grid[(pkmn.getX()+x)/24][(pkmn.getY()+y)/24]!=1 && grid[(pkmn.getX()+x)/24][(pkmn.getY()+y)/24]!=21){ 

			return 1;
		}
		
		for (Pokemon poke: allPokes){
			if(poke.getTX() - pkmn.getTX() == x && poke.getTY() - pkmn.getTY() == y){
				if (enemies.contains(pkmn)){
					if (poke == p){
						
						victim = poke;
						return 2;
				
					}
				}
				else if (pkmn == p){
					if(enemies.contains(poke)){
						victim = poke;
						return 2;
					
					}
				}
//if theres a pokemon in the way of another pokemon, he automatically becomes the target pokemon of an attack 
				if (enemies.contains(pkmn) == true && enemies.contains(poke)){
					return 3;
				}
			} 
		}
		
		
		
		return 0;
	}
	
	public boolean startedActions(ArrayList<Pokemon> pokemon){ //determines if all the pokemon in an arraylist have started an action, used for enemies
		for (Pokemon poke: pokemon){
			if (poke.getActionset() == false){
				return false;
			}
		}
		return true;
	}
	
	public boolean finishedActions(ArrayList<Pokemon> pokemon){ //determines if all the pokemon in an arraylist have finished
		for (Pokemon poke: pokemon){
			if (poke.getPerformed() == false){
				return false;
			}
		}
		return true;
	}
	
	
	public void resetActions(){ //determines if all pokemon are done performing an action
		int pnum = allPokes.size();
		int i=0;
		
		for (Pokemon poke: allPokes){
			if (poke.getPerformed() == true){
				i += 1;
			}
		}
		if (i == pnum){

			for (Pokemon poke:allPokes){
				poke.performed(false);
				poke.actionSet(false);
				
			}
			
			if (p.getExp()>=p.getTotalExp()){ //level up 
				
				p.levelUp();
				textque.add(p.getName()+" reached level "+p.getLevel()+"!");
				textque.add("Hp, attack and defence have all increased!");
				setPause(60);
				if (p.getMovelevels().contains(p.getLevel())){
					int index = p.getMovelevels().indexOf(p.getLevel());
					Move tempMove = p.getAllmoves().get(index);
					if (p.getMoveSet()[3]==null){
						p.setMoveset();
						textque.add(p.getName()+" learned "+tempMove.getName()+"!");
					}
					else{
						selectingnewmove = true;
						textque.add(p.getName()+" wants to learn "+tempMove.getName());
						textque.add("select a move to delete. press x to keep all moves");
						
					}
				}
			}	
		}	

	}
	
	
	public void drawPoke(Graphics g){
		int[] tmp;
		for (Pokemon poke: allPokes){
			tmp = cam.apply(poke.getX(), poke.getY());
		
        	g.drawImage(poke.getImage(), tmp[0]+12-poke.getWidth()/2, tmp[1]-poke.getHeight()/2, this);
		}
	}
	
	public void drawMap(Graphics g){
		
		//cam.update(p);
		
		int[] tmp;
		
		int sx = cam.getX()/24;
		int sy = cam.getY()/24;
		//Image img;
		
		g.setColor(Color.WHITE);
		for(int i=0; i<21; i++){
			
			for(int j=0; j<17; j++){
				
				if(sx+i<grid.length && sy+j<grid[0].length){

					tmp = cam.apply((sx+i)*24, (sy+j)*24);
					
					g.drawImage(tiles[grid[sx+i][sy+j]], tmp[0], tmp[1], this);
					
					if(grid[sx+i][sy+j] == 1 && keys[KeyEvent.VK_A]){
						g.drawRect(tmp[0], tmp[1], 24, 24);
					}
				}
			}
		}
	}
	
	public void drawMiniMap(Graphics g){
		
		for(int i=0; i<grid.length; i++){
			for(int j=0; j<grid[i].length; j++){
				
				if(grid[i][j] == 1){
					
					g.setColor(new Color(255,255,255, 100));
					g.fillRect(i*4, j*4, 4, 4);
					
				} else if (grid[i][j] != 0){
					
					g.setColor(new Color(0,255,0, 100));
					g.fillRect(i*4, j*4, 4, 4);
				}
			}
		}
		
		g.setColor(new Color(255,0, 0));
		g.fillOval(p.getX()/6, p.getY()/6, 4,4);
	}
	
	public void drawTransition(Graphics g){
			
		/*if(alph<=255){
			g.setColor(new Color(0, 0, 0, alph));
		} else{
			g.setColor(Color.BLACK);
		}
		
		g.fillRect(0, 0, 480, 384);
			
		alph++;
		
		if(510-alph>=0 && 510-alph<=255){
			
			g.setColor(new Color(19, 36, 100, 510-alph));
			g.setFont(new Font("Arial", Font.PLAIN, 36));
			g.drawString("", 80, 146);
			
		} else if(alph>=200){
			
			g.setColor(new Color(19, 36, 100, alph-200));
			g.setFont(new Font("Arial", Font.PLAIN, 36));
			g.drawString("", 80, 146);
		
		}
		
		if(510-alph == 0){
			alph = 0;
			transition = false;
		}	*/
		
		alph+= 3;
		
		if(alph<255){	
			g.setColor(new Color(0, 0, 0, alph));	
		} 
		else if(alph>=255 && alph<=510){
			g.setColor(new Color(0, 0, 0, 510-alph));
		}
			
		else{ 
			alph = 0;
			transition = false;
		}
		
		g.fillRect(0,0,480,384);	
	}
	
	public void drawMenu(Graphics g){
		//draws all of the in game menus, i.e. battle menu, stats menu
		if(g.getFont() != f){
			g.setFont(f);
		}
		
		
		g.drawImage(bg, 4, 384, this);
		
		g.setColor(Color.BLACK);
		g.drawString("BATTLE", 320, 430);
		g.drawString("ITEMS", 320, 475);
		g.drawString("PARTY", 320, 520);
		g.drawString("EXIT", 320, 565);
		
		if(menuActive == 1){
			
			//getMenuActions();
			
			g.setColor(new Color(38, 96, 171));
			g.drawString(menuName[menuInd], 320, 430+menuInd*45);
			
			if(partyOn){
				g.drawImage(pokeTab, 17, 398, this);
				g.drawImage(pokeTab, 163, 398, this);
				
				
				
				if(g.getFont() == f){
					g.setFont(new Font("Pokemon Pixel Font", Font.PLAIN, 24));
				}
				
				g.setColor(Color.BLACK);
				
				g.drawImage(p.get3DPic(), 85-p.get3DWidth()/2, 480-p.get3DHeight()/2, this);
				//g.drawImage(p.getMenuPic(), 17+146+95, 408, this);
				g.drawString(p.getName().toUpperCase(), 25+146, 425);
				g.drawString("Lvl. "+p.getLevel(), 25+146, 450);
				g.drawString("Exp: "+(int)p.getExp(), 25+146, 475);
				g.drawString("HP: "+(int)p.getCHp()+"/ "+(int)p.getMaxHp(), 25+146, 500);
				g.drawString("Atk: "+(int)p.getCAtk(), 25+146, 525);
				g.drawString("Def: "+(int)p.getCDef(), 25+146, 550);
			}
			
			else if(itemsOn){
				g.drawImage(itemTab, 17, 398, this);
			}
			else if(battleOn){
				
				g.drawImage(moveTab, 16, 384+32, this);
				g.drawImage(moveTab, 16, 384+113, this);
				g.drawImage(moveTab, 162, 384+32, this);
				g.drawImage(moveTab, 162, 384+113, this);
				
				
				int[][] tabPos = new int[][]{{16,384+32},{16, 384+113},{162,384+32},{162,384+113}};
				g.drawImage(tabSelect, tabPos[atkInd][0]-3, tabPos[atkInd][1]-4, this);
				
				if(g.getFont() == f){
					g.setFont(new Font("Pokemon Pixel Font", Font.PLAIN, 24));
				}
				
				for(int i=0; i<2; i++){
					
					Move m1 = p.getMoveSet()[i*2];
					Move m2 = p.getMoveSet()[i*2+1];
					g.setColor(Color.BLACK);
					if (m1 != null){
						g.drawString(m1.getName().toUpperCase(), 25+146*i, 438);
						g.drawString("PP: "+m1.getPPleft()+"/ "+m1.getPP(),  25+146*i, 480);
						g.setColor(Color.RED);
						g.drawString(m1.getType().getString().toUpperCase(), 17+146*i+8, 459);
					}
					g.setColor(Color.BLACK);
					if (m2 != null){
						g.drawString(m2.getName().toUpperCase(), 25+146*i, 438+80);
						g.drawString("PP: "+m2.getPPleft()+"/ "+m2.getPP(),  25+146*i, 480+80);
						g.setColor(Color.RED);
						g.drawString(m2.getType().getString().toUpperCase(), 17+146*i+8, 459+80);
					}
					
					
				}
				
			}
		}
	}
	
	public void paintComponent(Graphics g){
		
		if(!transition){
			drawMap(g);
		
		drawPoke(g);
		
        drawMiniMap(g);
        
        drawMenu(g);
		}
		else{
			drawTransition(g);
		}
		
        
        
        g.setColor(Color.BLACK);
		g.setFont(new Font("Pokemon Pixel Font", Font.PLAIN, 24));
		//draws action texts, i.e. "its super effective"
		for (int i=0; i<textque.size(); i++){
			if (i<3){
				g.drawString(textque.get(i).toUpperCase(), 40, 625+40*i);
			}
			
		}
    }
    
    public static void createPoke(String name, String level, boolean enemy, int px, int py){
    	//takes in the name, level, and position and creates a pokemon. if enemy is true, the pokemon is added to the enemies list
    	//else the player pokemon becomes the newly created pokemon
    	Scanner inFile = null;
    	
    	try{
    		inFile = new Scanner(new File(name+".txt"));
    	}
    	catch(IOException ex){
    	}
    	
    	int filepos = 0;
			
		String pokeName = "";
		String pokeTypeA = "";
		String pokeTypeB = "";
		String pHP = ""; 
		String pAtk = "";
		String pDef = "";
		double lvlHP = 0;
		double lvlAtk = 0;
		double lvlDef = 0;
		String expYield = "";
		String expType = "";
		String evLevel, evName;
		String lenFrames = "";
		//boolean isenemy = createpoke;
		ArrayList<Move> moveSet = new ArrayList<Move>();
		ArrayList<Integer> mLevels = new ArrayList<Integer>();
		
		while (inFile.hasNextLine()){
			
    		String line = inFile.nextLine();
    		
    		if (filepos == 0){
    			String[] pokeInfo = line.split(" ");
    			pokeName = pokeInfo[0];
    			pokeTypeA = pokeInfo[1];
    			pokeTypeB = pokeInfo[2];
    		}
    		else if (filepos == 1){
    			String [] stats = line.split(" ");
    			pHP = stats[0];
    			pAtk = stats[1];
    			pDef = stats[2];
    		}
    		else if (filepos == 2){
    			String [] stats = line.split(" ");
    			lvlHP = Double.parseDouble(stats[0]);
    			lvlAtk = Double.parseDouble(stats[1]);
    			lvlDef = Double.parseDouble(stats[2]);
    		}
    		else if (filepos == 3){
    			expYield = line;
    		}	
    			  			
    		else if (filepos == 4){
    			expType = line;
    		}
    		else if (filepos == 5){
    			String [] evolvedata = line.split(" ");
    			evLevel = evolvedata[0];
    			evName = evolvedata[1];
    		}
    		else if (filepos == 6){
    			lenFrames = line;
    		} 
    		if (filepos > 6){
    			String [] movedata = line.split(" ");
				
    			int movelevel = Integer.parseInt(movedata[0]);
    			int index = moveList.indexOf(movedata[1]);
    			Move tmove = allMoves.get(index);
    			Move pmove = new Move(tmove.getName(), tmove.getType().getString(), tmove.getPP()+"", tmove.getDamage()+"", tmove.getAcc()+"", tmove.getSpecial()); 
    			pmove.setType(tmove.getType());
    			//duplicates the move that already exists in the move database so that the moves can change independently
    			
    			moveSet.add(pmove);
    			mLevels.add(movelevel);	
    		}
    		filepos++;		
		}
		Pokemon	newPoke = new Pokemon(pokeName, pHP, pAtk, pDef, "0", level, expType, expYield, pokeTypeA, pokeTypeB, enemy, py, px, lenFrames);
		newPoke.createMoveSet(moveSet, mLevels);
		newPoke.setScaling(lvlHP, lvlAtk, lvlDef);
		allPokes.add(newPoke);
		if (enemy){
			enemies.add(newPoke);
		}
		else{
			p = newPoke;
		
		}
		
    }
    
    public static void loadMoves(){
		//creates the move database
		Scanner inFile = null;
    	
    	try{
    		inFile = new Scanner(new File("Data/MovesData.txt"));
    	}
    	
    	catch(IOException ex){}
    	
		while (inFile.hasNextLine()){
			
    		String line = inFile.nextLine();
    		
			String[] move = line.split(" ");
			
			Move newMove = new Move(move[0], move[1], move[2], move[3], move[4], move[5]);
			int index = Arrays.asList(typenames).indexOf(move[1]);
			newMove.setType(alltypes.get(index));
			allMoves.add(newMove); //entire move database; every move used in the game is held here
			moveList.add(move[0]); 
		}		
    }
    
    public static void loadTypes(){
    	
    	Scanner inFile = null;
    	
    	try{
    		inFile = new Scanner(new File("Data/Types.txt"));
    	}
    	
    	catch(IOException ex){}
    	
    	int filepos = 0;
    	while (inFile.hasNextLine()){
    		
    		String line = inFile.nextLine();
    	
			String[] typeinfo = line.split(" ");
			Type newtype = new Type(typenames[filepos]);
			
			for (int i=0; i<16; i++){
				if (typeinfo[i].equals("0.5")){
					newtype.addWeak(typenames[i]);
				}
				else if (typeinfo[i].equals("2")){
					newtype.addSuper(typenames[i]);
				}
				else if(typeinfo[i].equals("0")){
					newtype.addNoeffect(typenames[i]);
				}
			}
			alltypes.add(newtype);
			filepos+=1;
    	}
    }
    
   public boolean moveable(Pokemon poke){
    	if (poke.getStatusA().equals("paralyzed")){
    		poke.setAction("idle");
    		poke.performed(true);
    		return false;		
    	}
    	
    	else if (poke.getStatusA().equals("frozen")){
    		poke.setAction("idle");
    		poke.performed(true);
    		return false;
    			
    	}
    	
    	else if (poke.getStatusA().equals("asleep")){
    		poke.setAction("idle");
    		poke.performed(true);
    		return false;
    			
    	}
    	else{
    		return moveableB(poke);
    	}
    }
    
    
    public boolean moveableB(Pokemon poke){ //determines if pokemon needs to recharge or has flinched
    	if (poke.getStatusB().equals("recharge")){
    		poke.clearStatusB();
    		poke.setAction("idle");
    		poke.performed(true);
    		return false;
    	}
    	else if (poke.getStatusB().equals("flinch")){
    		poke.clearStatusB();
    		poke.setAction("idle");
    		poke.performed(true);
    		return false;
    	}
    	return true;
    }
    
    public void applySpecial (Move atk, Pokemon p1, Pokemon p2){
    	String special = atk.getSpecial();
		if (special.equals("none")){
			attack(atk, p1, p2);
		}
		
		//main status conditions
		else if (special.equals("poison")){
			if (attack(atk, p1, p2)){ //determines if attack does damage (if it doesnt miss or isnt a no effect move)
				if ((int)(Math.random()*10)>=7){
					p2.setStatusA("poisoned");
					if (p == p2){
						textque.add("You have been poisoned!");
					}
					else{
						textque.add(p2.getName()+" has been poisoned");
					}
				}
			}
		}
		else if (special.equals("Cpoison")){
			if (attack(atk, p1, p2)){
				p2.setStatusA("poison");
				if (p == p2){
					textque.add("You have been poisoned!");
				}
				else{
					textque.add(p2.getName()+" has been poisoned");
				}
			}
		}
		else if (special.equals("burn")){
			if (attack(atk, p1, p2)){
				if ((int)(Math.random()*10)>=7){
					p2.setStatusA("burned");
					if (p == p2){
						textque.add("You have been burned!");
					}
					else{
						textque.add(p2.getName()+" has been burned");
					}
				}
			}
		}
		else if (special.equals("freeze")){
			if (attack(atk, p1, p2)){
				if ((int)(Math.random()*10)>=7){
					p2.setStatusA("frozen");
					if (p == p2){
						textque.add("You have been frozen!");
					}
					else{
						textque.add(p2.getName()+" has been frozen");
					}	
				}
			}	
		}
		else if (special.equals("sleep")){
			if (attack(atk, p1, p2)){
				p2.setStatusA("asleep");
				if (p == p2){
					textque.add("You fell asleep!");
				}
				else{
					textque.add(p2.getName()+" fell asleep");
				}
			}
		}
		else if (special.equals("paralyze")){
			if (attack(atk, p1, p2)){
				if ((int)(Math.random()*10) == 1){ //10%
					p2.setStatusA("paralyzed");
					if (p == p2){
						textque.add("You were paralyzed!");
					}
					else{
						textque.add(p2.getName()+" was paralyzed");
					}
				}
			}
		}
		
		else if (special.equals("Cparalyze")){
			if (attack(atk, p1, p2)){
				p2.setStatusA("paralyzed");
				if (p == p2){
					textque.add("You were paralyzed!");
				}
				else{
					textque.add(p2.getName()+" was paralyzed");
				}
			}
		}
		
		//other status/move effects
		else if (special.equals("confuse")){ //30% confuse
			if (attack(atk, p1, p2)){
				if ((int)(Math.random()*10)>=7){
					p2.setStatusA("confused");
				}
			}
		}
		else if (special.equals("Cconfuse")){ //100% confuse
			if (attack(atk, p1, p2)){
				p2.setStatusA("confused");
			}
		}
		
		else if (special.equals("heal")){
			double hp1 = p2.getCHp();
			if (attack(atk, p1, p2)){
				double hp2 = p2.getCHp();
				double diff = Math.abs(hp1-hp2);
				p1.heal(diff);
			}
		}
		else if (special.equals("selfheal")){ //healing move, does no damage
			if (attack(atk, p1, p2)){
				if (atk.getName().equals("synthesis")){
					p1.heal(0.25*p1.getMaxHp());
				}
				if (atk.getName().equals("recover")){
					p1.heal(0.5*p1.getMaxHp());
				}
			}
		}
		else if (special.equals("flinch")){
			if (attack(atk, p1, p2)){
				if ((int)(Math.random()*10)>=7){
					p2.setStatusB("flinch");
				}
			}
		}
		else if (special.equals("recharge")){
			if (attack(atk, p1, p2)){
				p1.setStatusB("recharge");
			}
		}
		
		//unique specials
		else if (special.equals("level")){
			attack(atk, p1, p2, "level"); //seismic toss; damage based on level
		}
		else if (special.equals("forty")){
			attack(atk, p1, p2, "forty"); //dragon rage; damage = 40
		}	
		
		//perfect accuracy moves
		else if (special.equals("surehit")){
			attack(atk, p1, p2, "surehit");
		}
			
		//stat buffs/debuffs
		else if (special.equals("raisedefence")){ //move that raises attack, there does not need to be a victim in this case
			p1.setCDef(0.05*p1.getCDef());
			
			if (p == p1){
				textque.add("Your defence rose!");
			}
			else{
				textque.add(p1.getName()+"'s defence rose");
			}	
		}	
		else if (special.equals("raiseattack")){ //move that attack, there does not need to be a victim in this case
			p1.setCAtk(0.05*p1.getCAtk());

			if (p == p1){
				textque.add("Your attack rose!");
			}
			else{
				textque.add(p1.getName()+"'s attack rose");
			}		
		}
		else if (special.equals("raiseattackanddefence")){
			if (attack(atk, p1, p2)){
				p1.setCAtk(0.2*p1.getCAtk());
				p1.setCDef(0.1*p1.getCDef());
				if (p == p1){
					textque.add("Your defence rose!");
				}
				else{
					textque.add(p1.getName()+"'s defence rose");
				}
			}
		}
		
		else if (special.equals("decreaseenemydefence")){
			if (attack(atk, p1, p2)){
				p2.setCDef(-0.1*p1.getCDef());
				if (p == p2){
					textque.add("Your defence fell!");
				}
				else{
					textque.add(p2.getName()+"'s defence fell");
				}
			}	
		}	
		else if (special.equals("decreaseenemyattack")){
			if (attack(atk, p1, p2)){
				p2.setCAtk(-0.1*p1.getCAtk());
				if (p == p2){
					textque.add("Your attack fell!");
				}
				else{
					textque.add(p2.getName()+"'s attack fell");
				}
			}		
		}	
		else if (special.equals("decreaseenemyacc")){
			if (attack(atk, p1, p2)){
				p2.changeAcc(p2.getAcc()*0.9);
				if (p == p2){
					textque.add("Your accuracy fell!");
				}
				else{
					textque.add(p2.getName()+"'s accuracy fell");
				}
			}		
		}	
			
		
	}
	
	public boolean attack(Move move, Pokemon p1, Pokemon p2){ //no extra specified, extra = "none"
		return attack(move, p1, p2, "none");
	}
    
    public boolean attack (Move move, Pokemon p1, Pokemon p2, String extra){ //some moves have special effects, such has increased crit chance, damage based on level, etc, these are detemined by extra
    	
    	String message = ""; //super effective, no effect, not very effective, critical hit, miss
    	if (move.getType().getNoEffect().contains(p2.getType1()) || move.getType().getNoEffect().contains(p2.getType2())){
    		message = "no effect";
    		textque.add("It has no effect on "+p2.getName()+"...");
    	}
    	else if (!extra.equals("surehit")){ //moves with surehit cannot miss
    		if ((int)(Math.random()*100)>=move.getAcc()*p1.getAcc()){
 				message = "miss";
 				textque.add(p1.getName()+"'s attack missed!");
    		}
    	}
    	if (message.equals("no effect") || message.equals("miss")){
    		return false;
    	}
    	double damage = 0;
    	double multiplier = 1;
    	if (move.getDamage()>0){
    		
    		damage = ((2*p1.getLevel()/250.0)*(p1.getCAtk()/p2.getCDef())*move.getDamage()+2); //damage formula
    		multiplier = 1;
    		
    		
    		
	    	if (move.getType().getSuper().contains(p2.getType1())){ //super effective
	    		multiplier*=2;
	    	}
	    	if (move.getType().getSuper().contains(p2.getType2())){
	    		multiplier*=2;
	    		
	    	}
	    	if (move.getType().getWeak().contains(p2.getType1())){ //not very effective
	    		multiplier*=0.5;
	    	}   
	    	if (move.getType().getWeak().contains(p2.getType2())){
	    		multiplier*=0.5;
	    	}  	
	    	
	    	damage*=multiplier;
	    	
	    
	    	
	    	if (multiplier>1){
	    		textque.add("It's super effective!");
	    	}
	    	else if (multiplier<1){
	    		textque.add("It's not very effective...");
	    	}
	    		
	    		
	    	if ((int)(Math.random()*100)>90){ //critical hit
	    		damage*=1.5;
	    		textque.add("A critical hit!! Damage done: "+(int)damage);
	    	}
	    	else{
	    		textque.add("Damage done: "+(int)damage);
	    	}
	    	
	    	
    	}	
	    if (extra.equals("level")){
	    	damage = p1.getLevel(); //seismic toss; does damage = attacker level
	    	textque.add("Damage done: "+damage);
	    }
	    if (extra.equals("forty")){
	    	damage = 40; //dragon rage; does 40 damage regardless of stats/level
	    	textque.add("Damage done: "+damage);
	    }
	    
	    

	    p2.takeDamage(damage);
	   	if (p2.getCHp()<1){ //if the pokemon being attacked is dead
	   		if (p2.isEnemy()){ //checks if the pokemon being attacked is an enemy (meaning the attacker is the player)
	   			enemies.remove(p2);
	   			allPokes.remove(p2);

	   			textque.add(p2.getName()+" fainted!");
	   			pokefaint = true;
	   			double expgain = (victim.getExpgiven()*victim.getLevel())/7; //experience formula
				p1.addExp(expgain); //adds exp offered by the pokemon
				

	   		}

	   	}
	    return true;
	   
    }
    
    public static int randInt(int min, int max) {

	    // Usually this should be a field rather than a method variable so
	    // that it is not re-seeded every call.
	    Random rand = new Random();
	
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	
	    return randomNum;
	}
    

       
}
