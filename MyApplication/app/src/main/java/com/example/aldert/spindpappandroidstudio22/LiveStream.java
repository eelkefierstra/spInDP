package com.example.aldert.spindpappandroidstudio22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nhl.spindp.vision.Frame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import static com.example.aldert.spindpappandroidstudio22.MainActivity.ipAdress;

//import javax.imageio.ImageIO;


public class LiveStream extends AppCompatActivity {
    //private Screen screen;
    private Socket socket;
    Thread liveStreamThread = new Thread();
    static boolean liveStreamRunning = true;
    boolean connected = false;
    private ImageView liveStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        liveStreamConnect();
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        liveStreamConnection();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveStreamConnect();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void liveStreamConnection(){
        liveStreamThread = new Thread()
        {
            public void run(){
            while(liveStreamRunning){
                if(socket!=null){
                    try{
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        //InputStream inputStream = new ByteArrayInputStream(((Frame)in.readObject()).getFrameBuff());
                        byte[] message = ((Frame)in.readObject()).getFrameBuff();
                        liveStream = (ImageView) findViewById(R.id.liveStream);
                        //screen.SetImage(ImageIO.read(inputStream));
                        //InputStream inputStream  = new ByteArrayInputStream(decodedString);
                        Bitmap bitmap  = BitmapFactory.decodeByteArray(message, 0, message.length);
                        displayStream(bitmap);
                        //liveStream.setImageBitmap(bitmap);
                        //liveStream.invalidate();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            }
        };
        liveStreamThread.start();
    }

    public void liveStreamConnect(){
        liveStreamThread = new Thread()
        {
            public void run(){
            try
                {
                    socket = new Socket(ipAdress, 1339);
                    showToast("Connected Live");
                }
                catch (Exception ex)
                {
                    System.out.println("Maybe you should start the other program...");
                    showToast("Niet connected Live");
                    //System.exit(-1);
                }
            }
        };
        liveStreamThread.start();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(LiveStream.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayStream(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            public void run()
            {
                liveStream.setImageBitmap(bitmap);
                liveStream.invalidate();
            }
        });
    }

}

/*import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

//import org.opencv.core.Core;

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
		int imageSize = 52227;

		while (true)
		{
			ObjectInputStream in = new ObjectInputStream(instance.socket.getInputStream());
			InputStream inputStream = new ByteArrayInputStream(((Frame)in.readObject()).getFrameBuff());
			instance.screen.SetImage(ImageIO.read(inputStream));
		}
	}
}
*/
