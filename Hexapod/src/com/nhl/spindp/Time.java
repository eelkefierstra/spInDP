package com.nhl.spindp;

public class Time
{
	private static long lastTime = System.currentTimeMillis();
	
	public static double deltaTime;
	
	public static void updateDeltaTime()
	{
		long currentTime = System.currentTimeMillis();
		deltaTime = (double)(currentTime - lastTime) / 1000.0;
		lastTime = currentTime;
	}
}
