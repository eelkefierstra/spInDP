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
    private readonly int offset;
    private readonly int lowerRange;
	private readonly int upperRange;

	internal SpiderJoint(int servoId) : this(servoId, 0.0) { }

    internal SpiderJoint(int servoId, double angle) : this(servoId, angle, 0) { }

    internal SpiderJoint(int servoId, double angle, int offset) : this(servoId, angle, offset, 0, 300) { }

	internal SpiderJoint(int servoId, double angle, int offset, int lowerRange, int upperRange)
	{
        this.servoId    = servoId;
        this.angle      = angle;
        this.offset     = offset;
        this.lowerRange = lowerRange;
		this.upperRange = upperRange;
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
        else
        {
            double val = angle.ToDegrees() + offset;
            if (val > upperRange)
            {
                UnityEngine.Debug.Log("val of servo " + servoId + " clamped");
               // val = upperRange;
            }
            if (val < lowerRange)
            {
                UnityEngine.Debug.Log("val of servo " + servoId + " clamped");
               // val = lowerRange;
            }
            this.angle = val;
        }
	}

    private int mapPosition(double x, double in_min, double in_max, double out_min, double out_max)
    {
        if (x > in_max || x < in_min) UnityEngine.Debug.LogException(new ArgumentException("Input not between min and max", "x"));
        return (int)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }
}
