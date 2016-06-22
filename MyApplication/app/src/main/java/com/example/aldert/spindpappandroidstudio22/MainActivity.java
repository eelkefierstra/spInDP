package com.example.aldert.spindpappandroidstudio22;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static com.example.aldert.spindpappandroidstudio22.HellingInfo.HellingInfoRunning;
import static com.example.aldert.spindpappandroidstudio22.ServoInfo.ServoInfoRunning;

public class MainActivity extends AppCompatActivity {
    static String ipAdress = "141.252.236.31";
    static final ServerConnection conn = new ServerConnection(ipAdress, 1338);
    static boolean Connected = false;
    static Thread AdcThread = new Thread();
    static Thread ServoInfoThread = new Thread();
    static Thread HellingThread = new Thread();
    static boolean AcdInfoRunning = true;
    Activity activity;

    Thread timer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Connected){
            Connect();
        }
        //thread to ask Acd info
        AdcThread = new Thread()
        {
            public void run(){
                while(AcdInfoRunning){
                    //askAdcInfo();
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        //AdcThread.start();
        //Check if other threads are alive and close them
        if(HellingThread.isAlive()){

            HellingInfoRunning = false;
            try{
                HellingThread.join();
            }
            catch(Exception e){
            }
        }
        if(ServoInfoThread.isAlive()){
            ServoInfoRunning = false;
            try{
                ServoInfoThread.join();
            }
            catch(Exception e){
            }
        }

        //buttons for changing view
        Button ServoInfoBtn = (Button) findViewById(R.id.ServoInfoBtn);
        ServoInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ServoInfo.class));
            }
        });
        Button LiveStreamBtn = (Button) findViewById(R.id.LiveStreamBtn);
        LiveStreamBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LiveStream.class));
            }
        });
        Button HellingInfoBtn = (Button) findViewById(R.id.HellingInfoBtn);
        HellingInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HellingInfo.class));
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.RefreshButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connect();
            }
        });
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //ask ADC info
    public void askAdcInfo(){
        if(Connected){
            conn.sendString("ADC");
            drawAdcInfo(conn.getAdcInfo());
        }
    }

    //draw info
    public void drawAdcInfo(final String input){
        runOnUiThread(new Runnable() {
            public void run() {
                final TextView ChangAdcInfo = (TextView) findViewById(R.id.textView2);
                ChangAdcInfo.setText(input);
            }
        });
    }

    //Connect to the server
    public void Connect() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    conn.connect();
                    showToast("Connected");
                    conn.sendString("Heey");
                    Connected = true;
                    conn.readResponse();

                } catch (Exception e) {
                    e.printStackTrace();
                    Connected = false;
                    showToast("Niet connected");
                }
            }
        }).start();
    }
}
