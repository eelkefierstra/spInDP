package com.nhl.spindp;

import com.nhl.spindp.serialconn.ServoConnection;

import jssc.SerialPortException;

public class Main
{
	ServoConnection conn;
	
	public static void main(String[] args) throws SerialPortException
	{
		
		Main p = new Main();
		/*p.sPort = new SerialPort("/dev/ttyAMA0");
		p.sPort.setParams(1000000, 8, 1, 0);
		p.sPort.openPort();
		p.sPort.writeBytes(DatatypeConverter.parseHexBinary("FF FF 01 05 03 1E 32 03 A3"));*/
		
		p.conn = new ServoConnection("/dev/ttyAMA0");
		p.conn.sendResetToAll();
		if (!p.conn.sendInstruction((byte)1))
		{
			System.err.println("Instruction not recieved");
		}
		
		/*String[] portNames = SerialPortList.getPortNames();
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
		}*/
		
		
	}
}
