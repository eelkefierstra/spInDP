package com.nhl.spindp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
		
		while (instance.capture.isOpened())
		{
			instance.capture.read(frame);
			
			MatOfByte matOfByte = new MatOfByte();
			Imgcodecs.imencode(".jpg", frame, matOfByte);
			
			try
			{
				byte[] message = matOfByte.toArray();
				System.out.println("message lenght: " + String.valueOf(message.length));
				instance.screen.SetImage(ImageIO.read(new ByteArrayInputStream(message)));
				ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
				stream.writeObject(new Frame(message));
				stream.flush();
			}
			catch (SocketException ex)
			{
				System.out.println("Socket closed");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}

}
