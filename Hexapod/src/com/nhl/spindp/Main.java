package com.nhl.spindp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jdk.nashorn.internal.ir.WhileNode;

import org.opencv.core.Core;

import com.nhl.spindp.bluetooth.BluetoothConnection;
import com.nhl.spindp.sensors.I2C;
import com.nhl.spindp.sensors.DistanceMeter;
import com.nhl.spindp.netcon.AppConnection;
import com.nhl.spindp.netcon.WebSocket;
import com.nhl.spindp.serialconn.ServoConnection;
import com.nhl.spindp.spin.Dans;
import com.nhl.spindp.spin.SpiderBody;
import com.nhl.spindp.vision.ObjectRecognition;

public class Main
{
	public  static final boolean IS_ARM = System.getProperty("os.arch").equals("arm");
	private static Main instance;
	private static ServoConnection conn;
	private static WebSocket sock;
	private static AppConnection appConn;
	private static LedStrip ledStrip;
	public  static ObjectRecognition vision;
	private static BluetoothConnection blue;
	public  static DistanceMeter distance;
	private static SpiderBody body;
	private static Info info;
	private static boolean running = true;
	public  static List<Short> failedServos;
	private volatile double forward = .5;
	private volatile double right   = 0.0;
	private volatile boolean crab   = false;
	
	static
	{
		File lib = new File(Main.class.getResource("/libs/").getPath(), "libHexapod.so");
		System.load(lib.getAbsolutePath());
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private static native boolean isAlreadyRunning();
	
	/**
	 * Get current servo angle
	 * @param id Servo ID to get angle from
	 * @return angle in range: 0 - 1024
	 * @throws IOException
	 */
	public int readCurrentAngle(byte id) throws IOException
	{
		return conn.readPresentLocation(id);
	}
	
	/**
	 * Read current servo temperature
	 * @param id Servo ID to get temperature from
	 * @return current temperature
	 * @throws IOException
	 */
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
		info = instance.new Info();
		
		body = new SpiderBody((byte) 1);
		if (IS_ARM)
		{
			//start led strip color thread
			ledStrip = new LedStrip();
			ledStrip.setDaemon(true);
			ledStrip.setName("LedThread");
			ledStrip.start();
			
			//start distance meter
			distance = new DistanceMeter();
			//distance.start();
			
			//create and strat I2C communication
			I2C i2c = new I2C();
			i2c.start();
			
			//start listening to bluetooth info
			blue = new BluetoothConnection();
			blue.start();
			
			//start app connection server
			appConn = new AppConnection(1338);
			appConn.start();
			
			//connect to servo's
			conn = new ServoConnection();

			//Dans moves object
			Dans dans = new Dans();
			//dans.doDanceMoves();
		}
		
		//create vision object
		vision = new ObjectRecognition();
		vision.start("run");
		
		//start web server
		sock = new WebSocket(8000);
		sock.start();
		
		failedServos = new ArrayList<>();
		Time.updateDeltaTime();
		
		//byte[] ids    = new byte[]  { 1  , 4  , 2  , 5  , 3 , 6 };
		//short[] stand = new short[] { 512, 512, 650, 650, 50, 50};
		//conn.moveMultiple(ids, stand);
		
		Thread stopper = new Thread()
		{
			@Override
			public void run()
			{
				Scanner scan = new Scanner(System.in);
				while (true)
				{
					if(scan.hasNext())
					{
						if(scan.next().toLowerCase().equals("exit"))
						{
			        		Utils.shouldRun = false;
			        		break;
						}
					}
				}
				scan.close();
				vision.stop();
			}
		};
		stopper.setDaemon(true);
		stopper.setName("closer");
		stopper.start();
		
		//body.stabbyStab();
		while (Utils.shouldRun)
		{
			Time.updateDeltaTime();
			//body.moveToAngle(45, 118.7, 35.3);
			body.setHeight(80.0);
			body.setWidth(90.0);

			body.walk(instance.forward, instance.right);
			
			/*double[] adc = info.getAdc();
			System.out.println("0: "+adc[0]+" 1: "+adc[1]);
			double[] res = info.getGyro();
			System.out.println("x: "+res[0]+" y: "+res[1]);
			System.out.println();*/
			//System.out.println(vision.getX());
			
			//Thread.sleep(1);
			
		}
        Utils.shouldRun = false;
        
	}
	
	/**
	 * keep track of failed servo's
	 * @param id ID i=of failed servo
	 */
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
	 * set direction parameters for walking
	 * @param id spider ID
	 * @param forward how fast to walk in range: -1 - 1
	 * @param right wich way to walk in range: -1 - 1
	 */
	public void setDirection(int id, double forward, double right)
	{
		this.forward = forward;
		this.right   = right;
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
	
	public void driveServo(int[] ids, int[] angles, int[] speeds)
	{
		if (ids.length != angles.length) throw new IllegalArgumentException("Arrays must have the same length");
		for (int i = 0; i < ids.length; i++)
		{
			try
			{
				conn.moveServo((byte)ids[i], (short)angles[i], (short) speeds[i]);
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
	 * make spider stab a balloon
	 * @param id spider ID
	 */
	public void stab(int id)
	{
		try
		{
			body.stabbyStab();
		}
		catch (InterruptedException e) { }
	}
	
	public Info getInfo()
	{
		return info;
	}

	/**
	 * class with sensor info
	 * @author eelkef
	 *
	 */
	public class Info
	{
		private Object locker = new Object();
		private double gyroX = 360, gyroY = 360;
		private double adcSpanning = 0, adcStroom = 0; //spanning: V, stroom: A
		private double distance = 999999;
		private int x = 0;
		
		/**
		 * Get gyroscope angles
		 * @return array with x en y angle
		 */
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
		
		/**
		 * Set gyroscope angle
		 * @param data array with x and y angle
		 */
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
		
		/**
		 * Get adc info
		 * @return array with power and current
		 */
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
		
		/**
		 * Set adc info
		 * @param data array with power and current
		 */
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
		
		/**
		 * Get distance
		 * @return double with distance in cm
		 */
		public double getDistance()
		{
			double res;
			synchronized (locker)
			{
				res = distance;
			}
			return res;
		}
		
		/**
		 * Set distance
		 * @param distance measurement
		 */
		public void setDistance(double data)
		{
			synchronized (locker)
			{
				distance = data;
			}
		}
		
		public int getX()
		{ 
			synchronized (locker)
			{
				return x;
			}
		}
		
		public void setX(int value)
		{
			synchronized (locker)
			{
				x = value;
			}
		}
	}
}
