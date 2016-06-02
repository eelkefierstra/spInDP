package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.nhl.spindp.Time;
import com.nhl.spindp.spin.SpiderBody.SharedParams;

class SpiderLeg implements Runnable
{
	private static final double Weigth   = 24.525;
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
	
	private ExecutorService executor;
	private Future<?> future;
	private SpiderBody.SharedParams sharedParams;
	
	private double alpha   = Math.toRadians(Math.acos((Math.pow(A, 2) - Math.pow(C, 2) - Math.pow(B, 2)) / (-2 * C * B)));
	private double gamma   = Math.toRadians(Math.acos((Math.pow(C, 2.0) - Math.pow(B, 2.0) - Math.pow(A, 2.0)) / (-2 * B * A)));
	private double beta    = Math.toRadians(Math.acos((Math.pow(B, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
	private double EPSILON = Math.toRadians(Math.atan(E / D));
	private double DELTA   = Math.toRadians(Math.atan(D / E));
	private double step    = 0.0;
	private boolean set     = false;
	
	private double coxaChange = 0.0;
	
	private double t_femur;
    private double t_tibia;
    private double t_coxa;

    private final double small_l = Math.cos(A_RAD)*L;
    // bocht
    private static final double Length = 300;
    private static final double Width = 80;
    private static final double R = 500;
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
    private static double beta_RV;
    private double servoAngle;
    private double laccent;
    //private static double b_turn;
    //private static double servoAngle_rv;
    private double betaD1;
    private double betaD2;
    private double test1; //TODO: need name still
    private double B_MAX;

    private double t_e = 90.0;
	
	SpiderJoint[] servos = new SpiderJoint[3];
		
	SpiderLeg(ExecutorService executor ,SharedParams sharedParams,int startServoId)
	{
		//coxaChange += 30 * (startServoId / 3);
		set = (startServoId % 2) == 0;
		if (startServoId % 2 == 0)
		{
			coxaChange = 85;
			set = !set;
		}
		if (coxaChange > 90)
		{
			coxaChange -= 90;
		}
		// 200, 75, 175
		this.executor = executor;
		this.sharedParams = sharedParams;
		servos[SpiderJoint.COXA ] = new SpiderJoint(startServoId++, alpha, 100);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(startServoId++, gamma);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(startServoId++, beta, 25);
	}
	
	public boolean walk(double forward, double right)
	{
		boolean res = false;
		if (Time.shouldSync())
			coxaChange = (int)coxaChange;
		if (forward == 0 && right == 0)
		{
			future = executor.submit(this);
			res = true;
		}
		else if (right <= -.25 || .25 <= right)
		{
			if (!set) coxaChange += ((35.0 * Time.deltaTime) * forward);
			if ( set) coxaChange -= ((35.0 * Time.deltaTime) * forward);
			turn(right);
		}
		else if (forward <= -.25 || .25 <= forward)
		{
			if (!set) coxaChange += ((35.0 * Time.deltaTime) * forward);
			if ( set) coxaChange -= ((35.0 * Time.deltaTime) * forward);
			future = executor.submit(this);
			res = true;
		}
		return res;
	}
	
	public Future<?> getFuture()
	{
		return future;
	}
	
	@Override
	public void run()
	{
		if (coxaChange >= 80)
		{
			set = true;
			coxaChange = 80;
		}
		if (coxaChange <= 10)
		{
			set = false;
			coxaChange = 10;
		}
		servos[SpiderJoint.COXA ].setAngle(alpha = Math.toRadians(coxaChange));
		double lAccent = LACCENT / Math.cos(alpha  = Math.toRadians(Math.abs(coxaChange - (.5 * A_MAX))));
		double d = lAccent - F;
		double h = 0;
		step = Math.abs(Math.sqrt(Math.pow(lAccent, 2.0) - Math.pow(LACCENT, 2.0)));
		if (coxaChange < 45) step *= -1;
		if (!set) h = (PAR_Y * -1) * Math.pow(step, 2.0) + PAR_X;
		double b = Math.sqrt(Math.pow(d, 2.0) + Math.pow(E + h, 2.0));
		double test1 = Math.pow(C, 2.0), test2 = Math.pow(b, 2.0), test3 = Math.pow(A, 2.0), test4 = Math.acos((test1 - test2 - test3) / (-2 * b * A));
		servos[SpiderJoint.FEMUR].setAngle(gamma = test4);//Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		servos[SpiderJoint.TIBIA].setAngle(beta  = Math.acos((Math.pow(b, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
	}
	/// <summary>
    /// Main method for making a turn
    /// </summary>
	private void turn(double right)
	{
		int id = getFirstId() / 3;
        double r = 500.0;
        
        // check if turn is right
       if(right > 0)
        {   // select the right id for a right turn
            if (id + 3 > 5)
                id -= 3; // 3 -> 0, 4 -> 1 , 5 -> 2
            else
                id += 3; // 0 -> 3, 1 -> 4, 2 -> 5      
        }

		switch (id)
		{
			case 0:
			case 2:
				// Rechts voor en achter
				h = 0.5f * Length - 0.5f * (Math.sqrt(L * L - small_l* small_l) * 2);
				b = small_l + r + 0.5f * Width;
				r4 = Math.sqrt(h * h + b * b);
				I = 0.5f * Length;
				II = r + 0.5f * Width;
				l4 = Math.sqrt(I * I + II * II);
				a = Math.toDegrees(Math.atan(II / I));
				gamma_a = 180 - (A_MAX / 2) + (90 - a); //(180 - A_MAX) / 2 + 90 + (90 - a);
				alpha_a = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				gamma_b = a + ((180 - A_MAX) / 2);
				alpha_b = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_b)) * l4) / r4));
				beta_b = 180 - alpha_b - gamma_b;
				B_MAX = beta_b + beta_a;
				break;
			case 1:
				// rechts mid
				l4 = r + ((3f / 2f) * Width);
				r4 = l4 + small_l;
				gamma_a = 90 + (180 - A_MAX) / 2;
				alpha_a = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				B_MAX = 2 * beta_a;
				break;
			case 3:
			case 5:
				// Links voor en achter
				h = 0.5f * Length + 0.5f * (Math.sqrt(L * L - small_l * small_l) * 2);
				b = r - 0.5f * Width - small_l;
				r4 = Math.sqrt(h * h + b * b);
				I = 0.5f * Length;
				II = r - 0.5f * Width;
				l4 = Math.sqrt(I * I + II * II);
				a = Math.toDegrees(Math.atan(II / I));
				gamma_a = (A_MAX / 2) + (90 - a);
				alpha_a = 180 - Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				gamma_b = a - ((180 - A_MAX) / 2);
				alpha_b = 180 - Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_b)) * l4) / r4));
				beta_b = 180 - alpha_b - gamma_b;
				B_MAX = beta_b + beta_a;
				break;
			case 4:
				// links mid
				l4 = r - ((3f / 2f) * Width);
				r4 = Math.sqrt((l4 * l4 + L * L) - 2 * l4 * L * Math.cos(Math.toRadians(A_MAX / 2)));
				gamma_a = (A_MAX / 2);
				alpha_a = 180 - Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				B_MAX = 2 * beta_a;
				break;
			default:
				throw new IllegalArgumentException();
		}

        if (servoAngle >= 85)
		{
			set = true;
            servoAngle = 85;
		}
		else if (servoAngle <= 5)
		{
			set = false;
            servoAngle = 5;
		}

        switch (id)
        {
            case 0:
                //RV (leidend)   
                servoAngle = sharedParams.servoAngle_rv = coxaChange;
                gamma = servoAngle + gamma_a;
                alpha = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma)) * l4) / r4));
                beta = 180 - gamma - alpha;
                beta_RV = beta;
                laccent = (r4 * Math.sin(Math.toRadians(beta))) / Math.sin(Math.toRadians(gamma));
                sharedParams.b_turn = beta_a - beta;
                break;
            case 1:
                //RM
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(r4 * r4 + l4 * l4 - 2 * l4 * r4 * Math.cos(Math.toRadians(beta)));
                alpha = Math.toDegrees(Math.asin((l4 * Math.sin(Math.toRadians(beta))) / laccent));
                gamma = 180 - alpha - beta;
                servoAngle = gamma - 135;
                break;
            case 2:
                //RA
                servoAngle = sharedParams.servoAngle_rv;
                gamma = gamma_a + (90 - servoAngle);
                alpha = Math.asin((Math.sin(gamma*(Math.PI/180)) * l4) / r4)*(180 / Math.PI);
                beta = 180 - gamma - alpha;
                laccent = (r4 * Math.sin(beta*(Math.PI/180))) / Math.sin(gamma*(Math.PI/180));
               // Debug.Log("x:" + (int)coxaChange + ", id" + getFirstId() / 3 + ", c:" + (int)gamma + ", a:" + (int)alpha + ", b:" + (int)beta);
                break;
            case 3:
                //LV                 
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                alpha = 180 - Math.asin((Math.sin(beta*(Math.PI/180)) * l4) / laccent)*(180 / Math.PI);
                gamma = 180 - alpha - beta;
                servoAngle = gamma_a - gamma;
                break;
            case 4:
                //LM
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                alpha = 180 - Math.asin((l4 * Math.sin(beta*(Math.PI/180))) / laccent)*(180 / Math.PI);
                gamma = 180 - alpha - beta;
                servoAngle = (-gamma) + 45;
                break;
            case 5:
                //LA
                beta = beta_b - sharedParams.b_turn;
                laccent = Math.sqrt(r4 * r4 + l4 * l4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                if ((180 + Math.asin((Math.sin(beta*(Math.PI/180)) * l4) / laccent)*(180 / Math.PI)) > 180)                   
                    alpha = -Math.toDegrees(180 + (Math.asin((Math.sin(Math.toRadians(beta) * l4) / laccent)))) + 360;
                else
                    alpha = (180 + Math.toDegrees(Math.asin((Math.sin(Math.toRadians(beta)) * l4) / laccent)));
                gamma = 180 - alpha - Math.abs(beta);
                if (beta < 0)
                    servoAngle = gamma_b + gamma;
                else
                    servoAngle = gamma_b - gamma;
                break;
        }
        // set right COXA, FEMUR and TIBIA
        turn2();
        if (id%2 != 0)
            servoAngle = 90 - servoAngle;
        // t_tibia += 145;
       //  t_femur += -40;
         //servoAngle = 0;
        servos[SpiderJoint.COXA].setAngle(Math.toRadians(servoAngle));
        if (Double.isNaN(t_femur))
        	System.err.println("we have issues");
        servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(t_femur));
        servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(t_tibia));

        //Debug.Log("C:"+ (int)servoAngle +",F:"+ (int)t_femur +",T:"+ (int)t_tibia +",e:"+ (int)t_e);

        if (coxaChange >= 85) set = true;
        if (coxaChange <= 5) set = false;
    }

    public void turn2()
    {
        //calculate hight of legg depending on the servoAngle (aX^2 + b)
        double f_q = 10.0; // top is (p , q) 
        double f_p = 45.0;
        double f_a = (-f_q)/Math.pow(-f_p, 2);
        double f_h = f_a * Math.pow((sharedParams.servoAngle_rv - f_p), 2) + f_q;
        //Debug.Log("y:"+f_h+",x:"+sharedParams.servoAngle_rv + ",a:"+f_a);
        //  0 = a * (90-45)^2 + 45 
        // a = (-q)/(-p)^2
        double t_a = 80.0;
        double t_c = 160.0;
        if (set)
        {
            t_e = 90 - f_h;
           // t_e = 75;
        }
        else
            t_e = 90;

        
        double t_f = 35;
        double t_d = laccent - t_f;
        double t_b = Math.sqrt(t_d*t_d + t_e*t_e);
        if (t_b < 80)
            t_b = 80;
        double t_delta = Math.asin(t_d / t_b)*(180 / Math.PI);
        double t_E = Math.atan(t_e / t_d)*(180 / Math.PI);
        double t_A = Math.acos((t_a * t_a - t_c * t_c - t_b * t_b) / (-2 * t_c * t_b))*(180 / Math.PI);
        double t_AE = t_E + t_A;
        double t_tms = (Weigth / 3.0) * (E / 1000.0);
        double t_gamma = Math.acos((t_c * t_c - t_b * t_b - t_a * t_a) / (-2 * t_b * t_a))*(180 / Math.PI);
        t_femur = 360 - 90 - t_delta - t_gamma;
        t_tibia = Math.acos((t_b * t_b - t_a * t_a - t_c * t_c) / (-2 * t_a * t_c))*(180 / Math.PI);
        //if ( == 5)
           // Debug.Log("id:" + getFirstId() / 3 + "h:" + (int)f_h);

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

    /// <summary>
    /// Main method for turning around his Y axis
    /// </summary>
	@SuppressWarnings("unused")
	private void noscope360()
    {        
        int id = getFirstId() / 3;
        switch (id)
        {
            case 0:
            case 2:
            case 3:
            case 5:
                l4 = Math.sqrt(Math.pow(0.5f * Width, 2) + Math.pow(0.5f * Length, 2));
                I = 0.5 * Length + Math.sin(45.0*(Math.PI/180)) * (L / 2);
                II = 0.5 * Width + Math.cos(45.0*(Math.PI/180)) * (L / 2); 
                r4 = Math.sqrt(I * I + II * II);
                beta_a = Math.acos((L * L - l4 * l4 - r4 * r4) / (-2 * r4 * l4))*(180 / Math.PI);
                test1 = 180 - Math.asin((r4 * Math.sin(beta_a*(Math.PI/180))) / L)*(180 / Math.PI);//TODO: right name!
                beta_b = Math.acos((Math.pow((L / 2), 2) - r4 * r4 - l4 * l4) / (-2 * r4 * l4))*(180 / Math.PI);
                double test2 = 180 - (Math.asin((r4 * Math.sin(beta_b*(Math.PI/180))) / (L / 2f))*(180 / Math.PI));
                B_MAX = beta_a - beta_b;
                double C_MAX = test2 - test1;
                break;

            case 1:
            case 4:
                l4 = (3.0 / 2.0) * Width;
                r4 = Math.sqrt(l4 * l4 + L * L - 2 * l4 * L * Math.cos((180 - (90 - 0.5f * A_MAX))*(Math.PI/180)));
                gamma_a = 180 - (90 - 0.5f * A_MAX);
                beta_a = Math.asin((Math.sin(Math.toRadians(gamma_a)) * L) / r4)*(180 / Math.PI);
                B_MAX = beta_a * 2;
                break; 
            default:
                throw new IllegalArgumentException();
        }
        switch (id)
        {
            case 0://RV 
            case 5://LA                  
                servoAngle = coxaChange;
                sharedParams.servoAngle_rv = servoAngle;
                gamma = 360 - (Math.atan((0.5f * Length) / (0.5f * Width))*(180 / Math.PI) + (135 + servoAngle));
                alpha = Math.asin((Math.sin(gamma*(Math.PI/180)) * l4) / r4)*(180 / Math.PI);
                beta = 180 - gamma - alpha;
                laccent = (r4 * Math.sin(beta * (Math.PI / 180))) / (Math.sin(gamma * (Math.PI / 180)));
                sharedParams.b_turn = beta - beta_b;
                laccent = (r4 * Math.sin(beta*(Math.PI/180))) / (Math.sin(gamma*(Math.PI/180)));
                break;

            case 1://RM
            case 4://LM
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                alpha = Math.asin((Math.sin(beta*(Math.PI/180)) * l4) / laccent)*(180 / Math.PI);
                gamma = 180 - alpha - beta;
                servoAngle = gamma - 135;
                servoAngle = coxaChange;
                break;

            case 2://RA
            case 3://LV
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                alpha = Math.asin((Math.sin(Math.toRadians(beta)) * l4) / laccent)*(180 / Math.PI);
                gamma = 180 - alpha - beta;
                servoAngle = gamma - test1;
                break;
        }
        if (servoAngle >= 85)
        {
            set = true;
            servoAngle = 85;
        }
        else if (servoAngle <= 5)
        {
            set = false;
            servoAngle = 5;
        }
        turn2();
        /*
        if (id == 5)
            servoAngle = 180 - servoAngle;
            */            
            
        servos[SpiderJoint.COXA].setAngle(Math.toRadians(servoAngle));
        servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(t_femur));
        servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(t_tibia));
    }
	
	int[] getIds()
	{
		return new int[] { servos[0].getId(), servos[1].getId(), servos[2].getId() };
	}
	
	int getFirstId()
    {
        return servos[0].getId();
    }
	
	void moveToDegrees(double coxa, double femur, double tibia)
	{
		servos[SpiderJoint.COXA ].setAngle(Math.toRadians(coxa));
        servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(femur));
        servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(tibia));
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
