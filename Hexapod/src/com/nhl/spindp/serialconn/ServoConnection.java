package com.nhl.spindp.serialconn;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nhl.spindp.LedStrip;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A class to facilitate the connection with the servo via a serial port
 * @author Dudecake
 *
 */
public class ServoConnection
{
	//private Queue<byte[]> instructions;
	private ExecutorService serialWorker;
	private SerialPort serialPort;
	private byte error;
	
	/**
	 * Creates a ServoConnection object
	 */
	public ServoConnection()
	{
		//instructions = new LinkedList<>();
		serialWorker = Executors.newSingleThreadExecutor();
		serialPort = new SerialPort();
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				serialWorker.shutdown();
			}
		});
	}
		
	/**
	 * Sends tests instructions
	 * @throws IOException
	 */
	public void sendTestingInstruction() throws IOException
	{
		serialPort.writeBytes(new byte[] { (byte)0xFF, (byte)0xFF, 0x01, 0x02, 0x01, (byte)0xFB });
		serialPort.readBytes();
	}
	
	// /dev/ttyAMA0	
	/**
	 * Does nothing, reset instruction is in blacklist
	 * Sends a reset instruction to all possible servos
	 * @throws IOException
	 */
	@Deprecated
	public void sendResetToAll() throws IOException
	{
		synchronized (this)
		{
			byte[] buffer = Servo.createResetInstruction((Servo.BCASTID));
			serialPort.writeBytes(buffer);
		}
	}
	

	/**
	 * Write arbitrary data to the servo, and reads all the data it gets back from the servo
	 * @param id The id of the receiving servo
	 * @param address The address to be written
	 * @param data The data to be written
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean writeData(byte id, byte address, byte data) throws IOException
	{
		byte[] buffer = Servo.createWriteDataInstruction(id, address, data);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	
	/**
	 * Sets direction pin true and directly false
	 * @throws IOException
	 */
	public void sendAsyncInstruction() throws IOException
	{
		throw new NotImplementedException();
	}
	
	
	/**
	 * Pings a servo and reads the data it gets back from the servo
	 * @param id The id to be pinged
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean pingServo(byte id) throws IOException
	{
		byte[] buffer = Servo.createPingInstruction(id);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	

	/**
	 * Does nothing, reset instruction is in blacklist
	 * Sends a reset instruction to a servo and reads the data it gets back from the servo
	 * @param id The id of the servo to be reset
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	@Deprecated
	public boolean resetServo(byte id) throws IOException
	{
		byte[] buffer = Servo.createResetInstruction(id);
		serialPort.writeBytes(buffer);
		byte[] res = readData();
		return res.length != 0;
	}
	
	/**
	 * Sets the id of a servo  and reads the data it gets back from the servo
	 * @param id The current id of the servo
	 * @param newId The desired new id of the servo
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean setServoId(byte id, byte newId) throws IOException
	{
		byte[] buffer = Servo.createWriteDataInstruction(id, Servo.ADDRESS_ID, newId);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	

	/**
	 * Moves a servo to the given position and reads the data it gets back from the servo
	 * @param id The id of the servo to be moved 
	 * @param position The desired position
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public boolean moveServo(byte id, short position) throws IOException, InterruptedException
	{
		byte[] buffer = Servo.createMoveServoInstruction(id, position);
		//System.out.println(DatatypeConverter.printHexBinary(buffer));
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	

	/**
	 * Moves a servo to the given location with the given speed
	 * @param id The id of the servo to be moved
	 * @param position The desired position
	 * @param speed The desired speed
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public boolean moveServo(byte id, short position, short speed) throws IOException, InterruptedException
	{
		byte[] buffer = Servo.createMoveServoInstruction(id, position, speed);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	

	/**
	 * Writes a move instruction to the servo to be executed on a sendAction() call
	 * @param id The id of the servo to be written
	 * @param position the desired position
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position) throws IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	
	/**
	 * Writes a move instruction to the servo to be executed on a sendAction() call
	 * @param id The id of the servo to be written
	 * @param position the desired position
	 * @param speed The desired speed
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position, short speed) throws IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position, speed);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}

	/**
	 * Sends an instruction to execute the written instruction
	 * @throws IOException
	 */
	public void sendAction() throws IOException
	{
		byte[] buffer = Servo.createActionInstruction();
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
			LedStrip.throwError();
		}
	}
	
	/**
	 * Sends move instructions to multiple servo's
	 * @param ids The ids of the servo's to be moved
	 * @param positions The positions of the servo's to be moved
	 * @throws IOException
	 */
	public void moveMultiple(byte[] ids, short[] positions) throws IOException
	{
		if (ids.length != positions.length) throw new IllegalArgumentException("Arrays must be same length");
		byte[] parameters = new byte[ids.length * 3];
		for (int i = 0; i < ids.length * 3; i+=3)
		{
			parameters[i  ] = ids[i/3];
			if (positions[i/3] < 0)     positions[i/3] = 0;
			if (positions[i/3] >= 1024) positions[i/3] = 1023;
			parameters[i+1] = (byte)(positions[i/3] &0xFF);
			parameters[i+2] = (byte)((positions[i/3] >> 8) &0xFF);
		}
		byte[] buffer = Servo.createSyncWriteDataInstruction(Servo.ADDRESS_GOAL_POSITION, parameters);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
			LedStrip.throwError();
		}
	}
	

	/**
	 * Set the angle limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param cwLimit The clockwise limit
	 * @param ccwLimit The counterclockwise limit
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean setAngleLimit(byte id, short cwLimit, short ccwLimit) throws IOException
	{
		byte[] buffer = Servo.createSetAngleLimitInstruction(id, cwLimit, ccwLimit);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	
	
	/**
	 * Does nothing, set torque instruction is in blacklist
	 * Sets the torque limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param limit The desired limit
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	@Deprecated
	public boolean setTorqueLimit(byte id, short limit) throws IOException
	{
		byte[] buffer = Servo.createSetTorqueLimitInstruction(id, limit);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	

	/**
	 * Sets the punch limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param limit The desired limit
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean setPunchLimit(byte id, short limit) throws IOException
	{
		byte[] buffer = Servo.createSetPunchLimit(id, limit);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	
	/**
	 * ***********************************Geen idee wat dit is*******************
	 * @param id
	 * @param cwMargin
	 * @param ccwMargin
	 * @param cwSlope
	 * @param ccwSlope
	 * @return
	 * @throws IOException
	 */
	public boolean setCompliance(byte id, byte cwMargin, byte ccwMargin, byte cwSlope, byte ccwSlope) throws IOException
	{
		byte[] buffer = Servo.createSetComplianceInstruction(id, cwMargin, ccwMargin, cwSlope, ccwSlope);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return res.length != 0;
	}
	
	/**
	 * Reads the temperature of the given servo
	 * @param id The id of the servo to be read
	 * @return the (approximate) temperature
	 * @throws IOException
	 */
	public int readTemperature(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_TEMP);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return Byte.toUnsignedInt(res[0]);
	}
	
	/**
	 * Reads the current location of the given servo
	 * @param id The id of the servo to be read
	 * @return the current location of the servo
	 * @throws IOException
	 */
	public int readPresentLocation(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_POS);
		byte[] res = new byte[2];
		int i = 0;
		synchronized (this)
		{
			while (i < 15 && res[0] != 0)
			{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
			i++;
			}
		}
		return (res[0] << 8) | res[1];
	}
	
	
	/**
	 * Reads the voltage of the given servo
	 * @param id The id of the servo to be read
	 * @return The current voltage of the servo
	 * @throws IOException
	 */
	public int readVoltage(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_VOLTAGE); 
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return Byte.toUnsignedInt(res[0]);
	}
	

	/**
	 * Reads the speed of the given servo
	 * @param id The id of the servo to be read
	 * @return The current speed of the servo
	 * @throws IOException
	 */
	public int readSpeed(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_SPEED); 
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return (res[0] << 8) | res[1];
	}
	
	 
	/**
	 * Reads the load on the given servo
	 * @param id The id of the servo to be read
	 * @return The load on the servo
	 * @throws IOException
	 */
	public int readLoad(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_LOAD);
		byte[] res;
		synchronized (this)
		{
			if (!serialPort.writeBytes(buffer))
			{
				System.out.println("Send instruction failed");
				LedStrip.throwError();
			}
			res = readData();
		}
		return (res[0] << 8) | res[1];
	}
		
	
	/**
	 * Reads the data from the serial port
	 * @return The read data
	 * @throws IOException
	 */
	private byte[] readData() throws IOException
	{
		byte[] buffer = serialPort.readBytes();//(5, 100);
		byte[] data = new byte[0];
		//if prefix incorrect
		if(((buffer[0] != (byte)0xFF) || (buffer[1] != (byte)0xFF)) && (buffer.length < 5))
			return data;
		//System.out.println(buffer.length);
		if(buffer.length < 5)
			return data;
		//error = buffer[4];
		if (buffer[3] != 0)
		{
			//data = serialPort.readBytes(buffer[3] - 1);//, 10);
			data = Arrays.copyOfRange(buffer, 0, buffer.length);
			boolean checksum = Servo.compareChecksum(data, data[data.length - 1]);
			//System.out.println("Recieved " + String.valueOf(buffer.length) + " bytes");
			if(!checksum)
			{
				//System.err.println("checksum error!");
				//LedStrip.throwError();
			}
		}
		return data;
	}
}
