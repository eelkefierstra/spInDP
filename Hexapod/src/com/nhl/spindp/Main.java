package com.nhl.spindp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nhl.spindp.netcon.WebSocket;
import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

@SuppressWarnings("unused")
public class Main
{
	private static Main instance;
	private static ServoConnection conn;
	public static List<Short> failedServos;
	
	static
	{
		File lib = new File(Main.class.getResource("/libs/").getPath(), "libHexapod.so");
		System.load(lib.getAbsolutePath());
	}
	
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
		/*
		WebSocket sock = new WebSocket(8000);
		sock.start();*/
		
		failedServos = new ArrayList<>();
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
			body.walk(1.0, 0.0);
		}
	}
	
	public static void servoFailed(short id)
	{
		for (Short s : failedServos)
		{
			if (s.equals(id)) return;
		}
		failedServos.add(id);
		failedServos.sort(null);
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
	
	/**
	 * Dirty hack for if the C++ thing doesn't work out. Don't use, seriously
	 * @param ids The id's of the servo's to be moved
	 * @param angles The angles to move to
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Deprecated
	public void driveServoInPython(int[] ids, int[] angles) throws IOException, InterruptedException
	{
		if (ids.length != angles.length) throw new IllegalArgumentException("Arrays must have same length");
		String line = "";
		for (int i = 0; i < ids.length; i++)
		{
			Process p = new ProcessBuilder("python", "~/git/spInDP/python/moveTo.py", String.valueOf(ids[i]), String.valueOf(angles[i])).start();
			p.waitFor();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1);
			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);
			}
		}
	}
}
