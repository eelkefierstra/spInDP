using UnityEngine;
using System.Collections.Generic;

public class Walk : MonoBehaviour
{
    private SpiderBody body;
    public double offsetCoxa;
    public double offsetFemur;
    public double offsetTibia;
    public int testCoxaChange;
    public int[] testAngles;
    public GameObject[] allLegs;
    //public float rotateSpeed = 10f;

    // Use this for initialization
    void Start()
    {
        body = new SpiderBody();
    }

    // Update is called once per frame
    void Update()
    {
		Walk.Time.updateTime();
        body.walk(1.0, 0.0);
       // body.testIdleStance(testCoxaChange);
        KeyValuePair<int, double[]>[] pairs = body.getLegAngles();
        foreach (KeyValuePair<int, double[]> p in pairs)
        {
            moveSelectedLeg(p.Key, p.Value[0], p.Value[1], p.Value[2]);
        }
    }

    public void moveSelectedLeg(int id, double c, double a, double b)
    {
        // get right id     
        int legId = id / 3;
        GameObject gamma = allLegs[legId].transform.Find("poot1.1_pivot").gameObject;
        // set gamma
        gamma.transform.localRotation = Quaternion.AngleAxis((float)(offsetCoxa - c), (legId >= 3) ? Vector3.down : Vector3.up);
        // find alpha from leg
        GameObject alpha = gamma.transform.Find("poot1.2_pivot").gameObject;
        alpha.transform.localRotation = Quaternion.AngleAxis((float)(offsetFemur - a), Vector3.left);
        // find beta from leg
        GameObject beta = alpha.transform.Find("poot1.3_pivot").gameObject;
        beta.transform.localRotation = Quaternion.AngleAxis((float)(offsetTibia - b), Vector3.left);
    }

	public static class Time
	{
		private static double newSync = UnityEngine.Time.deltaTime;
		private static bool sync      = true;
		private static bool update    = true;

		public static void updateTime()
		{
			newSync += UnityEngine.Time.deltaTime;
			if (sync = newSync < 1.0)
			{
				newSync = 0;
			}
		}

		public static bool shouldSync()
		{
			return sync;
		}
	}
}
