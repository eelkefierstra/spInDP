package com.nhl.spindp.bluetooth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Scanner;
//import com.fazecast.jSerialComm.*;
import com.nhl.spindp.Utils;

public class BluetoothConnection
{
	private String btFile = "/tmp/BT_IN";
	private FileReader fReader;
	private Thread worker;
	
	public BluetoothConnection()
	{
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
					catch (IOException e) { }
				}
			}
		});
	}
	
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
							if ((char)c == '<') buff = "";
							buff += (char)c;
							if ((char)c == '>')
								System.out.println("Complete instruction");
						}
					}
					catch (Exception ex) { }
				}
			}
		};
		worker.setDaemon(true);
		worker.start();
	}
	
	public void blueLoop() throws IOException
	{
		//FileChannel.open(Paths.get(btFile)).truncate(0).close();
		fReader = null;
		BufferedReader reader = new BufferedReader(new FileReader(btFile));
		int c = 0;
		String buff = "";
		
		while (Utils.shouldRun)
		{
			if ((c = fReader.read()) != -1)
			{
				if ((char)c == '<') buff = "";
				buff += (char)c;
				if ((char)c == '>')
					System.out.println("Complete instruction");
			}
			if (reader.ready())
				System.out.println(reader.readLine());

		}
		reader.close();
	}
	/*SerialPort port;
	Scanner data;
	boolean done = false;
	Thread t2;
	
	public BluetoothConnection()
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				cleanupBluetooth();
			}
		});
	}
	
	public boolean setupBluetooth()
	{
		port = SerialPort.getCommPort("/dev/rfcomm1");
		try
		{
			Thread.sleep(10);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		if (port.openPort())
		{
			port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
			data = new Scanner(port.getInputStream());
			t2 = new Thread(){

				@Override
				public void run()
				{
					scanner();
				}
			};
			t2.start();
			System.out.println("port open succes, thread running");
			return true;
		}
		else
		{
			System.out.println("bluetooth error(port not opened)");
			return false;
		}
	}
	
	public void cleanupBluetooth()
	{
		done = true;
		try
		{
			if(t2 != null)
			{
				t2.interrupt();
				t2.join();
			}
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		if(data != null)
		    data.close();
		if(port != null)
		    port.closePort();
		System.out.println("bluetooth cleaned");
	}
	
	public void scanner()
	{
		while (!done)
		{
			while (data.hasNextLine())
			{
				String mes = data.nextLine();
				System.out.println(mes);
				Commandc.controller(mes);
			}
		}
	}*/
}
