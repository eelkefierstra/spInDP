﻿using UnityEngine;
using System.Collections.Generic;

public class walk : MonoBehaviour
{
    List<GameObject> allLegs = new List<GameObject>();
    SpiderBody body;
    public GameObject RV;
    public GameObject RM;
    public GameObject RA;
    public GameObject LV;
    public GameObject LM;
    public GameObject LA;
    public float rotateSpeed = 10f;
    public float c;
    public float a;
    public float b;

    // Use this for initialization
    void Start()
    {
        // add all legs to list
        allLegs.Add(RV);
        allLegs.Add(RM);
        allLegs.Add(RA);
        allLegs.Add(LV);
        allLegs.Add(LM);
        allLegs.Add(LA);
        body = new SpiderBody();
    }

    // Update is called once per frame
    void Update()
    {
        body.run();
        KeyValuePair<int, double[]>[] angles = body.getLegAngles();
        for (int i = 0; i < angles.Length; i++)
        {
            moveSelectedLeg(angles[i].Key, angles[i].Value[0], angles[i].Value[1], angles[i].Value[2]);
        }
        //moveSelectedLeg(1, 60, 70, 90);
    }

    public void moveSelectedLeg(int id, double c, double a, double b)
    {
        // get right id     
        int legId = (int)(Mathf.Floor(id / 3) + 1);
        legId = 1;
        // get leg with id
        GameObject leg = allLegs[legId];
        // find gamma from leg
        GameObject gamma = leg.transform.Find("poot1.1_pivot").gameObject;
        // set gamma

        gamma.transform.rotation = Quaternion.AngleAxis((float)c, Vector3.up);
        // find alpha from leg
        GameObject alpha = leg.transform.Find("poot1.1_pivot/poot1.2_pivot").gameObject;
        // set alpha
        alpha.transform.localRotation = Quaternion.AngleAxis((float)a, Vector3.left);
        // find beta from leg
        GameObject beta = leg.transform.Find("poot1.1_pivot/poot1.2_pivot/poot1.3_pivot").gameObject;
        // set beta
        beta.transform.localRotation = Quaternion.AngleAxis((float)b, Vector3.left);
    }
}
