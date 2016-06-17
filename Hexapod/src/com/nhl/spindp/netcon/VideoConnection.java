package com.nhl.spindp.netcon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.nhl.spindp.Utils;

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
				while (Utils.shouldRun)
				{
					try
					{
						Socket s = serverSocket.accept();
						System.out.println(s.getInetAddress().toString() + " connected");
						synchronized (this)
						{
							clients.add(s);
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		worker.setDaemon(true);
		worker.setName("streaming thread");
		worker.start();
	}
	
	public void sendObject(Object obj) throws IOException
	{
		for (int i = 0; i < clients.size(); i++)
		{
			Socket s = clients.get(i);
			try
			{
				new ObjectOutputStream(s.getOutputStream()).writeObject(obj);
			}
			catch (SocketException ex)
			{
				s.close();
				clients.remove(i);
				i--;
			}
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
