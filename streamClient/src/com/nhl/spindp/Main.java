package com.nhl.spindp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import com.nhl.spindp.vision.Frame;

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
		
		DecimalFormat format = new DecimalFormat("#.##");
		FPSCounter counter = instance.new FPSCounter();
		counter.setDaemon(true);
		counter.start();
		
		while (true)
		{
			ObjectInputStream in = new ObjectInputStream(instance.socket.getInputStream());
			InputStream inputStream = new ByteArrayInputStream(((Frame)in.readObject()).getFrameBuff());//baos.toByteArray());
			//baos.close();
			instance.screen.SetImage(ImageIO.read(inputStream));
			counter.interrupt();
			instance.screen.setTitle("CustomChrome stream: " + format.format(counter.getFPS()) + " fps");
		}
	}
	
	private class FPSCounter extends Thread
	{
		private long lastTime;
		private double fps;
		
		@Override
		public void run()
		{
			for(;;)
			{
				lastTime = System.nanoTime();
				try
				{
					Thread.sleep(5000);
				}
				catch (Exception e)
				{ }
				fps = 1000000000.0 / (System.nanoTime() - lastTime);
				lastTime = System.nanoTime();
			}
		}
		
		public double getFPS()
		{
			return fps;
		}
	}
}
