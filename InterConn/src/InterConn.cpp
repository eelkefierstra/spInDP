//============================================================================
// Name        : SerialTest.cpp
// Author      :
// Version     : 0.1b
// Copyright   : Your copyright notice
// Description : Hello Serial in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream>
#include <thread>
#include <chrono>
#include <iomanip>
#include <boost/asio.hpp>
#include "blocking_header.h"
#include <string>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <vector>
#include <iterator>


#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"
#define I2COUTPUT  "/tmp/I2C_OUT" //TODO: multiple files for multiple sensors??

using namespace std;

void i2c();

bool done = false;
string filename = "/dev/i2c-1";

int main(int argc, char *argv[])
{
	//start I2C reading
    thread t1 = thread(i2c);

	static boost::asio::io_service ios;
	string device = "/dev/serial0";
	if (argc > 1)
	{
		device = argv[1];
	}
	boost::asio::serial_port sp(ios, device);
	sp.set_option(boost::asio::serial_port::baud_rate(1000000));
	blocking_reader reader(sp, 5);
	string res;
	char c;

	int signalPin = 18;

	mkfifo(SERIAL_IN, 0666);
	mkfifo(SERIAL_OUT, 0666);
	//mknod(SERIAL_IN, S_IFIFO|0666, 0);
	//mknod(SERIAL_OUT, S_IFIFO|0666, 0);
	ofstream pigs;
	ofstream s_out;
	ifstream s_in;
	char charBuff[32];
	char readBuff[32];

	done = false;

	//char test[6] = { 0xFF, 0xFF, 0x01, 0x02, 0x00, 0xFC };

	s_in.open(SERIAL_OUT);
	s_out.open(SERIAL_IN);

	while (!done)
	{
		memset(readBuff, 0, sizeof readBuff);
		//s_in.open(SERIAL_OUT);
		pigs.open(PIGPIO);
		pigs << "w " << signalPin << " 1" << endl;
	    //getline(s_in, line);
		s_in.read(readBuff, 32);
		memcpy(charBuff, readBuff, 4 + readBuff[3]);
	    //s_in.close();
		sp.write_some(boost::asio::buffer(string(charBuff, 4 + charBuff[3])));
		this_thread::sleep_for(chrono::microseconds(1));
		pigs << "w " << signalPin << " 0" << endl;
		pigs.flush();
		pigs.close();
		ostringstream ss;
		ss << hex << setfill('0');
		for (char c : charBuff)
			ss << c;
		cout << "sent: " << ss.str() << endl;
		while(reader.read_char(c) && c != '\n')
		{
			res += c;
		}
		if (res.compare("") == 0)
		{
			char prefix[2] = { 0x00, 0x00 };
			res.append(prefix);
		}
		//sp.read_some(boost::asio::buffer(tmp));
		//s_out.open(SERIAL_IN);
		cout << "received: " << res << endl;
		s_out << res << endl;
		s_out.flush();
		//s_out.close();
		res = "";
	}
	sp.close();
	t1.join();
	return 0;
}


//===============================================
//	I2C communication methods
//===============================================

bool i2cSetup(int &file)
{
	if ((file=open(filename.c_str(),O_RDWR)) < 0)
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
	if (write(file, buf, 1) != 0)
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
	if (write(file, buff, 2) != 1)
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
	if (write(file, buff, 4) != 4)
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

void i2c()
{
	int file;
	int err = 0;
	bool gyro = false, adc = false;

	//open I2C bus
	while (!i2cSetup(file) && err < 5)
	{
		++err;
		if(err==5)
		{
			return;
		}
	}
	err = 0;


	//start continues scan gyro
	while (err < 3)
	{
		bool working = i2cSetupGyro(file);
		if(working)
		{
			gyro = true;
			break;
		}

		gyro = false;
		++err;
	}
	err = 0;

	//start continues scan ADC
	while (err < 3)
	{
		bool working = i2cSetupADC(file);
		if(working)
		{
			adc = true;
			break;
		}

		adc = false;
		++err;
	}
	err = 0;


	while (!done)
	{
		if(adc)
		{
			vector<char> result = i2cReadADC(file);
			//TODO set data in file
			for (char c : result)
			{
				cout << c;
			}
			cout << endl;
		}
		if(gyro)
		{
			vector<char> result = i2cReadGyro(file);
			//TODO set data in file
			for (char c : result)
			{
				cout << c;
			}
			cout << endl;
		}
		//TODO: add read for gyro, but since it is not connected it can wait
	}


	//cleanup your mess afterwards
	//stop gyro
	while (!i2cCleanGyro(file) && err<3)
		++err;
	err = 0;

	//stop ADC
	while (!i2cCleanADC(file) && err<3)
		++err;
	err = 0;

	//Kill everything(just I2C bus occupation)
	while (!i2cClean(file) && err<5)
		++err;
}
