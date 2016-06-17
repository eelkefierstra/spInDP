package com.nhl.spindp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

public class Main 
{
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private static Main instance;
	private Screen screen;
	private Socket socket;
	
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		instance = new Main();
		instance.screen = new Screen();
		instance.socket = new Socket("customchrome", 1339);
		int imageSize = 921600;
		
		while (true)
		{
			DataInputStream in = new DataInputStream(instance.socket.getInputStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int remainingBytes = imageSize;
			byte[] buff = new byte[1024];
			
			while (remainingBytes > 0)
			{
				int bytesRead = in.read(buff);
		    	if (bytesRead < 0)
		    	{
		    		throw new IOException("Unexpected end of data");
		    	}
		    	baos.write(buff, 0, bytesRead);
		    	remainingBytes -= bytesRead;
			}
			in.close();
			InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			instance.screen.SetImage(ImageIO.read(inputStream));
		}
	}
}
