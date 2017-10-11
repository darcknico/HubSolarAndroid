package com.ricardo.proyecto.conandard.manager;

import android.content.Context;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.Arduino;

/**
 * Created by karen on 1/10/2017.
 */

public final class ArduinoManager {

    public static final String SYNC = "1";
    public static final String LOG = "2";
    public static final String QUERY = "3";
    public static final String IMPORT = "4";
    public static final String EXPORT = "5";

    private static ArduinoManager arduinoManager = null;
    private static Arduino usbHelper = null;
    private static BluetoothHelper bluetoothHelper = new BluetoothHelper();;

    public ArduinoManager() {
    }

    public void setContext(Context context){
        usbHelper = new Arduino(context);
    }
    public Arduino getUsbHelper(){
        return usbHelper;
    }

    public BluetoothHelper getBluetoothHelper() {
        return bluetoothHelper;
    }

    public static ArduinoManager getInstance(){
        if( arduinoManager==null){
            arduinoManager = new ArduinoManager();
        }
        return arduinoManager;
    }
}
