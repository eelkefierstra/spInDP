package com.nhl.spindp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import com.nhl.spindp.vision.Frame;

public class Main
{
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private static Main instance;
	private Screen screen;
	private ServerSocket serverSocket;
	private VideoCapture capture;
	
	public static void main(String[] args)
	{
		Socket client = null;
		Mat frame = new Mat();
		instance = new Main();
		instance.screen = new Screen();
		try
		{
			instance.serverSocket = new ServerSocket(1339);
			client = instance.serverSocket.accept();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		instance.capture = new VideoCapture();
		instance.capture.open(0);
		
		DecimalFormat format = new DecimalFormat("#.##");
		FPSCounter counter = instance.new FPSCounter();
		counter.setDaemon(true);
		counter.start();
		
		while (instance.capture.isOpened())
		{
			instance.capture.read(frame);
			
			MatOfByte matOfByte = new MatOfByte();
			Imgcodecs.imencode(".jpg", frame, matOfByte);
			
			try
			{
				byte[] message = matOfByte.toArray();
				instance.screen.SetImage(ImageIO.read(new ByteArrayInputStream(message)));
				ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
				stream.writeObject(new Frame(message));
				stream.flush();
				counter.interrupt();
				instance.screen.setTitle("CustomChrome stream: " + format.format(counter.getFPS()) + " fps");
			}
			catch (SocketException ex)
			{
				System.out.println("Socket closed");
				System.exit(0);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				System.exit(-1);
			}
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
