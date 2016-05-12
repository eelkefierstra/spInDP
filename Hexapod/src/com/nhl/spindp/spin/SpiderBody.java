package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nhl.spindp.Main;

public class SpiderBody
{
	ExecutorService executor;
	SpiderLeg[] legs;
	
	public SpiderBody(int startId)
	{
		executor = Executors.newFixedThreadPool(3);
		legs     = new SpiderLeg[6];
		for (int i = legs.length-1; i >= 0; i--)
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
		boolean flip = false;
		while (true)
		{
			SpiderLeg leg = legs[5];
			//for (SpiderLeg leg : legs)
			{
				if (!flip) leg.coxaChange += 1;
				if ( flip) leg.coxaChange -= 1;
				if (leg.coxaChange > 90) flip = true;
				if (leg.coxaChange <= 0) flip = false;
				leg.run();
				Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
			}
		}
	}
}
