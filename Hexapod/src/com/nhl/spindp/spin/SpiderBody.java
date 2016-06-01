package com.nhl.spindp.spin;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nhl.spindp.Main;

public class SpiderBody
{
	ExecutorService executor;
	SpiderLeg[] legs;
	Future<?>[] futures;
	SharedParams sharedParams;
	
	public SpiderBody(int startId)
	{
		executor = Executors.newFixedThreadPool(3);
		legs     = new SpiderLeg[6];
		futures  = new Future<?>[6];
		sharedParams = new SharedParams();
		
		for (int i = 0; i < legs.length; i++)
		{
			legs[i] = new SpiderLeg(executor, sharedParams, startId);
			startId += 3;
		}
	}
	
	public void testCalcs()
	{
		int i = 0;
		long start = System.currentTimeMillis();
		for (SpiderLeg leg : legs)
		{
			leg.walk(0, 0);
			futures[i] = leg.getFuture();
			//leg.run();
			i++;
		}
		System.out.println("Calculated in: " + String.valueOf(System.currentTimeMillis() - start) + "ms");
	}
	
	/**
	 * Method that moves the spider
	 * @param forward speed and forward, backward direction of the spider
	 * @param right left, right direction of the spider
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void walk(double forward, double right) throws IOException, InterruptedException
	{
		int i = 0;
		for (SpiderLeg leg : legs)
		{
			if (leg.walk(forward, right))
				futures[i] = leg.getFuture();
			i++;
		}
		for (Future<?> f : futures)
		{
			try
			{
				//f.get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		for (SpiderLeg leg : legs)
		{
			Main.getInstance().driveServoInPython(leg.getIds(), leg.getAngles());
			for (short s : Main.failedServos)
				System.out.println(s);
		}
	}
	
	public class SharedParams
	{
	 	public final int firstId;
        public double firstCoxaChange;
        public double servoAngle_rv;
        public double b_turn;
        
        public SharedParams()
        { 
        	this(1);
        }

        public SharedParams(int firstId)
        { 
        	this(firstId, 0.0, 0.0, 0.0);
        }

        public SharedParams(int firstId, double firstCoxaChange, double servoAngle_rv, double b_turn)
        {
            this.firstId = firstId;
            this.firstCoxaChange = firstCoxaChange;
            this.servoAngle_rv = servoAngle_rv;
            this.b_turn = b_turn;
        }
		
	}
}
