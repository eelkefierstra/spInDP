package com.example.aldert.spindpappandroidstudio22;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final ServerConnection conn = new ServerConnection("141.252.231.29", 1337);
    boolean connected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable(){
            public void run(){
                try{
                    conn.connect();
                    conn.readResponse();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(500);
            conn.sendString("Heey");
        }
        catch(Exception b){
            b.printStackTrace();
        }

        Button ServoInfoBtn = (Button) findViewById(R.id.ServoInfoBtn);
        ServoInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ServoInfo.class));
            }
        });
        final Button LiveStreamBtn = (Button) findViewById(R.id.LiveStreamBtn);
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
    }

    public void Connect(){


    }
    public void buttonOnClick(View v){

    }
}
