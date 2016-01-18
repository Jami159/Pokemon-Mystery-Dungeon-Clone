
public class Test{
	public static void main(String[]args){
		Dungeon d = new Dungeon();
		d.createRooms();
		d.placeTiles();
		int[][] g = d.getGrid();
		System.out.println(d.toString());
		
		
	}
}