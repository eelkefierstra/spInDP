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

import com.nhl.spindp.Main;

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
			while (true){
				String MESSAGE = BR.readLine();
				System.out.println(MESSAGE);
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
		switch(input){
			case "Heey":
				Result = "Heey Terug";
				break;
			case "ServoInfo":
				Result = CreateServoXML();
				break;
			case "HellingHoek":
				break;
			case "LiveStream":
				break;
		}
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
	
	public String CreateServoXML(){
		String result = "";
		
		for(int i = 1; i <= 18; i++){
			try {
				int Id = i;
				int Hoek = Main.getInstance().readCurrentAngle((byte) i);
				int Temperatuur = Main.getInstance().readCurrentTemperature((byte) i);
				result += "<Servo><Id>"+ Integer.toString(Id) + "</Id><Hoek>" + Integer.toString(Hoek) + "</Hoek><Temperatuur>" + Integer.toString(Temperatuur)+ "</Temperatuur></Servo>";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				result +=  "<Servo><Id>"+ Integer.toString(i) + "</Id><Hoek>" +"-1"+ "</Hoek><Temperatuur>" + "-1"+ "</Temperatuur></Servo>";
			}
		}
		return result;
	}
	
	public void stop(){
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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
