//============================================================================
// Name        : SerialTest.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream>
#include <boost/asio.hpp>

#define SERIAL_IN  "/tmp/S_IN"
#define SERIAL_OUT "/tmp/S_OUT"
#define PIGPIO     "/dev/pigpio"

using namespace std;

int main()
{
	static boost::asio::io_service ios;
	boost::asio::serial_port sp(ios, "/dev/serial0");
	sp.set_option(boost::asio::serial_port::baud_rate(1000000));
	char tmp[64];

	int signalPin = 18;

	mknod(SERIAL_IN, S_IFIFO|0666, 0);
	mknod(SERIAL_OUT, S_IFIFO|0666, 0);
	ofstream pigs;
	ofstream s_out;
	ifstream s_in;
	string line;
	bool done = false;

	char test[] = { 0xFF, 0xFF, 0x01, 0x02, 0x00, 0xFC };

	while (!done)
	{
		s_in.open(SERIAL_OUT);
	    getline(s_in, line);
		s_in.close();
		cout << line << endl;
		pigs.open(PIGPIO);
		pigs << "w " << signalPin << " 1" << endl;
		sp.write_some(boost::asio::buffer(line));
		pigs << "w " << signalPin << " 0" << endl;
		pigs.flush();
		pigs.close();
		////sp.read_some(boost::asio::buffer(tmp));
		//s_out.open(SERIAL_IN);
		////s_out << tmp;
		//s_out << test;
		//s_out.flush();
		//s_out.close();
	}

	sp.close();
	return 0;
}
