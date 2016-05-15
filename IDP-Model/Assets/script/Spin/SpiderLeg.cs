﻿using System;

public class SpiderLeg
{
	private static readonly double A       =  80.0;
	private static readonly double A_MAX   =  90.0;
	private static readonly double A_RAD   = (A_MAX / 2.0).ToRadians();
	private static readonly double C       = 160.0;
	private static readonly double E       =  90.0;
	private static readonly double F       =  35.0;
	private static readonly double L       = 127.0;
	private static readonly double LACCENT = Math.Cos(A_RAD) * L;
	private static readonly double D       = F - LACCENT;
	private static readonly double B       = Math.Sqrt(Math.Pow(D, 2.0) + Math.Pow(E, 2));
    private static double PAR_X = 25;
    private static double PAR_Y = PAR_X / Math.Pow(Math.Sqrt(Math.Pow(L, 2.0) - Math.Pow(LACCENT, 2.0)) * 2, 2.0);

    private Object locker  = new Object();
    private double alpha   = Math.Acos((Math.Pow(A, 2) - Math.Pow(C, 2) - Math.Pow(B, 2)) / (-2 * C * B)).ToRadians();
	private double gamma   = Math.Acos((Math.Pow(C, 2.0) - Math.Pow(B, 2.0) - Math.Pow(A, 2.0)) / (-2 * B * A)).ToRadians();
	private double beta    = Math.Acos((Math.Pow(B, 2.0) - Math.Pow(A, 2.0) - Math.Pow(C, 2.0)) / (-2 * A * C)).ToRadians();
	private double EPSILON = Math.Atan(E / D).ToRadians();
	private double DELTA   = Math.Atan(D / E).ToRadians();
	private double step    = 0.0;
	private bool set       = false;
    
	public double coxaChange = 0.0;

	SpiderJoint[] servos = new SpiderJoint[3];

	internal SpiderLeg()
	{
		servos[SpiderJoint.COXA ] = new SpiderJoint(alpha);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(gamma);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(beta);
	}

	internal SpiderLeg(int startServoId)
	{

		servos[SpiderJoint.COXA ] = new SpiderJoint(startServoId++, alpha);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(startServoId++, gamma);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(startServoId++, beta);
	}

	public void run()
	{
        lock (locker)
        {
            servos[SpiderJoint.COXA].setAngle(alpha = Math.Abs(coxaChange - (.5 * A_MAX)).ToRadians());
            double lAccent = LACCENT / Math.Cos(alpha);
            double d = lAccent - F;
            double h = 0;
            step = Math.Abs(Math.Sqrt(Math.Pow(lAccent, 2.0) - Math.Pow(LACCENT, 2.0)));
            if (coxaChange < 45) step *= -1;
            if (set) h = (PAR_Y * -1) * Math.Pow(step, 2.0) + PAR_X;
            double b = Math.Sqrt(Math.Pow(d, 2.0) + Math.Pow(E - h, 2.0));
            double test1 = Math.Pow(C, 2.0), test2 = Math.Pow(b, 2.0), test3 = Math.Pow(A, 2.0), test4 = Math.Acos((test1 - test2 - test3) / (-2 * b * A));
            servos[SpiderJoint.FEMUR].setAngle(gamma = test4);//Math.Acos((Math.Pow(C, 2.0) - Math.Pow(b, 2.0) - Math.Pow(A, 2.0)) / (-2 * b * A)));
            servos[SpiderJoint.TIBIA].setAngle(beta = Math.Acos((Math.Pow(b, 2.0) - Math.Pow(A, 2.0) - Math.Pow(C, 2.0)) / (-2 * A * C)));
            if (coxaChange >= 90) set = false;
            if (coxaChange <= 0) set = true;
            //spider.GetComponent<walk>().moveSelectedLegg(servos[SpiderJoint.COXA].getId(), (float)gamma, (float)alpha, (float)beta);
        }
    }

	internal int[] getIds()
	{
		return new int[] { servos[0].getId(), servos[1].getId(), servos[2].getId() };
	}

    internal int getFirstId()
    {
        return servos[0].getId();
    }

    internal double[] getLegAngles()
    {
        lock (locker)
            return new double[] { servos[0].getAngle(), servos[1].getAngle(), servos[2].getAngle() };
    }

	internal int[] getAngles()
	{
        lock (locker)
            return new int[] { servos[0].getServoAngle(), servos[1].getServoAngle(), servos[2].getServoAngle() };
	}

	public double getAngle(int servo)
	{
		return servos[servo].getServoAngle();
	}
}
