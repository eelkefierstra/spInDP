using UnityEngine;
using System.Collections;

public class walk : MonoBehaviour {
    public GameObject legg1;
	public GameObject legg2;
    // Use this for initialization
    void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (Input.GetKeyDown("a")){
            legg1.gameObject.transform.Rotate(Vector3.left *100f* Time.deltaTime);
			//legg2.gameObject.transform.Rotate(Vector3.left *100f* Time.deltaTime);
        }
        else if (Input.GetKeyDown("d"))
        {
			legg1.gameObject.transform.Rotate(Vector3.right *100f* Time.deltaTime);
			//legg2.gameObject.transform.Rotate(Vector3.right *100f* Time.deltaTime);
        }
	
    }
}
