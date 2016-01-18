import java.util.*;
import java.io.*;

public class Dungeon{
	int height, width, roomNum, maxSize, minSize, numCol, numRow;
	
	int[][] grid;
	int[]widths = {60,51,60,80,40,80,36};
	int[]heights = {51,60,60,40,80,36,80};
	int[]columns = {3,3,3,4,2,4,2};
	int[]rows = {3,3,3,2,4,2,4};
	
	/*public Dungeon(int height, int width, int roomNum, int maxSize, int minSize){
		this.height = height;
		this.width = width;
		this.roomNum = roomNum;
		this.maxSize = maxSize;
		this.minSize = minSize;
		grid = new int[height][width];
		for (int i=0; i<height; i++){
			for (int j=0; j<width; j++){
				grid[i][j]=0;
			}
		}
	}*/
	
	public Dungeon(){
		int n = randInt(0,6);
		height = heights[n];
		width = widths[n];
		numCol = columns[n];
		numRow = rows[n];
		grid = new int[height][width];
	}
	
	public static ArrayList<Room> rooms = new ArrayList<Room>();
	
	public void createRooms(){
		int rheight = height/numRow;
		int rwidth = width/numCol;
		
		System.out.println(rheight+" "+rwidth);
		
		int n = randInt(6,8);
		int[]nx = new int[n];
		int[]ny = new int[n];
		for (int i=0; i<n; i++){
			nx[i]=-1;
			ny[i]=-1;
		}
		int count = 0;
		
		while (rooms.size()<n){
			boolean flag = true;
			int randX = randInt(0,numCol-1);
			int randY = randInt(0,numRow-1);
			
			for (int j=0; j<n; j++){
				if (nx[j]==randX && ny[j]==randY){
					flag = false;
				}
			}
		
			if (flag){
				
				int xPos = randInt(3,5)+randX*rwidth;
				int yPos = randInt(3,5)+randY*rheight;
				
				int hrange = randInt(rheight-10, rheight-7);
				int wrange = randInt(rwidth-10, rwidth-7);
				
				System.out.println(hrange+" "+wrange+"~");
				
				Room r = new Room(xPos,yPos,xPos+wrange,yPos+hrange);
				rooms.add(r);
				
				nx[count]=randX;
				ny[count]=randY;
				count++;
				
				Room tmp;
				if (rooms.size()>1){
					tmp = rooms.get(rooms.size()-2);
				
				
					if (randInt(0,1)==1){
						int a = randInt(r.x1, r.x2);
						int b = randInt(tmp.y1, tmp.y2);
						hCorridor(a, randInt(tmp.x1, tmp.x2), b);
						vCorridor(a, randInt(r.y1, r.y2), b);
					}
					else{
						int a = randInt(tmp.x1, tmp.x2);
						int b = randInt(r.y1, r.y2);
						vCorridor(a, b, randInt(tmp.y1, tmp.y2));
						hCorridor(randInt(r.x1, r.x2), a, b);
					}
				}
				
			}
		}
		
		/*int t = 0;
		while (t<10){
			int x = randInt(3, width-3);
			int y = randInt(3, height-3);
			Room c = new Room(x,y,x+1,y+1);
			boolean f = true;
			
			for (Room rm: rooms){
				if (c.cost(rm)<=3){
					f = false;
					break;
				}
			}
			if (f){
				rooms.add(c);
				t++;
			}
			
		}*/
		//I WAS TRYNA DO SOME TRYHARD SHIT HERE TO MAKE NERD CORRIDORS BUT AIN'T NOBODY GOT TIME FOR THAT
		//------------------------------------------------------------------------------
		//finds cost between rooms in order to find the closest rooms
		/*int [][] matrix = new int[n][n];
		
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (j!=i){
					matrix[i][j] = rooms.get(i).cost(rooms.get(j));
				}
			}
		}
		
		String s = "";
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				s+=matrix[i][j]+" ";
			}
			s+="\n";
		}
		System.out.println(s);
		
		int[][] xMatrix = new int[n][n];
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (j!=i){
					xMatrix[i][j] = rooms.get(i).xcost(rooms.get(j));
				}
			}
		}
		
		int[][] yMatrix = new int[n][n];
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (j!=i){
					yMatrix[i][j] = rooms.get(i).ycost(rooms.get(j));
				}
			}
		}
		
		int[][] visited  = new int[n][n];
		
			
		for (int i=0; i<n; i++){
			int[] tmp = new int[n];	
			for (int j=0; j<n; j++){
				tmp[i] = matrix[i][j];
			}
			Arrays.sort(tmp);
			
			
		}
		
		//------------------------------------------------------------------------------------
		
		for (Room rm: rooms){
			
		}*/
		
		for (Room rm: rooms){
			for (int i=rm.y1; i<=rm.y2; i++){
				for (int j=rm.x1; j<=rm.x2; j++){
					grid[i][j]=1;
				}
			}
		}
		
	}
	
	public void hCorridor(int x1, int x2, int y){
		for (int i=Math.min(x1, x2); i<Math.max(x1,x2); i++){
			grid[y][i] = 1;
		}
	}
	
	public void vCorridor(int x, int y1, int y2){
		for (int i=Math.min(y1,y2); i<=Math.max(y1,y2); i++){
			grid[i][x] = 1;
		}
	}
	
	public String toString(){
		String str = "";
		System.out.println(height+" "+width);
		System.out.println(rooms.size());
		for (Room rm: rooms){
			
			System.out.println(rm.x1+" "+rm.x2+" "+rm.y1+" "+rm.y2);
		}
		for (int i=0; i<height; i++){
			for (int j=0; j<width; j++){
				str+=grid[i][j];
			}
			str+="\n";
		}
		return str;
	}
	
	/*public void placeRooms(){
		Room[] rooms = new Room[roomNum];
		for (int i=0; i<roomNum; i++){
			int w = randInt(minSize, maxSize);
			int h = randInt(minSize, maxSize);
			int x = randInt(1, width - w - 1);
			int y = randInt(1, height - h - 1);
			
			Room r = new Room(x,y,w,h);
			boolean flag = true;
			for (Room rm: rooms){
				if (r.cost(rm)<=5){
					flag = false;
					break;
				}
			}
			if (flag){
				rooms[i] = r;
			}
		}
	}*/
	
	/*public static int randInt(int a, int b){
		Random rand = new Random();
		return rand.nextInt(b+1)+a;
	}*/
	public static int randInt(int min, int max) {

	    // Usually this should be a field rather than a method variable so
	    // that it is not re-seeded every call.
	    Random rand = new Random();
	
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	
	    return randomNum;
	}
	
	//public static ArrayList<Item> numItems = new ArrayList<Item>();
	
	/*public void placeItems(){
		
		for (Room rm: rooms){
			int n = randInt(0,2);
			
			for (int i=0; i<n; i++){
				int x = randInt(rm.x1, rm.x2);
				int y = randInt(rm.y1, rm.y2);
				
				Item stuff = new Item(name,x,y);
				
				numItems.add(stuff);
				
			}
		}
		
		
		
	}*/
	
	public int[][] getGrid(){
		return grid;
	}
	
	
	public void placeTiles(){
		for (int i=1; i<height-1; i++){
			for (int j=1; j<width-1; j++){
				if (grid[i][j]==0 && grid[i+1][j+1]==1 && grid[i+1][j]!=1 && grid[i][j+1]!=1){
					grid[i][j]=2;
				}
				else if (grid[i][j]==0 && grid[i+1][j+1]==1 && grid[i+1][j]==1 && grid[i][j+1]==1){
					grid[i][j]=10;
				}					
				else if (grid[i][j]==0 && grid[i+1][j-1]==1 && grid[i+1][j]!=1 && grid[i][j-1]!=1){
					grid[i][j]=4;
				}
				else if (grid[i][j]==0 && grid[i+1][j-1]==1 && grid[i+1][j]==1 && grid[i][j-1]==1){
					grid[i][j]=11;
				}
				else if (grid[i][j]==0 && grid[i-1][j-1]==1 && grid[i-1][j]!=1 && grid[i][j-1]!=1){
					grid[i][j]=6;
				}
				else if (grid[i][j]==0 && grid[i-1][j-1]==1 && grid[i-1][j]==1 && grid[i][j-1]==1){
					grid[i][j]=12;
				}
				else if (grid[i][j]==0 && grid[i-1][j+1]==1 && grid[i-1][j]!=1 && grid[i][j+1]!=1){
					grid[i][j]=8;
				}
				else if (grid[i][j]==0 && grid[i-1][j+1]==1 && grid[i-1][j]==1 && grid[i][j+1]==1){
					grid[i][j]=13;
				}
				else if (grid[i][j]==0 && grid[i+1][j]==1){
					grid[i][j]=3;
				}				
				else if (grid[i][j]==0 && grid[i][j-1]==1){
					grid[i][j]=5;
				}				
				else if (grid[i][j]==0 && grid[i-1][j]==1){
					grid[i][j]=7;
				}				
				else if (grid[i][j]==0 && grid[i][j+1]==1){
					grid[i][j]=9;
				}	
			}
		}
	}
}

class Room{
	int x1, y1, x2, y2;
	
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
}

class Corridor{
	int x1, x2, x3, x4, y1, y2, y3, y4;
	Corridor(){
		
	}
}

//http://www.psypokes.com/dungeon/mechanics.php
//http://www.serebii.net/mysteriousdungeon/dungeon/
//http://gamedevelopment.tutsplus.com/tutorials/create-a-procedurally-generated-dungeon-cave-system--gamedev-10099