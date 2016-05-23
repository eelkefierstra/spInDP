package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nhl.spindp.Main;
import com.nhl.spindp.Time;

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
			legs[i] = new SpiderLeg(startId);
			startId += 3;
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
	
	/**
	 * Method that moves the spider
	 * @param forward speed and forward, backward direction of the spider
	 * @param right left, right direction of the spider
	 */
	public void walk(double forward, double right)
	{
		for (SpiderLeg leg : legs)
		{
			if (!leg.set) leg.coxaChange += ((25.0 * Time.deltaTime) * forward);
			if ( leg.set) leg.coxaChange -= ((25.0 * Time.deltaTime) * forward);
			leg.run();
			Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
			for (short s : Main.failedServos)
				System.out.println(s);
		}
	}
}
