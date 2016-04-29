package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import jssc.*;

/**
 * A class to facilitate the connection with the servo via a serial port
 * @author Dudecake
 *
 */
public class ServoConnection
{
	private SerialPort serialPort;
	private Servo[] servos;
	private byte error;
	private int signalPin;
	private static final File pigpioFile = new File("/dev/pigpio");
	
	/**
	 * Creates a ServoConnection object
	 */
	public ServoConnection()
	{
		servos = new Servo[18];
		signalPin = 18;
	}
	
	// /dev/ttyAMA0
	/**
	 * Creates a ServoConnection object and connects to the given serial port
	 * @param device The name of the serial port to connect to
	 * @throws SerialPortException
	 */
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
	
	/**
	 * Connects to the given serial port
	 * @param device The name of the serial port to connect to
	 * @throws SerialPortException
	 */
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
	
	/**
	 * Sends a reset instruction to all possible servos
	 * @throws SerialPortException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void sendResetToAll() throws SerialPortException, InterruptedException, IOException
	{
		for (int i = 0; i < Servo.BCASTID; i++)
		{
			byte[] buffer = Servo.createResetInstruction((byte)i);
			setDirectionPin(true);
			serialPort.writeBytes(buffer);
			setDirectionPin(false);
		}
	}
	
	/**
	 * Write arbitrary data to the servo
	 * @param id The id of the receiving servo
	 * @param address The address to be written
	 * @param data The data to be written
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean writeData(byte id, byte address, byte data) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createWriteDataInstruction(id, address, data);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	public void sendAsyncInstruction() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		setDirectionPin(true);
		
		setDirectionPin(false);
		throw new NotImplementedException();
	}
	
	/**
	 * Pings a servo
	 * @param id The id to be pinged
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean pingServo(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createPingInstruction(id);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sends a reset instruction to a servo
	 * @param id The id of the servo to be reset
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean resetServo(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createResetInstruction(id);
		setDirectionPin(true);
		serialPort.writeBytes(buffer);
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sets the id of a servo
	 * @param id The current id of the servo
	 * @param newId The desired new id of the servo
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean setServoId(byte id, byte newId) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createWriteDataInstruction(id, Servo.ADDRESS_ID, newId);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Moves a servo to the given position
	 * @param id The id of the servo to be moved 
	 * @param position The desired position
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean moveServo(byte id, short position) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createMoveServoInstruction(id, position);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Moves a servo to the given location with the given speed
	 * @param id The id of the servo to be moved
	 * @param position The desired position
	 * @param speed The desired speed
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean moveServo(byte id, short position, short speed) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createMoveServoInstruction(id, position, speed);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Writes a move instruction to the servo to be executed on a sendAction() call
	 * @param id The id of the servo to be written
	 * @param position the desired position
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Writes a move instruction to the servo to be executed on a sendAction() call
	 * @param id The id of the servo to be written
	 * @param position the desired position
	 * @param speed The desired speed
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position, short speed) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position, speed);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sends an instruction to execute the written instruction
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void sendAction() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createActionInstruction();
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
	}
	
	public void moveMultiple(byte[] ids, short[] positions) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		// TODO: Figure out if this is a good implementation.
		if (ids.length != positions.length) throw new IllegalArgumentException("Arrays must be same length");
		byte[] parameters = new byte[3];
		for (int i = 0; i < ids.length * 3; i+=3)
		{
			parameters[i  ] = ids[i/3];
			if (positions[i/3] < 0)     positions[i/3] = 0;
			if (positions[i/3] >= 1024) positions[i/3] = 1023;
			parameters[i+1] = (byte)(positions[i/3] &0xFF);
			parameters[i+2] = (byte)((positions[i/3] >> 8) &0xFF);
		}
		byte[] buffer = Servo.createSyncWriteDataInstruction(Servo.ADDRESS_GOAL_POSITION, parameters);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		//return true;
	}
	
	/**
	 * Set the angle limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param cwLimit The clockwise limit
	 * @param ccwLimit The counterclockwise limit
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean setAngleLimit(byte id, short cwLimit, short ccwLimit) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createSetAngleLimitInstruction(id, cwLimit, ccwLimit);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sets the torque limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param limit the desired limit
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean setTorqueLimit(byte id, short limit) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createSetTorqueLimitInstruction(id, limit);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sets the punch limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param limit The desired limit
	 * @return Whether the servo returns a status packet
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean setPunchLimit(byte id, short limit) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createSetPunchLimit(id, limit);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	public boolean setCompliance(byte id, byte cwMargin, byte ccwMargin, byte cwSlope, byte ccwSlope) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createSetComplianceInstruction(id, cwMargin, ccwMargin, cwSlope, ccwSlope);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Reads the temperature of the given servo
	 * @param id The id of the servo to be read
	 * @return the (approximate) temperature
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int readTemperature(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_TEMP);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return Byte.toUnsignedInt(res[0]);
	}
	
	/**
	 * Reads the current location of the given servo
	 * @param id The id of the servo to be read
	 * @return the current location of the servo
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int readPresentLocation(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_POS);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return (res[0] << 8) | res[1];
	}
	
	/**
	 * Reads the voltage of the given servo
	 * @param id The id of the servo to be read
	 * @return The current voltage of the servo
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int readVoltage(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_VOLTAGE); 
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return Byte.toUnsignedInt(res[0]);
	}
	
	/**
	 * Reads the speed of the given servo
	 * @param id The id of the servo to be read
	 * @return The current speed of the servo
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int readSpeed(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_SPEED); 
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return (res[0] << 8) | res[1];
	}
	
	/**
	 * Reads the load on the given servo
	 * @param id The id of the servo to be read
	 * @return The load on the servo
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int readLoad(byte id) throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_LOAD);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData();
		for (byte b : res)
		{
			System.out.println(b);
		}
		return (res[0] << 8) | res[1];
	}
	
	/**
	 * Closes the connection with the serial port
	 */
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
	
	/**
	 * Reads the data from the serial port
	 * @return The read data
	 * @throws SerialPortException
	 * @throws SerialPortTimeoutException
	 */
	private byte[] readData() throws SerialPortException, SerialPortTimeoutException
	{
		byte[] buffer = serialPort.readBytes(5, 10);
		byte[] data = new byte[0];
		//if prefix incorrect
		if((buffer[0] != 0xFF) || (buffer[1] != 0xFF)) return data;
		error = buffer[4];
		if (buffer[3] != 0)
		{
			data = serialPort.readBytes(buffer[3] - 1, 10);
			boolean checksum = Servo.compareChecksum(concat(buffer, data), data[data.length - 1]);
			System.out.println("Recieved " + String.valueOf(buffer.length) + " bytes");
			for (byte b : data)
			{
				System.out.print(String.valueOf(b) + ' ');
			}
			if(!checksum)
			{
				System.err.println(" .----------------.  .----------------.  .----------------.  .----------------.  .----------------. ");
				System.err.println("| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |");
				System.err.println("| |  _________   | || |  _______     | || |  _______     | || |     ____     | || |  _______     | |");
				System.err.println("| | |_   ___  |  | || | |_   __ \\    | || | |_   __ \\    | || |   .'    `.   | || | |_   __ \\    | |");
				System.err.println("| |   | |_  \\_|  | || |   | |__) |   | || |   | |__) |   | || |  /  .--.  \\  | || |   | |__) |   | |");
				System.err.println("| |   |  _|  _   | || |   |  __ /    | || |   |  __ /    | || |  | |    | |  | || |   |  __ /    | |");
				System.err.println("| |  _| |___/ |  | || |  _| |  \\ \\_  | || |  _| |  \\ \\_  | || |  \\  `--'  /  | || |  _| |  \\ \\_  | |");
				System.err.println("| | |_________|  | || | |____| |___| | || | |____| |___| | || |   `.____.'   | || | |____| |___| | |");
				System.err.println("| |              | || |              | || |              | || |              | || |              | |");
				System.err.println("| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |");
				System.err.println("'----------------'  '----------------'  '----------------'  '----------------'  '----------------' ");
				/*
 .----------------.  .----------------.  .----------------.  .----------------.  .----------------. 
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |  _________   | || |  _______     | || |  _______     | || |     ____     | || |  _______     | |
| | |_   ___  |  | || | |_   __ \    | || | |_   __ \    | || |   .'    `.   | || | |_   __ \    | |
| |   | |_  \_|  | || |   | |__) |   | || |   | |__) |   | || |  /  .--.  \  | || |   | |__) |   | |
| |   |  _|  _   | || |   |  __ /    | || |   |  __ /    | || |  | |    | |  | || |   |  __ /    | |
| |  _| |___/ |  | || |  _| |  \ \_  | || |  _| |  \ \_  | || |  \  `--'  /  | || |  _| |  \ \_  | |
| | |_________|  | || | |____| |___| | || | |____| |___| | || |   `.____.'   | || | |____| |___| | |
| |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
				 */
			}
			System.out.println();
		}
		return data;
	}
	
	/**
	 * Sets the direction pin with the specified value
	 * @param val The value to set the pin to
	 * @throws IOException
	 */
	private void setDirectionPin(boolean val) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		writer.write(String.format("w %s %s\n", signalPin, val ? 1 : 0));
		writer.flush();
		writer.close();
	}
	
	public static byte[] concat(byte[] first, byte[]... rest)
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
	}
	
	//in case shutdown thread doesn't work use this
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
}
