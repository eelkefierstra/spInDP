package com.nhl.spindp.bluetooth;

import java.util.Scanner;

import com.nhl.spindp.serialconn.SerialPort;

public class BluetoothConnection
{
	SerialPort port;
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
		if (port.openPort())
		{
			port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
			data = new Scanner(port.getInputStream());
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void cleanupBluetooth()
	{
		data.close();
	}
	
	public void scanner()
	{
		while (!done)
		{
			while (data.hasNextLine())
			{
				String mes = data.nextLine();
				System.out.println(mes);
			}
		}
	}
}
