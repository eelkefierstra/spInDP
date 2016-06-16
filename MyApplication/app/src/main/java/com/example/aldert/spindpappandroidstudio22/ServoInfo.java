package com.example.aldert.spindpappandroidstudio22;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static com.example.aldert.spindpappandroidstudio22.HellingInfo.HellingInfoRunning;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.AcdInfoRunning;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.AdcThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.Connected;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.HellingThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.ServoInfoThread;
import static com.example.aldert.spindpappandroidstudio22.MainActivity.conn;

public class ServoInfo extends AppCompatActivity {
    static boolean ServoInfoRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateServoInfo();
                if(!ServoInfoThread.isAlive()){
                    ServoInfoThread.start();
                }
            }
        });
        ServoInfoRecieved(conn.getAllServoInfo());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ServoInfoThread = new Thread()
        {
            public void run(){
                while(ServoInfoRunning){
                    UpdateServoInfo();
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        ServoInfoThread.start();
        if(HellingThread.isAlive()){
            HellingInfoRunning = false;
            try{
                HellingThread.join();
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
        if(AdcThread.isAlive()){
            AcdInfoRunning = false;
            try{
                AdcThread.join();
            }
            catch(Exception e){
                e.printStackTrace();

            }

        }
    }

    private void UpdateServoInfo(){
        if(Connected){
            conn.sendString("ServoInfo");
            ServoInfoRecieved(conn.getAllServoInfo());
        }
    }

    public void ServoInfoRecieved(final Servo[] servos){
        runOnUiThread(new Runnable() {
            public void run() {
                int Id = 0;
                int hoek = 0;
                int Temperatuur = 0;
                for(Servo s : servos){
                    if(s != null){
                        Id = s.getId();
                        hoek = s.getHoek();
                        Temperatuur = s.getTemperatuur();
                        switch(Id){
                            case 1:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID1);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek1);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp1);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 2:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID2);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek2);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp2);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 3:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID3);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek3);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp3);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 4:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID4);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek4);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp4);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 5:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID5);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek5);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp5);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 6:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID6);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek6);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp6);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 7:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID7);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek7);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp7);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 8:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID8);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek8);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp8);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 9:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID9);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek9);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp9);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 10:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID10);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek10);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp10);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 11:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID11);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek11);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp11);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 12:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID12);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek12);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp12);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 13:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID13);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek13);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp13);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 14:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID14);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek14);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp14);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 15:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID15);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek15);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp15);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 16:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID16);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek16);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp16);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 17:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID17);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek17);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp17);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                            case 18:
                                if(true){
                                    final TextView ChangeIDTextView = (TextView) findViewById(R.id.ID18);
                                    ChangeIDTextView.setText(Integer.toString(Id));
                                    final TextView ChangeHoekTextView = (TextView) findViewById(R.id.Hoek18);
                                    ChangeHoekTextView.setText(Integer.toString(hoek));
                                    final TextView ChangeTemperatuuTextView = (TextView) findViewById(R.id.Temp18);
                                    ChangeTemperatuuTextView.setText(Integer.toString(Temperatuur));
                                }
                                break;
                        }
                    }
                }
            }
        });

    }

}
