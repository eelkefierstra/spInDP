package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

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
	
	/**
	 * Writes message to serialOutFile
	 * @param message The message to send
	 * @return True when no Exceptions occur
	 * @throws IOException
	 */
	boolean writeBytes(byte[] message) throws IOException
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
	 * Reads the data in serialInFile
	 * @return The data from serialInFile
	 * @throws IOException
	 */
	byte[] readBytes(int id) throws IOException
	{
		//System.out.print("Received: ");
		byte[] buffer = new byte[32];
		FileInputStream reader = new FileInputStream(serialInFile);
		int read = reader.read(buffer);
		//System.out.println(DatatypeConverter.printHexBinary(buffer));
		reader.close();
		if (read < 0)
		{
			Main.servoFailed((byte) id);
			//throw new IOException("No reaction from servo" + String.valueOf(id) + '!');
			System.err.println("No reaction from servo" + String.valueOf(id) + '!');
		}
		return Arrays.copyOf(buffer, read);
	}
	
	/**
	 * Reads the serialInFile for an given length
	 * @param len The length to read the serialInFile
	 * @return A byte array with the data from serialInFile 
	 * @throws IOException
	 */
	byte[] readBytes(int id, int len) throws IOException
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
}
