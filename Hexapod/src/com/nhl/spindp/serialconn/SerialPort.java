package com.nhl.spindp.serialconn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class SerialPort
{
	private static final File serialInFile  = new File("/tmp/S_IN");
	private static final File serialOutFile = new File("/tmp/S_OUT");
	
	boolean writeBytes(byte[] message) throws IOException
	{
		System.out.print("Sent: ");
		FileOutputStream writer = new FileOutputStream(serialOutFile);
		System.out.println(DatatypeConverter.printHexBinary(message));
		writer.write(message);
		writer.flush();
		writer.close();
		return true;
	}
	
	byte[] readBytes() throws IOException
	{
		System.out.print("Received: ");
		byte[] buffer = new byte[64];
		FileInputStream reader = new FileInputStream(serialInFile);
		int read = reader.read(buffer);
		System.out.println(DatatypeConverter.printHexBinary(buffer));
		reader.close();
		if (read < 0) throw new IOException("No reaction from servo!");
		return Arrays.copyOf(buffer, read);
	}
	
	byte[] readBytes(int len) throws IOException
	{
		byte[] buffer = new byte[64];
		int temp = 0;
		FileInputStream reader = new FileInputStream(serialInFile);
		int i = 0;
		for (; i < len; i++)
		{
			temp = (byte)reader.read();
			if (temp == -1) break;
			buffer[i] = (byte)temp;
		}
		reader.close();
		return Arrays.copyOf(buffer, i);
	}
}
