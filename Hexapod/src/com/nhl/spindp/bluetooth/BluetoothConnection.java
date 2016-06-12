package com.nhl.spindp.bluetooth;

import java.io.FileReader;
import java.io.IOException;
import com.nhl.spindp.Utils;

public class BluetoothConnection
{
	private String btFile = "/tmp/BT_IN";
	
	public void blueLoop() throws IOException
	{
		//FileChannel.open(Paths.get(btFile)).truncate(0).close();
		FileReader fReader = new FileReader(btFile);
		//BufferedReader reader = new BufferedReader(new FileReader(btFile));
		int c = 0;
		String buff = "";
		
		while(Utils.shouldRun)
		{
			if ((c = fReader.read()) != -1)
			{
				if((char) c == '\n')
					break;
				buff += (char)c;
				if((char)c == '>')
				{
					Commandc.controller(buff);
					buff = "";
				}
			}
			/*if (reader.ready())
				System.out.println(reader.readLine());*/

		}
		fReader.close();
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
