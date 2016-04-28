package com.nhl.spindp.spin;

public class SpiderJoint
{
	public static final int COXA  = 0;
	public static final int FEMUR = 1;
	public static final int TIBIA = 2;
	
	private double angle;
	
	public SpiderJoint()
	{
		angle = 0.0;
	}
	
	public SpiderJoint(double angle)
	{
		this.angle = angle;
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public void setAngle(double angle)
	{
		this.angle = angle;
	}
}
