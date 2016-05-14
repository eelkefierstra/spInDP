package com.nhl.spindp.spin;

class SpiderLeg implements Runnable
{
	private static final double A        =  80.0;
	private static final double A_MAX    =  90.0;
	private static final double A_RAD    = Math.toRadians(A_MAX / 2.0);
	private static final double C        = 160.0;
	private static final double E        =  90.0;
	private static final double F        =  35.0;
	private static final double L        = 127.0;
	private static final double LACCENT  = Math.cos(A_RAD) * L;
	private static final double D        = F - LACCENT;
	private static final double B        = Math.sqrt(Math.pow(D, 2.0) + Math.pow(E, 2));
	private static volatile double PAR_X = 25;
	private static volatile double PAR_Y = PAR_X / Math.pow(Math.sqrt(Math.pow(L, 2.0) - Math.pow(LACCENT, 2.0)) * 2, 2.0);
	
	private double alpha   = Math.toRadians(Math.acos((Math.pow(A, 2) - Math.pow(C, 2) - Math.pow(B, 2)) / (-2 * C * B)));
	private double gamma   = Math.toRadians(Math.acos((Math.pow(C, 2.0) - Math.pow(B, 2.0) - Math.pow(A, 2.0)) / (-2 * B * A)));
	private double beta    = Math.toRadians(Math.acos((Math.pow(B, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
	private double EPSILON = Math.toRadians(Math.atan(E / D));
	private double DELTA   = Math.toRadians(Math.atan(D / E));
	private double step    = 0.0;
	private boolean set    = false;
	
	public double coxaChange = 0.0;
	
	SpiderJoint[] servos = new SpiderJoint[3];
		
	SpiderLeg(int startServoId)
	{
		servos[SpiderJoint.COXA ] = new SpiderJoint(startServoId++, alpha);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(startServoId++, gamma);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(startServoId++, beta);
	}
	
	@Override
	public void run()
	{
		servos[SpiderJoint.COXA ].setAngle(alpha  = Math.toRadians(Math.abs(coxaChange - (.5 * A_MAX))));
		double lAccent = LACCENT / Math.cos(alpha);
		double d = lAccent - F;
		double h = 0;
		step = Math.abs(Math.sqrt(Math.pow(lAccent, 2.0) - Math.pow(LACCENT, 2.0)));
		if (coxaChange < 45) step *= -1;
		if (set ) h = (PAR_Y * -1) * Math.pow(step, 2.0) + PAR_X;
		double b = Math.sqrt(Math.pow(d, 2.0) + Math.pow(E - h, 2.0));
		//double test1 = Math.pow(C, 2.0), test2 = Math.pow(b, 2.0), test3 = Math.pow(A, 2.0), test4 = Math.acos((test1 - test2 - test3) / (-2 * b * A));
		servos[SpiderJoint.FEMUR].setAngle(gamma = Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		servos[SpiderJoint.TIBIA].setAngle(beta  = Math.acos((Math.pow(b, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
		if (coxaChange >= 90) set = false;
		if (coxaChange <= 0) set = true;
	}
	
	int[] getIds()
	{
		return new int[] { servos[0].getId(), servos[1].getId(), servos[2].getId() };
	}
	
	int[] getAngles()
	{
		return new int[] { servos[0].getServoAngle(), servos[1].getServoAngle(), servos[2].getServoAngle() };
	}
	
	public double getAngle(int servo)
	{
		return servos[servo].getServoAngle();
	}
}
