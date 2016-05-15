﻿using System.Collections.Generic;

public class SpiderBody
{
    SpiderLeg[] legs;
	bool flip = false;

	// Use this for initialization
	public SpiderBody()
	{
		legs = new SpiderLeg[6];
		for (int i = 0; i < legs.Length; i++)
		{
			legs[i] = new SpiderLeg((i * 3) + 1);
		}
		//body.transform.childCount
	}
	
	// Update is called once per frame
	public void run()
	{
		foreach (SpiderLeg leg in legs)
		{
			if ( flip) leg.coxaChange += 1;
			if (!flip) leg.coxaChange -= 1;
			if (leg.coxaChange > 90) flip = true;
			if (leg.coxaChange <= 0) flip = true;
			leg.run();
		}
	}

    public KeyValuePair<int, double[]>[] getLegAngles()
    {
        KeyValuePair<int, double[]>[] res = new KeyValuePair<int, double[]>[6];
        
        for (int i = 0; i < legs.Length; i++)
        {
            res[i] = new KeyValuePair<int, double[]>(legs[i].getFirstId(), legs[i].getLegAngles());
            //res[leg.getFirstId()] = leg.getLegAngles();
        }
        return res;
    }

	public void testLegMovements()
	{
		bool flip = false;
		while (true)
		{
			foreach (SpiderLeg leg in legs)
			{
				if ( flip) leg.coxaChange += 1;
				if (!flip) leg.coxaChange -= 1;
				if (leg.coxaChange > 90) flip = true;
				if (leg.coxaChange <= 0) flip = true;
				leg.run();
				//Main.getInstance().driveServo(leg.getIds(), leg.getAngles());
			}
		}
	}

}
