package com.nhl.spindp.sensors;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import com.nhl.spindp.Utils;
import com.nhl.spindp.bluetooth.Commandc;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

public class DistanceMeter
{
	private Thread worker;
	
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
		worker.start();
	}
	
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
		
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		BufferedReader reader = new BufferedReader(new FileReader(pigOut));
		
		String s;
		
		
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
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		
			while ((s = reader.readLine()).compareTo("0") == 0)
			{
			
				startTime = System.nanoTime();
				writer.write(String.format("r %s\n", signalPin));
				writer.flush();
			}	
			
			writer.write(String.format("r %s\n", signalPin));
			writer.flush();
			while ((s = reader.readLine()).compareTo("1") == 0)
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
			

			
			//System.out.println(microTime);
			
			//System.out.println(c);
			}
		}
		writer.close();
		reader.close();
	}	
}		


