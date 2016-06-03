package com.nhl.spindp.spin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SpiderBody
{
	ExecutorService executor;
	SpiderLeg[] legs;
	Queue<Future<?>> futures;
	SharedParams sharedParams;
	
	public SpiderBody(byte startId)
	{
		executor = Executors.newFixedThreadPool(3);
		legs     = new SpiderLeg[6];
		futures  = new LinkedList<>();
		sharedParams = new SharedParams();
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				executor.shutdown();
			}
		});
		
		for (int i = 0; i < legs.length; i++)
		{
			legs[i] = new SpiderLeg(executor, sharedParams, startId);
			startId += 3;
		}
	}
	
	public void testCalcs()
	{
		long start = System.currentTimeMillis();
		for (SpiderLeg leg : legs)
		{
			leg.walk(0, 0);
			futures.offer(leg.getFuture());
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
		for (SpiderLeg leg : legs)
		{
			if (leg.walk(forward, right))
				futures.offer(leg.getFuture());
		}
		while (!futures.isEmpty())
		{
			try
			{
				futures.poll().get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void moveToAngle(double coxa, double femur, double tibia)
	{
		SpiderLeg leg = legs[0];
		//for (SpiderLeg leg : legs)
		{
			leg.moveToDegrees(coxa, femur, tibia);
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
