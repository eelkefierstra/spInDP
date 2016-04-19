package com.nhl.spindp.serialconn;

import jssc.*;

@SuppressWarnings("unused")
public class ServoConnection
{
	private SerialPort serialPort;
	private Servo[] servos;
	
	/**
	 * Prefix for every instruction. Can be converted to two bytes with: 
	 * (byte)(INSTRUCTION_PREFIX & 0xff);
	 * (byte)((INSTRUCTION_PREFIX >> 8) &0xff);
	 */
	private static final short INSTRUCTION_PREFIX    = (short)0xFFFF;
	//ret[0] = (byte)(x & 0xff);
	//ret[1] = (byte)((x >> 8) & 0xff);
	
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
		servos = new Servo[18];
	}
	
	// /dev/ttyAMA0
	public ServoConnection(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.openPort();
		servos = new Servo[18];
	}
	
	public void connect(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.openPort();
		servos = new Servo[18];
	}
	
	public boolean sendInstruction()
	{
		
		return true;
	}
	
	public void sendAsyncInstruction()
	{
		
	}
	
	private byte computeChecksum(byte id, byte length)
	{
		return (byte)~(id + length);
	}
	
	private byte computeChecksum(byte id, byte length, byte ... parameters)
	{
		int temp = id + length;
		for (byte b : parameters)
		{
			temp += b;
		}
		return (byte)~temp;
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
