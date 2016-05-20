//============================================================================
// Name        : SerialTest.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream>
#include <thread>
#include <chrono>
#include <boost/asio.hpp>
#include "blocking_header.h"

#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"

using namespace std;

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
		this_thread::sleep_for(chrono::microseconds(1));
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
