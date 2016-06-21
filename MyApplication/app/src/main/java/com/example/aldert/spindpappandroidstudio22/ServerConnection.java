package com.example.aldert.spindpappandroidstudio22;


import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Aldert on 27-5-2016.
 */

public class ServerConnection {
    private Servo[] mServos = new Servo[19];
    private String mAdc;
    private int[] mHelling;
    private String mHostname;
    private int mPort;
    private Socket mSocket;

    //Initialize ip and port
    public ServerConnection(String hostname, int port) {
        this.mHostname = hostname;
        this.mPort = port;
    }


    //connect
    public void connect() throws IOException {
        System.out.println("Attempting to connect to " + mHostname + ":" + mPort);
        mSocket = new Socket(mHostname, mPort);
        System.out.println("Connection Established");
    }

    //read data from server
    public int readResponse() throws IOException, XmlPullParserException {
        String userInput;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

        while ((userInput = stdIn.readLine()) != null || (userInput = stdIn.readLine()) != "") {
           //Do nothing when we get reaction from our heey
            if(userInput.equals("Heey terug")){

            }
            //Als data is bigger then 20 it is servo info
            else if(userInput.length() > 20){
                mServos = new XMLreader(userInput).getServo();
            }
            //if inut is smaller then 10 then it is adc info
            else if(userInput.length() <10){
                mAdc = userInput;
            }
            //else it is helling info
            else if(userInput.length() < 20){
                mHelling = new XMLreader(userInput).getHellingInfo();
            }
            System.out.println(userInput);
        }
        System.out.println(userInput);
        return 0;
    }

    //getters
    public Servo[] getAllServoInfo(){return this.mServos;}

    public String getAdcInfo(){return this.mAdc;}

    public int[] getHellingInfo(){return this.mHelling;}

    //Send string, used to asked data
    public void sendString(String data) {
        try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            writer.write(data);
            writer.newLine();
            writer.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
