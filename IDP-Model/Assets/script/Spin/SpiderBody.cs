using UnityEngine;
using System.Collections;

public class SpiderBody : MonoBehaviour
{
	Rigidbody body;
	SpiderLeg[] legs;
	bool flip = false;

	// Use this for initialization
	void Start ()
	{
		body = GetComponent<Rigidbody>();
		legs = new SpiderLeg[6];
		for (int i = 0; i < legs.Length; i++)
		{
			legs[i] = new SpiderLeg(i+1);
		}
		//body.transform.childCount
	}
	
	// Update is called once per frame
	void Update ()
	{
		foreach (SpiderLeg leg in legs)
		{
			if ( flip) leg.coxaChange += 1;
			if (!flip) leg.coxaChange -= 1;
			if (leg.coxaChange > 90) flip = true;
			if (leg.coxaChange <= 0) flip = true;
			leg.run ();
		}
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
