using System;

public static class NumericExtensions
{

    /// <summary>
    /// Between check <![CDATA[min <= value <= max]]> 
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="value">the value to check</param>
    /// <param name="min">Inclusive minimum border</param>
    /// <param name="max">Inclusive maximum border</param>
    /// <returns>return true if the value is between the min and max else false</returns>
    public static bool IsBetweenII<T>(this T value, T min, T max) where T : IComparable
    {
        return (min.CompareTo(value) <= 0) && (value.CompareTo(max) <= 0);
    }

    /// <summary>
    /// Between check <![CDATA[min <= value <= max]]>
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="value">the value to check</param>
    /// <param name="min">Exclusive minimum border</param>
    /// <param name="max">Inclusive maximum border</param>
    /// <returns>return true if the value is between the min and max else false</returns>
    public static bool IsBetweenEI<T>(this T value, T min, T max) where T : IComparable
    {
        return (min.CompareTo(value) < 0) && (value.CompareTo(max) <= 0);
    }

    /// <summary>
    /// between check <![CDATA[min <= value <= max]]>
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="value">the value to check</param>
    /// <param name="min">Inclusive minimum border</param>
    /// <param name="max">Exclusive maximum border</param>
    /// <returns>return true if the value is between the min and max else false</returns>
    public static bool IsBetweenIE<T>(this T value, T min, T max) where T : IComparable
    {
        return (min.CompareTo(value) <= 0) && (value.CompareTo(max) < 0);
    }

    /// <summary>
    /// between check <![CDATA[min <= value <= max]]>
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="value">the value to check</param>
    /// <param name="min">Exclusive minimum border</param>
    /// <param name="max">Exclusive maximum border</param>
    /// <returns>return true if the value is between the min and max else false</returns>
    public static bool IsBetweenEE<T>(this T value, T min, T max) where T : IComparable
    {
        return (min.CompareTo(value) < 0) && (value.CompareTo(max) < 0);
    }

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
