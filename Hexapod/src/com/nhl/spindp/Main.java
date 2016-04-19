package com.nhl.spindp;

import jssc.*;

public class Main
{
	SerialPort sPort;
	
	
	public static void main(String[] args) throws SerialPortException
	{
		
		//Main p = new Main();
		/*p.sPort = new SerialPort("/dev/ttyAMA0");
		p.sPort.setParams(1000000, 8, 1, 0);
		p.sPort.openPort();
		p.sPort.writeBytes(DatatypeConverter.parseHexBinary("FF FF 01 05 03 1E 32 03 A3"));*/
		
		String[] portNames = SerialPortList.getPortNames();
		if (portNames.length == 0)
		{
			System.out.println("No serial devices found");
		}
		else
		{
			for (String s : portNames)
			{
				System.out.println(s);
			}
		}
		
	}
}
