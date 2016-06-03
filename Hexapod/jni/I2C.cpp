#include <jni.h>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <unistd.h>
//#include <linux/i2c-dev.h>
#include "I2Cdev.h"
//#include <sys/ioctl.h>
#include <vector>
#include <iterator>
#include <fcntl.h>
#include "com_nhl_spindp_i2c_I2C.h"

#define DEVICE "/dev/i2c-1"

using namespace std;

bool gyro = false, adc = false;

short readWord(uint8_t addr, uint8_t reg)
{
	uint8_t buf[2] = { 0 };
	if (I2Cdev::readBytes(addr, reg, 2, buf) != 2)
		return 0;
	short result = (buf[0] << 8) | buf[1];
	return result;
}

bool i2cSetupGyro()
{
    //wake up gyro
	return I2Cdev::writeBit( 0x68, 0x6b, 6, 0b0);
}

bool i2cSetupADC()
{
    //wake up adc
    uint8_t buff[2] = { 0x04, 0x83 };
    return I2Cdev::writeBytes( 0x44, 0x01, 2, buff);
}

bool i2cCleanGyro()
{
	//knock down gyro
	return I2Cdev::writeBit( 0x68, 0x6b, 6, 1);
}

bool i2cCleanADC()
{
	//knock down adc
	uint8_t buff[2] = { 0x04, 0x03 };
    return I2Cdev::writeBytes( 0x44, 0x01, 2, buff);
}

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_i2c_I2C_initI2c
  (JNIEnv *, jobject)
{
	int err = 0;
	while (err < 3)
	{
		if (i2cSetupGyro())
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
		if (i2cSetupADC())
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

JNIEXPORT void JNICALL Java_com_nhl_spindp_i2c_I2C_loopI2c
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
		jfieldID adcValField = env->GetFieldID(dataCls, "adcVal", "S");
		if (env->ExceptionCheck()) return;
		env->SetShortField(dataObj, adcValField, readWord(0x44,0x00));
		if (env->ExceptionCheck()) return;
	}

	if(gyro)
	{
		jfieldID accDataXField = env->GetFieldID(dataCls, "accDataX", "S");
		jfieldID accDataYField = env->GetFieldID(dataCls, "accDataY", "S");
		jfieldID accDataZField = env->GetFieldID(dataCls, "accDataZ", "S");
		//jfieldID tmpField      = env->GetFieldID(dataCls, "tmp", "S");
		jfieldID gyroXField    = env->GetFieldID(dataCls, "gyroX", "S");
		jfieldID gyroYField    = env->GetFieldID(dataCls, "gyroY", "S");
		jfieldID gyroZField    = env->GetFieldID(dataCls, "gyroZ", "S");
		if (env->ExceptionCheck()) return;
		env->SetShortField(dataObj, accDataXField, readWord(0x68,0x3B));
		env->SetShortField(dataObj, accDataYField, readWord(0x68,0x3D));
		env->SetShortField(dataObj, accDataZField, readWord(0x68,0x3F));
		//env->SetShortField(dataObj,      tmpField, (result[ 6] << 8) | result[ 7]);
		env->SetShortField(dataObj,    gyroXField, readWord(0x68,0x43));
		env->SetShortField(dataObj,    gyroYField, readWord(0x68,0x45));
		env->SetShortField(dataObj,    gyroZField, readWord(0x68,0x47));
		if (env->ExceptionCheck()) return;
	}
}

JNIEXPORT void JNICALL Java_com_nhl_spindp_i2c_I2C_cleanupI2c
  (JNIEnv *, jobject)
{
	int err = 0;
	//cleanup your mess afterwards
	//stop gyro
	while (!i2cCleanGyro() && err<3)
		++err;
	err = 0;

	//stop ADC
	while (!i2cCleanADC() && err<3)
		++err;
}
