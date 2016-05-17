package com.nhl.spindp.spin;

public class SpiderJoint
{
	private static final double MIN_ANGLE       =    0.0;
	private static final double MAX_ANGLE       =  300.0;
	private static final double MIN_SERVO_ANGLE =    0.0;
	private static final double MAX_SERVO_ANGLE = 1023.0;
	
	public static final int COXA  = 0;
	public static final int FEMUR = 1;
	public static final int TIBIA = 2;
	
	private int servoId;
	private double angle;
	private final int offset;
	private final int lowerRange;
	private final int upperRange;
	
	SpiderJoint(int servoId)
	{
		this(servoId, 0.0);
	}
	
	SpiderJoint(int servoId, double angle)
	{
		this(servoId, angle, 0);
	}
	
	SpiderJoint(int servoId, double angle, int offset)
	{
		this(servoId, angle, offset, 0, 300);
	}
	
	SpiderJoint(int servoId, double angle, int offset, int lowerRange, int upperRange)
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
		return mapPosition(angle + offset, MIN_ANGLE, MAX_ANGLE, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
	}
	
	public double getAngle()
	{
		return angle + offset;
	}
	
	void setAngle(double angle)
	{
		double val = Math.toDegrees(angle) + offset;
		if (Double.isNaN(val)) throw new IllegalArgumentException("angle must not be NaN");
		else
		{
			if (val > upperRange)
            {
                System.err.println("val of servo " + servoId + " clamped");
                val = upperRange;
            }
            if (val < lowerRange)
            {
            	System.err.println("val of servo " + servoId + " clamped");
                val = lowerRange;
            }
			this.angle = val;
		}
	}
	
	private int mapPosition(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (int)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
	}
	
	@Override
	public String toString()
	{
		return "Id: " + String.valueOf(servoId) + " angle: " + String.valueOf(angle) + " offset: " + String.valueOf(lowerRange) + " range: " + String.valueOf(upperRange);
	}
}
