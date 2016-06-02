package com.nhl.spindp.i2c;

public class I2C
{
	private I2CData data;
	
	public I2C()
	{
		data = new I2CData();
		initI2c();
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				cleanupI2c();
			}
		});
	}
	
	public native boolean initI2c();
	
	public native void i2cLoop();
	
	private native void cleanupI2c();
	
	public I2CData getData()
	{
		return data;
	}
	
	public class I2CData
	{
		private short adcVal;
		private short accDataX;
		private short accDataY;
		private short accDataZ;
		private short tmp;
		private short gyroX;
		private short gyroY;
		private short gyroZ;
	}
}
