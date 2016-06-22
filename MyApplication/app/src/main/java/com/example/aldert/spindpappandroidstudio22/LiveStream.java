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
    private ImageView liveStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Connect to livestream server
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

    //While live stream is running read the data we get from the server
    public void liveStreamConnection(){
        liveStreamThread = new Thread()
        {
            public void run(){
            while(liveStreamRunning){
                if(socket!=null){
                    try{
                        //read data
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        byte[] message = ((Frame)in.readObject()).getFrameBuff();
                        Bitmap bitmap  = BitmapFactory.decodeByteArray(message, 0, message.length);
                        displayStream(bitmap);
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

    //Connect with the server
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
                }
            }
        };
        liveStreamThread.start();
    }

    //Show toast, when connected or not connected
    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(LiveStream.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display the data from the bitmap on the imageview
    public void displayStream(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            public void run()
            {
                liveStream = (ImageView) findViewById(R.id.liveStream);
                liveStream.setImageBitmap(bitmap);
                liveStream.invalidate();
            }
        });
    }

}
