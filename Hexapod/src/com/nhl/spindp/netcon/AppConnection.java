package com.nhl.spindp.netcon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AppConnection
{
	private ServerSocket serverSocket;
	
	public AppConnection() throws IOException
	{
		serverSocket = new ServerSocket(1338);
	}
	
	public AppConnection(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}
	
	public void mainLoop() throws IOException
	{
		Socket clientSocket = serverSocket.accept();
		ObjectInputStream iStream = new ObjectInputStream(clientSocket.getInputStream());
		
		while (true)
		{
			try
			{
				System.out.println(iStream.readObject());
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
}
