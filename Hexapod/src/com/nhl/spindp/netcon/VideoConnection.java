package com.nhl.spindp.netcon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VideoConnection
{
	private ServerSocket serverSocket;
	private List<Socket> clients;
	
	public VideoConnection() throws IOException
	{
		this(1339);
	}
	
	public VideoConnection(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
		clients      = new ArrayList<>();
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				if (serverSocket != null)
				{
					try
					{
						serverSocket.close();
					}
					catch (IOException e) { }
				}
			}
		});
	}
	
	public void sendBytes(byte[] message) throws IOException
	{
		for (Socket s : clients)
		{
			new DataOutputStream(s.getOutputStream()).write(message);
		}
	}
}
