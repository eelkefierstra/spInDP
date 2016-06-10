package com.nhl.spindp.sensors;

import com.nhl.spindp.Main;
import com.nhl.spindp.Main.Info;

public class I2C
{
	private Object locker = new Object();
	private I2CData data;
	private double arx, ary, arz; //acc angles
	private double grx, gry, grz; //gyro angles
	private double gsx, gsy, gsz; //scaled gyro data
	private double rx = -1.0, ry = -1.0, rz = -1.0;    //filtered info
	private double adc0 = -1.0, adc1 = -1.0;
	private boolean done = true;
	private Info info;
	private Thread t1;
	
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
		info = Main.getInstance().getInfo();
	}
	
	public native boolean initI2c();
	
	public native void loopI2c();
	
	private native void cleanupI2c();
	
	private void loop()
	{
		initI2c();
		while (!done)
		{
			loopI2c();
			filter();
			info.setAdc(new double[] {adc0, adc1});
		}
		cleanupI2c();
	}
	
	public void start()
	{
		done = false;
		t1 = new Thread()
		{
			@Override
			public void run()
			{
				loop();
			}
		};
		t1.start();
	}
	
	public void stop()
	{
		done = true;
		try
		{
			if(t1 != null)
			    t1.join();
		}
		catch (InterruptedException e)
		{
			
		}
	}
	
	private void scale()
	{
		//scale gyro values
		gsx = (double) data.gyroX / 131;
		gsy = (double) data.gyroY / 131;
		gsz = (double) data.gyroZ / 131;
	}
	
	private double dist(double a, double b)
	{
		return Math.sqrt((a*a)+(b*b));
	}
	
	//loop this method to continuesly refresh gyro values
	private void filter()
	{
		double accXscale = (double)data.accDataX / 16384.0;
		double accYscale = (double)data.accDataY / 16384.0;
		double accZscale = (double)data.accDataZ / 16384.0;
		rx = Math.toDegrees(Math.atan2(accYscale, dist(accXscale, accZscale)));
		ry = Math.toDegrees(Math.atan2(accXscale, dist(accYscale, accZscale)));
		info.setGyro(new double[] {rx, ry});
	}
	
	@Deprecated
	public double[] getGyroInfo()
	{
		double[] res = { -1, -1, -1};
		synchronized (locker)
		{
			res[0] = rx;
			res[1] = ry;
			res[2] = rz;
		}
		return res;
	}
	
	@Deprecated
	public double[] runOnceAndGetGyroInfo()
	{
		initI2c();
		loopI2c();
		filter();
		cleanupI2c();
		
		double[] res = { -1, -1, -1};
		res[0] = rx;
		res[1] = ry;
		res[2] = rz;
		return res;
	}
	
	@Deprecated
	public short getADCInfo()
	{
		short res = -1;
		synchronized (locker)
		{
			res = data.adcVal0;
		}
		return res;
	}
	
	public I2CData getData()
	{
		return data;
	}

	public class I2CData
	{
		private short adcVal0  = -1;
		private short adcVal1  = -1;
		private short accDataX = -1;
		private short accDataY = -1;
		private short accDataZ = -1;
		//private short tmp      = -1;
		private short gyroX    = -1;
		private short gyroY    = -1;
		private short gyroZ    = -1;
	}
}