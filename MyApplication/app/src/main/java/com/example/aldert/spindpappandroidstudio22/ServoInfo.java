package com.example.aldert.spindpappandroidstudio22;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ServoInfoRecieved(conn.getAllServoInfo());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void ServoInfoRecieved(Servo[] servos){
        for(Servo s : servos){
            if(s != null){
                final TextView ChangeIDTextView = (TextView) findViewById(R.id.textView4);
                ChangeIDTextView.setText(Integer.toString(s.getId()));
                final TextView ChangeHoekTextView = (TextView) findViewById(R.id.textView5);
                ChangeHoekTextView.setText(Integer.toString(s.getHoek()));
                final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.textView6);
                ChangeTemperatuuTextView.setText(Integer.toString(s.getTemperatuur()));
            }
        }


    }

}
