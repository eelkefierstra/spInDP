package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpiderBody
{
	ExecutorService executor;
	SpiderLeg[] legs;
	
	public SpiderBody()
	{
		executor = Executors.newFixedThreadPool(3);
		legs = new SpiderLeg[6];
		for (int i = 0; i < legs.length; i++)
		{
			legs[i] = new SpiderLeg();
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
}
