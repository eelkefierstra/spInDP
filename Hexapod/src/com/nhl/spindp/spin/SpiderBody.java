package com.nhl.spindp.spin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nhl.spindp.Main;

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
	 * @throws ExecutionException 
	 */
	public void walk(double forward, double right) throws IOException, InterruptedException, ExecutionException
	{
		for (SpiderLeg leg : legs)
		{
			//leg.setHeight(120.0);
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
		for (SpiderLeg leg : legs)
		{
			//leg.getAll();
			if (Main.IS_ARM)
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
		}
		sharedParams.sync = false;
	}
	
	public void setHeight(double height)
	{
		for (SpiderLeg leg : legs)
		{
			leg.setHeight(height);
		}
	}
	
	public void setWidth(double width)
	{
		for (SpiderLeg leg : legs)
		{
			leg.setWidth(width);
		}
	}
	
	public void stabbyStab() throws InterruptedException
	{
		legs[0].moveToDegrees(45.0, 145.0, 135.0);
		legs[1].moveToDegrees( 0.0, 115.0,  45.0);
		legs[2].moveToDegrees(90.0, 115.0,  45.0);
		legs[3].moveToDegrees(45.0, 145.0,  90.0);
		legs[4].moveToDegrees( 0.0, 115.0,  45.0);
		legs[5].moveToDegrees(45.0, 115.0, 135.0);
		for (SpiderLeg leg : legs)
		{
			if (Main.IS_ARM)
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
		}
		Thread.sleep(500);
		legs[0].moveToDegrees(-30.0, 145.0, 135.0);
		legs[3].moveToDegrees(-30.0, 145.0, 135.0);
		for (SpiderLeg leg : legs)
		{
			if (Main.IS_ARM)
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
		}
	}
	
	public int[] getIds()
	{
		int[] res = new int[legs.length * 3];
		for (int i = 0; i < legs.length; i++)
		{
			System.arraycopy(legs[i].getIds(), 0, res, i * 3, 3);
		}
		return res;
	}
	
	public int[] getAngles()
	{
		int[] res = new int[legs.length * 3];
		for (int i = 0; i < legs.length; i++)
		{
			System.arraycopy(legs[i].getAngles(), 0, res, i * 3, 3);
		}
		return res;
	}
	
	public void moveToDegrees(double coxa, double femur, double tibia) throws InterruptedException, ExecutionException
	{
		//SpiderLeg leg = legs[0];
		for (SpiderLeg leg : legs)
		{
			leg.moveToDegrees(coxa, femur, tibia);
		}
		for (SpiderLeg leg : legs)
		{
			//leg.getAll();
			if (Main.IS_ARM)
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
		}
	}
	
	public void moveTo(byte id, short coxa, short femur, short tibia)
	{
		legs[id].moveTo(coxa, femur, tibia);
		if (Main.IS_ARM)
			Main.getInstance().driveServo(legs[id].getIds(), legs[id].getAngles());
	}
	
	public class SharedParams
	{
		public boolean sync;
	 	public final int firstId;
        public double firstCoxaChange;
        public double servoAngle_rv;
        public double b_turn;
        public double beta_RV;
        public int legSetID;
        public boolean set_rv;
        
        public SharedParams()
        { 
        	this(false, 1);
        }

        public SharedParams(boolean sync,int firstId)
        { 
        	this(sync, firstId, 0.0, 0.0, 0.0, 0.0);
        }

        public SharedParams(boolean sync, int firstId, double firstCoxaChange, double servoAngle_rv, double b_turn, double beta_RV)
        {
        	this.sync = sync;
            this.firstId = firstId;
            this.firstCoxaChange = firstCoxaChange;
            this.servoAngle_rv = servoAngle_rv;
            this.b_turn = b_turn;
            this.beta_RV = beta_RV; 
            this.legSetID = 0;
            this.set_rv = true;
        }
		
	}
}
