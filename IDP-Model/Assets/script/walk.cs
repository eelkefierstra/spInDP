using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class walk : MonoBehaviour
{
    private GameObject legg;
    List<GameObject> allLeggs = new List<GameObject>();
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
        // add all leggs to list
        allLeggs.Add(RV);
        allLeggs.Add(RM);
        allLeggs.Add(RA);
        allLeggs.Add(LV);
        allLeggs.Add(LM);
        allLeggs.Add(LA);
        
    }

    // Update is called once per frame
    void Update()
    {
        moveSelectedLegg(1, 60, 70, 90);
    }

    public void moveSelectedLegg(int id, float c, float a, float b)
    {   
        // get right id     
        id = (int)(Mathf.Floor(id / 3) + 1);
        id = 1;
        // get legg with id
        legg = allLeggs[id];
        // find gamma from legg
        GameObject gamma = legg.transform.Find("poot1.1_pivot").gameObject;
        // set gamma
        gamma.transform.rotation = Quaternion.AngleAxis(c, Vector3.up);
        // find alpha from legg
        GameObject alpha = legg.transform.Find("poot1.1_pivot/poot1.2_pivot").gameObject;
        // set alpha
        alpha.transform.localRotation = Quaternion.AngleAxis(a, Vector3.left);
        // find beta from legg
        GameObject beta = legg.transform.Find("poot1.1_pivot/poot1.2_pivot/poot1.3_pivot").gameObject;
        // set beta
        beta.transform.localRotation = Quaternion.AngleAxis(b, Vector3.left);
    }
}
