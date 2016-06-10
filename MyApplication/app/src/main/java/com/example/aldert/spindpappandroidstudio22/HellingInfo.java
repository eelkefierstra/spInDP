package com.example.aldert.spindpappandroidstudio22;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentController;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.aldert.spindpappandroidstudio22.MainActivity.Connected;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.conn;
public class HellingInfo extends AppCompatActivity {
    private Canvas mCanvas;
    Thread timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helling_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timer = new Thread()
        {
            public void run(){
                while(true){
                    UpdateHellingsHoek();
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        timer.start();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateHellingsHoek();
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
                if(helling != null){
                    final ProgressBar HellingX = (ProgressBar) findViewById(R.id.HellingX);
                    HellingX.setMinimumWidth(500);
                    HellingX.setProgress(helling[0]);
                }
                if(helling != null) {
                    final ProgressBar HellingY = (ProgressBar) findViewById(R.id.HellingY);
                    HellingY.setMinimumWidth(500);
                    HellingY.setProgress(helling[1]);
                }
            }
        });
    }


}
