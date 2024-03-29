package com.nhl.spindp.spin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.nhl.spindp.Time;
import com.nhl.spindp.spin.SpiderBody.SharedParams;

class SpiderLeg implements Runnable
{
	private static final double MINE     =  35.0;
	private static final double MINL     =  75.0;
	private static final double MAXL     = 150.0;
	private static final double A        =  80.0;
	private static final double A_MAX    =  90.0;
	private static final double A_RAD    = Math.toRadians(A_MAX / 2.0);
	private static final double C        = 160.0;
	private static final double F        =  35.0;
	//private static final double L        = 127.0;
	//private static final double LACCENT  = Math.cos(A_RAD) * L;
	//private static final double D        = F - LACCENT;
	//private static final double B        = Math.sqrt(Math.pow(D, 2.0) + Math.pow(E, 2));
	private static final double coxalimL =  10.0;
	private static final double coxalimH =  80.0;
	private static volatile double PAR_X =  35.0;
	//private static volatile double PAR_Y = PAR_X / Math.pow(Math.sqrt(Math.pow(L, 2.0) - Math.pow(LACCENT, 2.0)), 2.0);
	
	private ExecutorService executor;
	private Future<?> future;
	private SpiderBody.SharedParams sharedParams;
	
	private double alpha   =   0.0;// Math.toRadians(Math.acos((Math.pow(A, 2) - Math.pow(C, 2) - Math.pow(B, 2)) / (-2 * C * B)));
	private double gamma   =   0.0;// Math.toRadians(Math.acos((Math.pow(C, 2.0) - Math.pow(B, 2.0) - Math.pow(A, 2.0)) / (-2 * B * A)));
	private double beta    =   0.0;// Math.toRadians(Math.acos((Math.pow(B, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
	private double epsilon =   0.0;// Math.toRadians(Math.atan(E / D));
	private double step    =   0.0;
	private double e       = 110.0;
	private double eLocal  =  80.0;
	private boolean set    = false;
	
	private static final double MAXE     = Math.sqrt(Math.pow((A + C), 2) + Math.pow((127 - F), 2) ); //200;
	
	private double coxaChange = coxalimL;
	 //crab L
    private double c_l = 0;
	
	private double t_femur;
    private double t_tibia;
    private double t_coxa;

    //private final double small_l = Math.cos(A_RAD)*l;
    // bocht
    private static final double Length = 300;
    private static final double Width  =  80;
    //private static final double R = 500;
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
    //private static double beta_RV;
    private double servoAngle;
    private double l;
    private double laccent;
    //private static double b_turn;
    //private static double servoAngle_rv;
    private double betaD1;
	private double betaD2;
    private double test1; //TODO: need name still
    private double B_MAX;
    
    private final int[][] legPairs = {{0,5},{1,4},{2,3}};
    private String walkType = "3leg";
	
	SpiderJoint[] servos = new SpiderJoint[3];
		
	SpiderLeg(ExecutorService executor, SharedParams sharedParams, byte startServoId)
	{
		/*if (startServoId % 2 == 0)
		{
			coxaChange = coxalimH;
			set = true;
		}*/
		
		sharedParams.set_rv = true;
		
		/*coxaChange += 30 * (startServoId / 3);
		set = (startServoId % 2) == 0;
		if (coxaChange > 90)
		{
			coxaChange -= 90;
		}*/
		// 105, 240, 0
		this.l = 127.0;
		this.executor = executor;
		this.sharedParams = sharedParams;
		servos[SpiderJoint.COXA ] = new SpiderJoint(startServoId++, alpha, 105);//, 90, 210);
		servos[SpiderJoint.FEMUR] = new SpiderJoint(startServoId++, gamma, 231);
		servos[SpiderJoint.TIBIA] = new SpiderJoint(startServoId++,  beta,   5);
		
		servos[SpiderJoint.COXA].setAngle(Math.toRadians(45));
		servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(45));
		servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(45));
	}
	
	public boolean walk(double forward, double right)
	{
		boolean res = false;
		if(forward > 0.25 || forward < -0.25)
		{
		if (getFirstId()/3 == 0)
		{
			if (!set)
				sharedParams.servoAngle_rv += 180.0 * Time.deltaTime * forward;//* (forward * Math.abs(1/forward)) ;
			else
				sharedParams.servoAngle_rv -= 180.0 * Time.deltaTime * forward ;//* (forward * Math.abs(1/forward));
			//System.out.println("coxa:"+(int)sharedParams.servoAngle_rv+",set:"+set);
			
			if (sharedParams.servoAngle_rv >= coxalimH)
			{
				sharedParams.servoAngle_rv = coxalimH;
				sharedParams.set_rv = !sharedParams.set_rv;				
			}
			else if (sharedParams.servoAngle_rv <= coxalimL)
			{			
				sharedParams.servoAngle_rv = coxalimL;
				sharedParams.set_rv = !sharedParams.set_rv;
			}
			set = sharedParams.set_rv;
		}
		// set all Legs in right set
		selectSet(walkType);
		run2(right);
		//System.out.println("");
			
//		if (forward == 0 && right == 0)
//		{
//			//future = executor.submit(this);
//			//res = true;
//		}
//		else if (right <= -.25 || right >= .25)
//		{
//			/*
//			if (!set)
//				coxaChange += ((90.0 * Time.deltaTime));
//			else
//				coxaChange -= ((90.0 * Time.deltaTime));
//			*/
//			
//			//if(right <= -0.9 || right >= 0.9)
//				//noscope360(right);
//			//else
//				turn(right);
//		}
//		else if (forward <= -.25 || .25 <= forward)
//		{
//			/*if (!set)
//				coxaChange += ((90.0 * Time.deltaTime) * forward);
//			else
//				coxaChange -= ((90.0 * Time.deltaTime) * forward);
//				*/
//			run();
//			//future = executor.submit(this);
//			res = false;
//		}
		}
		return res;
	}
	
	private void setNextPair(String type)
	{
		// type 3 = 3 legs grouped
		if(type.equals("2leg"))
		{
			sharedParams.legSetID++;
			if(sharedParams.legSetID > 2)
				sharedParams.legSetID = 0;			
		}
		else if (type.equals("3leg"))
		{
			sharedParams.set_rv = !sharedParams.set_rv;
		}		
	}
	
	private void selectSet(String type)
	{
		int id = getFirstId() / 3;
		if(type.equals("3leg"))// 3 paired
		{
			// set all even legs equal to set_rv and invert the others
			if(id%2 == 0)
				set = sharedParams.set_rv;
			else
				set = !sharedParams.set_rv;	
		}
		else if(type.equals("2leg"))// pairs of 2
		{
			// check if current leg is part of the selected pair
			if(legPairs[sharedParams.legSetID][0] == id ||legPairs[sharedParams.legSetID][1] == id)
				set = true;
			else
				set = false;		
		}			
	}
	
	public Future<?> getFuture()
	{
		return future;
	}
	
	public double[] geteLocal()
	{
		return new double[]{e,eLocal};
	}
	
	public void setHeight(double height)
	{
		if (height < MINE)
			height = MINE;
		else if (height > MAXE)
			height = MAXE;
		e = height;
	}
	
	public void setWidth(double width)
	{
		if (width < MINL)
			width = MINL;
		else if (width > MAXL)
			width = MAXL;
		l = width;
	}
	
	public byte[][] getAll() throws InterruptedException, ExecutionException
	{
		byte[][] res = new byte[servos.length][];
		for (int i = 0; i < res.length; i++)
		{
			res[i] = servos[i].getFuture().get();
			//if (res[i][0] != (byte)0xFF || res[i][1] != (byte)0xFF)
				//System.out.println();//DatatypeConverter.printHexBinary(res[i]));
		}
		return res;
	}
	
	@Override
	public void run()
	{/*
		
		if (coxaChange >= coxalimH)
		{
			coxaChange = coxalimH;
		}
		if (coxaChange <= coxalimL)
		{
			set = !set;
			//setNextPair(2);
			coxaChange = coxalimL;
			//if (getFirstId() == 1)
				//sharedParams.sync = true;
		}*/		
		
		if(set == sharedParams.set_rv)
			coxaChange = sharedParams.servoAngle_rv;
		else
			coxaChange = coxalimH - sharedParams.servoAngle_rv;
			
		//compensate for walking at an angle
		int id = getFirstId() / 3;
		/*
		if (sharedParams.currentAngleX <= 0)
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e;
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				default:
					eLocal = e;
					break;
			}
		}
		else
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
				default:
					eLocal = e;
					break;
			}
		}
		*/
		
		//System.out.println("id:"+sharedParams.firstId/3+", coxa:"+(int)coxaChange); //TODO: clean sys out
		double lAccent = Math.cos(A_RAD) * l;
		servos[SpiderJoint.COXA ].setAngle(alpha = Math.toRadians(coxaChange));
		double lAccentAccent = lAccent / Math.cos(alpha  = Math.toRadians(Math.abs(coxaChange - (.5 * A_MAX))));
		double d = lAccentAccent - F;
		double f_p = (coxalimH - coxalimL) / 2;
		double f_a = (-PAR_X)/Math.pow(-f_p, 2);
		double h = 0;// f_a * Math.pow(((coxaChange - coxalimL) - f_p), 2) + PAR_X;
		step = Math.abs(Math.sqrt(Math.pow(lAccentAccent, 2.0) - Math.pow(lAccent, 2.0)));
		if (coxaChange < 45) step *= -1;
		double t_e = 90;//eLocal;
		/*if (false)
		{
			//double par_Y = PAR_X / Math.pow(Math.sqrt(Math.pow(l, 2.0) - Math.pow(lAccent, 2.0)), 2.0);
			//h = (par_Y * -1) * Math.pow(step, 2.0) + PAR_X;
            t_e = e - h;
           // t_e = 75;
		}*/
		//h *= 5;
		double b = Math.sqrt(Math.pow(d, 2.0) + Math.pow(t_e - h, 2.0));
		double delta = Math.atan(d / (t_e - h));
		double test1 = Math.pow(C, 2.0), test2 = Math.pow(b, 2.0), test3 = Math.pow(A, 2.0), test4 = (test1 - test2 - test3) / (-2 * b * A);
		if (!set || coxaChange < coxalimL + 5.0) 
			servos[SpiderJoint.FEMUR].setAngle(gamma = Math.toRadians(270.0 - Math.toDegrees(delta) - Math.toDegrees(Math.acos(test4))));//Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		else 
			servos[SpiderJoint.FEMUR].setAngle(gamma = Math.toRadians(230.0 - Math.toDegrees(delta) - Math.toDegrees(Math.acos(test4))));//Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		servos[SpiderJoint.TIBIA].setAngle(beta  = Math.acos((Math.pow(b, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
		//if (coxaChange >= coxalimH) 
		//	set = !set;
	}
	public void run2(double right)
	{/*
		
		if (coxaChange >= coxalimH)
		{
			coxaChange = coxalimH;
		}
		if (coxaChange <= coxalimL)
		{
			set = !set;
			//setNextPair(2);
			coxaChange = coxalimL;
			//if (getFirstId() == 1)
				//sharedParams.sync = true;
		}*/		
		
		if(set == sharedParams.set_rv)
			coxaChange = sharedParams.servoAngle_rv;
		else
			coxaChange = (coxalimH + 22.5) - sharedParams.servoAngle_rv;
			
		//compensate for walking at an angle
		int id = getFirstId() / 3;
		/*
		if (sharedParams.currentAngleX <= 0)
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e;
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				default:
					eLocal = e;
					break;
			}
		}
		else
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
				default:
					eLocal = e;
					break;
			}
		}
		*/
		
		//System.out.println("id:"+sharedParams.firstId/3+", coxa:"+(int)coxaChange); //TODO: clean sys out
		double lAccent = Math.cos(A_RAD) * l;
		
		double lAccentAccent = lAccent / Math.cos(alpha  = Math.toRadians(Math.abs(coxaChange - (.5 * A_MAX))));
		double d = lAccentAccent - F;
		double f_p = (coxalimH - coxalimL) / 2;
		double f_a = (-PAR_X)/Math.pow(-f_p, 2);
		double h = 0;// f_a * Math.pow(((coxaChange - coxalimL) - f_p), 2) + PAR_X;
		step = Math.abs(Math.sqrt(Math.pow(lAccentAccent, 2.0) - Math.pow(lAccent, 2.0)));
		if (coxaChange < 45) step *= -1;
		double t_e = 90;//eLocal;
		/*if (false)
		{
			//double par_Y = PAR_X / Math.pow(Math.sqrt(Math.pow(l, 2.0) - Math.pow(lAccent, 2.0)), 2.0);
			//h = (par_Y * -1) * Math.pow(step, 2.0) + PAR_X;
            t_e = e - h;
           // t_e = 75;
		}*/
		//h *= 5;
		double b = Math.sqrt(Math.pow(d, 2.0) + Math.pow(t_e - h, 2.0));
		double delta = Math.atan(d / (t_e - h));
		double test1 = Math.pow(C, 2.0), test2 = Math.pow(b, 2.0), test3 = Math.pow(A, 2.0), test4 = (test1 - test2 - test3) / (-2 * b * A);
		if((id == 1 && right > 0.25) || (id == 4 && right < -0.25))
		{
			//servos[SpiderJoint.COXA ].setAngle(alpha = Math.toRadians(45));
			servos[SpiderJoint.COXA ].setAngle(alpha = Math.toRadians((coxaChange - 45.0) * -.25 + 45.0));
		}
		else
		{
			servos[SpiderJoint.COXA ].setAngle(alpha = Math.toRadians(coxaChange));
		}
		   if (!set || coxaChange < coxalimL + 5.0) 
		      	servos[SpiderJoint.FEMUR].setAngle(gamma = Math.toRadians(270.0 - Math.toDegrees(delta) - Math.toDegrees(Math.acos(test4))));//Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		   else 
		    	servos[SpiderJoint.FEMUR].setAngle(gamma = Math.toRadians(230.0 - Math.toDegrees(delta) - Math.toDegrees(Math.acos(test4))));//Math.acos((Math.pow(C, 2.0) - Math.pow(b, 2.0) - Math.pow(A, 2.0)) / (-2 * b * A)));
		   
		   servos[SpiderJoint.TIBIA].setAngle(beta  = Math.acos((Math.pow(b, 2.0) - Math.pow(A, 2.0) - Math.pow(C, 2.0)) / (-2 * A * C)));
		//if (coxaChange >= coxalimH) 
		//	set = !set;
	}
	
	/**
	 * calculate angles for turning
	 * @param right how far to turn to the right
	 */
	private void turn(double right)
	{
		final double scale = 1500.0;
		int id = getFirstId() / 3;
		// check if turn is right
		
 		if(right > 0)
 		{  
 			System.out.println("RACHTS");
 			// select the right id for a right turn
 			if (id > 2)
 			    id -= 3; // 3 -> 0, 4 -> 1 , 5 -> 2
 			else
 			    id += 3; // 0 -> 3, 1 -> 4, 2 -> 5      
 		}
 		else
 		{
 			System.out.println("LAFT");
 		}
 		
		//calculate the radius of turn
		double r = 700; //500.0 + (scale - scale*Math.abs(right)); //237 500
		/*
		if (coxaChange >= coxalimH)
		{
			coxaChange = coxalimH;
		}
		else if (coxaChange <= coxalimL)
		{			
			coxaChange = coxalimL;
		}*/
		
		double small_l = Math.cos(A_RAD)*l;
		switch (id)
		{
			case 0:
			case 2:
				// Rechts voor en achter
				h = 0.5 * Length - 0.5 * (Math.sqrt(l * l - small_l* small_l) * 2);
				b = small_l + r + 0.5 * Width;
				r4 = Math.sqrt(h * h + b * b);
				I = 0.5 * Length;
				II = r + 0.5 * Width;
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
				l4 = r + ((3.0 / 2.0) * Width);
				r4 = l4 + small_l;
				gamma_a = 90 + (180 - A_MAX) / 2;
				alpha_a = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				B_MAX = 2 * beta_a;
				break;
			case 3:
			case 5:
				// Links voor en achter
				h = 0.5 * Length + 0.5 * (Math.sqrt(l * l - small_l * small_l) * 2);
				b = r - 0.5 * Width - small_l;
				r4 = Math.sqrt(h * h + b * b);
				I = 0.5 * Length;
				II = r - 0.5 * Width;
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
				l4 = r - ((3.0 / 2.0) * Width);
				r4 = Math.sqrt((l4 * l4 + l * l) - 2 * l4 * l * Math.cos(Math.toRadians(A_MAX / 2)));
				gamma_a = (A_MAX / 2);
				alpha_a = 180 - Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma_a)) * l4) / r4));
				beta_a = 180 - alpha_a - gamma_a;
				B_MAX = 2 * beta_a;
				break;
			default:
				throw new IllegalArgumentException();
		}
        switch (id)
        {
            case 0:
                //RV (leidend)   
                servoAngle = sharedParams.servoAngle_rv;
                gamma = servoAngle + gamma_a;
                alpha = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(gamma)) * l4) / r4));
                beta = 180 - gamma - alpha;
                sharedParams.beta_RV = beta;
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
                    alpha = -(180 + Math.toDegrees((Math.asin((Math.sin(Math.toRadians(beta)) * l4) / laccent)))) + 360;
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
        turn2(right);
     
        if(set == sharedParams.set_rv)
        	servoAngle = sharedParams.servoAngle_rv;
		else
			servoAngle = coxalimH - sharedParams.servoAngle_rv;
        //if (id % 2 == 1)
        //   servoAngle = 90 - servoAngle;
        // t_tibia += 145;
        //t_femur += -40;
        //servoAngle = 0;
        if(id == 0)
           System.out.println("A:"+(int)servoAngle);
        servos[SpiderJoint.COXA].setAngle(Math.toRadians(servoAngle));
        if (Double.isNaN(t_femur))
        	System.err.println("we have issues");
        servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(t_femur));
        servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(t_tibia));

        //Debug.Log("C:"+ (int)servoAngle +",F:"+ (int)t_femur +",T:"+ (int)t_tibia +",e:"+ (int)t_e);

       // if (coxaChange >= coxalimH) set = !set;
       // if (coxaChange <= coxalimL) set = !set;
    }

    public void turn2( double right)
    {
        //calculate hight of legg depending on the servoAngle (aX^2 + b)
    	double t_e = 90;
        double f_p = 45.0;
        if(Math.abs(right) > 0.9)
        	f_p = 52.0/2.0;
        double f_a = (-PAR_X)/Math.pow(-f_p, 2);
        double f_h = f_a * Math.pow((sharedParams.servoAngle_rv - f_p), 2) + PAR_X;
        //Debug.Log("y:"+f_h+",x:"+sharedParams.servoAngle_rv + ",a:"+f_a);
        //  0 = a * (90-45)^2 + 45 
        // a = (-q)/(-p)^2
        //double t_a = 80.0;
        //double t_c = 160.0;
        int id = getFirstId() / 3;
        /*
		if (sharedParams.currentAngleX <= 0)
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e;
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				default:
					eLocal = e;
					break;
			}
		}
		else
		{
			switch (id)
			{
				case 0:
				case 3:
					eLocal = e + 300 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 1:
				case 4:
					eLocal = e + 150 * Math.atan(Math.abs(sharedParams.currentAngleX));
					break;
				case 2:
				case 5:
				default:
					eLocal = e;
					break;
			}
		}
		*/
        if (set)
        {
            t_e = 90 - f_h;
           // t_e = 75;
        }
        else
        	t_e = 90;

        
        //double t_f = 35;
        double t_d = laccent - F;
        double t_b = Math.sqrt(t_d*t_d + t_e*t_e);
        if (t_b < 80)
            t_b = 80;
        double t_delta = Math.asin(t_d / t_b)*(180 / Math.PI);
        //double t_E = Math.atan(t_e / t_d)*(180 / Math.PI);
        //t_A = Math.acos((A * A - C * C - t_b * t_b) / (-2 * C * t_b))*(180 / Math.PI);
        //double t_AE = t_E + t_A;
        //double t_tms = (Weigth / 3.0) * (e / 1000.0);
        double t_gamma = Math.acos((C * C - t_b * t_b - A * A) / (-2 * t_b * A))*(180 / Math.PI);
        t_femur = 360 - 90 - t_delta - t_gamma;
        t_tibia = Math.acos((t_b * t_b - A * A - C * C) / (-2 * A * C))*(180 / Math.PI);
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
    

	private void noscope360(double right)
    {    //coxalimH =45.0;   
        int id = getFirstId() / 3;
        
        if (coxaChange >= 52.0)
		{
			set = true;
			coxaChange = 52.0;
		}
		else if (coxaChange <= coxalimL)
		{
			set = false;
			coxaChange = coxalimL;
		}
        
        // check if turn is right
        /*if(right > 0)
         {   // select the right id for a right turn
             if (id + 3 > 5)
                 id -= 3; // 3 -> 0, 4 -> 1 , 5 -> 2
             else
                 id += 3; // 0 -> 3, 1 -> 4, 2 -> 5      
         }*/
        
        switch (id)
        {
            case 0:
            case 2:
            case 3:
            case 5:
                l4 = Math.sqrt(Math.pow(0.5 * Width, 2) + Math.pow(0.5 * Length, 2));
                I = 0.5 * Length + Math.sin(45.0*(Math.PI/180)) * (l / 2);
                II = 0.5 * Width + Math.cos(45.0*(Math.PI/180)) * (l / 2); 
                r4 = Math.sqrt(I * I + II * II);
                beta_a = Math.acos((l * l - l4 * l4 - r4 * r4) / (-2 * r4 * l4))*(180 / Math.PI);
                test1 = 180 - Math.asin((r4 * Math.sin(beta_a*(Math.PI/180))) / l)*(180 / Math.PI);//TODO: right name!
                beta_b = Math.acos((Math.pow((l / 2), 2) - r4 * r4 - l4 * l4) / (-2 * r4 * l4))*(180 / Math.PI);
                double test2 = 180 - (Math.asin((r4 * Math.sin(beta_b*(Math.PI/180))) / (l / 2.0))*(180 / Math.PI));
                B_MAX = beta_a - beta_b;
                double C_MAX = test2 - test1;
                break;
            case 1:
            case 4:
                l4 = (3.0 / 2.0) * Width;
                r4 = Math.sqrt(l4 * l4 + l * l - 2 * l4 * l * Math.cos((180 - (90 - 0.5 * A_MAX))*(Math.PI/180)));
                gamma_a = 180 - (90 - 0.5 * A_MAX);
                beta_a = Math.asin((Math.sin(Math.toRadians(gamma_a)) * l) / r4)*(180 / Math.PI);
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
                /*if(id == 5)// if LA
                	servoAngle = 90 - servoAngle;*/
                gamma = 360 - (Math.atan((0.5 * Length) / (0.5 * Width))*(180 / Math.PI) + (135 + servoAngle));
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
                if(id == 4)// if LM
                	servoAngle = 90 - servoAngle;
                break;

            case 2://RA
            case 3://LV
                beta = beta_a - sharedParams.b_turn;
                laccent = Math.sqrt(l4 * l4 + r4 * r4 - 2 * r4 * l4 * Math.cos(beta*(Math.PI/180)));
                alpha = Math.asin((Math.sin(Math.toRadians(beta)) * l4) / laccent)*(180 / Math.PI);
                gamma = 180 - alpha - beta;
                servoAngle = gamma - 59.93; //gamma - test1;                
                if(id == 3)// if RA
                	servoAngle = 90 - servoAngle;
                break;
        }
        
        turn2(right);
        
        if (id % 2 == 1)
            servoAngle = Math.abs(servoAngle - 90);  
        servos[SpiderJoint.COXA].setAngle(Math.toRadians(servoAngle));
        if (Double.isNaN(t_femur))
        	System.err.println("we have issues");
        servos[SpiderJoint.FEMUR].setAngle(Math.toRadians(t_femur));
        servos[SpiderJoint.TIBIA].setAngle(Math.toRadians(t_tibia));
    }
	
	@SuppressWarnings("unused")
	private void crab()
	{	
		int id = getFirstId() / 3;
		
		if (c_l >= 90)
		{
			set = !set;
			c_l = 90;
		}
		if (c_l <= 64)
		{
			set = !set;
			c_l = 64;
		}
		if(id >= 3)
			c_l  = 90 - c_l;
		// 63,7 -> 90 
		double c_d = c_l - F;
		double c_e = e;
		
		if(set && id < 3)
			c_e = e - 10;
		else if(!set && id >= 3)
			c_e = e - 10;
			
		double c_b = Math.sqrt(c_d*c_d + c_e*c_e);
		double c_beta = Math.acos((c_b*c_b - C*C - A*A)/(-2*C*A));
		double c_gamma = Math.toRadians(180) - Math.asin((Math.sin(c_beta)*C)/c_b);
		double c_delta = Math.atan(c_d/c_e);
		double c_femur = Math.toRadians(270.0) - c_gamma - c_delta;
		if(id == 0){
		System.out.println("c_fem"+Math.toDegrees(c_femur));
		System.out.println("c_beta"+Math.toDegrees(c_beta));
		}
		servos[SpiderJoint.COXA].setAngle(Math.toRadians(45));
		servos[SpiderJoint.FEMUR].setAngle(c_femur);
		servos[SpiderJoint.TIBIA].setAngle(c_beta);		
		
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
	
	void moveTo(int coxa, int femur, int tibia)
	{
		servos[SpiderJoint.COXA ].setServoAngle((short)coxa);
        servos[SpiderJoint.FEMUR].setServoAngle((short)femur);
        servos[SpiderJoint.TIBIA].setServoAngle((short)tibia);
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
