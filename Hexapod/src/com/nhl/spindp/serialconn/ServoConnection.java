package com.nhl.spindp.serialconn;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import jssc.*;

@SuppressWarnings("unused")
public class ServoConnection
{
	private SerialPort serialPort;
	private SerialPortReader reader;
	private Servo[] servos;
	private GpioPinDigitalOutput signalPin;
	private final GpioController gpio = GpioFactory.getInstance();
	
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
		reader = new SerialPortReader();
		servos = new Servo[18];
		signalPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18, "Signal Pin", PinState.LOW);
	}
	
	// /dev/ttyAMA0
	public ServoConnection(String device) throws SerialPortException
	{
		reader = new SerialPortReader();
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
		servos = new Servo[18];
		signalPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18, "Signal Pin", PinState.LOW);
	}
	
	public void connect(String device) throws SerialPortException
	{
		serialPort = new SerialPort(device);
		serialPort.openPort();
		serialPort.setParams(Servo.BAUDRATE_1, Servo.DATABITS, Servo.STOPBITS, Servo.PARITY);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
		servos = new Servo[18];
		signalPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18, "Signal Pin", PinState.LOW);
	}
	
	public void sendResetToAll() throws SerialPortException
	{
		signalPin.setState(PinState.HIGH);
		serialPort.writeBytes(new byte[]
				{	(byte)(INSTRUCTION_PREFIX & 0xff),
					(byte)((INSTRUCTION_PREFIX >> 8) & 0xff),
					Servo.BCASTID,
					(byte)0x02,
					INSTRUCTION_RESET,
					(byte)0xF7 });
		signalPin.setState(PinState.LOW);
	}
	
	public boolean sendInstruction(byte id) throws SerialPortException
	{
		signalPin.setState(PinState.HIGH);
		serialPort.writeBytes(new byte[]
				{	(byte)(INSTRUCTION_PREFIX & 0xff),
					(byte)((INSTRUCTION_PREFIX >> 8) & 0xff),
					id,
					(byte)0x04,
					INSTRUCTION_READ_DATA,
					(byte)0x2B,
					(byte)0x01,
					(byte)0xCC });
		signalPin.setState(PinState.LOW);
		for (byte b : reader.getData())
		{
			System.out.println(b);
		}
		return reader.getRecieved();
	}
	
	public void sendAsyncInstruction() throws SerialPortException
	{
		signalPin.setState(PinState.HIGH);
		
		signalPin.setState(PinState.LOW);
		
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
	
	private class SerialPortReader implements SerialPortEventListener
	{
		private boolean recieved = false;
		private byte error = 0;
		private byte[] data = new byte[0];
		
		public boolean getRecieved()
		{
			return recieved;
		}
		
		public byte[] getData()
		{
			return data;
		}
		
		@Override
		public void serialEvent(SerialPortEvent serialPortEvent)
		{
			if (serialPortEvent.isRXCHAR())
			{
				try
				{
					byte[] buffer = serialPort.readBytes(serialPortEvent.getEventValue());
					byte id = buffer[2];
					error   = buffer[4];
					if (error != 0) return;
					System.arraycopy(buffer, 5, data, 0, buffer.length - 6);
					
				} catch (SerialPortException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (serialPortEvent.isCTS())
			{
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
				if(serialPortEvent.getEventValue() == 1)
				{
                    System.out.println("DSR - ON");
                }
                else
                {
                    System.out.println("DSR - OFF");
                }
			}
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
