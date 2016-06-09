package com.nhl.spindp.bluetooth;
import com.nhl.spindp.Main;

//This code works with the information provided by the controller
public class commandc
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
		
		//loops through string commando
		for (int i = 0; i < commando.length(); i++)
		{
			
			//searches for 'a' in "commando", skips to the value and then changes object a to high if the value is 1
			if (commando.charAt(i) == 'a')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					a = "high";
				}
			}
						
		//searches for 'b' in "commando", skips to the value and then changes object b to high if the value is 1
			else if (commando.charAt(i) == 'b')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					b = "high";
				}
			}
			
			//searches for 'c' in "commando", skips to the value and then changes object c to high if the value is 1
			else if (commando.charAt(i) == 'c')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					c = "high";
				}
			}
			
			//searches for 'x' in "commando", skips to the value and then changes object x to an integer between 0 and 1023
			else if (commando.charAt(i) == 'x')
			{
				i+=2;
				while (i < commando.length() && Character.isDigit(commando.charAt(i)))
				{					
					x *= 10;
					x += Character.getNumericValue(commando.charAt(i));
					i++;
				}
			}
			
			//searches for 'y' in "commando", skips to the value and then changes object y to an integer between 0 and 1023
			else if (commando.charAt(i) == 'y')
			{
				i+=2;
				while (i < commando.length() && Character.isDigit(commando.charAt(i)))
				{					
					y *= 10;
					y += Character.getNumericValue(commando.charAt(i));
					i++;					
				}
			}
			
			//searches for 's' in "commando", skips to the value and then changes object s to an integer between 0 and 10 (this is a mode)
			else if (commando.charAt(i) == 's')
			{
				while (i < commando.length() && Character.isDigit(commando.charAt(i)))
				{					
					s *= 10;
					s += Character.getNumericValue(commando.charAt(i));
					i++;					
				}
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
		map(x, 0, 1023, -1.0, 1.0);
		
		
		//walk straight
		map(y, 0, 1023, -1.0, 1.0);
		
		//modes of the lcd screen (for example: dance, race mode etc.)
		//the switch case calls the methods for each mode
		switch (ss) {		
		
		case 0:			
			Main.getInstance().setDirection(0, 0, 0);
			break;
        
		case 1:
			Main.getInstance().setDirection(0, map(y, 0, 1023, -1.0, 1.0), map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 2:
			Main.getInstance().setDirection(0, map(y, 0, 1023, -1.0, 1.0), map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 3:
			Main.getInstance().vision.start("line");
			break;
			
		case 4:
			Main.getInstance().setDirection(0, map(y, 0, 1023, -1.0, 1.0), map(x, 0, 1023, -1.0, 1.0));
			break;
			
		case 5:
			Main.getInstance().vision.start("balloon");
			break;
			
		case 6:
			
			break;
			
		case 7:
			
			break;
			
		case 8:
			Main.getInstance().setDirection(0, map(y, 0, 1023, -1.0, 1.0), map(x, 0, 1023, -1.0, 1.0));
			break;
			
		default:
			break;
		}
		
			}
	
	//sets to the x and y value to -1 or 1
	private static double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
}
