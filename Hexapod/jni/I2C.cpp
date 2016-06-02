#include <jni.h>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <unistd.h>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <vector>
#include <iterator>
#include <fcntl.h>
#include "com_nhl_spindp_i2c_I2C.h"

#define DEVICE "/dev/i2c-1"

using namespace std;

int device = 0;
bool gyro = false, adc = false;

bool i2cSetup(int &file)
{
	if ((file = open(DEVICE, O_RDWR)) < 0)
	{
        cout << "Failed to open the bus." << endl;
        return false;
	}
    return true;
}

bool setGyro(int &file)
{
	int addr = 0x68;

	if (ioctl(file, I2C_SLAVE, addr) < 0)
	{
		cout << "Failed to acquire bus access and/or talk to gyro." << endl;
		return false;
	}
	return true;
}

bool setADC(int &file)
{
	int addr = 0x44;

	if (ioctl(file, I2C_SLAVE, addr) < 0)
	{
		cout << "Failed to acquire bus access and/or talk to adc." << endl;
		return false;
	}
	return true;
}

bool i2cSetupGyro(int &file)
{
	if (!setGyro(file))
		return false;

    //wake up gyro
    char buff[2]={0x6B,0};
    if (write(file,buff,2) < 0)
	{
		cout << "failed to wake device(gyro)" << endl;
		return false;
	}
    return true;
}

bool i2cSetupADC(int &file)
{
	if (!setADC(file))
		return false;

    //wake up adc
    char buff[3] = { 0x01, 0x04, (char)0x83 };
    if (write(file,buff,3) < 0)
	{
		cout << "failed to wake device(adc)" << endl;
		return false;
	}
    return true;
}

vector<char> i2cReadGyro(int &file)
{
	if (!setGyro(file))
		return vector<char>();

	char buf[28] = { 0 };

	buf[0] = 0x3B;
	if (write(file, buf, 1) < 0)
	{
		cout << "register request failed(gyro)" << endl;
	}
	else
	{
		// Using I2C Read
		if (read(file, buf, 14) != 14)
		{
			/* More data expected*/
			cout << "Failed to read correctly from the i2c bus." << endl;
		}
		else
		{
			vector<char> data(begin(buf), end(buf));
			return data;
		}
	}
	return vector<char>();
}

vector<char> i2cReadADC(int &file)
{
	if (!setADC(file))
		return vector<char>();

	/*
		[0]register to read(0b000 00..)
	*/
	char buf[2] = { 0x00 };

	if (write(file, buf, 1) < 0)
	{
		cout << "register request failed(ADC)" << endl;
	}
	else
	{
		// Using I2C Read
		if (read(file, buf, 2) != 2)
		{
			/* More data expected*/
			cout << "Failed to read correctly from the i2c bus. (ADC)" << endl;
		}
		else
		{
			vector<char> data(begin(buf), end(buf));
			return data;
		}
	}
	return vector<char>();
}

bool i2cCleanGyro(int &file)
{
	if (!setGyro(file))
		return false;

	//knock down gyro
	char buff[2] = { 0x6B, 1 };
	if (write(file, buff, 2) < 0)
	{
		cout << "Failed to put gyro to sleep" << endl;
		return false;
	}
	return true;
}

bool i2cCleanADC(int &file)
{
	if (!setADC(file))
		return false;

	//knock down adc
	char buff[4] = { 0x01, 0x04, 0x03 };
	if (write(file, buff, 4) != 4 < 0)
	{
		cout << "failed to put ADC to sleep" << endl;
	}
	return true;
}

bool i2cClean(int &file)
{
	if (close(file) < 0)
	{
		cout << "Failed to close the file." << endl;
		return false;
	}
	return true;
}

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_i2c_I2C_initI2c
  (JNIEnv *, jobject)
{
	int err = 0;
	while (!i2cSetup(device) && err < 5)
	{
		err++;
		if (err == 5) return false;
	}

	err = 0;
	while (err < 3)
	{
		bool working = i2cSetupGyro(device);
		if (working)
		{
			gyro = true;
			break;
		}
		gyro = false;
		err++;
	}

	err = 0;
	while (err < 3)
	{
		bool working = i2cSetupADC(device);
		if (working)
		{
			adc = true;
			break;
		}
		adc = false;
		err++;
	}
	err = 0;
	return true;
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_i2c_I2C_i2cLoop
  (JNIEnv *env, jobject thisObj)
{
	jclass dataCls = env->FindClass("com/nhl/spindp/i2c/I2C$I2CData");
	if (env->ExceptionCheck()) return;
	jfieldID dataField = env->GetFieldID(env->GetObjectClass(thisObj), "data", "Lcom/nhl/spindp/i2c/I2C$I2CData;");
	if (env->ExceptionCheck()) return;
	jobject dataObj = env->GetObjectField(thisObj, dataField);
	if (env->ExceptionCheck()) return;
	if(adc)
	{
		vector<char> result = i2cReadADC(device);
		//TODO set data in file
		jfieldID adcValField = env->GetFieldID(dataCls, "adcVal", "S");
		if (env->ExceptionCheck()) return;
		env->SetShortField(dataObj, adcValField, (result[0] << 8) | result[1]);
		if (env->ExceptionCheck()) return;
	}
	if(gyro)
	{
		vector<char> result = i2cReadGyro(device);
		//TODO set data in file
		jfieldID accDataXField = env->GetFieldID(dataCls, "accDataX", "S");
		jfieldID accDataYField = env->GetFieldID(dataCls, "accDataY", "S");
		jfieldID accDataZField = env->GetFieldID(dataCls, "accDataZ", "S");
		jfieldID tmpField      = env->GetFieldID(dataCls, "tmp", "S");
		jfieldID gyroXField    = env->GetFieldID(dataCls, "gyroX", "S");
		jfieldID gyroYField    = env->GetFieldID(dataCls, "gyroY", "S");
		jfieldID gyroZField    = env->GetFieldID(dataCls, "gyroZ", "S");
		if (env->ExceptionCheck()) return;
		env->SetShortField(dataObj, accDataXField, (result[ 0] << 8) | result[ 1]);
		env->SetShortField(dataObj, accDataYField, (result[ 2] << 8) | result[ 3]);
		env->SetShortField(dataObj, accDataZField, (result[ 4] << 8) | result[ 5]);
		env->SetShortField(dataObj,      tmpField, (result[ 6] << 8) | result[ 7]);
		env->SetShortField(dataObj,    gyroXField, (result[ 8] << 8) | result[ 9]);
		env->SetShortField(dataObj,    gyroYField, (result[10] << 8) | result[11]);
		env->SetShortField(dataObj,    gyroZField, (result[12] << 8) | result[13]);
		if (env->ExceptionCheck()) return;
	}
	//TODO: add read for gyro, but since it is not connected it can wait
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_i2c_I2C_cleanupI2c
  (JNIEnv *, jobject)
{
	int err = 0;
	//cleanup your mess afterwards
	//stop gyro
	while (!i2cCleanGyro(device) && err<3)
		++err;
	err = 0;

	//stop ADC
	while (!i2cCleanADC(device) && err<3)
		++err;
	err = 0;

	//Kill everything(just I2C bus occupation)
	while (!i2cClean(device) && err<5)
		++err;
}
