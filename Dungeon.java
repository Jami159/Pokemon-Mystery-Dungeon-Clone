import java.util.*;
import java.io.*;

public class Dungeon{
	private int height, width, roomNum, maxSize, minSize, numCol, numRow;
	
	//differnet room sizes/layouts
	private int[][] grid;
	private int[]widths = {37,41,37,41,62,41,62,37,53,41,53,37,62,53,62,83,41,83,37,53,83,62,83,125,119,35};
	private int[]heights = {41,37,37,41,41,62,37,62,41,53,37,53,53,62,62,41,83,37,83,83,53,83,62,37,41,30};
	private int[]columns = {2,2,2,2,3,2,3,2,3,2,3,2,3,3,3,4,2,4,2,3,4,3,4,6,6,1};
	private int[]rows = {2,2,2,2,2,3,2,3,2,3,2,3,3,3,3,2,4,2,4,4,3,4,3,2,2,1};
	
	
	private int d1;// floor sizes
	private int d2;
	
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
	
	public Dungeon(int d1, int d2){
		this.d1=d1;
		this.d2=d2;
		int n;
		if (3<=d1 && d2<=4){
			n=randInt(0,3);
		}
		else if (4<=d1 && d2<=6){
			n=randInt(4,11);
		}
		else if (6<=d1 && d2<=8){
			n=randInt(12,18);
		}
		else if (9<=d1 && d2<=11){
			n=randInt(19,24);
		}
		else{
			n=25;
		}
		height = heights[n];
		width = widths[n];
		numCol = columns[n];
		numRow = rows[n];
		grid = new int[height][width];
		createRooms();
		placeTiles();
	}
	
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	public void createRooms(){
		//heights/widths of rows/columns
		int rheight = height/numRow;
		int rwidth = width/numCol;
		
		//System.out.println(rheight+" "+rwidth);
		
		int n = randInt(d1,d2);
		int[]nx = new int[n];
		int[]ny = new int[n];
		for (int i=0; i<n; i++){//keeps track of whether a position has already been chosen so it doesn't get picked again
			nx[i]=-1;
			ny[i]=-1;
		}
		int count = 0;
		
		while (rooms.size()<n){
			boolean flag = true;
			int randX = randInt(0,numCol-1);//rnadom x/y pos
			int randY = randInt(0,numRow-1);
			
			for (int j=0; j<n; j++){
				if (nx[j]==randX && ny[j]==randY){
					flag = false;
				}
			}
		
			if (flag){
				
				int xPos = randInt(3,5)+randX*rwidth;
				int yPos = randInt(3,5)+randY*rheight;
				
				int hrange;
				int wrange;
				
				if (d1==1 && d2==1){
					hrange = 20;
					wrange = 24;
				}
				else{
					hrange = randInt(rheight-10, rheight-7);
					wrange = randInt(rwidth-10, rwidth-7);
				}
				
				//System.out.println(hrange+" "+wrange+"~");
				
				Room r = new Room(xPos,yPos,xPos+wrange,yPos+hrange);
				rooms.add(r);
				
				//System.out.println(r.getX1()+" "+r.getX2()+" "+r.getY1()+" "+r.getY2());
				
				nx[count]=randX;
				ny[count]=randY;
				count++;
				
				Room tmp;
				if (rooms.size()>1){//places horizontal and vertical corridors at random intervals
					tmp = rooms.get(rooms.size()-2);
				
				
					if (randInt(0,1)==1){
						int a = randInt(r.getX1(), r.getX2());
						int b = randInt(tmp.getY1(), tmp.getY2());
						hCorridor(a, randInt(tmp.getX1(), tmp.getX2()), b);
						vCorridor(a, randInt(r.getY1(), r.getY2()), b);
					}
					else{
						int a = randInt(tmp.getX1(), tmp.getX2());
						int b = randInt(r.getY1(), r.getY2());
						vCorridor(a, b, randInt(tmp.getY1(), tmp.getY2()));
						hCorridor(randInt(r.getX1(), r.getX2()), a, b);
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
		//COMPLEX CORRIDOR STUFF
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
		
		
			
		
		int a = randInt(0,n-1);
		int b = 0;
		for (Room rm: rooms){
			for (int i=rm.getY1(); i<=rm.getY2(); i++){
				for (int j=rm.getX1(); j<=rm.getX2(); j++){
					grid[i][j]=1;
				}
			}
			if (d1!=1 && d2!=1){//places the stairs (exit)
				if (b==a){
					stairX = randInt(rm.getY1()+1,rm.getY2()-1);
					stairY = randInt(rm.getX1()+1,rm.getX2()-1);
					grid[stairX][stairY]=21;
				}
			}
			b++;
		}
		
	}
	
	private int stairX;
	private int stairY;
	
	public int getStairX(){
		return stairX;
	}
	public int getStairY(){
		return stairY;
	}
	
	public void hCorridor(int x1, int x2, int y){//carves out horizontal corridors
		for (int i=Math.min(x1, x2); i<Math.max(x1,x2); i++){
			grid[y][i] = 1;
		}
	}
	
	public void vCorridor(int x, int y1, int y2){//carves out vertical corridors
		for (int i=Math.min(y1,y2); i<=Math.max(y1,y2); i++){
			grid[i][x] = 1;
		}
	}
	
	public String toString(){
		String str = "";
		System.out.println(height+" "+width);
		System.out.println(rooms.size());
		/*for (Room rm: rooms){
			
			System.out.println(rm.x1+" "+rm.x2+" "+rm.y1+" "+rm.y2);
		}*/
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				str+=grid[j][i];
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
	
	public void placePoke(Pokemon user, ArrayList<Pokemon> enemy){
		for (Room rm: rooms){
			boolean flag = true;
			if (rm.getY1()<user.getX()/24 && user.getX()/24<rm.getY2()){
				if (rm.getX1()<user.getY()/24 && user.getY()/24<rm.getX2()){
					flag=false;
				}				
			}
			int c = 0;
			for (Pokemon poke: enemy){
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
				
			}
		}
		
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
	
	public ArrayList<Room> getRooms(){
		return rooms;
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
				if (grid[i][j]==0 && grid[i-1][j]==1 && grid[i+1][j]==1 && grid[i][j-1]==1 && grid[i][j+1]==1){
					grid[i][j]=20;
				}					
				else if (grid[i][j]==0 && grid[i-1][j]==1 && grid[i][j+1]==1 && grid[i][j-1]==1){
					grid[i][j]=14;
				}
				else if (grid[i][j]==0 && grid[i+1][j]==1 && grid[i][j+1]==1 && grid[i][j-1]==1){
					grid[i][j]=15;
				}
				else if (grid[i][j]==0 && grid[i][j+1]==1 && grid[i][j-1]==1){
					grid[i][j]=16;
				}
				else if (grid[i][j]==0 && grid[i][j-1]==1 && grid[i-1][j]==1 && grid[i+1][j]==1){
					grid[i][j]=17;
				}
				else if (grid[i][j]==0 && grid[i][j+1]==1 && grid[i-1][j]==1 && grid[i+1][j]==1){
					grid[i][j]=18;
				}
				else if (grid[i][j]==0 && grid[i-1][j]==1 && grid[i+1][j]==1){
					grid[i][j]=19;
				}
				else if (grid[i][j]==0 && grid[i+1][j+1]==1 && grid[i+1][j]!=1 && grid[i][j+1]!=1){
					grid[i][j]=2;
				}
				else if (grid[i][j]==0 && grid[i+1][j+1]==1 && grid[i+1][j]==1 && grid[i][j+1]==1){
					grid[i][j]=10;
				}					
				else if (grid[i][j]==0 && grid[i+1][j-1]==1 && grid[i+1][j]!=1 && grid[i][j-1]!=1){
					grid[i][j]=8;
				}
				else if (grid[i][j]==0 && grid[i+1][j-1]==1 && grid[i+1][j]==1 && grid[i][j-1]==1){
					grid[i][j]=13;
				}
				else if (grid[i][j]==0 && grid[i-1][j-1]==1 && grid[i-1][j]!=1 && grid[i][j-1]!=1){
					grid[i][j]=6;
				}
				else if (grid[i][j]==0 && grid[i-1][j-1]==1 && grid[i-1][j]==1 && grid[i][j-1]==1){
					grid[i][j]=12;
				}
				else if (grid[i][j]==0 && grid[i-1][j+1]==1 && grid[i-1][j]!=1 && grid[i][j+1]!=1){
					grid[i][j]=4;
				}
				else if (grid[i][j]==0 && grid[i-1][j+1]==1 && grid[i-1][j]==1 && grid[i][j+1]==1){
					grid[i][j]=11;
				}
				else if (grid[i][j]==0 && grid[i+1][j]==1){
					grid[i][j]=9;
				}				
				else if (grid[i][j]==0 && grid[i][j-1]==1){
					grid[i][j]=7;
				}				
				else if (grid[i][j]==0 && grid[i-1][j]==1){
					grid[i][j]=5;
				}				
				else if (grid[i][j]==0 && grid[i][j+1]==1){
					grid[i][j]=3;
				}	
			}
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