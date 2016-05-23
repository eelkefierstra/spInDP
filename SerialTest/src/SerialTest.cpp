//============================================================================
// Name        : SerialTest.cpp
// Author      : 
// Version     : 0.1b
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream>
#include <thread>
#include <chrono>
#include <boost/asio.hpp>
#include "blocking_header.h"
#include <glib.h>
#include <glib/gprintf.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"

using namespace std;

void i2c();

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
	bool done = false;

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

void i2cSetup()
{
	int file;
	string filename = "/dev/i2c-1";
	const gchar *buffer;
	int addr = 0x68;

	if ((file=open(filename,O_RDWR)) < 0)
	{
        cout << "Failed to open the bus." << endl;
        exit(1);
	}

    if (ioctl(file,I2C_SLAVE,addr) < 0)
    {
        cout << "Failed to acquire bus access and/or talk to slave." << endl;
        exit(1);
    }
}

void i2cRead(int *file)
{
	char buf[20] = {0};
	float data;
	char channel;

	// Using I2C Read
	if (read(file,buf,14) != 14)
	{
		/* ERROR HANDLING: i2c transaction failed
		 * More data expected*/
		cout << "Failed to read from the i2c bus." << endl;
		buffer = g_strerror(errno);
		printf(buffer);
		cout << endl;
	}
	else
	{
		data = (float)((buf[0] & 0b00001111)<<8)+buf[1];
		data = data/4096*5;
		channel = ((buf[0] & 0b00110000)>>4);
		cout << "Data: " << data<< endl;
	}
}

void sensors_ADC_init()
{
    int file;
    char filename[40];
    const gchar *buffer;
    int addr = 0b00101001;        // The I2C address of the ADC

    sprintf(filename,"/dev/i2c-2");
    if ((file = open(filename,O_RDWR)) < 0)
    {
        printf("Failed to open the bus.");
        /* ERROR HANDLING; you can check errno to see what went wrong */
        exit(1);
    }

    if (ioctl(file,I2C_SLAVE,addr) < 0)
    {
        printf("Failed to acquire bus access and/or talk to slave.\n");
        /* ERROR HANDLING; you can check errno to see what went wrong */
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
            /* ERROR HANDLING: i2c transaction failed */
            printf("Failed to read from the i2c bus.\n");
            buffer = g_strerror(errno);
            printf(buffer);
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
        /* ERROR HANDLING: i2c transaction failed */
        printf("Failed to write to the i2c bus.\n");
        buffer = g_strerror(errno);
        printf(buffer);
        printf("\n\n");
    }
}
