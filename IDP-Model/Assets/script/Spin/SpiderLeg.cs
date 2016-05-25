using Executors;
using System;

public class SpiderLeg : ICallable<object>
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
    private readonly double small_l = Math.Cos(A_RAD)*L;
    private double total_step;
	public double coxaChange = 0.0;
    double speed     = 0.0;
    double direction = 6.0;
    
    // bocht
    private static readonly double Length = 300;
    private static readonly double Width = 80;
    private static readonly double R = 400;
    private  double h;
    private  double b;
    private  double r4;
    private  double I;
    private  double II;
    private  double l4;
    private  double a;
    private  double gamma_a;
    private  double alpha_a;
    private  double beta_a;
    private  double gamma_b;
    private  double alpha_b;
    private  double beta_b;
    private  double beta_RV;
    private  double servoAngle;
    private  double laccent = LACCENT;
    private  double b_turn;
    private  double servoAngle_rv;
    private  double betaD1;
    private  double betaD2;
    private  double test1; //TODO: need name still
    private double B_MAX;
   


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
    public object Call()
    {
        if (speed > 0.0)
            forward();
        
        return new object();
    }
    /// <summary>
    /// Main method for making a turn
    /// </summary>
    public void turn()
    {
        
        switch (getFirstId() / 3)
        {
            case 0:
                //RV (leidend)                

                pootRVA();
                servoAngle_rv = 100;
                servoAngle = servoAngle_rv;
                gamma = servoAngle + gamma_a;
                if (Double.IsNaN(gamma))
                    UnityEngine.Debug.Log("gammaRV");
                alpha = Math.Asin((Math.Sin(gamma.ToRadians()) * l4) / r4).ToDegrees();
                if (Double.IsNaN(alpha))
                    UnityEngine.Debug.Log("alphaRV");
                beta = 180 - gamma - alpha;
                beta_RV = beta;
                
                laccent = (r4 * Math.Sin(beta.ToRadians())) / Math.Sin(gamma.ToRadians());
                b_turn = beta_a - beta;
                break;
            case 1:
                //RM
                pootRM();                
                beta = beta_a - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * l4 * r4 * Math.Cos(beta.ToRadians()));
                alpha = Math.Asin((l4 * Math.Sin(beta.ToRadians())) / laccent).ToDegrees();
                if (Double.IsNaN(alpha))
                    UnityEngine.Debug.Log("alphaRM");
                gamma = 180 - alpha - beta;
                servoAngle = gamma - 135;                
                break;              
                
            case 2:
                //RA
                pootRVA();
                servoAngle = servoAngle_rv;
                gamma = gamma_a + (90 - servoAngle);
                alpha = Math.Asin((Math.Sin(gamma.ToRadians()) * l4) / r4).ToDegrees();
                if (Double.IsNaN(alpha))
                    UnityEngine.Debug.Log("alphaRA");
                beta = 180 - gamma - alpha;
                laccent = (r4 * Math.Sin(beta.ToRadians())) / Math.Sin(gamma.ToRadians());
                break;

            case 3:
                //LV
                pootLVA();
                servoAngle = servoAngle_rv;
                beta = servoAngle + beta_a;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));
                alpha = 180 - Math.Asin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees();
                gamma = 180 - alpha - beta;
                                
                break;
            case 4:
                //LM
                pootLM();
                beta = beta_a - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));
                alpha = 180 - Math.Asin((l4 * Math.Sin(beta.ToRadians())) / laccent).ToDegrees();
                gamma = 180 - alpha - beta;
                servoAngle = gamma + 45;   
                                
                break;
            case 5:
                //LA
                pootLVA();
                beta = beta_b - b_turn;
                laccent = Math.Sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));                
                if ((180 + Math.Asin(Math.Sin((beta.ToRadians()) * l4) / laccent).ToDegrees()) > 180)                   
                    alpha = -(180 + (Math.Asin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees())) + 360;
                else
                    alpha = (180 + (Math.Asin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToDegrees())) + 360;
                gamma = 180 - alpha - Math.Abs(beta);
                if (beta < 0)
                    servoAngle = gamma_b + gamma;
                else
                    servoAngle = gamma_b - gamma;   
                break;
            default:
                throw new InvalidOperationException();
        }

        UnityEngine.Debug.Log("id"+getFirstId() / 3+",c:"+(int)gamma + ",a:" + (int)alpha + ",b:" + (int)beta);
        servos[SpiderJoint.COXA].setAngle(gamma.ToRadians());
        servos[SpiderJoint.FEMUR].setAngle(alpha.ToRadians());
        servos[SpiderJoint.TIBIA].setAngle(beta.ToRadians());
     

    }

    public void setServo()
    {
        
    }
    public void getServo(int id)
    {  
         

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
        h = 0.5f * Length - 0.5f * (Math.Sqrt(L * L - small_l* small_l) * 2);
        b = small_l + R + 0.5f * Width;
        r4 = Math.Sqrt(h * h + b * b);        
        I = 0.5f * Length;
        II = R + 0.5f * Width;
        l4 = Math.Sqrt(I * I + II * II);
        a = Math.Atan(II / I).ToDegrees();        
        gamma_a = 180 - (A_MAX / 2) + (90 - a); //(180 - A_MAX) / 2 + 90 + (90 - a);
        alpha_a = Math.Asin((Math.Sin(gamma_a.ToRadians()) * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        gamma_b = a + ((180 - A_MAX) / 2);
        alpha_b = Math.Asin((Math.Sin(gamma_b.ToRadians()) * l4) / r4).ToDegrees();
        beta_b = 180 - alpha_b - gamma_b;
        B_MAX = beta_b + beta_a;
    }
    
    public void pootLVA()
    {
        // Links voor en achter
        h = 0.5f * Length + 0.5f * (Math.Sqrt(L * L - small_l * small_l) * 2);
        b = R - 0.5f * Width - small_l;
        r4 = Math.Sqrt(h * h + b * b);
        I = 0.5f * Length;
        II = R - 0.5f * Width;
        l4 = Math.Sqrt(I * I + II * II);
        a = Math.Atan(II / I).ToDegrees();
        gamma_a = (A_MAX / 2) + (90 - a);
        alpha_a = 180 - Math.Asin((Math.Sin(gamma_a.ToRadians()) * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        gamma_b = a - ((180 - A_MAX) / 2);
        alpha_b = 180 - Math.Asin((Math.Sin(gamma_b.ToRadians()) * l4) / r4).ToDegrees();
        beta_b = 180 - alpha_b - gamma_b;
        B_MAX = beta_b + beta_a;        
    }
    public void pootLM()
    {
        // links mid
        l4 = R - ((3f / 2f) * Width);
        r4 = Math.Sqrt((l4 * l4 + L * L) - 2 * l4 * L * Math.Cos((A_MAX / 2).ToRadians()));        
        gamma_a = (A_MAX / 2);
        alpha_a = 180 - Math.Asin((Math.Sin(gamma_a.ToRadians()) * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        B_MAX = 2 * beta_a;
    }
    public void pootRM()
    {
        // rechts mid
        l4 = R + ((3f / 2f) * Width);
        r4 = l4 + small_l;        
        gamma_a = 90 + (180 - A_MAX) / 2;
        alpha_a = Math.Asin((Math.Sin(gamma_a.ToRadians()) * l4) / r4).ToDegrees();
        beta_a = 180 - alpha_a - gamma_a;
        B_MAX = 2 * beta_a;        
    }   
    /// <summary>
    /// Main method for turning around his Y axis
    /// </summary>
    public void noscope360()
    {
        //RV
        VA360();
        servoAngle = 5;
        gamma = 360 - (Math.Atan((0.5f * Length) / (0.5f * Width)).ToRadians() + (135 + servoAngle));
        alpha = Math.Asin((Math.Sin(gamma.ToRadians()) * l4) / r4).ToRadians();                  
        beta = 180 - gamma - alpha;
        laccent = (r4 * Math.Sin(beta * (Math.PI / 180))) / (Math.Sin(gamma * (Math.PI / 180)));
        b_turn = beta - beta_b;
        laccent = (r4 * Math.Sin(beta.ToRadians())) / (Math.Sin(gamma.ToRadians()));


        //LV
        VA360();
        beta = beta_a - b_turn;
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToRadians();

        gamma = 180 - alpha - Length;
        servoAngle = gamma - test1;     
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //RM
        M360();        
        beta = beta_a - b_turn;
        alpha = Math.Asin((Math.Sin(beta.ToRadians()) * l4) / Length).ToRadians();
        gamma = 180 - alpha - beta;
        servoAngle = gamma - 135;  
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //LM
        M360();
        beta = beta_a - b_turn;
        alpha = Math.Asin((Math.Sin(beta.ToRadians()) * l4) / Length).ToRadians();        
        gamma = 180 - alpha - beta;
        servoAngle = gamma - 135;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //RA
        VA360();
        beta = beta_a - b_turn;
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToRadians();
        gamma = 180 - alpha - Length;
        servoAngle = gamma - test1;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

        //LA
        VA360();
        alpha = Math.Sin((Math.Sin(beta.ToRadians()) * l4) / laccent).ToRadians();
        beta = beta_b + b_turn;
        gamma = 180 - Math.Abs(alpha) - Math.Abs(beta);        
        servoAngle = test1 - gamma;
        laccent = Math.Sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.Cos(beta.ToRadians()));

    }
    public void VA360()
    {
        l4 = Math.Sqrt(Math.Pow((0.5f * Width), 2) + Math.Pow((0.5f * Length), 2));        
        I = 0.5f * Length + Math.Sin(45.ToRadians()) * (L / 2); //TODO: right name?
        II = 0.5f * Width + Math.Sin(45.ToRadians()) * (L / 2); //TODO: right name? 
        r4 = Math.Sqrt(I * I + II * II);
        beta_a = Math.Acos((L * L - l4 * l4 - r4 * r4) / (-2 * r4 * l4)).ToRadians();
        test1 = 180 - Math.Asin((r4 * Math.Sin(beta_a.ToRadians())) / L).ToRadians();//TODO: right name!
        beta_b = Math.Acos((Math.Pow((L / 2), 2) - r4 * r4 - l4 * l4) / (-2 * r4 * l4)).ToRadians();
        double test2 = 180 - Math.Asin(r4 * Math.Sin(beta_b.ToRadians()) / (L / 2)).ToRadians();
        beta = beta_a - beta_b;
        gamma = test2 - test1;
    }
    public void M360()
    {
        l4 = (3 / 2) * l4;
        r4 = Math.Sqrt(l4 * l4 + L * L - 2 * l4 * L * Math.Cos(180 - (90 - 0.5f * A_MAX).ToRadians()));
        gamma_a = 180 - (90 - 0.5f * A_MAX);
        beta_a = Math.Asin((Math.Sin(gamma_a.ToRadians()) * L) / r4);
        beta = beta_a * 2;                
    }
    /// <summary>
    /// Main method for walking forward
    /// </summary>
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
