using UnityEngine;
using System;
using System.Collections.Generic;

public class walk : MonoBehaviour
{
    private SpiderBody body;
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
        
        //moveSelectedLeg(1, 60, 70, 90);
    }

    void FixedUpdate()
    {
        body.run();
        KeyValuePair<int, double[]>[] angles = body.getLegAngles();
        for (int i = 0; i < angles.Length; i++)
        {
            moveSelectedLeg(angles[i].Key, angles[i].Value[0], angles[i].Value[1], angles[i].Value[2]);
        }
    }

    public void moveSelectedLeg(int id, double c, double a, double b)
    {
        // get right id     
        int legId = id / 3;
        //legId = 1;
        // get leg with id
        // find gamma from leg
        GameObject gamma = allLegs[legId].transform.Find("poot1.1_pivot").gameObject;
        // set gamma
        gamma.transform.localRotation = Quaternion.AngleAxis((float)c, (legId >= 3) ? Vector3.down : Vector3.up);
        // find alpha from leg
        GameObject alpha = gamma.transform.Find("poot1.2_pivot").gameObject;
        //GameObject alpha = leg.transform.Find("poot1.1_pivot/poot1.2_pivot").gameObject;
        // set alpha
        alpha.transform.localRotation = Quaternion.AngleAxis((float)a, Vector3.left);
        // find beta from leg
        GameObject beta = alpha.transform.Find("poot1.3_pivot").gameObject;
        //GameObject beta = leg.transform.Find("poot1.1_pivot/poot1.2_pivot/poot1.3_pivot").gameObject;
        // set beta
        beta.transform.localRotation = Quaternion.AngleAxis((float)b, Vector3.left);
    }
}
