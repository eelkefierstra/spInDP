using System;

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
    private static double PAR_X            = 25;
    private static double PAR_Y            = PAR_X / Math.Pow(Math.Sqrt(Math.Pow(L, 2.0) - Math.Pow(LACCENT, 2.0)) * 2, 2.0);

    private double alpha   = Math.Acos((Math.Pow(A, 2) - Math.Pow(C, 2) - Math.Pow(B, 2)) / (-2 * C * B)).ToRadians();
	private double gamma   = Math.Acos((Math.Pow(C, 2.0) - Math.Pow(B, 2.0) - Math.Pow(A, 2.0)) / (-2 * B * A)).ToRadians();
	private double beta    = Math.Acos((Math.Pow(B, 2.0) - Math.Pow(A, 2.0) - Math.Pow(C, 2.0)) / (-2 * A * C)).ToRadians();
	private double EPSILON = Math.Atan(E / D).ToRadians();
	private double DELTA   = Math.Atan(D / E).ToRadians();
	private double step    = 0.0;
	public bool set        = false;
    
	public double coxaChange = 0.0;
    double speed     = 1.0;
    double direction = 1.0;
    // TODO: change variable names to english and camelCase
    // bocht
    private static readonly double Lengte = 300;
    private static readonly double Breedte = 80;
    private double R;
    private double h;
    private double b;
    private double r4;
    private double I;
    private double II;
    private double l4;
    private double a;
    private double gamma_a;
    private double alpha_a;
    private double beta_a;
    private double gamma_b;
    private double alpha_b;
    private double beta_b;
    private double beta_RV;
    private double servohoek;
    private double laccent;
    private double b_turn;
    private double servohoek_rv;
    private double betaD1;
    private double betaD2;
    private double test1; //TODO: need name still


    SpiderJoint[] servos = new SpiderJoint[3];

	internal SpiderLeg(int startServoId)
	{
        if (startServoId % 2 == 0) coxaChange = 90.0;
		servos[SpiderJoint.COXA ] = new SpiderJoint(startServoId++, alpha, 100);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(startServoId++, gamma, 75);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(startServoId++, beta, 175);
	}

    /// <summary>
    /// Stand in for Runnable.run() from java. Expect this block to run asynchronously.
    /// </summary>
    public void run()
    {
        if (speed > 0.0)
            forward();
        else if (!direction.IsBetweenII(-.25, .25))
            turn();
        else //360
            noscope360();

    }

    public void turn()
    {
        switch (getFirstId() / 3)
        {
            case 0:
                //RV (leidend)        
                pootRVA();
                servohoek_rv = 0;
                gamma = servohoek + gamma_a;
                alpha = Math.Sinh(Math.Sin(gamma.ToRadians() * l4) / r4).ToDegrees();
                beta = 180 - gamma - alpha;
                beta_RV = beta;
                servohoek = servohoek_rv;
                laccent = (r4 * Math.Sin(beta.ToRadians())) / Math.Sin(gamma.ToRadians());
                b_turn = beta - beta_a;
                break;
            case 1:
                //LV
                pootLVA();
                beta = servohoek + beta_a;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));
                alpha = 180 - Math.Sinh(Math.Sin(beta.ToRadians() * l4) / laccent).ToDegrees();
                gamma = 180 - alpha - beta;
                servohoek = 0;
                break;
            case 2:
                //RM
                pootRM();
                servohoek = gamma - 135;
                gamma = 180 - alpha - beta;
                alpha = Math.Sinh((r4 * Math.Sin(beta.ToRadians())) / laccent).ToRadians();
                beta = beta_a - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * l4 * r4 * Math.Cos(beta.ToRadians()));
                break;
            case 3:
            //LM
                pootLM();
                servohoek = gamma + 45;
                gamma = 180 - alpha - beta;
                alpha = 180 - Math.Sinh((l4 * Math.Sin(beta.ToRadians())) / laccent).ToDegrees();
                beta = beta_a - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));
                break;
            case 4:
                //RA
                pootRVA();
                servohoek = servohoek_rv;
                gamma = gamma_a + (90 - servohoek);
                alpha = Math.Sinh((Math.Sin(gamma.ToRadians()) * l4) / r4).ToDegrees();
                beta = 180 - gamma - alpha;
                laccent = (r4 * Math.Sin(beta.ToRadians())) / Math.Sin(gamma.ToRadians());
                break;
            case 5:
                //LA
                pootLVA();

                if (beta < 0)
                    servohoek = gamma_a + gamma;
                else
                    servohoek = gamma_a - gamma;
                gamma = 180 - alpha - Math.Abs(beta);
                if (180 + Math.Sinh(Math.Sin(beta.ToRadians()) * l4) / laccent > 180)
                    alpha = -(180 + (Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees())) + 360;
                else
                    alpha = (180 + (Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees())) + 360;
                beta = beta_b - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));
                break;
            default:
                throw new InvalidOperationException();
        }
    }

    public void smallBetaTurn()
    {
        /*
        if (beta_LVA < beta_LM)
            betaD1 = beta_LVA;
        else
            betaD1 = beta_LM;
        if (beta_RVA < beta_RM)
            betaD2 = beta_RVA;
        else
            betaD2 = beta_RM;

        double smallestBeta;

        if (betaD1 < betaD2)
            smallestBeta = betaD1;
        else
            smallestBeta = betaD2;
            */
    }
    public void pootRVA()
    {
        // Rechts voor en achter
        h = 0.5f * Breedte - 0.5f * step;
        b = R + 0.5f * Breedte + LACCENT;
        r4 = Math.Sqrt(h * h + b * b);
        I = 0.5f * Lengte;
        II = R - 0.5f * Breedte;
        l4 = Math.Sqrt(I * I + II * II);
        a = Math.Tanh((II / I).ToDegrees());
        gamma_a = 180 - (A_MAX / 2) + (90 - a); //(180 - A_MAX) / 2 + 90 + (90 - a);
        alpha_a = 180 - Math.Sinh(Math.Sin(gamma_a.ToRadians() * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        gamma_b = a - ((180 - A_MAX) / 2);
        alpha_b = 180 - Math.Sinh(Math.Sin(gamma_b.ToRadians() * l4) / r4).ToDegrees();
        beta_b = 180 - alpha_b - gamma_b;
        beta = beta_b + beta_a;
    }
    
    public void pootLVA()
    {
        // Links voor en achter
        h = 0.5f * Breedte + 0.5f *step;
        b = R - 0.5f * Breedte - LACCENT;
        r4 = Math.Sqrt(h * h + b * b);
        I = 0.5f * Lengte;
        II = R - 0.5f * Breedte;
        l4 = Math.Sqrt(I * I + II * II);
        a = Math.Tanh((II / I).ToDegrees());
        gamma_a = (A_MAX / 2) + (90 - a);
        alpha_a = 180 - Math.Sinh(Math.Sin(gamma_a.ToRadians() * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        gamma_b = a - ((180 - A_MAX) / 2);
        alpha_b = 180 - Math.Sinh(Math.Sin(gamma_b.ToRadians() * l4) / r4).ToDegrees();
        beta_b = 180 - alpha_b - gamma_b;
        beta = beta_b + beta_a;        
    }
    public void pootLM()
    {
        // links mid
        l4 = R - ((3 / 2) * Breedte);
        r4 = Math.Sqrt(l4 * l4 + L * L) - 2 * l4 * L * Math.Cos((A_MAX / 2).ToRadians());
        gamma_a = (A_MAX / 2);
        alpha_a = 180 - Math.Sinh(Math.Sin(gamma_a.ToRadians() * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        beta = 2 * beta_a;
    }
    public void pootRM()
    {
        // rechts mid
        l4 = R - ((3 / 2) * Breedte);
        r4 = Math.Sqrt(l4 * l4 + L * L) - 2 * l4 * L * Math.Cos((A_MAX / 2).ToRadians());
        gamma_a = (A_MAX / 2);
        alpha_a = 180 - Math.Sinh(Math.Sin(gamma_a.ToRadians() * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        beta = 2 * beta_a;        
    }   
    public void noscope360()
    {
        //RV
        VA360();
        servohoek = 5;
        gamma = 360 - (Math.Tanh((0.5f * Lengte) / (0.5f * Breedte)).ToDegrees() + (135 + servohoek));
        alpha = Math.Sinh((Math.Sin(gamma.ToRadians()) * l4) / r4).ToDegrees();
        beta = 180 - gamma - alpha;
        laccent = (r4 * Math.Sin(beta.ToRadians())) / (Math.Sin(gamma.ToRadians()));

        //LV
        VA360();
        beta = beta_a - b_turn;
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees();
        gamma = 180 - alpha - Lengte;
        servohoek = gamma - test1;     
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //RM
        M360();        
        beta = beta_a - b_turn;
        alpha = Math.Sinh((Math.Sin(beta.ToRadians()) * l4) / Lengte).ToDegrees();
        gamma = 180 - alpha - beta;
        servohoek = gamma - 135;  
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //LM
        M360();
        beta = beta_a - b_turn;
        alpha = Math.Sinh((Math.Sin(beta.ToRadians()) * l4) / Lengte).ToDegrees();        
        gamma = 180 - alpha - beta;
        servohoek = gamma - 135;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //RA
        VA360();
        beta = beta_a - b_turn;
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees();
        gamma = 180 - alpha - Lengte;
        servohoek = gamma - test1;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //LA
        VA360();
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees();
        beta = beta_b + b_turn;
        gamma = 180 - Math.Abs(alpha) - Math.Abs(beta);        
        servohoek = test1 - gamma;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

    }
    public void VA360()
    {
        l4 = Math.Sqrt(Math.Pow((0.5f * Breedte), 2) + Math.Pow((0.5f * Lengte), 2));        
        I = 0.5f * Lengte + Math.Sin(45.ToRadians()) * (L / 2); //TODO: right name?
        II = 0.5f * Breedte + Math.Sin(45.ToRadians()) * (L / 2); //TODO: right name? 
        r4 = Math.Sqrt(I * I + II * II);
        beta_a = Math.Cosh((L * L - l4 * l4 - r4 * r4) / (-2 * r4 * l4)).ToDegrees();
        test1 = 180 - Math.Sinh((r4 * Math.Sin(beta_a.ToRadians())) / L).ToDegrees();//TODO: right name!
        beta_b = Math.Cosh((Math.Pow((L / 2), 2) - r4 * r4 - l4 * l4) / (-2 * r4 * l4)).ToDegrees();
        double test2 = 180 - Math.Sinh(r4 * Math.Sin(beta_b.ToRadians()) / (L / 2)).ToDegrees();
        beta = beta_a - beta_b;
        gamma = test2 - test1;
    }
    public void M360()
    {
        l4 = (3 / 2) * l4;
        r4 = Math.Sqrt(l4 * l4 + L * L - 2 * l4 * L * Math.Cos(180 - (90 - 0.5f * A_MAX).ToRadians()));
        gamma_a = 180 - (90 - 0.5f * A_MAX);
        beta_a = Math.Sinh((Math.Sin(gamma_a.ToRadians()) * L) / r4);
        beta = beta_a * 2;                
    }

    public void forward()
    {
        
        servos[SpiderJoint.COXA].setAngle(alpha = coxaChange.ToRadians());
        double lAccent = LACCENT / Math.Cos(alpha = Math.Abs(coxaChange - (.5 * A_MAX)).ToRadians());
        double d = lAccent - F;
        double h = 0;
        step = Math.Abs(Math.Sqrt(Math.Pow(lAccent, 2.0) - Math.Pow(LACCENT, 2.0)));
        if (coxaChange < 45) step *= -1;
        if (!set) h = (PAR_Y * -1) * Math.Pow(step, 2.0) + PAR_X;
        double b = Math.Sqrt(Math.Pow(d, 2.0) + Math.Pow(E - h, 2.0));
        //double test1 = Math.Pow(C, 2.0), test2 = Math.Pow(b, 2.0), test3 = Math.Pow(A, 2.0), test4 = Math.Acos((test1 - test2 - test3) / (-2 * b * A));
        servos[SpiderJoint.FEMUR].setAngle(gamma = Math.Acos((Math.Pow(C, 2.0) - Math.Pow(b, 2.0) - Math.Pow(A, 2.0)) / (-2 * b * A)));
        servos[SpiderJoint.TIBIA].setAngle(beta  = Math.Acos((Math.Pow(b, 2.0) - Math.Pow(A, 2.0) - Math.Pow(C, 2.0)) / (-2 * A * C)));
        if (coxaChange >= 90) set = true;
        if (coxaChange <= 0) set = false;
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
        //lock (locker)
            return new double[] { servos[0].getAngle(), servos[1].getAngle(), servos[2].getAngle() };
    }

	internal int[] getAngles()
	{
        //lock (locker)
            return new int[] { servos[0].getServoAngle(), servos[1].getServoAngle(), servos[2].getServoAngle() };
	}

	public double getAngle(int servo)
	{
		return servos[servo].getServoAngle();
	}
}
