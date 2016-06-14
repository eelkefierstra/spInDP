package com.nhl.spindp.sensors;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.nhl.spindp.Utils;

public class DistanceMeter
{
	private OutputStreamWriter writer;
	private BufferedReader reader;
	private Thread worker;
	
	public DistanceMeter()
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (IOException e) { }
				}
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e) { }
				}
			}
		});
	}
	
	/**
	 * start distance measurements
	 */
	public void start()
	{
		worker = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					distanceBoi();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		worker.setDaemon(true);
		worker.setName("DistanceThread");
		worker.start();
	}
	
	/**
	 * measure distance to object in front of sensor
	 * @throws IOException
	 */
	private void distanceBoi() throws IOException 
	{
		String pigpioFile = "/dev/pigpio";
		String pigOut = "/dev/pigout";
		int signalPin = 23;
		long startTime = 0;
		long stopTime = 0;
		
		double a = 0;
		double b = 0;
		double c = 0;
		double d = 0;
		double e = 0;
		
		writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		reader = new BufferedReader(new FileReader(pigOut));
		
		while (Utils.shouldRun)
		{
			
			for (int i = -1; i <= 3; i++)
			{			
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
				
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e1) { }
			
				while (reader.readLine().compareTo("0") == 0)
				{
				
					startTime = System.nanoTime();
					writer.write(String.format("r %s\n", signalPin));
					writer.flush();
				}	
				
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
				while (reader.readLine().compareTo("1") == 0)
				{		
					stopTime = System.nanoTime();
					writer.write(String.format("r %s\n", signalPin));
					writer.flush();
				}	
				
				long estimatedTime = stopTime - startTime;
				double microTime = (double) (estimatedTime/58000.0);
				if (microTime < 0)
				{
					continue;
				}
				
				if (i == 0)
				{
					a = microTime;
				}
				
				else if (i == 1)
				{
					b = microTime;
				}
				
				else if (i == 2)
				{
					c = microTime;
				}
				
				else if (i == 3)
				{
					d = microTime;
				}
				
				else if (i == 4)
				{
					e = microTime;
				}
				
				else if (i == 5)
				{
					microTime  = (a + b + c + d + e) / 3;
					i = 0;
				}
			}
		}
		
		writer.close();
		reader.close();
	}	
}		


