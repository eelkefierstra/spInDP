/*
 * SerialPort.cpp
 *
 *  Created on: 18 May 2016
 *      Author: dudecake
 */

#include <jni.h>
#include <iostream>
#include <fstream>
#include <string.h>
#include "com_nhl_spindp_serialconn_SerialPort.h"

#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"

using namespace std;

void throw_java_exception(JNIEnv *env, char *classname, char *message)
{
	jclass ex = env->FindClass(classname);
	env->ThrowNew(ex, message);
}

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_serialconn_SerialPort_nativeWriteBytes
  (JNIEnv *env, jobject, jbyteArray message)
{
	jsize length = env->GetArrayLength(message);
	if (length == 0)
	{
		string clazz = "java/lang/IllegalArgumentException";
		string mess  = "message can't be empty";
		throw_java_exception(env, &clazz[0], &mess[0]);
	}
	jbyte *messPntr = env->GetByteArrayElements(message, NULL);
	//cout << "writing to s_out" << endl;
	ofstream s_out;
	s_out.open(SERIAL_OUT);
	s_out << messPntr;
	s_out.flush();
	s_out.close();
	return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_nhl_spindp_serialconn_SerialPort_nativeReadBytes
  (JNIEnv *env, jobject, jint)
{
	char buff[32];
	char readBuff[32];
	for (int i = 0; i < 32; i++)
		readBuff[i] = 0;

	cout << "reading from s_in" << endl;
	ifstream s_in;
	s_in.open(SERIAL_IN);
	s_in.read(readBuff, 32);
	//getline(s_in, buff);
	s_in.close();
	//cout << "received: " << buff << endl;
	jsize size = 4 + readBuff[3];
	memcpy(buff, readBuff, size);
	if (size == 4)
	{
		size = 2;
		buff[0] = buff[1] = 0xEE;
	}
	jbyte res[size];
	for (int i = 0; i < size; i++)
	{
		res[i] = buff[i];
	}
	jbyteArray resArr = env->NewByteArray(size);
	env->SetByteArrayRegion(resArr, 0, size, res);
	return resArr;
}
