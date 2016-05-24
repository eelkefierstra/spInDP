using UnityEngine;
using System.Collections.Generic;

public class walk : MonoBehaviour
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
		//body.testIdleStance(testCoxaChange);
		body.walk(0.0, 1.0);
        KeyValuePair<int, double[]>[] pairs = body.getLegAngles();
        foreach (KeyValuePair<int, double[]> p in pairs)
        {
            moveSelectedLeg(p.Key, p.Value[0], p.Value[1], p.Value[2]);
        }
        //for (int i = 1; i <= 18; i+=3)
        //moveSelectedLeg(0, testAngles[0], testAngles[1], testAngles[2]);
        //moveSelectedLeg(1, 60, 70, 90);
    }

    public void moveSelectedLeg(int id, double c, double a, double b)
    {
        // get right id     
        int legId = id / 3;
        //Debug.Log("Leg: " + legId + " has the angles: " + c + ", " + a + ", " + b, this);
        // get leg with id
        // find gamma from leg
        GameObject gamma = allLegs[legId].transform.Find("poot1.1_pivot").gameObject;
        // set gamma
        gamma.transform.localRotation = Quaternion.AngleAxis((float)(offsetCoxa - c), (legId >= 3) ? Vector3.down : Vector3.up);
        // find alpha from leg
        GameObject alpha = gamma.transform.Find("poot1.2_pivot").gameObject;
        //GameObject alpha = leg.transform.Find("poot1.1_pivot/poot1.2_pivot").gameObject;
        // set alpha
        alpha.transform.localRotation = Quaternion.AngleAxis((float)(offsetFemur - a), Vector3.left);
        // find beta from leg
        GameObject beta = alpha.transform.Find("poot1.3_pivot").gameObject;
        //GameObject beta = leg.transform.Find("poot1.1_pivot/poot1.2_pivot/poot1.3_pivot").gameObject;
        // set beta
        beta.transform.localRotation = Quaternion.AngleAxis((float)(offsetTibia - b), Vector3.left);
    }
}
