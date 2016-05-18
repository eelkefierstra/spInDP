/*
 * SerialPort.cpp
 *
 *  Created on: 18 May 2016
 *      Author: dudecake
 */

#include <jni.h>
#include <iostream>
#include <fstream>
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
	ofstream s_out;
	s_out.open(SERIAL_OUT);
	s_out << messPntr;
	s_out.flush();
	s_out.close();
	return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_nhl_spindp_serialconn_SerialPort_nativeReadBytes
  (JNIEnv *env, jobject, jint id)
{
	string buff;
	ifstream s_in;
	s_in.open(SERIAL_IN);
	getline(s_in, buff);
	s_in.close();
	jbyte res[buff.length()];
	for (unsigned i = 0; i < buff.length(); i++)
	{
		buff[i] = res[i];
	}
	jbyteArray resArr = env->NewByteArray(buff.length());
	env->SetByteArrayRegion(resArr, 0, buff.length(), res);
	return resArr;
}
