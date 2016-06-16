package com.nhl.spindp.spin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.nhl.spindp.Main;

public class Dans {
	private static int[] servoStanden = new int[19];
	
	/*public Dans(){
		doDanceMoves();
	}*/
	
	public static void doDanceMoves(){
		
		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader("D:\\IDPgit\\python\\Dans.txt"));
			br = new BufferedReader(new FileReader("C:\\Users\\Aldert\\Documents\\School\\NHL\\SpInDP\\SpInDP\\python\\Dans.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		    String line = br.readLine();

		    while (line != null) {
		    	checkString(line);
		        line = br.readLine();
		    }
		} 
		catch(Exception e){
			e.printStackTrace();
		}finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void checkString(String input){
		if(input.contains(":")){
			setServoStand(input);
		}
		else{
			moveServos();
			delay(input);
		}
	}
	
	public static void setServoStand(String input){
		String [] parts = input.split(":");
		int i = 0;
		i = Integer.parseInt(parts [0]);
		servoStanden [i] = Integer.parseInt(parts [1]);
	}
	
	public static void delay(String input){
		String[] parts = input.split("/");
		try {
			Thread.sleep(Integer.parseInt(parts[0]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void moveServos(){
		Main.getInstance().driveServo(new int[] {1, 2, 3}, servoStanden);
		
	}
}
