package com.nhl.spindp;

import jssc.SerialPortList;

import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

public class Main
{
	ServoConnection conn;
	
	public static void main(String[] args) throws Exception
	{
		Main p = new Main();
		
		SpiderBody body = new SpiderBody();
		body.testCalcs();		
		
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
		
		p.conn = new ServoConnection("/dev/ttyAMA0");
		/*System.out.print("Sending reset... ");
		p.conn.sendResetToAll();
		System.out.println("Reset send.");
		System.out.println("Sending instruction to: " + String.format("%2x", 1).toUpperCase());
		if (!p.conn.sendInstruction((byte)1, ServoConnection.INSTRUCTION_WRITE_DATA))
		{
			//System.err.println("Instruction not recieved: " + String.format("%2x", p.conn.getError()).toUpperCase());
			//System.exit(1);
		}
		else
		{
			System.out.println("Sent instruction to: " + String.format("%2x", 1).toUpperCase());
		}*/
		for (byte i = 1; i <= 18; i++)
		{
			for (short j = 0; j < 256; j++)
			{
				p.conn.move(i, (short)(j * 4));
			}
		}
		
		System.exit(0);
	}
}
