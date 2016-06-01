package com.example.aldert.spindpappandroidstudio22;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static com.example.aldert.spindpappandroidstudio22.MainActivity.conn;

public class ServoInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            conn.sendString("Servo Info");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServoInfoRecieved(conn.getAllServoInfo());
            }
        });
        ServoInfoRecieved(conn.getAllServoInfo());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void ServoInfoRecieved(Servo[] servos){
        int Id = 0;
        int hoek = 0;
        int Temperatuur = 0;
        String Hoek = "Hoek1";
        for(Servo s : servos){
            if(s != null){
                Id = s.getId();
                hoek = s.getHoek();
                Temperatuur = s.getTemperatuur();
                if(Id == 1){
                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.Servo1);
                    ChangeIDTextView.setText(Integer.toString(Id));
                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek1);
                    ChangeHoekTextView.setText(Integer.toString(hoek));
                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temperatuur1);
                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                }
                if(Id == 2){
                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.Servo2);
                    ChangeIDTextView.setText(Integer.toString(Id));
                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek2);
                    ChangeHoekTextView.setText(Integer.toString(hoek));
                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temperatuur2);
                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                }
                if(Id == 3){
                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.Servo3);
                    ChangeIDTextView.setText(Integer.toString(Id));
                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek3);
                    ChangeHoekTextView.setText(Integer.toString(hoek));
                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temperatuur3);
                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                }

            }
        }


    }

}
