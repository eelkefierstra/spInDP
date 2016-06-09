import com.nhl.spindp.Main;



public class commandc {

	public static void main(String [] args) throws Exception
	{
	     controller();     
	}
	
	public static void controller()
	{
		//voor elke waarde van de controller een object
		String commando = "a:1,b:0,c:0,x:1000,y:200,s:5";
		String a = "low";
		String b = "low";
		String c = "low";
		int x = 0;
		int y = 0;
		int s = -1;
		
		//loopt door string commando
		for (int i = 0; i < commando.length(); i++)
		{
			
			//zoekt naar de eerste 'a' in commando, skipt de ':' en kijkt of de waarde 1 is, indien 1 veranderd object a naar "high"
			if (commando.charAt(i) == 'a')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					a = "high";
				}
			}
						
			//zoekt naar de eerste 'b' in commando, skipt de ':' en kijkt of de waarde 1 is, indien 1 veranderd object b naar "high"
			else if (commando.charAt(i) == 'b')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					b = "high";
				}
			}
			
			//zoekt naar de eerste 'c' in commando, skipt de ':' en kijkt of de waarde 1 is, indien 1 veranderd object c naar "high"
			else if (commando.charAt(i) == 'c')
			{
				i+=2;
				if (commando.charAt(i) == '1')
				{
					c = "high";
				}
			}
			
			//zoekt naar de eerste 'x' in commando, skipt de ':' en kijkt wat de waarde is, veranderd object 'x' naar deze waarde
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
			
			//zoekt naar de eerste 'y' in commando, skipt de ':' en kijkt wat de waarde is, veranderd object 'y' naar deze waarde
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
			
			//zoekt naar de eerste 's' in commando, skipt de ':' en kijkt wat de waarde is, veranderd object 's' naar deze waarde
			else if (commando.charAt(i) == 's')
			{
					i+=2;			
					s += Character.getNumericValue(commando.charAt(i));
			}
		}
		
		//b is ingedrukt dus de spin wordt gestart
		if (a == "high")
		{
			//Main.
		}
		
		//b is ingedrukt dus de kill switch wordt geactiveerd
		else if (b == "high")
		{
			
		}
		
		//c is ingedrukt dus de spin gaat de ballon prikken
		else if (c == "high")
		{
			
		}
		
		//bocht maken
		map(x, 0, 1023, -1.0, 1.0);
		
		
		//rechtuit lopen
		map(y, 0, 1023, -1.0, 1.0);
		
		
		//opties van het schermpje van de controller (dans, race, ballondetectie etc.)
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
