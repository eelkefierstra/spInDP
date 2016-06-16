package com.nhl.spindp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

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
		DataInputStream dataInputStream = new DataInputStream(instance.socket.getInputStream());
		byte[] buff = new byte[1280 * 720 * 4];
		
		
		while (true)
		{
			dataInputStream.read(buff, 0, buff.length);
			InputStream inputStream = new ByteArrayInputStream(buff);
			instance.screen.SetImage(ImageIO.read(inputStream));
		}
	}
}
