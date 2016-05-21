using System;

public static class NumericExtensions
{
    public static int ToRadians(this int val)
    {
        return (int)(val * (Math.PI / 180));
    }

    public static int ToDegrees(this int val)
    {
        return (int)(val / (Math.PI / 180));
    }

	public static double ToRadians(this double val)
	{
		return val * (Math.PI / 180);
	}

    public static double ToDegrees(this double val)
    {
        return val / (Math.PI / 180);
    }
}
