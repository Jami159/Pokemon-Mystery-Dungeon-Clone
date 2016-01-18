
public class Camera{
	
	int x, y;
	
	int offsetMaxX, offsetMinX, offsetMaxY, offsetMinY;

	public Camera(Dungeon d, int w, int h){
		
		offsetMaxX = d.getGrid().length*24 - w;
		offsetMaxY = d.getGrid()[0].length*24 - h;
		offsetMinX = 0;
		offsetMinY = 0;
		
		x=0;
		y=0;
			
	}
	
	public int[] apply(int x, int y){
		
		int[] tmp = new int[]{x-this.x, y-this.y};
		
		return tmp;
	}
	
	public void update(Pokemon p){

		x = p.getX() - 480 / 2;
		y = p.getY() - 384 / 2;
			
		if(x > offsetMaxX){
			
			x = offsetMaxX;
				
		} else if (x < offsetMinX){
			
			x = offsetMinX;
		}
		
		if(y > offsetMaxY){
			
			y = offsetMaxY;
				
		} else if (y < offsetMinY){
			
			y = offsetMinY;
		}
    		
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
}

