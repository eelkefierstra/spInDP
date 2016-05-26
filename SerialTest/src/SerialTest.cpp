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

using namespace std;

void i2c();

bool done = false;
string filename = "/dev/i2c-1";

int main()
{
	static boost::asio::io_service ios;
	boost::asio::serial_port sp(ios, "/dev/serial0");
	sp.set_option(boost::asio::serial_port::baud_rate(1000000));
	blocking_reader reader(sp, 5);
	string res;
	char c;

	int signalPin = 18;

	mknod(SERIAL_IN, S_IFIFO|0666, 0);
	mknod(SERIAL_OUT, S_IFIFO|0666, 0);
	ofstream pigs;
	ofstream s_out;
	ifstream s_in;
	string line;

	//char test[] = { 0xFF, 0xFF, 0x01, 0x02, 0x00, 0xFC };

	while (!done)
	{
		s_in.open(SERIAL_OUT);
		pigs.open(PIGPIO);
		pigs << "w " << signalPin << " 1" << endl;
	    getline(s_in, line);
	    s_in.close();
		sp.write_some(boost::asio::buffer(line));
		this_thread::sleep_for(chrono::microseconds(2));
		pigs << "w " << signalPin << " 0" << endl;
		pigs.flush();
		pigs.close();
		cout << "sent: " << line << endl;
		while(reader.read_char(c) && c != '\n')
		{
			res += c;
		}
		//sp.read_some(boost::asio::buffer(tmp));
		s_out.open(SERIAL_IN);
		cout << "received: " << res << endl;
		s_out << res;
		s_out.flush();
		s_out.close();
		res = "";
	}

	sp.close();
	return 0;
}

bool i2cSetup(int &file)
{
	int addr = 0x68;

	if ((file=open(filename.c_str(),O_RDWR)) < 0)
	{
        cout << "Failed to open the bus." << endl;
        return false;
	}

    if (ioctl(file,I2C_SLAVE,addr) < 0)
    {
        cout << "Failed to acquire bus access and/or talk to slave." << endl;
        return false;
    }

    //wake up gyro
    char buff[2]={0x6B,0};
    if (write(file,buff,2) != 1)
	{
		cout << "failde to wake device" << endl;
		return false;
	}
    return true;
}

vector<char> i2cRead(int &file)
{
	char buf[28] = {0};

	buf[0]=0x3B;
	if (write(file,buf,1) != 1)
	{
		cout << "register request failed" << endl;
	}
	else
	{
		// Using I2C Read
		if (read(file,buf,14) != 14)
		{
			/* More data expected*/
			cout << "Failed to read correctly from the i2c bus." << endl;
			cout << endl;
		}
		else
		{
			vector<char> data(begin(buf),end(buf));
			return data;
		}
	}
	return vector<char>();
}

bool i2cClean(int &file)
{
	//wake up gyro
	char buff[2]={0x6B,1};
	if (write(file,buff,2) != 1)
	{
		cout << "fail to put deviceto sleep" << endl;
		return false;
	}

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

	i2cSetup(file);
	while (!done)
	{
		i2cRead(file);
	}
	i2cClean(file);
}


/*
void sensors_ADC_init()
{
    int file;
    char filename[40];
    int addr = 0b00101001;        // The I2C address of the ADC

    sprintf(filename,"/dev/i2c-2");
    if ((file = open(filename,O_RDWR)) < 0)
    {
        printf("Failed to open the bus.");
        //ERROR HANDLING; you can check errno to see what went wrong
        exit(1);
    }

    if (ioctl(file,I2C_SLAVE,addr) < 0)
    {
        printf("Failed to acquire bus access and/or talk to slave.\n");
        //ERROR HANDLING; you can check errno to see what went wrong
        exit(1);
    }

    char buf[10] = {0};
    float data;
    char channel;

    for(int i = 0; i<4; i++)
    {
        // Using I2C Read
        if (read(file,buf,2) != 2)
        {
            //ERROR HANDLING: i2c transaction failed
            printf("Failed to read from the i2c bus.\n");
            printf("\n\n");
        }
        else
        {
            data = (float)((buf[0] & 0b00001111)<<8)+buf[1];
            data = data/4096*5;
            channel = ((buf[0] & 0b00110000)>>4);
            printf("Channel %02d Data:  %04f\n",channel,data);
        }
    }

    //unsigned char reg = 0x10; // Device register to access
    //buf[0] = reg;
    buf[0] = 0b11110000;

    if (write(file,buf,1) != 1)
    {
        //ERROR HANDLING: i2c transaction failed
        printf("Failed to write to the i2c bus.\n");
        printf("\n\n");
    }
}*/
