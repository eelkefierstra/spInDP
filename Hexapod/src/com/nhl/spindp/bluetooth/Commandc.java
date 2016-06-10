package com.nhl.spindp.bluetooth;

import com.nhl.spindp.Main;
//import com.nhl.spindp.vision.*;
import com.nhl.spindp.Utils;


//this code works with the information provided by the controller

public class Commandc
{
	public static void controller(String commando)
	{		
		//for each value an object
		String a = "low"; //button a
		String b = "low"; //button b
		String c = "low"; //button c
		int x = 0;        //horizontal value joystick
		int y = 0;        //vertical value joystick
		int s = 0;        //value lcd
		int ss = 0;       //switch value lcd
		
		if (commando.startsWith("<") && commando.endsWith(">"))
		{
			commando = commando.substring(1, commando.length() - 1);
		}
		else
		{
			return;
		}
		//loops through string commando
		for (String subStr : commando.split(","))
		{
			
			//searches for 'a' in "commando", skips to the value and then changes object a to high if the value is 1
			if (subStr.charAt(0) == 'a')
			{
				if (subStr.endsWith("1"))
				{
					a = "high";
				}
			}
						
		//searches for 'b' in "commando", skips to the value and then changes object b to high if the value is 1
			else if (subStr.charAt(0) == 'b')
			{
				if (subStr.endsWith("1"))
				{
					b = "high";
				}
			}
			
			//searches for 'c' in "commando", skips to the value and then changes object c to high if the value is 1
			else if (subStr.charAt(0) == 'c')
			{
				if (subStr.endsWith("1"))
				{
					c = "high";
				}
			}
			
			//searches for 'x' in "commando", skips to the value and then changes object x to an integer between 0 and 1023
			else if (subStr.charAt(0) == 'x')
			{
				x = Integer.parseInt(subStr.substring(2));
			}
			
			//searches for 'y' in "commando", skips to the value and then changes object y to an integer between 0 and 1023
			else if (subStr.charAt(0) == 'y')
			{
				y = Integer.parseInt(subStr.substring(2));
			}
			
			//searches for 's' in "commando", skips to the value and then changes object s to an integer between 0 and 10 (this is a mode)
			else if (subStr.charAt(0) == 's')
			{
				s = Integer.parseInt(subStr.substring(2));
			}
		}
		
		//b is pressed, selected mode will start
		if (b == "high")
		{
			ss = 0;
		}
		
		//a is pressed, which kills all actions "killswitch"
		else if (a == "high")
		{
            ss = s;
		}

		//c is pressed, the spider tries to destroy a balloon
		else if (c == "high")
		{
			
		}
		else if(s == -1)
		{
			s = 0;
		}
		
		//make a turn
		//Utils.map(x, 0, 1023, -1.0, 1.0);
		
		
		//walk straight
		//Utils.map(y, 0, 1023, -1.0, 1.0);
		
		//modes of the lcd screen (for example: dance, race mode etc.)
		//the switch case calls the methods for each mode
		switch (ss)
		{
		case 0:			
			Main.getInstance().setDirection(0, 0, 0);
			break;
        
		case 1:
			Main.getInstance().setDirection(0, Utils.map(y, 0, 1023, -1.0, 1.0), Utils.map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 2:
			Main.getInstance().setDirection(0, Utils.map(y, 0, 1023, -1.0, 1.0), Utils.map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 3:
			Main.getInstance().vision.start("line");
			break;
			
		case 4:
			Main.getInstance().setDirection(0, Utils.map(y, 0, 1023, -1.0, 1.0), Utils.map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 5:
			Main.getInstance().vision.start("balloon");
			break;
			
		case 6:
			
			break;
			
		case 7:
			
			break;
			
		case 8:
			Main.getInstance().setDirection(0, Utils.map(y, 0, 1023, -1.0, 1.0), Utils.map(x, 0, 1023, -1.0, 1.0));
			break;
			
		default:
			break;
		}
	}	
}
