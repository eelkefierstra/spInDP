package com.example.aldert.spindpappandroidstudio22;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final ServerConnection conn = new ServerConnection("10.42.1.1", 1338);
    static boolean Connected = false;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connect();

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
