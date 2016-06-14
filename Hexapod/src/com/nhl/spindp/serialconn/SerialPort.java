package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A class to facilitate the serial port connection
 * @author dudeCake
 *
 */
public class SerialPort
{
	private static final File serialInFile  = new File("/tmp/S_IN");
	private static final File serialOutFile = new File("/tmp/S_OUT");
	
	public SerialPort()
	{
		initPort("/dev/serial0");
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				cleanupPort();
			}
		});
	}
	
	/**
	 * Writes message to native method
	 * @param message The message to send
	 * @return True when no Exceptions occur
	 * @throws IOException
	 */
	synchronized boolean writeBytes(byte[] message) throws IOException
	{
		return nativeWrite(message);
	}
	
	/**
	 * Writes message to serial file
	 * @param message The message to send
	 * @return True when no Exceptions occur
	 * @throws IOException
	 */
	@Deprecated
	private boolean writeBits(byte[] message) throws IOException
	{
		//System.out.print("Sent: ");
		FileOutputStream writer = new FileOutputStream(serialOutFile);
		//System.out.println(DatatypeConverter.printHexBinary(message));
		writer.write(message);
		//writer.flush();
		writer.close();
		return true;
	}
	
	/**
	 * initializes native serial port
	 * @param port
	 */
	private native void initPort(String port);
	
	/**
	 * closes the native serial port
	 */
	private native void cleanupPort();
	
	/**
	 * writes the given byte[] to the serial port
	 * @param message message to be written
	 * @return whether the write succeeded
	 * @throws IOException
	 */
	private native boolean nativeWrite(byte[] message) throws IOException;
	
	/**
	 * reads a message from the serial port
	 * @return the read message
	 * @throws IOException
	 */
	private native byte[] nativeRead() throws IOException;
	
	/**
	 * writes the given byte[] to the serial port
	 * @param message message to be written
	 * @return whether the write succeeded
	 * @throws IOException
	 */
	private native boolean nativeWriteBytes(byte[] message) throws IOException;
	
	/**
	 * reads a message from the serial port
	 * @return the read message
	 * @throws IOException
	 */
	private native byte[] nativeReadBytes() throws IOException;
	
	/**
	 * reads a message from the serial port
	 * @return the read message
	 * @throws IOException
	 */
	private byte[] readBits() throws IOException
	{
		byte[] buffer = new byte[32];
		FileInputStream reader = new FileInputStream(serialInFile);
		int read = reader.read(buffer);
		reader.close();
		if (read < 0)
		{
			System.err.println("No reaction from servo!");
		}
		return Arrays.copyOf(buffer, read);
	}
	
	/**
	 * reads a message from the serial port
	 * @param len the length of the message
	 * @return the read message
	 * @throws IOException
	 */
	private byte[] readBits(int len) throws IOException
	{
		byte[] buffer = new byte[len];
		Arrays.fill(buffer, (byte)-1);
		int temp = 0;
		FileInputStream reader = new FileInputStream(serialInFile);
		int i = 0;
		for (; i < len; i++)
		{
			temp = reader.read();
			if (temp == -1) break;
			buffer[i] = (byte)temp;
		}
		reader.close();
		return Arrays.copyOf(buffer, i);
	}
	
	/**
	 * Reads the data in serialInFile
	 * @return The data from serialInFile
	 * @throws IOException
	 */
	synchronized byte[] readBytes() throws IOException
	{
		return nativeRead();
	}
	
	/**
	 * Reads the serialInFile for an given length
	 * @param len The length to read the serialInFile
	 * @return A byte array with the data from serialInFile 
	 * @throws IOException
	 */
	synchronized byte[] readBytes(int len) throws IOException
	{
		return nativeRead();
	}
}
