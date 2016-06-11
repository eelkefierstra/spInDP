package com.nhl.spindp;

public class Utils
{
	public static boolean shouldRun = true;
	
	public static double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
