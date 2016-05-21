using UnityEngine;
using System.Collections.Generic;

public class SpiderBody
{
    SpiderLeg[] legs;

	// Use this for initialization
	public SpiderBody()
	{
		legs = new SpiderLeg[6];
		for (int i = 0; i < legs.Length; i++)
		{
			legs[i] = new SpiderLeg((i * 3) + 1);
		}
	}
	
	// Update is called once per frame
	public void walk(double forward, double right)
	{
		foreach (SpiderLeg leg in legs)
		{
            if (forward != 0.0)
            {
                if (!leg.set) leg.coxaChange += (50 * Time.deltaTime * forward);
                if (leg.set) leg.coxaChange -= (50 * Time.deltaTime * forward);
                leg.run();
            }
            if (!forward.IsBetweenII(-.25, .25))
            {
                // curve or something...
            }
		}
	}

    public KeyValuePair<int, double[]>[] getLegAngles()
    {
        KeyValuePair<int, double[]>[] res = new KeyValuePair<int, double[]>[6];
        
        for (int i = 0; i < legs.Length; i++)
        {
            res[i] = new KeyValuePair<int, double[]>(legs[i].getFirstId(), legs[i].getLegAngles());
        }
        return res;
    }

    public void testIdleStance(int coxaChange)
    {
        foreach (SpiderLeg leg in legs)
        {
            leg.coxaChange = coxaChange;
            leg.run();
        }
    }
}
