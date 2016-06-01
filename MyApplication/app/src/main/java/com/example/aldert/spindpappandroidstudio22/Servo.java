package com.example.aldert.spindpappandroidstudio22;



/**
 * Created by Aldert on 31-5-2016.
 */

public class Servo {
    private int Id;
    private int Hoek;
    private int Temperatuur;
    public Servo(int id, int Hoek, int Temperatuur)  {
        this.Id = id;
        this.Hoek = Hoek;
        this.Temperatuur = Temperatuur;
    }

    public int getId(){ return this.Id; }
    public int getHoek(){ return this.Hoek; }
    public int getTemperatuur(){ return this.Temperatuur; }
}
