package com.nhl.spindp.sensors;

import com.nhl.spindp.Main;
import com.nhl.spindp.Main.Info;

/**
 * Communicate with I2C device with native methods
 * @author eelkef
 *
 */
public class I2C
{
	private Object locker = new Object();
	private I2CData data;
	private double arx, ary, arz; //acc angles
	private double grx, gry, grz; //gyro angles
	private double gsx, gsy, gsz; //scaled gyro data
	private double rx = -1.0, ry = -1.0, rz = -1.0;    //filtered info
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
	/**
	 * Setup gyro and ADC before use
	 * @return false if both fail
	 */
	public native boolean initI2c();
	
	/**
	 * loop to get new info from devices
	 */
	private native void loopI2c();
	
	/**
	 * clean devices
	 */
	private native void cleanupI2c();
	
	/**
	 * loop to start -> update values -> clean devices
	 * Best used in thread
	 */
	private void loop()
	{
		initI2c();
		while (!done)
		{
			loopI2c();
			filter();
			info.setAdc(new double[] {data.adcVal0, data.adcVal1});
		}
		cleanupI2c();
	}
	
	/**
	 * start i2c thread to update values
	 */
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
	
	/**
	 * stop i2c thread
	 */
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
	
	/**
	 * scale gyroscope values
	 */
	private void scale()
	{
		//scale gyro values
		gsx = (double) data.gyroX / 131;
		gsy = (double) data.gyroY / 131;
		gsz = (double) data.gyroZ / 131;
	}
	
	/**
	 * pythagoras
	 * @param a length straight side a
	 * @param b length straight side b
	 * @return length of side
	 */
	private double dist(double a, double b)
	{
		return Math.sqrt((a*a)+(b*b));
	}
	
	/**
	 * calculate angles of accelerometer
	 */
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
	/**
	 * get gyro angle
	 * @return array with angles[ x, y, z]
	 */
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
	/**
	 * get the gyro values once
	 * @return array with angles[ x, y, z]
	 */
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
	/**
	 * get adc value
	 * @return short with value from adc
	 */
	public short getADCInfo()
	{
		short res = -1;
		synchronized (locker)
		{
			res = data.adcVal0;
		}
		return res;
	}
	
	/**
	 * get data class with i2c info
	 * @return data class object
	 */
	public I2CData getData()
	{
		return data;
	}

	/**
	 * class with all i2c info
	 * @author eelkef
	 *
	 */
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