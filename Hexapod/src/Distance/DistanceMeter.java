package Distance;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class DistanceMeter {

	public void distanceBoi() throws IOException 
	{
		String pigpioFile = "/dev/pigpio";
		String pigOut = "/dev/pigout";
		int signalPin = 23;
		long startTime = 0;
		
		try {
			Thread.sleep(10);		
		
	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
	BufferedReader reader = null;
	for (int i = 0; i <=1000; i++)
	{
	writer.write(String.format("r %s\n", signalPin));
	writer.flush();
	reader = new BufferedReader(new FileReader(pigOut));
	
		while (reader.readLine() == "0")
	{
			startTime = System.nanoTime();
	}	
		
		while (reader.readLine() == "1")
		{
		}	
		
	long estimatedTime = System.nanoTime() - startTime;
	
    int microTime = (int) (estimatedTime/58000);
    System.out.println(microTime);
  }
	writer.close();
	reader.close();
  }	
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
}		
}

