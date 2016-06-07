package com.nhl.spindp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.nhl.spindp.i2c.I2C;
import com.nhl.spindp.netcon.AppConnection;
import com.nhl.spindp.netcon.WebSocket;
import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;

public class Main
{
	private static Main instance;
	private static ServoConnection conn;
	private static WebSocket sock;
	private static AppConnection appConn;
	private static boolean running = true;
	public static List<Short> failedServos;
	private volatile double forward = 1.0;
	private volatile double right   = 0.0;
	
	static
	{
		File lib = new File(Main.class.getResource("/libs/").getPath(), "libHexapod.so");
		System.load(lib.getAbsolutePath());
	}
	
	private static native boolean isAlreadyRunning();
	
	private static native void cleanup();
	
	public int readCurrentAngle(byte id) throws IOException{
		return conn.readPresentLocation(id);
	}
	
	public int readCurrentTemperature(byte id) throws IOException{
		return conn.readTemperature(id);
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
	{/*
		if (isAlreadyRunning())
		{
			System.out.println("Hexapod is already running");
			System.exit(1);
		}
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				cleanup();
			}
		});*/
		instance = new Main();
		
		/*
		Thread webWorker = new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("webWorker started");
				try
				{
					sock = new WebSocket(8000);
					sock.start();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					//System.exit(-1);
				}
			}
		};
		webWorker.start();*/

		I2C i2c = new I2C();
		i2c.start();
		//i2c.loopI2c();
		//i2c.getData();
		for (int i = 0;i < Integer.MAX_VALUE; i++)// (double d : i2c.getGyroInfo())
		{
			double[] da = i2c.getGyroInfo();
			System.out.println("x: "+da[0]+" y: "+da[1]);
		}
		i2c.stop();
		
		Thread appConnection = new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("App Server started123");
				try
				{
					appConn = new AppConnection(1338);
					appConn.mainLoop();
					System.out.println("App Server started");
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
		appConnection.start();
		conn = new ServoConnection();
		
		failedServos = new ArrayList<>();
		Time.updateDeltaTime();
		SpiderBody body = new SpiderBody((byte) 1);
		//body.testCalcs();
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
			conn.moveServo(i, (short)(j * 4));
		}*/
		//body.moveToAngle(45.0, 45.0, 45.0);
		
		while (running)
		{
			Time.updateDeltaTime();
			body.walk(instance.forward, instance.right);
			Thread.sleep(50);
		}
		
		if (sock != null)
		{
			sock.stop();
		}
		//webWorker.join();
		if(appConn != null)
		{
			appConn.stop();
		}
		appConnection.join();
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
	
	public void setDirection(int id, double forward, double right)
	{
		this.forward = forward;
		this.right   = right;
	}
	
	public static Future<byte[]> submitInstruction(byte[] message)
	{
		return conn.submitInstruction(message);
	}
	
	/**
	 * Sends instructions to connection to drive servo's
	 * @param ids The id's of the servo's to be moved
	 * @param angles The angles to move to
	 */
	@Deprecated
	public void driveServo(int[] ids, int[] angles)
	{
		if (ids.length != angles.length) throw new IllegalArgumentException("Arrays must have the same length");
		for (int i = 0; i < ids.length; i++)
		{
			try
			{
				conn.moveServo((byte)ids[i], (short)angles[i]);
				//System.out.println(conn.readPresentLocation((byte)i));
				Thread.sleep(5);
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
			Process p = new ProcessBuilder("python",
					"~/git/spInDP/python/goto.py",
					String.valueOf(ids[i]),
					String.valueOf(angles[i])).start();
					//.directory(new File("~/git/spInDP/python")).start();
			p.waitFor();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1);
			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);
			}
		}
	}
}
