package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import jssc.*;

@SuppressWarnings("unused")
public class ServoConnection
{
	private SerialPort serialPort;
	private Servo[] servos;
	private byte error;
	private int signalPin;
	private static final File pigpioFile = new File("/dev/pigpio");
	
	/**
	 * Prefix for every instruction.
	 */
	private static final byte INSTRUCTION_PREFIX      = (byte)0xFF;
	
	/**
	 * No action, Used for obtaining a Status Packet.
	 */
	public  static final byte INSTRUCTION_PING        = (byte) 0x01;
	
	/**
	 * Reading values in the Control Table.
	 */
	public  static final byte INSTRUCTION_READ_DATA   = (byte) 0x02;
	
	/**
	 * Writing values to the Control Table.
	 */
	public  static final byte INSTRUCTION_WRITE_DATA  = (byte) 0x03;
	
	/**
	 * Similar to WRITE_DATA, but stays in standby mode until the ACTION instruction is given.
	 */
	public  static final byte INSTRUCTION_REG_WRITE   = (byte) 0x04;
	
	/**
	 * Triggers the action registered by the REG_WRITE instruction.
	 */
	public  static final byte INSTRUCTION_ACTION      = (byte) 0x05;
	
	/**
	 * Changes the Control Table values of the Dynamixel actuator to the factory default value settings.
	 */
	public  static final byte INSTRUCTION_RESET       = (byte) 0x06;
	
	/**
	 * Used for controlling many Dynamixel actuators at the same time.
	 */
	public  static final byte INSTRUCTION_SYNC_WRITE  = (byte) 0x86;
	
	//EEPROM
	private static final byte ADDRESS_ID              = (byte) 0x03;
	private static final byte ADDRESS_BAUD_RATE       = (byte) 0x04;
	private static final byte ADDRESS_RETURN_DELAY    = (byte) 0x05;
	private static final byte ADDRESS_CW_ANGLE_LIMIT  = (byte) 0x06;
	private static final byte ADDRESS_CCW_ANGLE_LIMIT = (byte) 0x08;
	private static final byte ADDRESS_TEMP_LIMIT      = (byte) 0x0B;
	private static final byte ADDRESS_VOLT_LIMIT_HIGH = (byte) 0x0C;
	private static final byte ADDRESS_VOLT_LIMIT_LOW  = (byte) 0x0D;
	private static final byte ADDRESS_MAX_TORQUE      = (byte) 0x0E;
	private static final byte ADDRESS_STATUS_RETURN   = (byte) 0x10;
	private static final byte ADDRESS_ALARM_LED       = (byte) 0x11;
	private static final byte ADDRESS_ALARM_SHUTDOWN  = (byte) 0x12;
	private static final byte ADDRESS_DOWN_CALIB      = (byte) 0x14;
	private static final byte ADDRESS_UP_CALIB        = (byte) 0x16;
	
	//RAM
	private static final byte ADDRESS_TORQUE_ENABLE   = (byte) 0x18;
	private static final byte ADDRESS_LED             = (byte) 0x19;
	private static final byte ADDRESS_CW_COMP_MARGIN  = (byte) 0x1A;
	private static final byte ADDRESS_CCW_COMP_MARGIN = (byte) 0x1B;
	private static final byte ADDRESS_CW_COMP_SLOPE   = (byte) 0x1C;
	private static final byte ADDRESS_CCW_COMP_SLOPE  = (byte) 0x1D;
	private static final byte ADDRESS_GOAL_POSITION   = (byte) 0x1E;
	private static final byte ADDRESS_MOVING_SPEED    = (byte) 0x20;
	private static final byte ADDRESS_TORQUE_LIMIT    = (byte) 0x22;
	private static final byte ADDRESS_PRESENT_POS     = (byte) 0x24;
	private static final byte ADDRESS_PRESENT_SPEED   = (byte) 0x26;
	private static final byte ADDRESS_PRESENT_LOAD    = (byte) 0x28;
	private static final byte ADDRESS_PRESENT_VOLTAGE = (byte) 0x2A;
	private static final byte ADDRESS_PRESENT_TEMP    = (byte) 0x2B;
	private static final byte ADDRESS_REGISTERED_INST = (byte) 0x2C;
	private static final byte ADDRESS_MOVING          = (byte) 0x2E;
	private static final byte ADDRESS_LOCK            = (byte) 0x2F;
	private static final byte ADDRESS_PUNCH           = (byte) 0x30;
	
	
	public ServoConnection()
	{
		servos = new Servo[18];
		signalPin = 18;
	}
	
	// /dev/ttyAMA0
	public ServoConnection(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		servos = new Servo[18];
		signalPin = 18;
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("doei!");
				try
				{
					serialPort.closePort();
				}
				catch (SerialPortException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public void connect(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		servos = new Servo[18];
		signalPin = 18;
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
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
		});
	}
		
	public void sendResetToAll() throws SerialPortException, InterruptedException, IOException
	{
		for (int i = 0; i < Servo.BCASTID; i++)
		{
			byte[] buffer = 
				{
					INSTRUCTION_PREFIX,
					INSTRUCTION_PREFIX,
					(byte)i,
					0,
					INSTRUCTION_RESET,
					0
				};
			buffer[4] = (byte)(buffer.length - 4);
			buffer[buffer.length - 1] = computeChecksum((byte)i, buffer[4], INSTRUCTION_RESET);
			setSignalPin(true);
			serialPort.writeBytes(buffer);
			setSignalPin(false);
		}
	}
	
	public boolean sendInstruction(byte id, byte instruction) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer =
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_ID,
				instruction,
				0
			};
		buffer[4] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_ID, instruction);
		setSignalPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setSignalPin(false);
		Thread.sleep(100);
		//reader.wait(250);
		for (byte b : readData())
		{
			System.out.println(b);
		}
		return true;
	}
	
	public void sendAsyncInstruction() throws SerialPortException, InterruptedException, IOException
	{
		setSignalPin(true);
		
		Thread.sleep(1);
		setSignalPin(false);
		
	}
	
	public boolean setID(byte id, byte newId) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_ID,
				newId,
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_ID, newId);
		setSignalPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setSignalPin(false);
		for (byte b : readData())
		{
			System.out.println(b);
		}
		return true;
	}
	
	public boolean move(byte id, short position) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		if (position < 0)     position = 0;
		if (position >= 1024) position = 1023;
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_GOAL_POSITION,
				(byte)(position &0xFF),
				(byte)((position >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF)); 
		setSignalPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setSignalPin(false);
		for (byte b : readData())
		{
			System.out.println(b);
		}
		return true;
	}
	
	public boolean move(byte id, int position) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		if (position < 0)     position = 0;
		if (position >= 1024) position = 1023;
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_GOAL_POSITION,
				(byte)(position &0xFF),
				(byte)((position >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF)); 
		setSignalPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setSignalPin(false);
		for (byte b : readData())
		{
			System.out.println(b);
		}
		return true;
	}
	
	public boolean moveMultiple(short position) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		throw new NotImplementedException();
		// TODO: Figure out if, and how we want to implement this.
		/*if (position < 0)     position = 0;
		if (position >= 1024) position = 1023;
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				Servo.BCASTID,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_GOAL_POSITION,
				(byte)(position &0xFF),
				(byte)((position >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		//buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF)); 
		setSignalPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setSignalPin(false);
		return true;*/
	}
	
	private byte computeChecksum(byte id, byte length)
	{
		return (byte)~((id + length) &0xFF);
	}
	
	private byte computeChecksum(byte id, byte length, byte ... parameters)
	{
		int res = id + length;
		for (byte b : parameters)
		{
			res += b;
		}
		return (byte)(~res &0xFF);
	}
	
	private boolean compareChecksum(byte[] buffer, byte[] data, byte checksum)
	{
		int res = buffer[2] + buffer[3];
		for (int i = 0; i < data.length - 1; i++)
		{
			res += data[i];
		}
		return ((byte)(~res &0xFF) == checksum);
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
	
	private byte[] readData() throws SerialPortException, SerialPortTimeoutException
	{
		byte[] buffer = serialPort.readBytes(4, 10);
		byte[] data = new byte[0];
		if (buffer[3] != 0)
		{
			data = serialPort.readBytes(buffer[3], 10);
			error = data[0];
			boolean checksum = compareChecksum(buffer, data, data[data.length - 1]);
			System.out.println("Recieved " + String.valueOf(buffer.length) + " bytes");
			for (byte b : data)
			{
				System.out.print(String.valueOf(b) + ' ');
			}
			System.out.println();
		}
		return data;
	}
	
	private void setSignalPin(boolean val) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		writer.write(String.format("w %s %s\n", signalPin, val ? 1 : 0));
		writer.flush();
		writer.close();
	}
	/*
	public static byte[] concatAll(byte[] first, byte[]... rest)
	{
		int totalLength = first.length;
		for (byte[] array : rest)
		{
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}*/
	
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
