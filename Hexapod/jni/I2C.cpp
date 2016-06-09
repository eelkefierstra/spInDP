#include <jni.h>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <unistd.h>
//#include <linux/i2c-dev.h>
#include "I2Cdev.h"
#include "MPU6050.h"
//#include <sys/ioctl.h>
#include <vector>
#include <iterator>
#include <fcntl.h>
#include "com_nhl_spindp_i2c_I2C.h"

#define DEVICE "/dev/i2c-1"

using namespace std;

bool gyro = false, adc = false;
MPU6050 gyroscope;
uint8_t gyroAddr = 0x68, adcAddr = 0x48;
/*
void throw_java_exception(JNIEnv *env, char *classname, char *message)
{
	jclass ex = env->FindClass(classname);
	env->ThrowNew(ex, message);
}*/

bool i2cSetupGyro()
{
    //wake up gyro
	//return I2Cdev::writeBit( gyroAddr, 0x6b, 6, 0b0);
	gyroscope.initialize();
	return gyroscope.testConnection();
}

bool i2cSetupADC()
{
    //wake up adc
    uint16_t buff = 0x0482;
    return I2Cdev::writeWord( adcAddr, 0x01, buff);
}

bool i2cCleanGyro()
{
	//knock down gyro
	return I2Cdev::writeBit( gyroAddr, 0x6b, 6, 1);
}

bool i2cCleanADC()
{
	//knock down adc
	uint16_t buff = 0x0403 ;
    return I2Cdev::writeWord( adcAddr, 0x01, buff);
}

JNIEXPORT jboolean JNICALL Java_com_nhl_spindp_i2c_I2C_initI2c
  (JNIEnv *env, jobject)
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
	if (!gyro && !adc)
	{
		string clazz = "java/io/IOException";
		string mess  = "Cant't open the device";
		jclass ex = env->FindClass(&clazz[0]);
		env->ThrowNew(ex, &mess[0]);
		return false;
	}
	cout<< "bools= adc: "<<adc<<" gyro: "<<gyro<<endl;
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
		uint8_t res[2] = { -1, -1};
		I2Cdev::readBytes( adcAddr, 0x00, 2, res);
		env->SetShortField(dataObj, adcValField, (res[ 0] << 8) | res[ 1]);
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
		
		int16_t gx, gy, gz, ax, ay, az;

		gyroscope.getMotion6( &ax, &ay, &az, &gx, &gy, &gz);

		env->SetShortField(dataObj, accDataXField, (short) ax);//readWord(0x68,0x3B)
		env->SetShortField(dataObj, accDataYField, (short) ay);//readWord(0x68,0x3D)
		env->SetShortField(dataObj, accDataZField, (short) az);//readWord(0x68,0x3F)
		//env->SetShortField(dataObj,      tmpField, (result[ 6] << 8) | result[ 7]);
		env->SetShortField(dataObj,    gyroXField, (short) gx);//readWord(0x68,0x43)
		env->SetShortField(dataObj,    gyroYField, (short) gy);//readWord(0x68,0x45)
		env->SetShortField(dataObj,    gyroZField, (short) gz);//readWord(0x68,0x47)
		if (env->ExceptionCheck()) return;
	}
	env->SetObjectField(thisObj, dataField, dataObj);
	if (env->ExceptionCheck()) return;
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
