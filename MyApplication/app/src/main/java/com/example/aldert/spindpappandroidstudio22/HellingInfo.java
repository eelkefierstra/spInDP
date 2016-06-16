package com.example.aldert.spindpappandroidstudio22;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import static com.example.aldert.spindpappandroidstudio22.MainActivity.AcdInfoRunning;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.AdcThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.Connected;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.HellingThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.ServoInfoThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.conn;
import static com.example.aldert.spindpappandroidstudio22.ServoInfo.ServoInfoRunning;

public class HellingInfo extends AppCompatActivity {
    static boolean HellingInfoRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helling_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ProgressBar HellingXNegative = (ProgressBar) findViewById(R.id.XNegative);
        HellingXNegative.setRotation(180);
        final ProgressBar HellingYNegative = (ProgressBar) findViewById(R.id.YNegative);
        HellingYNegative.setRotation(180);
        HellingThread = new Thread()
        {
            public void run(){
                while(HellingInfoRunning){
                    UpdateHellingsHoek();
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        HellingThread.start();
        if(ServoInfoThread.isAlive()){
            ServoInfoRunning = false;
            try{
                ServoInfoThread.join();
            }
            catch(Exception e){
            }
        }
        if(AdcThread.isAlive()){
            AcdInfoRunning = false;
            try{
                AdcThread.join();
            }
            catch(Exception e){
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateHellingsHoek();
                if(!HellingThread.isAlive()){
                    HellingThread.start();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void UpdateHellingsHoek(){
        if(Connected){
            conn.sendString("HellingInfo");
            drawHelling(conn.getHellingInfo());
        }
    }





    public void drawHelling(final int[] helling){
        runOnUiThread(new Runnable() {
            public void run() {
                final ProgressBar HellingXPositive = (ProgressBar) findViewById(R.id.XPositive);
                final ProgressBar HellingYPositive = (ProgressBar) findViewById(R.id.YPositive);
                final ProgressBar HellingXNegative = (ProgressBar) findViewById(R.id.XNegative);
                final ProgressBar HellingYNegative = (ProgressBar) findViewById(R.id.YNegative);

                if(helling != null){

                    if(helling[0]> 0){

                        HellingXPositive.setProgress(helling[0]);
                        HellingXNegative.setProgress(0);
                    }
                    else if(helling[0]<0){
                        HellingXNegative.setProgress(helling[0] * -1);
                        HellingXPositive.setProgress(0);
                    }
                }
                if(helling != null){
                    if(helling[1]> 0){
                        HellingYPositive.setProgress(helling[1]);
                        HellingYNegative.setProgress(0);

                    }
                    else if(helling[1]<0){
                        HellingYNegative.setProgress(helling[1] * -1);
                        HellingYPositive.setProgress(0);
                    }
                }
            }
        });
    }



}
