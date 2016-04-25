package com.nhl.spindp.serialconn;

import java.io.IOException;

import jssc.*;

public class ServoConnection
{
	private SerialPort serialPort;
	private SerialPortReader reader;
	private Servo[] servos;
	private int signalPin;
	private Runtime runtime;
	
	/**
	 * Prefix for every instruction.
	 */
	private static final byte INSTRUCTION_PREFIX     = (byte)0xFF;
	
	/**
	 * No action, Used for obtaining a Status Packet.
	 */
	private static final byte INSTRUCTION_PING       = (byte) 0x01;
	
	/**
	 * Reading values in the Control Table.
	 */
	private static final byte INSTRUCTION_READ_DATA  = (byte) 0x02;
	
	/**
	 * Writing values to the Control Table.
	 */
	private static final byte INSTRUCTION_WRITE_DATA = (byte) 0x03;
	
	/**
	 * Similar to WRITE_DATA, but stays in standby mode until the ACTION instruction is given.
	 */
	private static final byte INSTRUCTION_REG_WRITE  = (byte) 0x04;
	
	/**
	 * Triggers the action registered by the REG_WRITE instruction.
	 */
	private static final byte INSTRUCTION_ACTION     = (byte) 0x05;
	
	/**
	 * Changes the Control Table values of the Dynamixel actuator to the factory default value settings.
	 */
	private static final byte INSTRUCTION_RESET      = (byte) 0x06;
	
	/**
	 * Used for controlling many Dynamixel actuators at the same time.
	 */
	private static final byte INSTRUCTION_SYNC_WRITE = (byte) 0x86;
	
	public ServoConnection()
	{
		reader = new SerialPortReader();
		servos = new Servo[18];
		signalPin = 18;
		runtime = Runtime.getRuntime();
	}
	
	// /dev/ttyAMA0
	public ServoConnection(String device) throws SerialPortException
	{
		reader = new SerialPortReader();
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(reader);
		servos = new Servo[18];
		signalPin = 18;
		runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ShutdownHook(this));
	}
	
	public void connect(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(reader);
		servos = new Servo[18];
		signalPin = 18;
		runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ShutdownHook(this));
	}
	
	public byte getError()
	{
		return reader.getError();
	}
	
	public void sendResetToAll() throws SerialPortException, InterruptedException, IOException
	{
		//for (int i = 1; i < Servo.BCASTID; i++)
		{
			runtime.exec(String.format("pigs w %s 1", signalPin)).waitFor();
			serialPort.writeBytes(new byte[]
					{	(byte)INSTRUCTION_PREFIX,
						(byte)INSTRUCTION_PREFIX,
						(byte)1,
						(byte)0x02,
						INSTRUCTION_RESET,
						(byte)0xF7 });
			Thread.sleep(25);
			runtime.exec(String.format("pigs w %s 0", signalPin)).waitFor();
		}
	}
	
	public boolean sendInstruction(byte id) throws SerialPortException, InterruptedException, IOException
	{
		runtime.exec(String.format("pigs w %s 1", signalPin)).waitFor();
		if (!serialPort.writeBytes(new byte[]
				{	(byte)INSTRUCTION_PREFIX,
					(byte)INSTRUCTION_PREFIX,
					id,
					(byte)0x04,
					INSTRUCTION_WRITE_DATA,
					(byte)0x03,
					(byte)0x02,
					computeChecksum((byte)1, (byte)4, INSTRUCTION_WRITE_DATA, (byte)3, (byte)2) }))
		{
			System.out.println("Send instruction failed");
		}
		Thread.sleep(5000);
		runtime.exec(String.format("pigs w %s 0", signalPin)).waitFor();
		Thread.sleep(100);
		//reader.wait(250);
		for (byte b : reader.getData())
		{
			System.out.println(b);
		}
		return reader.getRecieved();
	}
	
	public void sendAsyncInstruction() throws SerialPortException, InterruptedException, IOException
	{
		runtime.exec(String.format("pigs w %s 1", signalPin)).waitFor();
		
		Thread.sleep(1);
		runtime.exec(String.format("pigs w %s 1", signalPin)).waitFor();
		
	}
	
	private byte computeChecksum(byte id, byte length)
	{
		return (byte)~((id + length) & 0xFF);
	}
	
	private byte computeChecksum(byte id, byte length, byte ... parameters)
	{
		int temp = id + length;
		for (byte b : parameters)
		{
			temp += b;
		}
		return (byte)(~temp & 0xFF);
	}
	
	public void disconnect()
	{
		try
		{
			serialPort.closePort();
		}
		catch (SerialPortException e)
		{
			e.printStackTrace();
		}
	}
	
	private class ShutdownHook extends Thread
	{
		ServoConnection conn;
		
		public ShutdownHook(ServoConnection conn)
		{
			this.conn = conn;
		}
		
		@Override
		public void run()
		{
			conn.disconnect();
		}
	}
	
	private class SerialPortReader implements SerialPortEventListener
	{
		private boolean recieved = false;
		private byte error = 0;
		private byte[] data = new byte[0];
		
		public boolean getRecieved()
		{
			return recieved;
		}
		
		public byte getError()
		{
			return error;
		}
		
		public byte[] getData()
		{
			return data;
		}
		
		@Override
		public void serialEvent(SerialPortEvent serialPortEvent)
		{
			//synchronized (this)
			//{
				System.out.print("Recieved something");
				if (serialPortEvent.isRXCHAR())
				{
					System.out.println(" RXCHAR");
					try
					{
						byte[] buffer = serialPort.readBytes(serialPortEvent.getEventValue());
						System.out.println("Recieved " + String.valueOf(buffer.length) + "bytes");
						System.out.println(String.format("%2x", buffer).toUpperCase());
						/*for (byte b : buffer)
						{
							System.out.println(b);
						}*/
						if (buffer.length > 4)
						{
							byte id = buffer[2];
							error   = buffer[4];
							if (error != 0)
							{
								//notifyAll();
								return;
							}
						}
						if (buffer.length > 5)
						{
							data = new byte[buffer.length - 6];
							System.arraycopy(buffer, 5, data, 0, buffer.length - 6);
						}
						
					}
					catch (SerialPortException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (serialPortEvent.isCTS())
				{
					System.out.println(" CTS");
					if (serialPortEvent.getEventValue() == 1)
					{
						System.out.println("CTS - ON");
					}
					else
					{
						System.out.println("CTS - OFF");
					}
				}
				else if (serialPortEvent.isDSR())
				{
					System.out.println(" DSR");
					if(serialPortEvent.getEventValue() == 1)
					{
	                    System.out.println("DSR - ON");
	                }
	                else
	                {
	                    System.out.println("DSR - OFF");
	                }
				}
			//}
			//notifyAll();
			recieved = true;
		}
	}
	
	private class Servo
	{
		public static final int BAUDRATE_1 = 1000000;
		public static final int BAUDRATE_3 =  500000;
		public static final int BAUDRATE_4 =  400000;
		public static final int BAUDRATE_7 =  250000;
		public static final int BAUDRATE_9 =  200000;
		public static final byte DATABITS  = 8;
		public static final byte STOPBITS  = 1;
		public static final byte PARITY    = 0;
		public static final byte BCASTID   = (byte)0xFE;
		
		public final byte id;
		
		public Servo(byte id)
		{
			this.id = id;
		}
	}
}
