using UnityEngine;
using Executors;
using System.Collections.Generic;

public class SpiderBody
{
    SpiderLeg[] legs;
    Future<object>[] futures;
    private IExecutor executor;

	// Use this for initialization
	public SpiderBody()
	{
		legs     = new SpiderLeg[6];
        futures  = new Future<object>[6];
        executor = new ImmediateExecutor();

		for (int i = 0; i < legs.Length; i++)
		{
			legs[i] = new SpiderLeg((i * 3) + 1);
		}
	}
	
	// Update is called once per frame
	public void walk(double forward, double right)
	{
        int i = 0;
		foreach (SpiderLeg leg in legs)
		{

			if (!leg.set) leg.coxaChange += 1;
			if ( leg.set) leg.coxaChange -= 1;
            //if (leg.coxaChange > 90) flip = true;
            //if (leg.coxaChange <= 0) flip = false;            
            //leg.turn();

            if (forward != 0.0)
            {
                Debug.Log("FW");
                if (!leg.set) leg.coxaChange += (50 * Time.deltaTime * forward);
                if ( leg.set) leg.coxaChange -= (50 * Time.deltaTime * forward);
                futures[i] = executor.Submit(leg);
            }
            if (!right.IsBetweenII(-.25, .25))
            {
                // curve or something...                
                leg.turn();
            }
            i++;
		}
       // foreach (Future<object> f in futures) f.GetResult();
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
            leg.Call();
        }
    }
}
