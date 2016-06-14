package com.nhl.spindp.bluetooth;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.nhl.spindp.Utils;

public class BluetoothConnection
{
	private String btFile = "/tmp/BT_IN";
	private FileReader fReader;
	private Thread worker;
	
	/**
	 * create new bluetooth listener
	 * has shutdownhook to automatically clean up
	 * @throws FileNotFoundException
	 */
	public BluetoothConnection() throws FileNotFoundException
	{
		fReader = new FileReader(btFile);
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				if (fReader != null)
				{
					try
					{
						fReader.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * start bluetooth reading thread
	 */
	public void start()
	{
		worker = new Thread()
		{
			@Override
			public void run()
			{
				String buff = "";
				int c = 0;
				while (Utils.shouldRun)
				{
					try
					{
						if ((c = fReader.read()) != -1)
						{
							if((char)c == '\n')
								continue;
							buff += (char)c;
							if ((char)c == '<')
							{
								buff = "<";
							}
							else if ((char)c == '>')
							{
								System.out.println("Complete instruction: "+ buff);
								Commandc.controller(buff);
							}
						}
					}
					catch (Exception ex) { 
						ex.printStackTrace();
					}
				}
			}
		};
		worker.setDaemon(true);
		worker.setName("BlueThread");
		worker.start();
	}
}
