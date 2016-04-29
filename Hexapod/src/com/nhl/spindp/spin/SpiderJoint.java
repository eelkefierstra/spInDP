package com.nhl.spindp.spin;

public class SpiderJoint
{
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
		return (int)(angle + offset);
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	void setAngle(double angle)
	{
		this.angle = angle;
	}
}
