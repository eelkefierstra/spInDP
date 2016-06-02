/*
 * SerialPort.cpp
 *
 *  Created on: 18 May 2016
 *      Author: dudecake
 */

#include <jni.h>
#include <iostream>
#include <fstream>
#include <thread>
#include <chrono>
#include <string.h>
#include <errno.h>
#include <termios.h>
#include <unistd.h>
#include <fcntl.h>
#include "com_nhl_spindp_serialconn_SerialPort.h"

#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"

using namespace std;

int fd = 0;

void throw_java_exception(JNIEnv *env, char *classname, char *message)
{
	jclass ex = env->FindClass(classname);
	env->ThrowNew(ex, message);
}

int set_interface_attribs(int fd, int speed, int parity)
{
        struct termios tty;
        memset(&tty, 0, sizeof tty);
        if (tcgetattr (fd, &tty) != 0)
        {
                cout << "error %d from tcgetattr" << errno << endl;
                return -1;
        }

        cfsetospeed (&tty, speed);
        cfsetispeed (&tty, speed);

        tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;     // 8-bit chars
        // disable IGNBRK for mismatched speed tests; otherwise receive break
        // as \000 chars
        tty.c_iflag &= ~IGNBRK;         // disable break processing
        tty.c_lflag = 0;                // no signaling chars, no echo,
                                        // no canonical processing
        tty.c_oflag = 0;                // no remapping, no delays
        tty.c_cc[VMIN]  = 0;            // read doesn't block
        tty.c_cc[VTIME] = 5;            // 0.5 seconds read timeout

        tty.c_iflag &= ~(IXON | IXOFF | IXANY); // shut off xon/xoff ctrl

        tty.c_cflag |= (CLOCAL | CREAD);// ignore modem controls,
                                        // enable reading
        tty.c_cflag &= ~(PARENB | PARODD);      // shut off parity
        tty.c_cflag |= parity;
        tty.c_cflag &= ~CSTOPB;
        tty.c_cflag &= ~CRTSCTS;

        if (tcsetattr (fd, TCSANOW, &tty) != 0)
        {
                cout << "error " << errno << " from tcsetattr" << endl;
                return -1;
        }
        return 0;
}

void set_blocking (int fd, int should_block)
{
        struct termios tty;
        memset(&tty, 0, sizeof tty);
        if (tcgetattr (fd, &tty) != 0)
        {
                cout << "error " << errno <<" from tggetattr" << endl;
                return;
        }

        tty.c_cc[VMIN]  = should_block ? 1 : 0;
        tty.c_cc[VTIME] = 1;            // 0.5 seconds read timeout

        if (tcsetattr (fd, TCSANOW, &tty) != 0)
                cout << "error " << errno << " setting term attributes" << endl;
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_serialconn_SerialPort_initPort
  (JNIEnv *env, jobject, jstring port)
{
	char* device = (char*)env->GetStringUTFChars(port, NULL);
	if ((fd = open(device, O_RDWR | O_NOCTTY | O_SYNC)) < 0)
	{
		string clazz = "java/io/IOException";
		string mess  = "Cant't open the device";
		throw_java_exception(env, &clazz[0], &mess[0]);
		return;
	}
	set_interface_attribs(fd, B1000000, 0);
	set_blocking(fd, 0);
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_serialconn_SerialPort_cleanupPort
  (JNIEnv *env, jobject)
{
	close(fd);
	fd = 0;
}

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_serialconn_SerialPort_nativeWrite
  (JNIEnv *env, jobject, jbyteArray message)
{
	if (fd <= 0)
	{
		string clazz = "java/lang/IllegalStateException";
		string mess  = "initPort must be previously called";
		throw_java_exception(env, &clazz[0], &mess[0]);
	}
	jsize length = env->GetArrayLength(message);
	if (length == 0)
	{
		string clazz = "java/lang/IllegalArgumentException";
		string mess  = "message can't be empty";
		throw_java_exception(env, &clazz[0], &mess[0]);
	}
	ofstream pigs;
	int signalPin = 18;
	pigs.open(PIGPIO);
	pigs << "w " << signalPin << " 1" << endl;
	this_thread::sleep_for(chrono::microseconds(10));
	jbyte *messPntr = env->GetByteArrayElements(message, NULL);
	cout << "sending: " << messPntr << endl;
	write(fd, messPntr, length);
	this_thread::sleep_for(chrono::microseconds(20));
	pigs << "w " << signalPin << " 0" << endl;
	pigs.flush();
	pigs.close();

	return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_nhl_spindp_serialconn_SerialPort_nativeRead
  (JNIEnv *env, jobject, jint)
{
	char buff[32];
	memset(&buff, 0, sizeof buff);
	int len = 0;
	if ((len = read(fd, buff, sizeof buff)) < 0)
	{
		close(fd);
		fd = 0;
		string clazz = "java/io/IOException";
		string mess  = "Failed to read from serial port";
		throw_java_exception(env, &clazz[0], &mess[0]);
	}
	cout << "received: " << buff << endl;
	jsize size = 4 + buff[3];
	jbyte res[size];
	for (int i = 0; i < size; i++)
	{
		res[i] = buff[i];
	}
	jbyteArray resArr = env->NewByteArray(size);
	env->SetByteArrayRegion(resArr, 0, size, res);
	return resArr;
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
	memset(readBuff, 0, sizeof readBuff);

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
