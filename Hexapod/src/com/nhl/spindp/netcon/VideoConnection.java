package com.nhl.spindp.netcon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VideoConnection
{
	private ServerSocket serverSocket;
	private Thread worker;
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
	
	public void start()
	{
		worker = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					clients.add(serverSocket.accept());
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		worker.setDaemon(true);
		worker.setName("streaming thread");
		worker.start();
	}
	
	public void sendObject(Object obj) throws IOException
	{
		for (Socket s : clients)
		{
			new ObjectOutputStream(s.getOutputStream()).writeObject(obj);
		}
	}
	
	public void sendBytes(byte[] message) throws IOException
	{
		for (Socket s : clients)
		{
			new DataOutputStream(s.getOutputStream()).write(message);
		}
	}
}
