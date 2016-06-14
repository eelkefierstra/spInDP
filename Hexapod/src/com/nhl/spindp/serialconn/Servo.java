package com.nhl.spindp.serialconn;

import java.util.Arrays;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Servo
{
	/**
	 * Prefix for every instruction.
	 */
	static final byte INSTRUCTION_PREFIX      = (byte)0xFF;
	
	/**
	 * No action, Used for obtaining a Status Packet.
	 */
	static final byte INSTRUCTION_PING        = (byte) 0x01;
	
	/**
	 * Reading values in the Control Table.
	 */
	static final byte INSTRUCTION_READ_DATA   = (byte) 0x02;
	
	/**
	 * Writing values to the Control Table.
	 */
	static final byte INSTRUCTION_WRITE_DATA  = (byte) 0x03;
	
	/**
	 * Similar to WRITE_DATA, but stays in standby mode until the ACTION instruction is given.
	 */
	static final byte INSTRUCTION_REG_WRITE   = (byte) 0x04;
	
	/**
	 * Triggers the action registered by the REG_WRITE instruction.
	 */
	static final byte INSTRUCTION_ACTION      = (byte) 0x05;
	
	/**
	 * Changes the Control Table values of the Dynamixel actuator to the factory default value settings.
	 */
	static final byte INSTRUCTION_RESET       = (byte) 0x06;
	
	/**
	 * Used for controlling many Dynamixel actuators at the same time.
	 */
	static final byte INSTRUCTION_SYNC_WRITE  = (byte) 0x86;
	
	//EEPROM
	static final byte ADDRESS_ID              = (byte) 0x03;
	static final byte ADDRESS_BAUD_RATE       = (byte) 0x04;
	static final byte ADDRESS_RETURN_DELAY    = (byte) 0x05;
	static final byte ADDRESS_CW_ANGLE_LIMIT  = (byte) 0x06;
	static final byte ADDRESS_CCW_ANGLE_LIMIT = (byte) 0x08;
	static final byte ADDRESS_TEMP_LIMIT      = (byte) 0x0B;
	static final byte ADDRESS_VOLT_LIMIT_HIGH = (byte) 0x0C;
	static final byte ADDRESS_VOLT_LIMIT_LOW  = (byte) 0x0D;
	static final byte ADDRESS_MAX_TORQUE      = (byte) 0x0E;
	static final byte ADDRESS_STATUS_RETURN   = (byte) 0x10;
	static final byte ADDRESS_ALARM_LED       = (byte) 0x11;
	static final byte ADDRESS_ALARM_SHUTDOWN  = (byte) 0x12;
	static final byte ADDRESS_DOWN_CALIB      = (byte) 0x14;
	static final byte ADDRESS_UP_CALIB        = (byte) 0x16;
	
	//RAM
	static final byte ADDRESS_TORQUE_ENABLE   = (byte) 0x18;
	static final byte ADDRESS_LED             = (byte) 0x19;
	static final byte ADDRESS_CW_COMP_MARGIN  = (byte) 0x1A;
	static final byte ADDRESS_CCW_COMP_MARGIN = (byte) 0x1B;
	static final byte ADDRESS_CW_COMP_SLOPE   = (byte) 0x1C;
	static final byte ADDRESS_CCW_COMP_SLOPE  = (byte) 0x1D;
	static final byte ADDRESS_GOAL_POSITION   = (byte) 0x1E;
	static final byte ADDRESS_MOVING_SPEED    = (byte) 0x20;
	static final byte ADDRESS_TORQUE_LIMIT    = (byte) 0x22;
	static final byte ADDRESS_PRESENT_POS     = (byte) 0x24;
	static final byte ADDRESS_PRESENT_SPEED   = (byte) 0x26;
	static final byte ADDRESS_PRESENT_LOAD    = (byte) 0x28;
	static final byte ADDRESS_PRESENT_VOLTAGE = (byte) 0x2A;
	static final byte ADDRESS_PRESENT_TEMP    = (byte) 0x2B;
	static final byte ADDRESS_REGISTERED_INST = (byte) 0x2C;
	static final byte ADDRESS_MOVING          = (byte) 0x2E;
	static final byte ADDRESS_LOCK            = (byte) 0x2F;
	static final byte ADDRESS_PUNCH           = (byte) 0x30;
	
	//Other info
	static final int BAUDRATE_1 = 1000000;
	static final int BAUDRATE_3 =  500000;
	static final int BAUDRATE_4 =  400000;
	static final int BAUDRATE_7 =  250000;
	static final int BAUDRATE_9 =  200000;
	static final byte DATABITS  = 8;
	static final byte STOPBITS  = 1;
	static final byte PARITY    = 0;
	static final byte BCASTID   = (byte)0xFE;
	
	private final byte id;
	
	public Servo(byte id)
	{
		this.id = id;
	}
	
	byte getId()
	{
		return id;
	}
	
	/**
	 * Create's an write data instruction for the servo
	 * @param id The id of the servo
	 * @param address The adress to write
	 * @param data The data to write
	 * @return Data instruction
	 */
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
		buffer[3] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_ID, data);
		return buffer;
	}
	
	/**
	 * 
	 * @param address
	 * @param parameters
	 * @return
	 */
	public static byte[] createSyncWriteDataInstruction(byte address, byte[] parameters)
	{
		byte[] buffer = new byte[parameters.length + 7];
		buffer[0] = buffer[1] = INSTRUCTION_PREFIX;
		buffer[2] = BCASTID;
		buffer[3] = INSTRUCTION_SYNC_WRITE;
		buffer[4] = (byte)(buffer.length - 4);
		buffer[5] = address;
		int i = 6;
		for (byte data : parameters)
		{
			buffer[i] = data;
			i++;
		}
		buffer[buffer.length - 1] = computeChecksum(BCASTID, buffer[4], INSTRUCTION_SYNC_WRITE, address, Arrays.copyOfRange(buffer, 6, buffer.length));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param address
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_READ_DATA, address);
		return buffer;
	}
	
	/**
	 * 
	 * @return
	 */
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
		buffer[3] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(BCASTID, buffer[3], INSTRUCTION_RESET);
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
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
		buffer[3] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_RESET);
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
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
		buffer[3] = (byte)(buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_PING);
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param newId
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_ID, newId);
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param position
	 * @param speed
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF), (byte)(speed &0xFF), (byte)((speed >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param position
	 * @param speed
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, (byte)(position &0xFF), (byte)((position >> 8) &0xFF), (byte)(speed &0xFF), (byte)((speed >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(BCASTID, buffer[3], INSTRUCTION_ACTION);
		return buffer;
	}
	
	/**
	 * 
	 * @return
	 */
	public static byte[] createSyncWriteInstruction()
	{
		throw new NotImplementedException();
	}
	
	/**
	 * 
	 * @param id
	 * @param cwLimit
	 * @param ccwLimit
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_CW_ANGLE_LIMIT, (byte)(cwLimit &0xFF), (byte)((cwLimit >> 8) &0xFF), (byte)(ccwLimit &0xFF), (byte)((ccwLimit >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param limit
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_MAX_TORQUE, (byte)(limit &0xFF), (byte)((limit >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param limit
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_WRITE_DATA, ADDRESS_PUNCH, (byte)(limit &0xFF), (byte)((limit >> 8) &0xFF));
		return buffer;
	}
	
	/**
	 * 
	 * @param id
	 * @param cwMargin
	 * @param ccwMargin
	 * @param cwSlope
	 * @param ccwSlope
	 * @return
	 */
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
		buffer[3] = (byte) (buffer.length - 4);
		buffer[buffer.length - 1] = computeChecksum(id, buffer[3], INSTRUCTION_REG_WRITE, ADDRESS_GOAL_POSITION, cwMargin, ccwMargin, cwSlope, ccwSlope);
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
	 * 
	 * @param id
	 * @param length
	 * @param instruction
	 * @param address
	 * @param parameters
	 * @return
	 */
	private static byte computeChecksum(byte id, byte length, byte instruction, byte address, byte[] parameters)
	{
		int res = id + length + instruction + address;
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