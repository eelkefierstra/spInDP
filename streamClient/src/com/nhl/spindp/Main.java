package com.nhl.spindp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

//import org.opencv.core.Core;

public class Main 
{
	static
	{
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private static Main instance;
	private Screen screen;
	private Socket socket;
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		instance = new Main();
		try
		{
			instance.socket = new Socket("localhost", 1339);
		}
		catch (ConnectException ex)
		{
			System.out.println("Maybe you should start the other program...");
			System.exit(-1);
		}
		instance.screen = new Screen();
		int imageSize = 52227;
		//DataInputStream in = new DataInputStream(instance.socket.getInputStream());
		
		while (true)
		{
			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int remainingBytes = imageSize;
			byte[] buff = new byte[4096];
			
			while (remainingBytes > 0)
			{
				int bytesRead = in.read(buff);
		    	if (bytesRead < 0)
		    	{
		    		throw new IOException("Unexpected end of data");
		    	}
		    	baos.write(buff, 0, bytesRead);
		    	remainingBytes -= bytesRead;
			}*/
			ObjectInputStream in = new ObjectInputStream(instance.socket.getInputStream());
			InputStream inputStream = new ByteArrayInputStream(((Frame)in.readObject()).getFrameBuff());//baos.toByteArray());
			//baos.close();
			instance.screen.SetImage(ImageIO.read(inputStream));
		}
	}
}
