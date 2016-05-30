package com.nhl.spindp.netcon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ControllerConnection
{
	private ServerSocket serverSocket;
	private volatile double forward;
	private volatile double right;
	private Object data;
	
	public ControllerConnection() throws IOException
	{
		serverSocket = new ServerSocket(1337);
	}
	
	public ControllerConnection(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}
	
	public double getForward()
	{
		return forward;
	}
	
	public double getRight()
	{
		return right;
	}

	public void mainLoop() throws IOException
	{
		Socket clientSocket        = serverSocket.accept();
		ObjectInputStream iStream  = new ObjectInputStream(clientSocket.getInputStream());
		ObjectOutputStream oStream = new ObjectOutputStream(clientSocket.getOutputStream());
		
		while (true)
		{
			forward = iStream.readDouble();
			right   = iStream.readDouble();
			oStream.writeObject(data);
		}
	}
}
