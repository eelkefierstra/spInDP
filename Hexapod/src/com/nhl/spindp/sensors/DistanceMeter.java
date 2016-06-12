package com.nhl.spindp.sensors;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class DistanceMeter
{
	public void distanceBoi() throws IOException 
	{
		String pigpioFile = "/dev/pigpio";
		String pigOut = "/dev/pigout";
		int signalPin = 23;
		long startTime = 0;
		long stopTime = 0;
		
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		BufferedReader reader = new BufferedReader(new FileReader(pigOut));
		//for (int i = 0; i <=1000; i++)
		boolean running = true;
		
		while (running)
		{
			writer.write(String.format("r %s\n", signalPin));
			writer.flush();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		
			while (reader.readLine() == "0")
			{
			
				startTime = System.nanoTime();
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
			}	
			
			writer.write(String.format("r %s\n", signalPin));
			writer.flush();
			while (reader.readLine() == "1")
			{		
				stopTime = System.nanoTime();
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
			}	
			
			long estimatedTime = stopTime - startTime;
		
			double microTime = (double) (estimatedTime*17150);
			System.out.println(microTime);
			//System.out.println(reader.readLine());
		}
		writer.close();
		reader.close();
	}	
}		


