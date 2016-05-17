package com.nhl.spindp.serialconn;

import java.io.IOException;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	
	/**
	 * Creates a ServoConnection object
	 */
	public ServoConnection()
	{
		serialPort = new SerialPort();
		servos = new Servo[18];
	}
	
	/**
	 * Sends tests instructions
	 * @throws IOException
	 */
	public void sendTestingInstruction() throws IOException
	{
		setDirectionPin(true);
		serialPort.writeBytes(new byte[] { (byte)0xFF, (byte)0xFF, 0x01, 0x02, 0x01, (byte)0xFB });
		setDirectionPin(false);
		serialPort.readBytes(1);
	}
	
	// /dev/ttyAMA0	
	/**
	 * Sends a reset instruction to all possible servos
	 * @throws IOException
	 */
	public void sendResetToAll() throws IOException
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	/**
	 * Sets direction pin true and directly false
	 * @throws IOException
	 */
	public void sendAsyncInstruction() throws IOException
	{
		setDirectionPin(true);
		setDirectionPin(false);
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	

	/**
	 * Sends a reset instruction to a servo and reads the data it gets back from the servo
	 * @param id The id of the servo to be reset
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean resetServo(byte id) throws IOException
	{
		byte[] buffer = Servo.createResetInstruction(id);
		setDirectionPin(true);
		serialPort.writeBytes(buffer);
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
		}
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		//Thread.sleep(1000);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public boolean moveServo(byte id, short position, short speed) throws IOException
	{
		byte[] buffer = Servo.createMoveServoInstruction(id, position, speed);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position) throws IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public boolean writeMoveServo(byte id, short position, short speed) throws IOException
	{
		byte[] buffer = Servo.createWriteMoveServoInstruction(id, position, speed);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
	}
	
	/**
	 * Sends move instructions to multiple servo's
	 * @param ids The ids of the servo's to be moved
	 * @param positions The positions of the servo's to be moved
	 * @throws IOException
	 */
	public void moveMultiple(byte[] ids, short[] positions) throws IOException
	{
		// TODO: Figure out if this is a good implementation.
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
		}
		return res.length != 0;
	}
	
	
	/**
	 * Sets the torque limit of the given servo
	 * @param id The id of the servo to be limited
	 * @param limit The desired limit
	 * @return Whether the servo returns a status packet
	 * @throws IOException
	 */
	public boolean setTorqueLimit(byte id, short limit) throws IOException
	{
		byte[] buffer = Servo.createSetTorqueLimitInstruction(id, limit);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public boolean setPunchLimit(byte id, short limit) throws IOException
	{
		byte[] buffer = Servo.createSetPunchLimit(id, limit);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
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
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public int readTemperature(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_TEMP);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public int readPresentLocation(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_POS);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public int readVoltage(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_VOLTAGE); 
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public int readSpeed(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_SPEED); 
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
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
	 * @throws IOException
	 */
	public int readLoad(byte id) throws IOException
	{
		byte[] buffer = Servo.createReadDataInstruction(id, Servo.ADDRESS_PRESENT_LOAD);
		setDirectionPin(true);
		if (!serialPort.writeBytes(buffer))
		{
			System.out.println("Send instruction failed");
		}
		setDirectionPin(false);
		byte[] res = readData(id);
		for (byte b : res)
		{
			System.out.println(b);
		}
		return (res[0] << 8) | res[1];
	}
		
	
	/**
	 * Reads the data from the serial port
	 * @return The read data
	 * @throws IOException
	 */
	private byte[] readData(int id) throws IOException
	{
		byte[] buffer = serialPort.readBytes(id);//(5, 100);
		byte[] data = new byte[0];
		//if prefix incorrect
		if((buffer[0] != 0xFF) || (buffer[1] != 0xFF)) return data;
		error = buffer[4];
		if (buffer[3] != 0)
		{
			//data = serialPort.readBytes(buffer[3] - 1);//, 10);
			data = Arrays.copyOfRange(buffer, 5, buffer.length);
			boolean checksum = Servo.compareChecksum(concat(buffer, data), data[data.length - 1]);
			//System.out.println("Recieved " + String.valueOf(buffer.length) + " bytes");
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
				 * This is an error warning!
				 */
			}
			//System.out.println();
		}
		return data;
	}
	
	/**
	 * Sets the direction pin with the specified value
	 * @param val The value to set the pin to
	 * @throws IOException
	 */
	//TODO: vragen of dit de bedoeling is
	private void setDirectionPin(boolean val) throws IOException
	{/*
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pigpioFile));
		writer.write(String.format("w %s %s\n", signalPin, val ? 1 : 0));
		writer.flush();
		writer.close();*/
	}
	
	/**
	 * Combines a list of arrays into one byte array
	 * @param first the first array
	 * @param rest the rest of the array's
	 * @return
	 */
	// TODO: Vragen wat byte[]... betekend  
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
}
