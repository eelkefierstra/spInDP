package com.nhl.spindp;

import java.io.IOException;

import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

import jssc.SerialPortException;

public class Main
{
	ServoConnection conn;
	
	public static void main(String[] args) throws SerialPortException, InterruptedException, IOException
	{
		Main p = new Main();
		/*p.sPort = new SerialPort("/dev/ttyAMA0");
		p.sPort.setParams(1000000, 8, 1, 0);
		p.sPort.openPort();
		p.sPort.writeBytes(DatatypeConverter.parseHexBinary("FF FF 01 05 03 1E 32 03 A3"));*/
		
		SpiderBody body = new SpiderBody();
		body.testCalcs();		
		
		p.conn = new ServoConnection("/dev/ttyAMA0");
		System.out.print("Sending reset... ");
		p.conn.sendResetToAll();
		System.out.println("Reset send.");
		System.out.println("Sending instruction to: " + String.format("%2x", 1).toUpperCase());
		if (!p.conn.sendInstruction((byte)1))
		{
			System.err.println("Instruction not recieved: " + String.format("%2x", p.conn.getError()).toUpperCase());
			//System.exit(1);
		}
		else
		{
			System.out.println("Sent instruction to: " + String.format("%2x", 1).toUpperCase());
		}
		for (short i = 0; i < 1024; i++)
		{
			p.conn.move((byte) 1, i);
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
		
		System.exit(0);
	}
}
