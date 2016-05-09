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
	private int offset;
	private int range;
	
	SpiderJoint()
	{
		this.servoId = 0;
		this.angle   = 0.0;
		this.offset  = 0;
		this.range   = 300;
	}
	
	SpiderJoint(int servoId)
	{
		this.servoId = servoId;
		this.angle   = 0.0;
		this.offset  = 0;
		this.range   = 300;
	}
	
	SpiderJoint(double angle)
	{
		this.servoId = 0;
		this.angle   = angle;
		this.offset  = 0;
		this.range   = 300;
	}
	
	SpiderJoint(int servoId, double angle)
	{
		this.servoId = servoId;
		this.angle   = angle;
		this.offset  = 0;
		this.range   = 300;
	}
	
	SpiderJoint(int servoId, double angle, int offset)
	{
		this.servoId = servoId;
		this.angle   = angle;
		this.offset  = offset;
		this.range   = 300;
	}
	
	SpiderJoint(int servoId, double angle, int offset, int range)
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
		return mapPosition(angle, MIN_ANGLE, MAX_ANGLE, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	void setAngle(double angle)
	{
		double val = Math.toDegrees(angle);
		if (val > range ) val = range;
		if (val < offset) val = offset;
		this.angle = val;
	}
	
	private int mapPosition(double x, double in_min, double in_max, double out_min, double out_max)
	{
		if (x > in_max || x < in_min) throw new IllegalArgumentException("Input not between min and max");
		return (int)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
}
}
