package com.nhl.spindp;

import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

public class Main
{
	private static Main instance;
	private static ServoConnection conn;
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static void main(String[] args) throws Exception
	{
		instance = new Main();
		
		SpiderBody body = new SpiderBody(1);
		body.testCalcs();
				
		conn = new ServoConnection();
		/*System.out.print("Sending reset... ");
		conn.sendResetToAll();
		System.out.println("Reset send.");
		System.out.println("Sending instruction to: " + String.format("%2x", 1).toUpperCase());
		if (!instance.conn.sendInstruction((byte)1, ServoConnection.INSTRUCTION_WRITE_DATA))
		{
			//System.err.println("Instruction not recieved: " + String.format("%2x", p.conn.getError()).toUpperCase());
			//System.exit(1);
		}
		else
		{
			System.out.println("Sent instruction to: " + String.format("%2x", 1).toUpperCase());
		}*/
		/*
		for (byte i = 1; i <= 18; i++)
		{
			for (short j = 0; j < 256; j++)
			{
				conn.moveServo(i, (short)(j * 4));
			}
		}*/
		body.testLegMovements();
	}
	
	public void driveServo(int[] ids, int[] angles)
	{
		if (ids.length != angles.length) throw new IllegalArgumentException("Arrays must have the same length");
		try
		{
			for (int i = 0; i < ids.length; i++)
			{
				conn.moveServo((byte)ids[i], (short)angles[i]);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
