package com.nhl.spindp.vision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import com.nhl.spindp.Main;
import com.nhl.spindp.Utils;
import com.nhl.spindp.netcon.VideoConnection;

public class ObjectRecognition
{
	private Object locker;
	private VideoConnection conn;
	private int x = 0;
	private String type = "balloon";
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture(0);
	// a flag to change the button behavior
	private boolean cameraActive = false;
	
	/**
	 * Get x coordinate of found object
	 * @return
	 */
	public int getX()
	{
		int res = 0;
		synchronized (locker)
		{
			res = x;
		}
		return res;
	}
	
	/**
	 * start vision
	 * @param sort "line" to search for line and "balloon" to search for balloon
	 */
	public void start(String sort)
	{
		if (sort.equals("line"))
			type = "line";
		else if(sort.equals("balloon"))
			type = "balloon";
		else
			type = "balloon";
		
		if(!cameraActive)
			startCamera();
	}
	
	/**
	 * stop vision search
	 */
	public void stop()
	{
		if(cameraActive)
		{
			startCamera();
		}
	}
	
	/**
	 * start camera and search
	 */
	private void startCamera()
	{		
		if (!cameraActive)
		{
			// start the video capture
			capture.open(0);
			
			// is the video stream available?
			if (capture.isOpened())
			{
				cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable()
				{
					@Override
					public void run()
					{
						grabFrame();
					}
				};
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			cameraActive = false;

			try
			{
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
			
			// release the camera
			capture.release();
		}
	}
	
	private void sendFrame(Mat mat)
	{
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, matOfByte);
		try
		{
			conn.sendBytes(matOfByte.toArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 */
	private void grabFrame()
	{
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (capture.isOpened())
		{
			try
			{
				// read the current frame
				capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					sendFrame(frame);
					// init
					Mat blurredImage = new Mat();
					Mat hsvImage = new Mat();
					Mat mask = new Mat();
					Mat morphOutput = new Mat();
					
					// remove some noise
					Imgproc.blur(frame, blurredImage, new Size(7, 7));
					
					// convert the frame to HSV
					Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
					
					//thresholding values					
					int hMin = 0;
					int hMax = 17;
					int sMin = 86;
					int sMax = 255;
					int vMin = 80;
					int vMax = 188;
					
					if(type == "balloon")
					{
						//threshold for the balloon
						hMin = 0;
						hMax = 17;
						sMin = 86;
						sMax = 255;
						vMin = 80;
						vMax = 188;
					}
					else // type = "line"
					{
						//threshold for the line
						hMin = 0;
						hMax = 17;
						sMin = 86;
						sMax = 255;
						vMin = 80;
						vMax = 188;
					}
					
					Scalar minValues = new Scalar(hMin, sMin,vMin);
					Scalar maxValues = new Scalar(hMax, sMax, vMax);
										
					// threshold HSV image to select balloons
					Core.inRange(hsvImage, minValues, maxValues, mask);
					
					// morphological operators
					// dilate with large element, erode with small ones
					Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
					Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

					Imgproc.erode(mask, morphOutput, erodeElement);
					Imgproc.erode(mask, morphOutput, erodeElement);
					
					Imgproc.dilate(mask, morphOutput, dilateElement);
					Imgproc.dilate(mask, morphOutput, dilateElement);
					
					if(type == "balloon")
					{
						Mat removeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(16, 16));
						Imgproc.erode(mask, morphOutput, removeElement);
					}
		
					// find the tennis ball(s) contours and show them
					frame = findAndDraw(morphOutput, frame);
				}
				
			}
			catch (Exception e)
			{
				// log the (full) error
				System.err.print("ERROR");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Given a binary image containing one or more closed surfaces, use it as a
	 * mask to find and highlight the objects contours
	 * 
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the
	 *            objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	private Mat findAndDraw(Mat maskedImage, Mat frame)
	{
		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		
		
		// find contours
		Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		// if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
		{
			// for each contour, display it in blue
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
			{
				x = Imgproc.boundingRect(contours.get(idx)).x + Imgproc.boundingRect(contours.get(idx)).width/2;				
				//System.out.println(x);
				Main.getInstance().setDirection(0,1.0 , Utils.map((double)x, 0.0, 1280.0, -1.0, 1.0));
			}
		}
		return frame;
	}
}
