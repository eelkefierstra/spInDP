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
#include <iomanip>
#include <boost/asio.hpp>
#include "blocking_header.h"


#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"

using namespace std;

int main(int argc, char *argv[])
{
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
	for (int i = 0; i < 32; i++)
		readBuff[i] = 0;
	bool done = false;

	//char test[6] = { 0xFF, 0xFF, 0x01, 0x02, 0x00, 0xFC };

	while (!done)
	{
		s_in.open(SERIAL_OUT);
		pigs.open(PIGPIO);
		pigs << "w " << signalPin << " 1" << endl;
	    //getline(s_in, line);
		s_in.read(readBuff, 32);
		memcpy(charBuff, readBuff, 4 + readBuff[3]);
	    s_in.close();
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
		s_out.open(SERIAL_IN);
		cout << "received: " << res << endl;
		s_out << res << endl;
		s_out.flush();
		s_out.close();
		res = "";
	}
	sp.close();
	return 0;
}
