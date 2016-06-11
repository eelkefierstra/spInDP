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
import com.nhl.spindp.Main.Info;

public class AppConnection
{
	private Info info = Main.getInstance().getInfo();
	private ServerSocket serverSocket;
	private Thread worker;
	
	public AppConnection() throws IOException
	{
		serverSocket = new ServerSocket(1338);
	}
	
	public AppConnection(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
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
					mainLoop();
				}
				catch (IOException e) { }
			}
		};
		worker.setDaemon(true);
		worker.start();
	}
	
	public void mainLoop() throws IOException
	{
		while (true)
		{
			Socket clientSocket = null;
			try
			{
				clientSocket = serverSocket.accept();
			}
			catch (Exception ex)
			{
				break;
			}
			
			InputStreamReader iStream = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader BR = new BufferedReader(iStream);
			while (true)
			{
				String MESSAGE = BR.readLine();
				System.out.println(MESSAGE);
				try
				{
					if(iStream != null)
					{
						System.out.println(iStream);
						//SendDataToApp();
						OutputStream out = clientSocket.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
						writer.write(CreateDataToWrite(MESSAGE));
						writer.newLine();
						writer.flush();
					}
				}
				catch(Exception e)
				{
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
				Result = "Heey terug";
				break;
			case "ServoInfo":
				Result = CreateRandomXML();
				break;
			case "HellingInfo":
				Result = CreateHellingInfo();
				break;
			case "LiveStream":
				break;
		}
		return Result;
	} 
	
	public String CreateRandomXML()
	{
		String result = "";
		 Random randomGenerator = new Random();
		 for(int i = 1; i <= 18; i++)
			{
				 int Id = i;
				 int Hoek = randomGenerator.nextInt(90);
				 int Temperatuur = randomGenerator.nextInt(90);
				 result = "<Servo><Id>"+ Integer.toString(Id) + "</Id><Hoek>" + Integer.toString(Hoek) + "</Hoek><Temperatuur>" + Integer.toString(Temperatuur)+ "</Temperatuur></Servo>";					
			}
		return result;
	}
	
	//<Servo><Id>1</Id><Hoek>20</Hoek><Temperatuur>15</Temperatuur></Servo>
	public String CreateServoXML()
	{
		String result = "";
		
		for(int i = 1; i <= 18; i++)
		{
			try
			{
				int Id = i;
				int Hoek = Main.getInstance().readCurrentAngle((byte) i);
				int Temperatuur = Main.getInstance().readCurrentTemperature((byte) i);
				result += "<Servo><Id>"+ Integer.toString(Id) + "</Id><Hoek>" + Integer.toString(Hoek) + "</Hoek><Temperatuur>" + Integer.toString(Temperatuur)+ "</Temperatuur></Servo>";
			}
			catch (IOException e)
			{
				result +=  "<Servo><Id>"+ Integer.toString(i) + "</Id><Hoek>" +"-1"+ "</Hoek><Temperatuur>" + "-1"+ "</Temperatuur></Servo>";
			}
		}
		return result;
	}
	
	public void stop()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	//<X:10,Y:16>	 
	 public String CreateHellingInfo(){
		 String result = "";
		 double[] gyro = info.getGyro();
		 int X = -1;
		 int Y = -1;
		 if(gyro != null){
			 X = (int)gyro[1];
			 Y = (int)gyro[0]; 
		 }
		 //Random randomGenerator = new Random();
		 result =  "<X:"+Integer.toString(X)+",Y:"+Integer.toString(Y)+">";
		 return result;
	 }
}


