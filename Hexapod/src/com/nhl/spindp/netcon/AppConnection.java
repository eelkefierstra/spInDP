package com.nhl.spindp.netcon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class AppConnection {

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
		while (true){
			Socket clientSocket = serverSocket.accept();
			InputStreamReader iStream = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader BR = new BufferedReader(iStream);
			
			String MESSAGE = BR.readLine();
			System.out.println(MESSAGE);
			while (true)
			{
				try{
					if(iStream != null){
						System.out.println(iStream);
						//SendDataToApp();
						OutputStream out = clientSocket.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
						writer.write(CreateDataToWrite(MESSAGE));
						writer.newLine();
						writer.flush();
					}
				}
				catch(Exception e){
					e.printStackTrace();
					
					clientSocket.close();
					break;
				}
			}
		}
	}
	
	public String CreateDataToWrite(String input){
		String Result = "";
		//if(input.equals("Servo Info"))
			Result = CreateRandomXML();
		return Result;
	}
	
	public String CreateRandomXML(){
		String result = "";
		 Random randomGenerator = new Random();
		 int Id = randomGenerator.nextInt(18);
		 int Hoek = randomGenerator.nextInt(90);
		 int Temperatuur = randomGenerator.nextInt(90);
		 result = "<Servo><Id>"+ Integer.toString(Id) + "</Id><Hoek>" + Integer.toString(Hoek) + "</Hoek><Temperatuur>" + Integer.toString(Temperatuur)+ "</Temperatuur></Servo>";
		return result;
	}
}


/*import java.io.*;
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
