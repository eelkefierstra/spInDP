package com.nhl.spindp.spin;

public class SpiderJoint
{
	public static final int COXA  = 0;
	public static final int FEMUR = 1;
	public static final int TIBIA = 2;
	
	private int servoId;
	private double angle;
	
	SpiderJoint()
	{
		this.servoId = 0;
		this.angle   = 0.0;
	}
	
	SpiderJoint(int servoId)
	{
		this.servoId = servoId;
		this.angle   = 0.0;
	}
	
	SpiderJoint(double angle)
	{
		this.servoId = 0;
		this.angle   = angle;
	}
	
	SpiderJoint(int servoId, double angle)
	{
		this.servoId = servoId;
		this.angle   = angle;
	}
	
	public int getId()
	{
		return servoId;
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
