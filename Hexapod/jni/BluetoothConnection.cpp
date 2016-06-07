#include <jni.h>
#include "com_nhl_spindp_bluetooth_BluetoothConnection.h"

using namespace std;

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_bluetooth_BluetoothConnection_setupBluetooth
  (JNIEnv *, jobject)
{
	return false;
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_bluetooth_BluetoothConnection_connectionLoop
  (JNIEnv *, jobject)
{

}

JNIEXPORT void JNICALL Java_com_nhl_spindp_bluetooth_BluetoothConnection_cleanupBluetooth
  (JNIEnv *, jobject)
{

}
