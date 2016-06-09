/*
 * Main.cpp
 *
 *  Created on: Jun 6, 2016
 *      Author: pi
 */

#include <unistd.h>
#include <iostream>
#include <fstream>
#include <sys/stat.h>
#include "com_nhl_spindp_Main.h"

#define PIDFILE "/tmp/Hexapod.pid"

using namespace std;

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_Main_isAlreadyRunning
  (JNIEnv *, jclass)
{
	struct stat buff;
	if (stat(PIDFILE, &buff) == 0)
	{
		return true;
	}
	pid_t pid = getpid();
	ofstream pidFile;
	pidFile.open(PIDFILE);
	pidFile << pid << endl;
	pidFile.close();
	return false;
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_Main_cleanup
  (JNIEnv *, jclass)
{
	remove(PIDFILE);
}
