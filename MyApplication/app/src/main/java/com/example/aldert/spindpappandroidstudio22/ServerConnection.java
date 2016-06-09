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
    private String mHostname;
    private int mPort;
    private Socket mSocket;

    public ServerConnection(String hostname, int port) {
        this.mHostname = hostname;
        this.mPort = port;
    }



    public void connect() throws IOException {
        System.out.println("Attempting to connect to " + mHostname + ":" + mPort);
        mSocket = new Socket(mHostname, mPort);
        System.out.println("Connection Established");
    }

    public int readResponse() throws IOException, XmlPullParserException {
        String userInput;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

        while ((userInput = stdIn.readLine()) != null || (userInput = stdIn.readLine()) != "") {
            //if(!userInput.contains("start")) {
                //Servo servo = (Servo) new XMLreader().parse(mSocket.getInputStream());
                //Record record = new GsonBuilder().create().fromJson(new JSONObject(userInput.replace("next", "")).getJSONObject("record").toString(), Record.class);
                //simulation.containerReceived(record);
            //}
            if(userInput.length() > 10){
                mServos = new XMLreader(userInput).getServo();
            }
            System.out.println(userInput);
        }
        System.out.println(userInput);
        return 0;
    }

    public Servo[] getAllServoInfo(){return this.mServos;}

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
