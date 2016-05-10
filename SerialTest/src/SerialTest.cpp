//============================================================================
// Name        : SerialTest.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <boost/asio.hpp>

using namespace std;

int main()
{
	static boost::asio::io_service ios;
	boost::asio::serial_port sp(ios, "/dev/serial0");
	sp.set_option(boost::asio::serial_port::baud_rate(1000000));
	// You can set other options using similar syntax
	char tmp[64];
	//auto length = sp.read_some(boost::asio::buffer(tmp));
	// process the info received
	std::string message = "hello, world";
	sp.write_some(boost::asio::buffer(message));
	//char* mess = { 255, 255, 1, 2, 1, 251 };
	//sp.write_some(mess);
	sp.close();
	return 0;
}
