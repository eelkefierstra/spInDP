package com.nhl.spindp;

public class Time
{
	private static long lastTime        = System.currentTimeMillis();
	private static long newSync         = lastTime;
	private static boolean shouldSync   = true;
	private static boolean shouldUpdate = true;
	
	public static double deltaTime;
	
	/**
	 * update time since last time
	 */
	public static void updateDeltaTime()
	{
		long currentTime = System.currentTimeMillis();
		deltaTime = (double)(currentTime - lastTime) / 1000.0;
		lastTime = currentTime;
		if (shouldSync = newSync < currentTime)
		{
			newSync += 1000;
		}
	}
	
	/**
	 * should times get synced
	 * @return true if it should be synced
	 */
	public static boolean shouldSync()
	{
		return shouldSync;
	}
	
	/**
	 * should time get updated
	 * @return true if time should get updated
	 */
	public static boolean shouldUpdate()
	{
		return shouldUpdate;
	}
}
