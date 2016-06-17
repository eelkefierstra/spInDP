package com.nhl.spindp;

import java.util.Arrays;

public class Utils
{
	public static boolean shouldRun = true;
	
	public static double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	public static short mapServoPosition(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (short)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
	}
	
	/**
	 * Combines a list of arrays into one byte array
	 * @param first the first array
	 * @param rest the rest of the arrays
	 * @return
	 */
	public static byte[] concat(byte[] first, byte[]... rest)
	{
		int totalLength = first.length;
		for (byte[] array : rest)
		{
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
}
