package com.nhl.spindp.netcon;

import java.io.*;
import java.net.*;

public class AppConnection {

	public static void main (String[] args) throws Exception 
	{
		AppConnection Server = new AppConnection();
	    Server.run(1337);
	}
	
	public void run(int port) throws Exception
	{
		
//		 ServerSocket welcomeSocket = new ServerSocket(port);
//		 while (true) {
//		     Socket socket = welcomeSocket.accept();
//		     new Thread(new RunnableSocketWorker(socket));
//		 }
		ServerSocket SRVSOCK = new ServerSocket(port);
		while(true){
			Socket SOCK = SRVSOCK.accept();
			InputStreamReader IR = new InputStreamReader(SOCK.getInputStream());
			while(true){
				try{
					BufferedReader BR = new BufferedReader(IR);
					String MESSAGE = BR.readLine();
					System.out.println(MESSAGE);
					
					if(MESSAGE != null)
					{
						PrintStream PS = new PrintStream(SOCK.getOutputStream());
						PS.println(SendAskedData(MESSAGE));
						System.out.println("Message send");
					}
				}
				catch(Exception e){
					System.out.println(e);
					e.printStackTrace();
				}
			}
		}
	}
	
	public String SendAskedData(String askedData){
		String result = null;
		return result;
	}
}

/*
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
				if(iStream != null){
					SendDataToApp();
				}
				System.out.println(iStream.readObject());
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void SendDataToApp(){
		try{
			FileOutputStream fos = new FileOutputStream("t.tmp");
			ObjectOutputStream oStream = new ObjectOutputStream(fos);
			oStream.writeObject("Today");
			oStream.close();
		 
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
*/
