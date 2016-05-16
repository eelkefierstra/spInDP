using System;

public class SpiderJoint
{
    private static readonly double MIN_ANGLE       =    0.0;
    private static readonly double MAX_ANGLE       =  300.0;
    private static readonly double MIN_SERVO_ANGLE =    0.0;
    private static readonly double MAX_SERVO_ANGLE = 1023.0;

    public static readonly int COXA  = 0;
	public static readonly int FEMUR = 1;
	public static readonly int TIBIA = 2;

	private int servoId;
	private double angle;
	private int offset;
	private int range;

	internal SpiderJoint()
	{
		this.servoId = 0;
		this.angle   = 0.0;
		this.offset  = 0;
		this.range   = 300;
	}

	internal SpiderJoint(int servoId) : this()
	{
		this.servoId = servoId;
	}

	internal SpiderJoint(int servoId, double angle) : this(servoId)
	{
		this.angle = angle;
	}

	internal SpiderJoint(int servoId, double angle, int offset) : this(servoId, angle)
	{
		this.offset = offset;
	}

	internal SpiderJoint(int servoId, double angle, int offset, int range) : this (servoId, angle, offset)
	{
		this.range = range;
	}

	public int getId()
	{
		return servoId;
	}

	public int getServoAngle()
	{
        return mapPosition(angle, MIN_ANGLE, MAX_ANGLE, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
    }

	public double getAngle()
	{
		return angle;
	}

	internal void setAngle(double angle)
	{
        if (Double.IsNaN(angle)) //throw new ArgumentException("Argument must not be NaN", "angle");
        {
            UnityEngine.Debug.LogException(new ArgumentException("Argument must not be NaN", "angle"));
        }
        double val = angle.ToDegrees();
        if (val > range ) val = range;
        if (val < offset) val = offset;
		this.angle = val;
	}

    private int mapPosition(double x, double in_min, double in_max, double out_min, double out_max)
    {
        if (x > in_max || x < in_min) UnityEngine.Debug.LogException(new ArgumentException("Input not between min and max", "x"));
        return (int)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }
}
