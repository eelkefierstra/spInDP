package com.nhl.spindp.spin;

class SpiderLeg implements Runnable
{
	private static final double A       =  80.0;
	private static final double A_MAX   =  90.0;
	private static final double A_RAD   = Math.toRadians(A_MAX / 2.0);
	private static final double C       = 160.0;
	private static final double E       = 90.0;
	private static final double F       = 35.0;
	private static final double L       = 127.0;
	private static final double LACCENT = Math.cos(A_RAD) * L;
	private static final double D       = F - LACCENT;
	private static final double B       = Math.sqrt(Math.pow(D, 2.0) + Math.pow(E, 2));
	private static final double ALPHA   = Math.toRadians(Math.acos((Math.pow(A, 2) - Math.pow(C, 2) - Math.pow(B, 2)) / (-2 * C * B)));
	private static final double GAMMA   = Math.toRadians(Math.acos((Math.pow(C, 2.0) - Math.pow(B, 2.0) - Math.pow(A, 2.0)) / (-2 * B * A)));
	private static final double BETA    = Math.toRadians(Math.acos((Math.pow(B, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
	private static final double EPSILON = Math.toRadians(Math.atan(E / D));
	private static final double DELTA   = Math.toRadians(Math.atan(D / E));
	
	private double coxaAngle  = 0.0;
	private double femurAngle = 0.0;
	private double tibiaAngle = 0.0;
	
	public double coxaChange = 0.0;
	
	SpiderJoint[] servos = new SpiderJoint[3];
	
	public SpiderLeg()
	{
		for (byte i = 0; i <= SpiderJoint.MAX; i++)
		{
			servos[i] = new SpiderJoint();
		}
	}
	
	@Override
	public void run()
	{
		coxaAngle  = Math.abs(coxaChange - (.5 * A_MAX));
		double lAccent = LACCENT / Math.cos(Math.toDegrees(coxaAngle));
		double d = lAccent - F;
		double b = Math.sqrt(Math.pow(d, 2.0) + Math.pow(E, 2.0));
		femurAngle = Math.toRadians(Math.acos(Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A));
		tibiaAngle = Math.toRadians(Math.acos(Math.pow(b, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C));
	}
	
	private class SpiderJoint
	{
		private static final int COXA = 0;
		private static final int FEMUR = 1;
		private static final int TIBIA = 2;
		private static final int MAX   = TIBIA;
		
		private SpiderJoint()
		{
			
		}
	}

}
