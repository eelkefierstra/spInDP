package com.nhl.spindp.vision;

import java.io.Serializable;
/**
 * Created by Aldert on 17-6-2016.
 */
public class Frame implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 5434537626681470138L;
    private byte[] frameBuff;

    public Frame(byte[] frameBuff)
    {
        this.frameBuff = frameBuff;
    }

    public byte[] getFrameBuff()
    {
        return frameBuff;
    }
}
