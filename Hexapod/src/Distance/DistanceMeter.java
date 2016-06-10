package Distance;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class DistanceMeter {

	String pigpioFile = "/dev/pigpio";
	String pigOut = "/dev/pigout";
	int signalPin = 23;
	
	public void distanceBoi() throws IOException 
	{
	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
	BufferedReader reader = null;
	for (int i = 0; i <=1000; i++)
	{
	writer.write(String.format("r %s\n", signalPin));
	writer.flush();
	reader = new BufferedReader(new FileReader(pigOut));
	
	long startTime = System.nanoTime();
	while (reader.readLine() == "1")
	{
	}	
	long estimatedTime = System.nanoTime() - startTime;
	
    int microTime = (int) (estimatedTime/1000);
    double distanceCm = microTime/58.0;
    System.out.println(distanceCm);
  }
	writer.close();
	reader.close();
  }	
}

