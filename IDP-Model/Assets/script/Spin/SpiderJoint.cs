using System;

public class SpiderJoint
{
    private static readonly double MIN_ANGLE =   0.0;
    private static readonly double MAC_ANGLE = 300.0;

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

	internal SpiderJoint(int servoId)
	{
		this.servoId = servoId;
		this.angle   = 0.0;
		this.offset  = 0;
		this.range   = 300;
	}

	internal SpiderJoint(double angle)
	{
		this.servoId = 0;
		this.angle   = angle;
		this.offset  = 0;
		this.range   = 300;
	}

	internal SpiderJoint(int servoId, double angle)
	{
		this.servoId = servoId;
		this.angle   = angle;
		this.offset  = 0;
		this.range   = 300;
	}

	internal SpiderJoint(int servoId, double angle, int offset)
	{
		this.servoId = servoId;
		this.angle   = angle;
		this.offset  = offset;
		this.range   = 300;
	}

	internal SpiderJoint(int servoId, double angle, int offset, int range)
	{
		this.servoId = servoId;
		this.angle   = angle;
		this.offset  = offset;
		this.range   = range;
	}

	public int getId()
	{
		return servoId;
	}

	public int getServoAngle()
	{
		return (int)(angle + offset);
	}

	public double getAngle()
	{
		return angle;
	}

	internal void setAngle(double angle)
	{
        if (Double.IsNaN(angle)) //throw new ArgumentException("Argument must not be NaN", "angle");
        {
            //UnityEngine.Debug.LogException(new ArgumentException("Argument must not be NaN", "angle"));
            angle = 150.0;
        }
        double val = angle.ToDegrees();
        if (val > range ) val = range;
        if (val < offset) val = offset;
		this.angle = val;
	}
}
