package com.ricardo.proyecto.conandard.arduino;

import android.content.Context;

import me.aflak.arduino.Arduino;

/**
 * Created by karen on 1/10/2017.
 */

public final class ArduinoManager {

    private static ArduinoManager arduinoManager = null;
    private static Arduino arduino = null;

    public ArduinoManager() {
    }

    public static void setContext(Context context){
        arduino = new Arduino(context);
    }
    public static Arduino getArduino(){
        return arduino;
    }

    public static ArduinoManager getInstance(){
        if( arduinoManager==null){
            arduinoManager = new ArduinoManager();
        }
        return arduinoManager;
    }
}
