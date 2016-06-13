package com.nhl.spindp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LedStrip extends Thread
{
	private static LedStrip instance;
	private volatile boolean interrupted;
	
	@Override
	public void run()
	{
		instance         = this;
		this.interrupted = false;
		int[] rgbColour  = new int[3];
		// Start off with red.
		rgbColour[0] = 255;
		rgbColour[1] = 0;
		rgbColour[2] = 0;

		while (Utils.shouldRun)
		{
			// Choose the colours to increment and decrement.
			for (int decColour = 0; decColour < 3; decColour += 1)
			{
				int incColour = decColour == 2 ? 0 : decColour + 1;
	
				// cross-fade the two colours.
				for(int i = 0; i < 255; i += 1)
				{
					rgbColour[decColour] -= 1;
					rgbColour[incColour] += 1;
					try
					{
						setColourRgb(rgbColour[0], rgbColour[1], rgbColour[2]);
						if (interrupted)
						{
							setColourRgb(255, 0, 0);
							Thread.sleep(1000);
							interrupted = false;
						}
						else Thread.sleep(25);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
	  		}
		}
	}
	
	public static void throwError()
	{
		instance.interrupted = true;
	}

	private static void setColourRgb(int r, int g, int b) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("/dev/pigpio"));
		writer.write("p 6 "+ String.valueOf(r) +" p 13 "+ String.valueOf(g) +" p 5 " + String.valueOf(b) + '\n');
		writer.flush();
		writer.close();
	}
}
