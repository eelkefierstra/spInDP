package com.nhl.spindp.sensors;

import com.nhl.spindp.Main;
import com.nhl.spindp.Main.Info;
import com.nhl.spindp.Utils;

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
	private Thread worker;
	
	public I2C()
	{
		data = new I2CData();
		info = Main.getInstance().getInfo();
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
		while (!Utils.shouldRun)
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
		worker = new Thread()
		{
			@Override
			public void run()
			{
				loop();
			}
		};
		worker.setDaemon(true);
		worker.setName("I2CWorker");
		worker.start();
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
	@SuppressWarnings("unused")
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