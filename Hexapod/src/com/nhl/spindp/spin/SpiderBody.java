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
	
	public void testLegMovements()
	{
		//SpiderLeg leg = legs[5];
		for (SpiderLeg leg : legs)
		{
			if (!leg.set) leg.coxaChange += (25.0 * Time.deltaTime);
			if ( leg.set) leg.coxaChange -= (25.0 * Time.deltaTime);
			leg.run();
			Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
			for (short s : Main.failedServos)
				System.out.println(s);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
