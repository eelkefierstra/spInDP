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
		
		while (true)
		{
			ObjectInputStream in = new ObjectInputStream(instance.socket.getInputStream());
			InputStream inputStream = new ByteArrayInputStream(((Frame)in.readObject()).getFrameBuff());
			instance.screen.SetImage(ImageIO.read(inputStream));
		}
	}
}
