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
			legs[i] = new SpiderLeg(ref executor, (i * 3) + 1);
		}
	}
	
	// Update is called once per frame
	public void walk(double forward, double right)
	{
        int i = 0;
		foreach (SpiderLeg leg in legs)
		{
			if (leg.walk(forward, right))
				futures[i] = leg.getFuture();
            i++;
        }
        i++;
	}
       // foreach (Future<object> f in futures) f.GetResult();
	

    public KeyValuePair<int, double[]>[] getLegAngles()
    {
        KeyValuePair<int, double[]>[] res = new KeyValuePair<int, double[]>[6];
        
        for (int i = 0; i < legs.Length; i++)
        {
            res[i] = new KeyValuePair<int, double[]>(legs[i].getFirstId(), legs[i].getLegAngles());
        }
        return res;
    }
	/*
    public void testIdleStance(int coxaChange)
    {
        foreach (SpiderLeg leg in legs)
        {
            leg.coxaChange = coxaChange;
            leg.Call();
        }
    }*/
}
