import com.nhl.spindp.Main;


//this code works with the information provided by the controller
public class commandc {

	public static void main(String [] args) throws Exception
	{
	     controller();     
	}
	
	public static void controller()
	{
		//for each value a object
		String commando = "a:1,b:0,c:0,x:1000,y:200,s:5";
		String a = "low";
		String b = "low";
		String c = "low";
		int x = 0;
		int y = 0;
		int s = -1;
		
		//loops through string commando
		for (int i = 0; i < commando.length(); i++)
		{
			
			//searches for 'a' in "commando", skips to the value and then changes a to high if the value is 1
			if (commando.charAt(i) == 'a')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					a = "high";
				}
			}
						
			//searches for 'b' in "commando", skips to the value and then changes b to high if the value is 1
			else if (commando.charAt(i) == 'b')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					b = "high";
				}
			}
			
			//searches for 'c' in "commando", skips to the value and then changes c to high if the value is 1
			else if (commando.charAt(i) == 'c')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					c = "high";
				}
			}
			
			//searches for 'x' in "commando", skips to the value and then changes x to an integer between 0 and 1023
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
			
			//searches for 'y' in "commando", skips to the value and then changes y to an integer between 0 and 1023
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
			
			//searches for 's' in "commando", skips to the value and then changes s to an integer between -1 and 9 (this is a mode)
			else if (commando.charAt(i) == 's')
			{
					i+=2;			
					s += Character.getNumericValue(commando.charAt(i));
			}
		}
		
		//a is pressed, selected mode will start
		if (a == "high")
		{
			//Main.
		}
		
		//b is pressed, which kills all actions "killswitch"
		else if (b == "high")
		{
			
		}
		
		//c is pressed, the spider tries to destroy the balloon
		else if (c == "high")
		{
			
		}
		
		//make a turn
		map(x, 0, 1023, -1.0, 1.0);
		
		
		//walk straight
		map(y, 0, 1023, -1.0, 1.0);
		
		
		//modes on the controller screen, when a is pressed it switches
		switch (s) {
		
		case -1:
			
			break;
		
		case 0:
		
			break;
        
		case 1:
			
			break;
			
		case 2:
			
			break;
			
		case 3:
			
			break;
			
		case 4:
			
			break;
			
		case 5:
			
			break;
			
		case 6:
			
			break;
			
		case 7:
			
			break;
			
		case 8:
			
			break;
			
		default:
			break;
		}
		
			}
	
	private static double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
}
