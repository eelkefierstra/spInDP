using UnityEngine;
using System.Collections;

public class walk : MonoBehaviour
{
    private GameObject legg; 
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
        legg = RV.gameObject;  
    }

    // Update is called once per frame
    void Update()
    {  
              
        moveSelectedLegg(legg, c, a, b);
    }

    void moveSelectedLegg(GameObject Legg, float c, float a, float b)
    {
        GameObject gamma = Legg.transform.Find("poot1.1_pivot").gameObject;
        gamma.transform.rotation = Quaternion.AngleAxis(c, Vector3.up);
        GameObject alpha = Legg.transform.Find("poot1.1_pivot/poot1.2_pivot").gameObject;
        alpha.transform.localRotation = Quaternion.AngleAxis(a, Vector3.left);
        GameObject beta = Legg.transform.Find("poot1.1_pivot/poot1.2_pivot/poot1.3_pivot").gameObject;
        beta.transform.localRotation = Quaternion.AngleAxis(b, Vector3.left);
    }
}
