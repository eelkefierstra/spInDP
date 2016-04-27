package com.nhl.spindp.serialconn;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Servo
{
	/**
	 * Prefix for every instruction.
	 */
	public static final byte INSTRUCTION_PREFIX      = (byte)0xFF;
	
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
	public static final byte ADDRESS_ID              = (byte) 0x03;
	public static final byte ADDRESS_BAUD_RATE       = (byte) 0x04;
	public static final byte ADDRESS_RETURN_DELAY    = (byte) 0x05;
	public static final byte ADDRESS_CW_ANGLE_LIMIT  = (byte) 0x06;
	public static final byte ADDRESS_CCW_ANGLE_LIMIT = (byte) 0x08;
	public static final byte ADDRESS_TEMP_LIMIT      = (byte) 0x0B;
	public static final byte ADDRESS_VOLT_LIMIT_HIGH = (byte) 0x0C;
	public static final byte ADDRESS_VOLT_LIMIT_LOW  = (byte) 0x0D;
	public static final byte ADDRESS_MAX_TORQUE      = (byte) 0x0E;
	public static final byte ADDRESS_STATUS_RETURN   = (byte) 0x10;
	public static final byte ADDRESS_ALARM_LED       = (byte) 0x11;
	public static final byte ADDRESS_ALARM_SHUTDOWN  = (byte) 0x12;
	public static final byte ADDRESS_DOWN_CALIB      = (byte) 0x14;
	public static final byte ADDRESS_UP_CALIB        = (byte) 0x16;
	
	//RAM
	public static final byte ADDRESS_TORQUE_ENABLE   = (byte) 0x18;
	public static final byte ADDRESS_LED             = (byte) 0x19;
	public static final byte ADDRESS_CW_COMP_MARGIN  = (byte) 0x1A;
	public static final byte ADDRESS_CCW_COMP_MARGIN = (byte) 0x1B;
	public static final byte ADDRESS_CW_COMP_SLOPE   = (byte) 0x1C;
	public static final byte ADDRESS_CCW_COMP_SLOPE  = (byte) 0x1D;
	public static final byte ADDRESS_GOAL_POSITION   = (byte) 0x1E;
	public static final byte ADDRESS_MOVING_SPEED    = (byte) 0x20;
	public static final byte ADDRESS_TORQUE_LIMIT    = (byte) 0x22;
	public static final byte ADDRESS_PRESENT_POS     = (byte) 0x24;
	public static final byte ADDRESS_PRESENT_SPEED   = (byte) 0x26;
	public static final byte ADDRESS_PRESENT_LOAD    = (byte) 0x28;
	public static final byte ADDRESS_PRESENT_VOLTAGE = (byte) 0x2A;
	public static final byte ADDRESS_PRESENT_TEMP    = (byte) 0x2B;
	public static final byte ADDRESS_REGISTERED_INST = (byte) 0x2C;
	public static final byte ADDRESS_MOVING          = (byte) 0x2E;
	public static final byte ADDRESS_LOCK            = (byte) 0x2F;
	public static final byte ADDRESS_PUNCH           = (byte) 0x30;
	
	//Other info
	public static final int BAUDRATE_1 = 1000000;
	public static final int BAUDRATE_3 =  500000;
	public static final int BAUDRATE_4 =  400000;
	public static final int BAUDRATE_7 =  250000;
	public static final int BAUDRATE_9 =  200000;
	public static final byte DATABITS  = 8;
	public static final byte STOPBITS  = 1;
	public static final byte PARITY    = 0;
	public static final byte BCASTID   = (byte)0xFE;
	
	private final byte id;
	
	public Servo(byte id)
	{
		this.id = id;
	}
	
	public static byte[] createWriteDataInstruction(byte id, byte address, byte data)
	{
		byte[] buffer =
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				address,
				data,
				0
			};
		buffer[4] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_ID, data);
		return buffer;
	}
	
	public static byte[] createReadDataInstruction(byte id, byte address)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_READ_DATA,
				address,
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_READ_DATA, address);
		return buffer;
	}
	
	public static byte[] createResetInstruction()
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				BCASTID,
				0,
				INSTRUCTION_RESET,
				0
			};
		buffer[4] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(BCASTID, buffer[4], INSTRUCTION_RESET);
		return buffer;
	}
	
	public static byte[] createResetInstruction(byte id)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_RESET,
				0
			};
		buffer[4] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_RESET);
		return buffer;
	}
	
	public static byte[] createPingInstruction(byte id)
	{
		byte[] buffer =
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_PING,
				0
			};
		buffer[4] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_PING);
		return buffer;
	}
	
	public static byte[] createSetServoIdInstruction(byte id, byte newId)
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
		return buffer;
	}
	
	public static byte[] createMoveServoInstruction(byte id, short position)
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
		return buffer;
	}
	
	public static byte[] createMoveServoInstruction(byte id, short position, short speed)
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
				(byte)(speed &0xFF),
				(byte)((speed >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF), (byte)(speed &0xFF), (byte)((speed >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createWriteMoveServoInstruction(byte id, short position)
	{
		if (position < 0)     position = 0;
		if (position >= 1024) position = 1023;
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_REG_WRITE,
				ADDRESS_GOAL_POSITION,
				(byte)(position &0xFF),
				(byte)((position >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createWriteMoveServoInstruction(byte id, short position, short speed)
	{
		if (position < 0)     position = 0;
		if (position >= 1024) position = 1023;
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_REG_WRITE,
				ADDRESS_GOAL_POSITION,
				(byte)(position &0xFF),
				(byte)((position >> 8) &0xFF),
				(byte)(speed &0xFF),
				(byte)((speed >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF), (byte)(speed &0xFF), (byte)((speed >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createActionInstruction()
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				BCASTID,
				0,
				INSTRUCTION_ACTION,
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(BCASTID, buffer[4], INSTRUCTION_ACTION);
		return buffer;
	}
	
	public static byte[] createSyncWriteInstruction()
	{
		throw new NotImplementedException();
	}
	
	public static byte[] createSetAngleLimitInstruction(byte id, short cwLimit, short ccwLimit)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_CW_ANGLE_LIMIT,
				(byte)(cwLimit &0xFF),
				(byte)((cwLimit >> 8) &0xFF),
				(byte)(ccwLimit &0xFF),
				(byte)((ccwLimit >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_CW_ANGLE_LIMIT, (byte)(cwLimit &0xFF), (byte)((cwLimit >> 8) &0xFF), (byte)(ccwLimit &0xFF), (byte)((ccwLimit >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createSetTorqueLimitInstruction(byte id, short limit)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_MAX_TORQUE,
				(byte)(limit &0xFF),
				(byte)((limit >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_MAX_TORQUE, (byte)(limit &0xFF), (byte)((limit >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createSetPunchLimit(byte id, short limit)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_WRITE_DATA,
				ADDRESS_PUNCH,
				(byte)(limit &0xFF),
				(byte)((limit >> 8) &0xFF),
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_WRITE_DATA, ADDRESS_PUNCH, (byte)(limit &0xFF), (byte)((limit >> 8) &0xFF));
		return buffer;
	}
	
	public static byte[] createSetComplianceInstruction(byte id, byte cwMargin, byte ccwMargin, byte cwSlope, byte ccwSlope)
	{
		byte[] buffer = 
			{
				INSTRUCTION_PREFIX,
				INSTRUCTION_PREFIX,
				id,
				0,
				INSTRUCTION_REG_WRITE,
				ADDRESS_CW_COMP_MARGIN,
				cwMargin,
				ccwMargin,
				cwSlope,
				ccwSlope,
				0
			};
		buffer[4] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[4], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, cwMargin, ccwMargin, cwSlope, ccwSlope);
		return buffer;
	}
	
	/**
	 * Calculates the checksum for the instruction packet
	 * @param id The id of the receiving servo
	 * @param length The length of the instruction
	 * @return The checksum of the instruction packet
	 */
	private static byte computeChecksum(byte id, byte length, byte instruction)
	{
		return (byte)~((id + length + instruction) &0xFF);
	}
	
	/**
	 * Calculates the checksum for the instruction packet
	 * @param id The id of the receiving servo
	 * @param length The length of the instruction
	 * @param parameters The parameters of the instruction
	 * @return The checksum of the instruction packet
	 */
	private static byte computeChecksum(byte id, byte length, byte instruction, byte ... parameters)
	{
		int res = id + length + instruction;
		for (byte b : parameters)
		{
			res += b;
		}
		return (byte)~(res &0xFF);
	}
	
	/**
	 * Compares the received checksum with the calculated checksum
	 * @param buffer Buffer containing the parameters
	 * @param checksum The checksum of the status packet
	 * @return Whether 
	 */
	static boolean compareChecksum(byte[] data, byte checksum)
	{
		int res = 0;
		for (int i = 2; i < data.length - 1; i++)
		{
			res += data[i];
		}
		return ((byte)~(res &0xFF) == checksum);
	}
}