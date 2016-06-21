package com.nhl.spindp.spin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.nhl.spindp.LedStrip;
import com.nhl.spindp.Main;
import com.nhl.spindp.Utils;

public class Dans
{
	private boolean dancing;
	private  int[] servoStanden = new int[18];
	private int[] servoNumbers = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
	boolean first = true;
	
	
	//Do all the dance mover
	public void doDanceMoves()
	{
		dancing = true;
		BufferedReader br = null;
		LedStrip.danceStart();
		//read the file with all the moves programmed in it
		try
		{
		    br = new BufferedReader(new FileReader(new File(Main.class.getResource("/Dans.txt").getPath()).getAbsolutePath()));
			String line = "";

		    while ((line = br.readLine()) != null)
		    {
		    	//for evrye string in the file check what it is
		    	checkString(line);
		    	if (!Utils.shouldRun)
		    		break;
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
		dancing = false;
	}
	
	//Check if a string contains : or / or , Denpending on that char it needs to set a servostand or change the collor of the ledstrip, or moves all the servo's
	public void checkString(String input)
	{
		if(input.isEmpty())
			return;
		else if(input.contains(":"))
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
	
	//Set the servo stand good
	public void setServoStand(String input)
	{
		String [] parts = input.split(":");
		int i = 0;
		i = Integer.parseInt(parts [0])-1;
		servoStanden [i] = Integer.parseInt(parts [1]);
	}
	
	//sleep for the time that is programmed in the dance text
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
	
	//Move all the servo's with there standen
	public void moveServos()
	{
		Main.getInstance().driveServo(servoNumbers, servoStanden);
		Arrays.fill(servoStanden, 0);
	}
	
	//change the color of the ledstrip
	public void changeColorLedStrip(String input)
	{
		String[] parts = input.split(",");
		int r = Integer.parseInt(parts[1]);
		int g = Integer.parseInt(parts[2]);
		int b = Integer.parseInt(parts[3]);
		try {
			LedStrip.setColourRgb(r,g,b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//returns true when is dancing
	public boolean isDancing()
	{
		return dancing;
	}
	
}
