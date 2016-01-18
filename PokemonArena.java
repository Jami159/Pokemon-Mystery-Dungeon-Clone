
import java.util.*;
import java.io.*;
public class PokemonArena {
	private static Scanner kb = new Scanner(System.in);
	
	private static ArrayList <Pokemon> allpokemon = new ArrayList <Pokemon>();
	//holds all pokemon
	
	private static ArrayList <Pokemon> goodpoke = new ArrayList <Pokemon>();
	//holds the user's pokemon
	
	private static ArrayList <Pokemon> enemies = new ArrayList <Pokemon>();
	//holds the enemy pokemon

	private static Pokemon p1, p2; //pokemon currently in battle, where p1 is the user and p2 is the enemy
	
	
	public static void getPokemon(){
		//reads the textfile of pokemon
		
		ArrayList <String> wholefile = new ArrayList <String>();
		Scanner inFile = null;
    	try{
    		inFile = new Scanner(new File("pokemon.txt"));
    	}
    	catch(IOException ex){
    		System.out.println("Did we forget to make pokemon.txt?");
    	}
    	int filepos = 0;
    	while (inFile.hasNextLine()){
    		String line = inFile.nextLine();
    		wholefile.add(line);

    		
    	}
    	inFile.close();
		
		wholefile.remove(0);
		//removes the number specifying the number of pokemon in the text file
		
    	for (int i = 0; i<wholefile.size(); i++){
    		String[] stats = wholefile.get(i).split(",");
    		Pokemon newPoke = new Pokemon(stats);
    		allpokemon.add(newPoke);

    	}

	}
	
	
	public static void selectPokemon(){
		//allows the user to select the four pokemon he/she will be using for the tournament
		

		for (int i = 0; i<allpokemon.size(); i++){
			System.out.printf("%-3s %-15s\n",(i+1)+".",allpokemon.get(i).getName());
		}
		
		int pokemoncount = 0; //keeps track of how many pokemon the user has chosen
		
		System.out.println("");
		
		while (true){
			if (pokemoncount == 4){
				break;
			}
			System.out.println("Choose your Pokemon! Please enter a number from 1 - "+allpokemon.size()+": ");
			
			int in = kb.nextInt(); //allows the user to enter a number rather than type the pokemon name
			if (in>0 && in<=allpokemon.size()){
				//checks that entries are valid 
				
				if (goodpoke.contains(allpokemon.get(in-1))){
					//makes sure the user won't select two of the same pokemon
					
					System.out.println("That pokemon has already been chosen");
				}
				else{
					goodpoke.add(allpokemon.get(in-1));
					pokemoncount += 1;
				}
			}
			else{
				System.out.println("Invalid number ");
			}
			
		}
		for (int i=0; i<allpokemon.size(); i++){
			//remaining pokemon become enemies 
			
			if (!goodpoke.contains(allpokemon.get(i))){
				enemies.add(allpokemon.get(i));
			}
		}
		
		System.out.println("");
	}
	 
	public static void battleStart(){
		//This is the main battle loop, where pokemon battle against each other
		//until either the computer's or the user's pokemon are wiped out
	
		System.out.println("Welcome to Pokemon arena!");
		
		getPokemon();
		selectPokemon();		

		Pokemon p1 = select("none");
		p2 = enemySelect();
		
		while (true){
			
		
			pokebattle();
			
			if (goodpoke.size()==0){
				System.out.println("You have run out of pokemon!");
				System.out.println("Game Over!");
				break;
			}
			if (enemies.size()==0){
				System.out.println("You have defeated all enemy pokemon!");
				System.out.println("Congratulations! You are Victorious!");
				break;
			}
			
			//restores hp to the user's pokemon
			for (int i=0; i<goodpoke.size(); i++){
				goodpoke.get(i).changeHp(20);
			}
			
			//new pokemon are selected once one faints
			if (p1.getHp()<=0){
				p1 = select(p1.getName());
			}
			if (p2.getHp()<=0){
				p2 = enemySelect();
			}
			
		}
		
		System.out.println("Do you want to play again? Yes or no?");
		String in = kb.nextLine();
		if (in.toLowerCase().equals("yes")){
			goodpoke = new ArrayList <Pokemon>();
			enemies = new ArrayList <Pokemon>();
			battleStart();
		}
		else{
			System.out.println("Thanks for playing!");
		}
		
		
	}
	
	public static void pokebattle(){
		//battle between two pokemon; ends when the enemy faints
		
		int rand = (int)(Math.random()*2);
		Pokemon [] inbattle = new Pokemon[2];
		
		//determines which pokemon goes first randomly at the start of each battle
		inbattle[rand] = p1;
		inbattle[Math.abs(rand-1)] = p2;


		Attack a1, a2; //attack about to be used, where a1 is the user's attack and a2 is the enemy attack

		while (true){
			System.out.println("\nPokemon currently in battle:");
			System.out.printf("%-15s | Health: %2d | Energy: %2d |\n",p1.getName(), p1.getHp(), p1.getEnergy());
			System.out.printf("%-15s | Health: %2d | Energy: %2d |\n\n",p2.getName(), p2.getHp(), p2.getEnergy());
			
			
			String [] userAtks = actionMenu(p1.getAtks(),p1.getEnergy()); 
			String [] enemyAtks = enemyAtkSelect(p2.getAtks(),p2.getEnergy());
			
			if (!userAtks[0].equals("none")){
				a1 = new Attack(userAtks[0],userAtks[1],userAtks[2],userAtks[3]);	
			}
			else{
				a1 = null; 
			}
			
			if (!enemyAtks[0].equals("none")){
				a2 = new Attack(enemyAtks[0],enemyAtks[1],enemyAtks[2],enemyAtks[3]);	
			}
			else{
				a2 = null;
			}

			boolean battleend = false;
			//action loop
			//user attacks the enemy
			for (int i=0; i<2; i++){
				if (inbattle[i].getName().equals(p1.getName())){
					if (!userAtks[0].equals("none")){
						if (!p1.stunnedornot().equals("stunned")){
							//only moves if either an attack has been selected and the pokemon is not stunned
							
							System.out.println(p1.getName()+" used "+a1.getMove()+"!");
							p2.takeDamage(p1,a1,effectiveness(p1,p2));

							if (p2.getHp()<=0){
								System.out.println("Foe "+p2.getName()+" fainted!");
								enemies.remove(enemies.indexOf(p2));
								System.out.println("");
								battleend = true; //battle ends when enemy is defeated
								break;
							}
						}
						else{
							//removes stun after stun causes the pokemon to miss a turn
							System.out.println("Your "+p1.getName()+" is stunned! You can't attack!");

							p1.changeStunStatus("");
						}

						
					}
				}
				
				//same as above, except enemy attacks user
				if (inbattle[i].getName().equals(p2.getName())){
					if (!enemyAtks[0].equals("none")){
						if (!p2.stunnedornot().equals("stunned")){

							System.out.println(p2.getName()+" used "+a2.getMove()+"!");
							p1.takeDamage(p2,a2,effectiveness(p2,p1));
							
							if (p1.getHp()<=0){
								System.out.println("Your "+p1.getName()+" fainted!");
								goodpoke.remove(goodpoke.indexOf(p1));
								if(goodpoke.size()>=1){
									select(p1.getName());
								}
								else{
									battleend = true; //battle ends when no pokemon left for the user to pick
								}
								System.out.println("");
								break;
							}
						}
						else{
							System.out.println("Foe "+p2.getName() +" is stunned! It can't attack!");
							p2.changeStunStatus("");
						}
					}
					else{
						System.out.println("Foe "+p2.getName()+" can't attack! Not enough energy!");
					}
				}
				System.out.println("");
			}
			
			//restores energy of all pokemon 
			for (int i=0; i<goodpoke.size(); i++){
				goodpoke.get(i).energySet(10);
			}
			for (int i=0; i<enemies.size(); i++){
				enemies.get(i).energySet(10);
			}
			
			if (battleend == true){
				break;
			}
			
			System.out.println("All pokemon have regained 10 energy\n");
		}
			

	}
	

	
	public static Pokemon enemySelect(){
		//reselects an enemy pokemon
		Pokemon randenemy = enemies.get((int)(Math.random()*enemies.size()));
		System.out.println("The enemy has chosen "+ randenemy.getName()+"!");
		return randenemy;
	}
	
	public static String[] enemyAtkSelect(String [] atks, int energy){
		//selects an enemy attack option, returns an array containing "none" if none are viable 
		
		String [] none = {"none"};
		
		ArrayList <String> valid = new ArrayList <String>();
		for (int i=0; i<atks.length; i+=4){
			//adds attacks to the arraylist if the pokemon has enough energy to use it
			
			if (Integer.parseInt(atks[i+1])<=energy){
				for (int j=0; j<4; j++){
					valid.add(atks[i+j]);
				}
			}
		}

		if (valid.size()==0){
			p2.changeStunStatus("");
			//passing a turn is the same as being stunned for a turn, so an existing stun is removed
			
			return none;
		}
		else{
			int rand = (int)(Math.random()*(valid.size()/4));
			String [] attacks = {valid.get(rand*4), valid.get(rand*4+1), valid.get(rand*4+2), valid.get(rand*4+3)};
			return attacks;
		}
	


	}
	
		
	public static Pokemon select(String current){
		//select a pokemon to send into battle; current is the name of the pokemon already in battle
		
		System.out.println("Pokemon left: ");
		for (int i = 0; i<goodpoke.size(); i++){
			System.out.println((i+1)+" "+goodpoke.get(i).getName());
		}
		System.out.println("");
		while (true){
			System.out.println("Which pokemon do you want to use? Enter a number from 1-"+goodpoke.size()+": ");
			int in = kb.nextInt();
			if (in>0 && in<=goodpoke.size()){
				if (current.equals(goodpoke.get(in-1).getName())){
					System.out.println("That pokemon is already in battle!");
				}
				else{
					p1 = goodpoke.get(in-1);
					break;
				}

			}
			else{
				System.out.println("Invalid number");
			}
		}
		System.out.println(p1.getName()+" I choose YOU!");
		return p1;
	
	}
	
	public static String[] actionMenu(String [] atks, int energy){
		//displays the actions for the user to choose; the attacks, retreat, or pass
		
		String [] noattack = {"none"};
		int choice;
		while (true){
			System.out.println("Choose your action! Enter a number from 1 - " + (atks.length/4+2));
			for (int i=0; i<(atks.length)/4; i++){
					
				String a = atks[i*4];
				String b = atks[i*4+1];
				String c = atks[i*4+2];
				String d = atks[i*4+3]; //used so that the next line isn't too ugly
					
				System.out.printf("%-2s %-15s Energy Cost:%3s Damage:%3s %10s\n",i+1+".",a,b,c,d);
			}
			System.out.println((atks.length/4+1)+". switch pokemon");
			System.out.println((atks.length/4+2)+". pass");
				
			choice = kb.nextInt();
			
			System.out.println("");
			
			if (choice>=1 && choice<=atks.length/4+2){
				if (choice>=1 && choice <=atks.length/4){
					if (Integer.parseInt(atks[(choice-1)*4+1])>energy){
						System.out.println("You do not have enough energy for that attack!");
					}
					else{
						break;
					}
				}
				else{
					if (choice == atks.length/4+1 && goodpoke.size() == 1){
						//does not allow the user to retreat if all other pokemon have fainted
						System.out.println("All of your other pokemon have fainted! You cannot switch!");
					}
					else{
						p1.changeStunStatus("");
						//a pokemon switch or pass counts as a turn, so stuns are removed
						
						break;
					}
					
				}

			}
			else{
				System.out.println("Please choose a valid option");
			}
		}
		
		if (choice == atks.length/4+1){
			//this is the retreat option, goes to select to allow user to select another pokemon
			p1 = select(p1.getName());

		}
		if (choice < atks.length/4+1){
			
			//returns attack array in the form: name, energy cost, dmg, special
			return Arrays.copyOfRange(atks,4*(choice-1),4*(choice-1)+4);
		}
		return noattack;
		
	}
	

	public static double effectiveness(Pokemon atker, Pokemon victim){
		//determines whether an attack has increased or reduced damage
		if (victim.getWeak().equals(atker.getType())){
			return 2;
		}
		if (victim.getResist().equals(atker.getType())){
			return 0.5;
		}
		return 1;
	} 
    
    public static void main(String[]args){
    	battleStart();
    }
}