package com.nhl.spindp.spin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.nhl.spindp.LedStrip;
import com.nhl.spindp.Main;

public class Dans
{
	private  int[] servoStanden = new int[18];
	private int[] servoNumbers = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
	boolean first = true;
	
	public void doDanceMoves()
	{
		BufferedReader br = null;
		LedStrip.danceStart();
		try
		{
			//br = new BufferedReader(new FileReader("D:\\IDPgit\\python\\Dans.txt"));
			//br = new BufferedReader(new FileReader("/home/pi/git/spInDP/python/Dans.txt"));
		    br = new BufferedReader(new FileReader(Main.class.getResource("Dans.txt").getFile()));
			String line = "";

		    while ((line = br.readLine()) != null)
		    {
		    	checkString(line);
		    }
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
		    try
		    {
		    	if (br != null)
		    		br.close();
			}
		    catch (IOException e)
		    {
				e.printStackTrace();
			}
		}
	}
	
	public void checkString(String input)
	{
		if(input.contains(":"))
		{
			setServoStand(input);
		}
		else if(input.contains("/"))
		{
			moveServos();
			delay(input);
		}
		else if(input.contains(","))
		{
			changeColorLedStrip(input);
		}
	}
	
	public void setServoStand(String input)
	{
		String [] parts = input.split(":");
		int i = 0;
		i = Integer.parseInt(parts [0])-1;
		servoStanden [i] = Integer.parseInt(parts [1]);
	}
	
	public void delay(String input)
	{
		String[] parts = input.split("/");
		if (parts[0].isEmpty()) return;
		try
		{
			Thread.sleep(Integer.parseInt(parts[0]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void moveServos()
	{
		Main.getInstance().driveServo(servoNumbers, servoStanden);
		servoStanden = null;
	}
	
	public void changeColorLedStrip(String input)
	{
		String[] parts = input.split(",");
		int r = Integer.parseInt(parts[1]);
		int g = Integer.parseInt(parts[2]);
		int b = Integer.parseInt(parts[3]);
		try {
			LedStrip.setColourRgb(r,g,b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
