package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nhl.spindp.Main;

public class SpiderBody
{
	ExecutorService executor;
	SpiderLeg[] legs;
	
	public SpiderBody()
	{
		executor = Executors.newFixedThreadPool(3);
		legs     = new SpiderLeg[6];
		for (int i = 0; i < legs.length; i++)
		{
			legs[i] = new SpiderLeg();
		}
	}
	
	public SpiderBody(int startId)
	{
		executor = Executors.newFixedThreadPool(3);
		legs     = new SpiderLeg[6];
		for (int i = 0; i < legs.length; i++)
		{
			legs[i] = new SpiderLeg(startId++);
		}
	}
	
	public void testCalcs()
	{
		long start = System.currentTimeMillis();
		for (SpiderLeg leg : legs)
		{
			leg.coxaChange = 5.0;
			leg.run();
		}
		System.out.println("Calculated in: " + String.valueOf(System.currentTimeMillis() - start) + "ms");
	}
	
	public void testLegMovements()
	{
		while (true)
		{
			for (SpiderLeg leg : legs)
			{
				leg.coxaChange += 1;
				if (leg.coxaChange > 90) leg.coxaChange = 0.0;
				leg.run();
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
			}
		}
	}
}
