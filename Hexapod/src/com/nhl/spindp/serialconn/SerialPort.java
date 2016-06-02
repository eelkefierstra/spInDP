package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.nhl.spindp.Main;


/**
 * A class to facilitate the serialport connection
 * @author dudeCake
 *
 */
public class SerialPort
{
	private static final File serialInFile  = new File("/tmp/S_IN");
	private static final File serialOutFile = new File("/tmp/S_OUT");
	
	SerialPort()
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
	 * Writes message to serialOutFile
	 * @param message The message to send
	 * @return True when no Exceptions occur
	 * @throws IOException
	 */
	synchronized boolean writeBytes(byte[] message) throws IOException
	{
		return nativeWrite(message);
	}
	
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
	
	private native void initPort(String port);
	
	private native void cleanupPort();
	
	private native boolean nativeWrite(byte[] message) throws IOException;
	
	private native byte[] nativeRead() throws IOException;
	
	private native boolean nativeWriteBytes(byte[] message) throws IOException;
	
	private native byte[] nativeReadBytes() throws IOException;
	
	private byte[] readBits() throws IOException
	{
		//System.out.print("Received: ");
		byte[] buffer = new byte[32];
		FileInputStream reader = new FileInputStream(serialInFile);
		int read = reader.read(buffer);
		//System.out.println(DatatypeConverter.printHexBinary(buffer));
		reader.close();
		if (read < 0)
		{
			//throw new IOException("No reaction from servo" + String.valueOf(id) + '!');
			System.err.println("No reaction from servo!");
		}
		return Arrays.copyOf(buffer, read);
	}
	
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
