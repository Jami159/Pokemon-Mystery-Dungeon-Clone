//Pokemon class
//Stores all fields and methods of individual Pokemon objects

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;


public class Enemy extends Entity{
	
	int[][] ranMoves;
	int count = 0;
	int dx, dy;
	
	public Enemy(String name, String hp, String atk, String def, String exp, String level, String expType, String expYield, String type){
		
		super(name, hp, atk, def, exp, level, expType, expYield, type);

		ranMoves = new int[][]{{0,1},{0,-1},{-1,0},{1,0}};
		dx = 0;
		dy = -1;
		 
	}
	
//	public void move() {
//		
//		int ind = (int)(Math.random()*4);
//		
//		this.setDir(ind);
//		//this.addMove(directions[ind]);
//		
//		int dx = ranMoves[ind][0];
//		int dy = ranMoves[ind][1];
//		
//		//this.setRefPos(x, y);
//	//	this.setTX(x+24*dx);
//	//	this.setTY(y+24*dy);
//		
//		
//		//p.addMove("left");
//		this.setDir("left");
//		this.setAction("walk");
//		
//		setX(this.getX()+24*dx);
//		setY(this.getY()+24*dy);
//		
//		if(x <= 0){
//			x = 0;
//			tx = 0;
//		} else if (x > 1900){
//			x = 1900;
//			tx = 1900;
//		}
//		
//		if(y < 0){
//			y = 0;
//			ty = 0;
//		} else if (y > 2000){
//			y = 2000;
//			ty = 2000;
//		}
//	
//	}
	
	@Override
	public void move(int a, int b){
	
		if(x==tx && y == ty){
			
			setRefPos(x, y);
			
				
			int ind = (int)(Math.random()*4);

			setDir(directions[ind]);
			setAction("walk");
				
		
			dx = ranMoves[ind][0];
			dy = ranMoves[ind][1];

			tx = x+24*dx;
			ty = y+24*dy;
			
		}

		x += 2*dx;
		y += 2*dy;

	}
	
	@Override
	public void collide(int[][] grid){
		
		//FIX IT THIS DOESNT WORK
		int nx = tx/24;
		int ny = ty/24;
		
		
		if(grid[nx][ny] != 1){
			
			tx = ox;
			ty = oy;
			x = ox;
			y = oy;
			dx = 0;
			dy = 0;

		}
		
		int maxX = grid.length*24;
		int maxY = grid[0].length*24;
		
		if(x <= 0){
			x = 0;
			tx = 0;
		} else if (x > maxX){
			x = maxX;
			tx = maxX;
		}
		
		if(y < 0){
			y = 0;
			ty = 0;
		} else if (y > maxY){
			y = maxY;
			ty = maxY;
		}
	}
	

}