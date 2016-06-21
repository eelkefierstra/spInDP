package com.nhl.spindp.bluetooth;

import com.nhl.spindp.Main;
import com.nhl.spindp.Utils;

//This code works with the information provided by the controller
public class Commandc
{
	//for each value an object
	private static int a  = 0; //button a
	private static int b  = 0; //button b
	private static int c  = 0; //button c
	private static int x  = 0; //horizontal value joystick
	private static int y  = 0; //vertical value joystick
	private static int s  = 0; //value lcd
	private static int ss = 0; //switch value lcd
	
	/**
	 * proces bluetooth commands
	 * @param 	commando string from bluetooth device
	 * 			icludes both <>
	 */
	public static synchronized void controller(String commando)
	{
		if (commando.startsWith("<") && commando.endsWith(">"))
		{
			commando = commando.substring(1, commando.length() - 1);
		}
		else
		{
			return;
		}
		
		//loops through commando string
		String[] strArr = commando.split(",");
		if(strArr.length != 6)
			return;
		
		for (String subStr : strArr)
		{
			if (subStr.isEmpty())
				continue;
			char start = subStr.charAt(0);
			
			switch (start)
			{
				case 'a':
					a = Integer.parseInt(subStr.substring(2));
					break;
				case 'b':
					b = Integer.parseInt(subStr.substring(2));
					break;
				case 'c':
					c = Integer.parseInt(subStr.substring(2));
					break;
				case 'x':
					x = Integer.parseInt(subStr.substring(2));
					break;
				case 'Y':
					y = Integer.parseInt(subStr.substring(2));
					break;
				case 's':
					s = Integer.parseInt(subStr.substring(2));
					break;
				default:
					break;
			}
		}
		
		//b is pressed, which kills all actions "killswitch"
		if (b == 1)
		{
			Main.vision.stop();
			ss = 0;
		}
		
		//a is pressed, selected mode will start
		else if (a == 1)
		{
            ss = s;
		}

		//c is pressed, the spider tries to destroy a balloon
		else if (c == 1)
		{
			Main.getInstance().stab(0);
		}
		
		else if(s == -1)
		{
			s = 0;
		}
		
		//modes of the lcd screen (for example: dance, race mode etc.)
		//the switch case calls the methods for each mode
		switch (ss)
		{
			case 0:	//In main menu
				Main.getInstance().setDirection(0, 0, 0);
				break;
			case 3://Follow the line
				Main.vision.start("line");
				break;
			case 5://search balloon				
				Main.getInstance().vision.start("balloon");
				Main.getInstance().setDirection(0, 1.0,(Utils.map(Main.getInstance().getInfo().getX(), 0, 640, -1.0, 1.0) ));
				
				
				break;
			case 1://Spinnijdig race
			case 2://Spider Race
			case 4://Spider Gap
			case 8://Walking the gate
				Main.getInstance().setDirection(0, Utils.map(y, 0, 1023, -1.0, 1.0), Utils.map(x, 0, 1023, -1.0, 1.0));
				break;
			case 6://De paringsdans
				
				break;
			case 7://Dance
				Main.getDans().doDanceMoves();
				break;
			default:
				break;
		}
	}	
}
