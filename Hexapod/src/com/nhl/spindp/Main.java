package com.nhl.spindp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

import org.opencv.core.Core;

import com.nhl.spindp.bluetooth.BluetoothConnection;
import com.nhl.spindp.sensors.I2C;
import com.nhl.spindp.sensors.DistanceMeter;
import com.nhl.spindp.netcon.AppConnection;
import com.nhl.spindp.netcon.WebSocket;
import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.SpiderBody;
import com.nhl.spindp.vision.ObjectRecognition;

public class Main
{
	private static Main instance;
	private static ServoConnection conn;
	private static WebSocket sock;
	private static AppConnection appConn;
	private static LedStrip ledStrip;
	public ObjectRecognition vision;
	private static BluetoothConnection blue;
	public DistanceMeter distance;
	private static Info info;
	private static boolean running = true;
	public static List<Short> failedServos;
	private volatile double forward = 0.0;
	private volatile double right   = 0.0;
	
	static
	{
		File lib = new File(Main.class.getResource("/libs/").getPath(), "libHexapod.so");
		System.load(lib.getAbsolutePath());
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private static native boolean isAlreadyRunning();
	
	public int readCurrentAngle(byte id) throws IOException
	{
		return conn.readPresentLocation(id);
	}
	
	public int readCurrentTemperature(byte id) throws IOException
	{
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
	{
		/*if (isAlreadyRunning())
		{
			System.out.println("Hexapod is already running");
			//System.exit(1);
		}
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Files.deleteIfExists(new File("/tmp/Hexapod.pid").toPath());
				}
				catch (IOException e) { }
			}
		});*/
		instance = new Main();
		ledStrip = new LedStrip();
		ledStrip.setDaemon(true);
		ledStrip.start();
		instance.distance = new DistanceMeter();
		//instance.distance.start();
		info = instance.new Info();
		
		sock = new WebSocket(8000);
		sock.start();
		//instance.vision = new ObjectRecognition();
		I2C i2c = new I2C();
		i2c.start();/*
		Thread.sleep(10);
		for (int i = 0; i < Byte.MAX_VALUE; i++)
		{
			double[] adc = info.getAdc();
			System.out.println("0: "+adc[0]+" 1: "+adc[1]);
			double[] res = info.getGyro();
			System.out.println("x: "+res[0]+" y: "+res[1]);
			System.out.println();
		}*/
		
		blue = new BluetoothConnection();
		blue.start();
		
		appConn = new AppConnection(1338);
		appConn.start();
		
		conn = new ServoConnection();
		
		failedServos = new ArrayList<>();
		Time.updateDeltaTime();
		SpiderBody body = new SpiderBody((byte) 1);
		//body.testCalcs();
		//byte[] ids    = new byte[]  { 1  , 4  , 2  , 5  , 3 , 6 };
		//short[] stand = new short[] { 512, 512, 650, 650, 50, 50};
		//conn.moveMultiple(ids, stand);
		
		//body.stabbyStab();
		while (Utils.shouldRun)
		{
			Time.updateDeltaTime();
			body.walk(instance.forward, instance.right);
			/*double[] adc = info.getAdc();
			System.out.println("0: "+adc[0]+" 1: "+adc[1]);
			double[] res = info.getGyro();
			System.out.println("x: "+res[0]+" y: "+res[1]);
			System.out.println();*/
			Thread.sleep(1);
			/*if(scan.hasNext())
			{
				if((input = scan.next().toLowerCase()).equals("exit"))
				{
	        		running = false;
				}
			}*/
		}
        Utils.shouldRun = false;
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
	
	@Deprecated
	public static Future<byte[]> submitInstruction(byte[] message)
	{
		return conn.submitInstruction(message);
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
				//Thread.sleep(5);
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
	
	public Info getInfo()
	{
		return info;
	}

	public class Info
	{
		private Object locker = new Object();
		private double gyroX, gyroY;
		private double adcSpanning, adcStroom; //spanning: V, stroom: A
		
		public double[] getGyro()
		{
			double[] res = { -1, -1};
			synchronized (locker)
			{
				res[0] = gyroX;
				res[1] = gyroY;
			}
			return res;
		}
		
		public void setGyro(double[] data)
		{
			if(data.length < 2)
				return;
			synchronized (locker)
			{
				gyroX = data[0];
				gyroY = data[1];
			}
		}
		
		public double[] getAdc()
		{
			double[] res = { -1, -1};
			synchronized (locker)
			{
				res[0] = adcSpanning;
				res[1] = adcStroom;
			}
			return res;
		}
		
		public void setAdc(double[] data)
		{
			if(data.length < 2)
				return;
			synchronized (locker)
			{
				adcSpanning = data[0];
				adcStroom = data[1];
			}
		}
	}
}
