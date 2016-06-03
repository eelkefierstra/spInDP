package com.nhl.spindp.bluetooth;

import java.io.IOException;

public class BluetoothConnection
{
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
	
	public native boolean setupBluetooth();
	
	public native void connectionLoop() throws IOException;
	
	private native void cleanupBluetooth();
}
