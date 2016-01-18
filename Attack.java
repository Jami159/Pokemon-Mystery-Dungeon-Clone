

public class Attack {
	private String move, special;
	private int cost, dmg;
	
	public Attack(String move, String cost, String dmg, String special){
		this.move = move;
		this.cost = Integer.parseInt(cost);
		this.dmg = Integer.parseInt(dmg);
		this.special = special;
	}
	
    public int special(String special, Pokemon atker, Pokemon atked){ 
    	/*applies the special of anymove and returns the damage multiplier based on how many times
    	 the enemy is hit (generally one except in the cases of wildstorm and wildcard)
    	 */
    	
		int multiplier = 1;
		
    	if (special.equals("stun")){
    		stun(atked);
    	}
    	if (special.equals("wild card")){
    		multiplier = wildcard(atker); //attack multiplier can be either 1 or 0, 0 being a miss
    	}
    	if (special.equals("wild storm")){
    		multiplier = wildstorm(atker); //attack multiplier can be any integer 0 or above
    	}
    	if (special.equals("disable")){
    		disable(atked);
    	}
    	if (special.equals("recharge")){
    		recharge(atker);
    	}
    	return multiplier;
    }

    
    public String getMove(){
    	return move;
    }
    
    public String getSpecial(){
    	return special;
    }
    
    public int getCost(){
    	return cost;
    }
    
    public int getDmg(){
    	return dmg;
    }
    
    public void stun(Pokemon attacked){
		
    	if ((int)(Math.random()*2)==0){
    		attacked.changeStunStatus("stunned");  
    		System.out.println(attacked.getName() +" was stunned!"); 		
    	}
    	
    }
    
    public int wildcard(Pokemon attacker){
    	
    	if ((int)(Math.random()*2)==0){
    		System.out.println(attacker.getName() +"'s attack missed!");
    		return 0;
    	}
    	return 1;
    }
    
    public int wildstorm(Pokemon attacker){
    	int multiplier = 0;
    	while ((int)(Math.random()*2) == 0){
    		multiplier+=1;
    	}

    	if (multiplier == 0){
    		System.out.println(attacker.getName() +"'s attack missed!");
    	}
    	else{
    		System.out.println("Hit " + multiplier + " time(s)!");
    	}
    	return multiplier;
    }
    
    public void disable(Pokemon attacked){
    	attacked.changeDisableStatus("disabled");
    	System.out.println(attacked.getName() + " was disabled!");
    }
    

	public void recharge(Pokemon attacker){
		if (50 - attacker.getEnergy() < 20){
			//energy regained cannot exceed 50 when added to current energy
			System.out.println(attacker.getName()+" regained "+ (50-attacker.getEnergy()) + " energy");
		}
		else{
			System.out.println(attacker.getName()+" regained 20 energy");
		}
		attacker.energySet(20);

	}
    

   
}