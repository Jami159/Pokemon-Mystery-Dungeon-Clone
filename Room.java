import java.util.*;
import java.io.*;


public class Room{
	private int x1, y1, x2, y2;
	
	public Room(int x1, int y1, int x2, int y2){
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
	}
	
	public int cost(Room other){
		int c = 0;
		int x = Math.max(x1, other.x1) - Math.min(x2, other.x2);
		int y = Math.max(y1, other.y1) - Math.min(y2, other.y2);
		if (x>0) c+=x;
		if (y>0) c+=y;
		return c-1;
	}
	
	public int xcost(Room other){
		int y = Math.max(y1, other.y1) - Math.min(y2, other.y2);
		if (y<0){
			return Math.max(x1, other.x1) - Math.min(x2, other.x2);
		}
		else{
			return -1;
		}
	}
	public int ycost(Room other){
		int x = Math.max(y1, other.y1) - Math.min(y2, other.y2);
		if (x<0){
			return Math.max(y1, other.y1) - Math.min(y2, other.y2);
		}
		else{
			return -1;
		}
	}
	
	public int getX1(){
		return x1;
	}
	public int getX2(){
		return x2;
	}
	public int getY1(){
		return y1;
	}
	public int getY2(){
		return y2;
	}
}