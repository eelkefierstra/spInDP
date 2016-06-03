package com.nhl.spindp.i2c;

public class I2C
{
	private I2CData data;
	private double arx, ary, arz; //acc angles
	private double grx, gry, grz; //gyro angles
	private double gsx, gsy, gsz; //scaled gyro data
	private double rx, ry, rz;    //filtered info
	private double timeStep, time, timePrev;
	private boolean init = true, done = true;
	private Thread t1;
	private Object locker;
	
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
	
	public native void loopI2c();
	
	private native void cleanupI2c();
	
	private void loop()
	{
		initI2c();
		time = System.currentTimeMillis();
		while (!done)
		{
			loopI2c();
			filter();
		}
		cleanupI2c();
	}
	
	public void start()
	{
		done = false;
		t1 = new Thread(){

			@Override
			public void run()
			{
				loop();
			}
		};
	}
	
	public void stop()
	{
		done = true;
		try
		{
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
	
	//loop this method to continuesly refresh gyro values
	private void filter()
	{
		timePrev = time;
		time = System.currentTimeMillis();
		timeStep = (time / timePrev) / 1000; //timestep in seconds
		
		scale();
		//calc acc angles
		arx = Math.toDegrees(Math.atan((double) data.accDataX / Math.sqrt(Math.pow(data.accDataY,2) + Math.pow(data.accDataZ, 2))));
		ary = Math.toDegrees(Math.atan((double) data.accDataY / Math.sqrt(Math.pow(data.accDataX,2) + Math.pow(data.accDataZ, 2))));
		arz = Math.toDegrees(Math.atan(Math.sqrt(Math.pow(data.accDataY, 2) + Math.pow(data.accDataX, 2)) / (double) data.accDataZ));
		
		if (init)
		{
			//set initial val equal to gyro values
			grx = arx;
			gry = ary;
			grz = arz;
			
			init = !init;
		}
		//integrate to get gyro angle
		else
		{
			grx = grx + (timeStep * gsx);
			gry = gry + (timeStep * gsy);
			grz = grz + (timeStep * gsz);
		}
		
		//apply filter
		rx = (0.96 * arx) + (0.04 * grx);
		ry = (0.96 * ary) + (0.04 * gry);
		rz = (0.96 * arz) + (0.04 * grz);
	}
	
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
	
	public double[] runOnceAndGetGyroInfo()
	{
		initI2c();
		loopI2c();
		filter();
		cleanupI2c();
		
		double[] res = { -1, -1, -1};
		synchronized (locker)
		{
			res[0] = rx;
			res[1] = ry;
			res[2] = rz;
		}
		return res;
	}
	
	public I2CData getData()
	{
		return data;
	}

	public class I2CData
	{
		//private short adcVal   = -1;
		private short accDataX = -1;
		private short accDataY = -1;
		private short accDataZ = -1;
		//private short tmp      = -1;
		private short gyroX    = -1;
		private short gyroY    = -1;
		private short gyroZ    = -1;
	}
}
