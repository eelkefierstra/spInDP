using UnityEngine;
using Executors;
using System.Collections.Generic;

public class SpiderBody
{
    SpiderLeg[] legs;
    Queue<Future<object>> futures;
    private IExecutor executor;

	// Use this for initialization
	public SpiderBody()
	{
		legs     = new SpiderLeg[6];
        futures = new Queue<Future<object>>();
        executor = new ImmediateExecutor();
        SharedParams sharedParams = new SharedParams();

		for (int i = 0; i < legs.Length; i++)
		{
			legs[i] = new SpiderLeg(ref executor, ref sharedParams, (i * 3) + 1);
		}
	}
	
	// Update is called once per frame
	public void walk(double forward, double right)
	{
        int i = 0;
		foreach (SpiderLeg leg in legs)
		{
			if (leg.walk(forward, right))
				futures.Enqueue(leg.getFuture());
            i++;
        }
        i++;
        while (futures.Count != 0) futures.Dequeue().GetResult();
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
	/*
    public void testIdleStance(int coxaChange)
    {
        foreach (SpiderLeg leg in legs)
        {
            leg.coxaChange = coxaChange;
            leg.Call();
        }
    }*/

    public class SharedParams
    {
        public readonly int firstId;
        public double firstCoxaChange;
        public double b_turn;
        
        public SharedParams() : this(1) { }

        public SharedParams(int firstId) : this(firstId, 0.0, 0.0) { }

        public SharedParams(int firstId, double firstCoxaChange, double b_turn)
        {
            this.firstId = firstId;
            this.firstCoxaChange = firstCoxaChange;
            this.b_turn = b_turn;
        }
    }
}
