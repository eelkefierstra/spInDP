package Distance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DistanceMeter {
//"r 17\n" -> /dev/pigs
	
	// waarde <- /dev/pigout
	
	String pigpioFile = "/dev/pigpio";
	String pigOut = "/dev/pigout";
	int signalPin = 23;
	
	private void DistanceBoi(boolean val) throws IOException 
	{
	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
	writer.write(String.format("r %s\n", signalPin));
	System.out.println(pigOut);
	writer.flush();
	writer.close();
	}	
}

