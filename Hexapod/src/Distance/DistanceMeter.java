package Distance;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DistanceMeter {
//"r 17\n" -> /dev/pigs
	
	// waarde <- /dev/pigout
	
	String pigpioFile = "/dev/pigpio";
	String pigOut = "/dev/pigout";
	int signalPin = 23;
	
	public void distanceBoi() throws IOException 
	{
	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
	BufferedReader reader = null;
	for (int i = 0; i <=100; i++)
	{
	writer.write(String.format("r %s\n", signalPin));
	writer.flush();
	reader = new BufferedReader(new FileReader(pigOut));
	System.out.println(reader.readLine());
	}
	writer.close();
	reader.close();
  }	
}

