import java.util.*;
import java.io.*;

public class Type {
	String type;
	ArrayList<String> superEff;
	ArrayList<String> weakEff;
	ArrayList<String> noEff;
	
    public Type(String type) {
    	this.type = type;
    	
    	superEff = new ArrayList<String>();
    	weakEff = new ArrayList<String>();
    	noEff = new ArrayList<String>();
    	
    }
    public String getString(){
    	return type;
    }
    
    public void addWeak(String weak){
    	weakEff.add(weak);
    }
    
    public void addSuper(String strong){
    	superEff.add(strong);
    }
    
    public void addNoeffect(String noeffect){
    	noEff.add(noeffect);
    }
    
    public ArrayList<String> getSuper(){
    	return superEff;
    }
    
    public ArrayList<String> getWeak(){
    	return weakEff;
    }
    
    public ArrayList<String> getNoEffect(){
    	return noEff;
    }
}