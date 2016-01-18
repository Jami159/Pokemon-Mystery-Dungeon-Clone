

public class Move {
	private int damage, pp, acc, ppleft; //basedamage, total pp, accuracy, pp left
	private String special, name, typestring;
	private Type type;

    public Move(String name, String type, String pp, String damage, String acc, String special) {
    	this.name = name;
    	this.damage = Integer.parseInt(damage);
    	this.pp = Integer.parseInt(pp);
    	this.acc = Integer.parseInt(acc);
    	this.special = special;
		typestring = type;
    	ppleft = this.pp;
    }
    
    public void setType(Type t){
    	type = t;
    }
    public Type getType(){
    	return type;
    }
    
    public String getName(){
    	return name;
    }
    public int getDamage(){
    	return damage;
    }
    
    public int getAcc(){
    	return acc;
    }
    
    public int getPP(){
    	return pp;
    }
    public int getPPleft(){
    	return ppleft;
    }
    
    public void lowerPP(){
    	ppleft--;
    }
    
    public String getSpecial(){
    	return special;
    }
    
    public void applySpecial(Pokemon p1, Pokemon p2){

    }
    
}