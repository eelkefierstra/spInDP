package com.nhl.spindp.spin;

import java.util.concurrent.Future;

import com.nhl.spindp.LedStrip;
import com.nhl.spindp.Utils;

public class SpiderJoint
{
	private static final double MIN_ANGLE       =    0.0;
	private static final double MAX_ANGLE       =  300.0;
	private static final double MIN_SERVO_ANGLE =    0.0;
	private static final double MAX_SERVO_ANGLE = 1023.0;
	
	static final int COXA  = 0;
	static final int FEMUR = 1;
	static final int TIBIA = 2;
	
	private byte servoId;
	private double angle;
	private Future<byte[]> future;
	private final int offset;
	private final int lowerRange;
	private final int upperRange;
	
	SpiderJoint(byte servoId)
	{
		this(servoId, 0.0);
	}
	
	SpiderJoint(byte servoId, double angle)
	{
		this(servoId, angle, 0);
	}
	
	SpiderJoint(byte servoId, double angle, int offset)
	{
		this(servoId, angle, offset, 0, 300);
	}
	
	SpiderJoint(byte servoId, double angle, int offset, int lowerRange, int upperRange)
	{
		this.servoId    = servoId;
		this.angle      = angle;
		this.offset     = offset;
		this.lowerRange = lowerRange;
		this.upperRange = upperRange;
	}
	
	byte getId()
	{
		return servoId;
	}
	
	void setServoAngle(short pos)
	{
		Utils.map(pos, 0, 1023, 0, 300);
	}
	
	short getServoAngle()
	{
		return Utils.mapServoPosition(angle, MIN_ANGLE, MAX_ANGLE, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
	}
	
	double getAngle()
	{
		return angle;
	}
	
	Future<byte[]> getFuture()
	{
		return future;
	}
	
	void setAngle(double angle)
	{
		double val = Math.toDegrees(angle);// + offset;
		/*if (servoId == 2 || servoId == 3)
			System.out.println();*/
		if (servoId >= 10 && servoId % 3 == 1)
		{
			val = 200 - val;
		}
		else if (servoId % 3 == 2)
		{
			val = offset - val;
		}
		else
			val += offset;
		if (Double.isNaN(val)) 
		{
			LedStrip.throwError();
			//throw new IllegalArgumentException("angle must not be NaN");
		}
		else
		{
			if (val > upperRange)
            {
                System.err.println("val of servo " + servoId + " clamped");
                val = upperRange;
                LedStrip.throwError();
            }
			else if (val < lowerRange)
            {
            	System.err.println("val of servo " + servoId + " clamped");
                val = lowerRange;
                LedStrip.throwError();
            }
			this.angle = val;
		}
		//future = Main.submitInstruction(Servo.createMoveServoInstruction(getId(), getServoAngle()));
	}
	
	@Override
	public String toString()
	{
		return "Id: " + String.valueOf(servoId) + 
				" angle: " + String.valueOf(angle) + 
				" offset: " + String.valueOf(lowerRange) + 
				" range: " + String.valueOf(upperRange);
	}
}
