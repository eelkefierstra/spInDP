package Distance;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class DistanceMeter
{

	String pigpioFile = "/dev/pigpio";
	String pigOut = "/dev/pigout";
	int signalPin = 23;
	
	public void distanceBoi() throws IOException, InterruptedException 
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		BufferedReader reader = null;
		boolean running = true;
		for (int i = 0; i < 1000; i++)
		//while (running)
		{
			writer.write(String.format("r %s\n", signalPin));
			writer.flush();
			reader = new BufferedReader(new FileReader(pigOut));
			
			long startTime = System.nanoTime();
			while (reader.readLine().charAt(0) == '1')
			{
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
			}
			long estimatedTime = System.nanoTime() - startTime;
			System.out.println(estimatedTime + " ns");
		    int microTime = (int) (estimatedTime/100);
		    System.out.println(microTime + " muS");
		    double distanceCm = microTime/58.0;
		    System.out.println(distanceCm + " in cm(?)");
		    Thread.sleep(5);
		}
		writer.close();
		reader.close();
	}
}