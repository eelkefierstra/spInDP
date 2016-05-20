package com.nhl.spindp;

import java.util.Arrays;

import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

public class Main
{
	private static Main instance;
	private static ServoConnection conn;
	public static short[] failedServos;
	
	public static Main getInstance()
	{
		return instance;
	}
	
	
	/**
	 * Implementation of the main program.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		failedServos = new short[18];
		Arrays.fill(failedServos, (short)-1);
		instance = new Main();
		Time.updateDeltaTime();
		SpiderBody body = new SpiderBody(1);
		//body.testCalcs();
				
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
		
		while (true)
		{
			Time.updateDeltaTime();
			body.testLegMovements();
		}
	}
	
	public static void servoFailed(short id)
	{
		int i = 0;
		for (; i < failedServos.length; i++)
		{
			if (failedServos[i] == id) return;
			if (failedServos[i] == -1) break;
		}
		failedServos[i] = id;
		Arrays.sort(failedServos);
	}
	
	/**
	 * Sends instructions to connection to drive servo's
	 * @param ids The id's of the servo's to be moved
	 * @param angles The angles to move to
	 */
	public void driveServo(int[] ids, int[] angles)
	{
		if (ids.length != angles.length) throw new IllegalArgumentException("Arrays must have the same length");
		for (int i = 0; i < ids.length; i++)
		{
			try
			{
				conn.moveServo((byte)ids[i], (short)angles[i]);
				//System.out.println(conn.readPresentLocation((byte)i));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
			//Thread.sleep(1000);
	}
}
