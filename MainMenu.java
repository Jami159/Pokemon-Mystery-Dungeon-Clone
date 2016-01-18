import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;

public class MainMenu extends JFrame implements ActionListener{
	
	//MainMenu
	//Allows for user to play, ask for help, or exit the game
	
	JLabel bg;						//Background image					
	MenuPanel menu;	
	private javax.swing.Timer myTimer;
	
	public MainMenu(){
		
		//Basic setup of screen
		//Positions and sizes are set for all buttons and images
		super("Pokemon Mystery Dungeon");
		
		setLayout(null);
		setSize(480, 768);
		
		menu = new MenuPanel();								//Will draw basic Menu 
		menu.setSize(480, 768);								//Sets position location
		menu.setLocation(0, 0);
		add(menu);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//Program will exit on close
		setVisible(true);	
			
		myTimer = new javax.swing.Timer(15,this);			//Starts clock
		myTimer.start();								//Makes it visible
	
	}
	
	public void actionPerformed(ActionEvent e){
		
		//Checks for any actions
		
		//Object src = e.getSource();			//Gets source of ActionEvent
		
		String s = menu.update();				//Returns chosen pokemon or ""

		String[] info = s.split(" ");
		
		if(!info[0].equals("s")){
			new Main(info[0], info[1], info[2], info[3]);			//Passes in chosen pokemon to new game
			setVisible(false);
			myTimer.stop();											//Stops Menu and start Main game
			
		}
	}
	
	public static void main(String[] args){
		new MainMenu();
	}
}

class MenuPanel extends JPanel implements KeyListener{
	
	Font f; 			//Font used
	
	//All the images used in this Panel
	Image curPic, menuPic, optionTab, selected, controlsMenu, starterTab, pokeSelect, dungeonMenu, dTab, dSelect, titlePoke[], titleScreen;
	
	Pokemon[] pokeUnlocked;			//All the pokemon available for the user to pick
	String pokePick, pokeLvl, pokeExp, curDung, names[], dungeonNames[];	//Holds information about user's chosen pokemon
																			//Holds names of dungeons
	
	int ind, delayCount, alpha, pInd, dInd, tFrame;							//Holds indexes of chosen pokemon, dungeon, option
	int[][] pos = new int[][]{{25, 469},{25, 597},{253,469}, {253,597}};	//Positions for Images to be blit
	int[][] dPos;															//Positions for dungeons to be blit
	
	boolean[] keys, options;												//Holds all key data and which options are selected
	int[] keysHeld;															
	boolean fading, halfFade;												//Used for fading transition between screens
	
	public MenuPanel(){
		
		f = new Font("Pokemon Pixel Font", Font.PLAIN, 36);
		
		menuPic = new ImageIcon("menuPic.png").getImage();
		optionTab = new ImageIcon("optionTab.png").getImage();
		selected = new ImageIcon("selected.png").getImage();
		controlsMenu = new ImageIcon("controlsMenu.png").getImage();
		starterTab = new ImageIcon("starterTab.png").getImage();
		pokeSelect = new ImageIcon("pokeSelect.png").getImage();
		dungeonMenu = new ImageIcon("dungeonPic.png").getImage();
		dTab = new ImageIcon("dungeonTab.png").getImage();
		dSelect = new ImageIcon("dungeonSelect.png").getImage();
		titleScreen = new ImageIcon("titleScreen.png").getImage();
		
		curPic = menuPic;
		
		titlePoke = new Image[39];														//Aerodactyl animtation for title screen
		
		for(int i=0; i<39; i++){
			titlePoke[i] = new ImageIcon("aerodactylTitle/tmp-"+i+".gif").getImage();
		}
		
		pokeUnlocked = new Pokemon[4];
		names = new String[]{"bulbasaur", "squirtle", "charmander", "pikachu"};			//Available pokemon
		
		dungeonNames = new String[]{"tiny woods", "beach cave", "sinister woods", "shimmering desert", "mystifying forest", "mt. thunder"};	//Dungeons
		
		dInd = 0;
		curDung = "Dungeon"+Integer.toString(dInd+1);					//Default dungeon is set to 1
		for(int i=0; i<4; i++){
			pokeUnlocked[i] = createPoke("Pfiles/"+names[i], "20");		//Creates 4 Pokemon objects for user to choose from
		}
		
		dPos = new int[6][2];											//Dungeon positions
		
		int c = 0;														//Fills array with appropriate positions
		for(int i=0; i<2; i++){
			for(int j=0; j<3; j++){
				
				dPos[c] = new int[]{63+204*i, 384+61+100*j};
				c++;
			}
		}
		
		ind = 0;
		delayCount = 0;
		alpha = 0;
		tFrame = 0;
		fading = false;
		
		pokePick = "";
		pokeLvl = "5";
		pokeExp = "-1";
		
		keys = new boolean[2000];	
		keysHeld = new int[2000];
		options = new boolean[4];
		Arrays.fill(keysHeld, -1);
		
		addKeyListener(this);				//Allows panel to use KeyListener
		this.setFocusable(true);			//Sets focus on panel
        this.grabFocus();
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
	
	public void keyTyped(KeyEvent evt){} //Part of KeyListener implementation
		
	public String update(){	
		
		//Performs all MenuPanel tasks and actions
		
		getMenuActions();									//Checks to see if user has pressed keys and changed options
		
		repaint();											//Paints all components of panel
		
		if(keys[KeyEvent.VK_Z] || keys[KeyEvent.VK_X]){		//If current option is selected
			
			if(curPic == menuPic){							//Fading from menu into another screen
				fading = true;
				Arrays.fill(options, false);				//Sets desired screen to true
				options[ind] = true;
			}
			else if(curPic == controlsMenu){				//Same as above
				fading = true;
				Arrays.fill(options, false);
			}
			else if(curPic == starterTab){					//Same as above
				fading = true;
				pokePick = names[pInd];
				
				if(options[0]){								//Loads new pokemon
					wipeFile();
				} else{
					loadPoke(pokePick);
				}
				
				Arrays.fill(options, false);
								
			}
			else if(curPic == dungeonMenu){					//Chooses dungeon
				fading = true;
				curDung = "Dungeon"+Integer.toString(dInd+1);

				Arrays.fill(options, false);
			}		
		} 
		
		if(halfFade){
			if(options[0] || options[1]){
				curPic = starterTab;
			}
			else if(options[2]){
				curPic = controlsMenu;
			}
			else if(options[3]){
				curPic = dungeonMenu;
			}
			else{
				curPic = menuPic;
				
				if(Arrays.asList(names).contains(pokePick)){
					return pokePick+" "+pokeLvl+" "+pokeExp+" "+curDung;		//Starts game when pokemon is chosen
				}
			}
		}
		
		for(Pokemon p: pokeUnlocked){
			p.update();
		}
		
		tFrame++;
		
		return "s s s s";			//Returns useless String is pokemon not chosen
	}
	
	public void getMenuActions(){
		
		//Gets all key data from user

		if(keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_RIGHT]){

			delayCount++;
		}
		else{
			delayCount = 0;
		}
		
		if(delayCount != 0){
			
			if(keys[KeyEvent.VK_DOWN]){
				if(delayCount < 2){
					ind = (ind+1)%4;
					pInd = (pInd+1)%4;
					
					if(options[3]){
						dInd = (dInd+1)%6;
					}
					
				} 
				else if(delayCount%10 == 0){
					ind = (ind+1)%4;
					pInd = (pInd+1)%4;
					
					if(options[3]){
						dInd = (dInd+1)%6;
					}

				}
				
			} 
			else if(keys[KeyEvent.VK_UP]){
				if(delayCount < 2){
					ind--;
					pInd--;
					
					if(options[3]){
						dInd--;
					}
				} 
				else if(delayCount%10 == 0){
					ind--;
					pInd--;
					if(options[3]){
						dInd--;
					}
				}
				
				if(ind<0){
					ind = 3;
				}
				
				if(pInd<0){
					pInd = 3;
				}
				
				if(dInd<0){
					dInd = 5;
				}
			}
			else if(keys[KeyEvent.VK_RIGHT]){
				
				if(delayCount < 2){
					ind = (ind+2)%4;
					
					if(options[3]){
						dInd = (dInd+3)%6;
					}
					
				} 
				else if(delayCount%10 == 0){
					ind = (ind+2)%4;
					
					if(options[3]){
						dInd = (dInd+3)%6;	
					}
					
				}
			}
			else if(keys[KeyEvent.VK_LEFT]){
				if(delayCount < 2){
					ind-=2;
					
					if(options[3]){
						dInd -= 3;
					}
					
				} 
				else if(delayCount%10 == 0){
					ind-=2;
					
					if(options[3]){
						dInd -= 3;
					}
					
				}
				
				if(ind<0){
					ind += 4;
				}
				
				if(dInd<0){
					dInd += 6;
				}
			}
		}
	}
	
	public void drawFadeIn(Graphics g){
		
		//Draws fading transition
		
		alpha+= 3;
		
		if(alpha<255){	
			g.setColor(new Color(0, 0, 0, alpha));
		} 
		else if(alpha>=255 && alpha<=510){
			g.setColor(new Color(0, 0, 0, 510-alpha));
			
			halfFade = true;
		} 
		else{ 
			alpha = 0;
			fading = false;
			halfFade = false;
		}
		
		g.fillRect(0, 0, 480, 768);
		
	}
	
	public void drawDungeonMenu(Graphics g){
		
		//Draws all dugneon components
		
		int c = 0;
		g.setFont(new Font("Pokemon Pixel Font", Font.PLAIN, 24));
		g.setColor(new Color(0, 0, 0));
		for(int i=0; i<2; i++){
			
			for(int j=0; j<3; j++){
				
				g.drawImage(dTab, 63+204*i, 384+61+100*j, this);
				g.drawString(dungeonNames[c].toUpperCase(), 70+204*i, 384+32+61+100*j);
				c++;
			}
		}
		
		g.drawImage(dSelect, dPos[dInd][0]-3, dPos[dInd][1], this);
		
		
	}
	
	public void drawMenu(Graphics g){

		//Draws basic menu 
		
		for(int i=0; i<4; i++){
			g.drawImage(optionTab, pos[i][0], pos[i][1], this);
		}
		
		g.setFont(f);
		
		g.drawString("NEW GAME", 55, 525);
		g.drawString("LOAD GAME", 47, 652);
		g.drawString("CONTROLS", 279, 525);
		g.drawString("DUNGEONS", 315, 652);
	}
	

	
	public void paintComponent(Graphics g){
		
		//Draws all components
		
		g.setColor(new Color(92, 155, 236));
		g.fillRect(0, 0, 480, 768);
		
		g.drawImage(titleScreen, 0, 0, this);
		g.drawImage(titlePoke[(tFrame/2)%39], 146, 135, this);
		g.drawImage(curPic, -5, 384, this);
		
		if(curPic == menuPic){
			g.drawImage(selected, pos[ind][0], pos[ind][1]-4, this);
		}
		else if(curPic == starterTab){
			
			g.setColor(Color.BLACK);
			g.setFont(f);
			
			int[] pos = new int[]{460, 537, 615, 691};
			
			for(int i=0; i<4; i++){
				g.drawString(names[i].toUpperCase(), 188, pos[i]);
				g.drawImage(pokeUnlocked[i].getMenuPic(), 145, pos[i]-24, this);
			}
			
			g.drawImage(pokeSelect, 114, pos[pInd]-43, this);
			
			
		} 
		else if(curPic == dungeonMenu){
			
			drawDungeonMenu(g);
		
		}
		
		if(fading){
			drawFadeIn(g);
		}
	}
	
	public static Pokemon createPoke(String name, String level){
    	
    	//Loads pokemon and creates Pokemon objects
    	
    	Scanner inFile = null;
    	
    	try{
    		inFile = new Scanner(new File(name+".txt"));
    	}
    	catch(IOException ex){
    	}
    	
    	int filepos = 0;
			
		String pokeName = "";
		String pokeType = "";
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
    			pokeType = pokeInfo[1];
    		}
    		if (filepos == 1){
    			String [] stats = line.split(" ");
    			pHP = stats[0];
    			pAtk = stats[1];
    			pDef = stats[2];
    		}
    		if (filepos == 2){
    			String [] stats = line.split(" ");
    			lvlHP = Double.parseDouble(stats[0]);
    			lvlAtk = Double.parseDouble(stats[1]);
    			lvlDef = Double.parseDouble(stats[2]);
    		}
    		if (filepos == 3){
    			expYield = line;
    		}	
    			  			
    		if (filepos == 4){
    			expType = line;
    		}
    		if (filepos == 5){
    			String [] evolvedata = line.split(" ");
    			evLevel = evolvedata[0];
    			evName = evolvedata[1];
    		}
    		if(filepos == 6){
    			lenFrames = line;
    		}
    		filepos++;		
		}
		
		Pokemon	newPoke = new Pokemon(pokeName, pHP, pAtk, pDef, "0", level, expType, expYield, pokeType, "", false, 0, 0, lenFrames);

		return newPoke;
    }
    
    public void loadPoke(String name){
    	
    	//Loads existing pokemon from saveFile
    	
    	Scanner inFile = null;
    	
    	try{
    		inFile = new Scanner(new File("saveFile.txt"));
    	}
    	catch(IOException ex){
    	}
    	
    	while(inFile.hasNextLine()){
    		
    		String[] info = inFile.nextLine().split(" ");
    		
    		if(info[0] == name){
    			pokePick = info[0];
    			pokeLvl = info[1];
    			pokeExp = info[2];
    		}
    	}
    	
    }
    public void wipeFile(){
    	
    	//Clears current saveFile when new Game is selected
    	
    	try {
          PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter("saveFile.txt")));
	      outFile.close();
        } 
        catch ( IOException e ) {}
    }
}