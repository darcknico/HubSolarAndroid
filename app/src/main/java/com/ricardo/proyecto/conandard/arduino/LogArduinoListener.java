package com.ricardo.proyecto.conandard.arduino;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

/**
 * Created by karen on 1/10/2017.
 */

public class LogArduinoListener implements ArduinoListener {

    private TextView textView;
    private Activity activity;

    public LogArduinoListener(TextView textView, Activity activity){
        this.textView = textView;
        this.activity = activity;
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino unido!");
        ArduinoManager.getInstance().getArduino().open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino separado");
    }

    StringBuilder sb = new StringBuilder();

    @Override
    public void onArduinoMessage(byte[] bytes) {
        sb.append( new String(bytes));
        if(sb.toString().contains("\n")){
            String parte[]=sb.toString().split("\n");
            display("$: "+parte[0]);
            sb = new StringBuilder();
            boolean primero = true;
            for(String str :parte){
                if(primero){
                    primero = false;
                } else {
                    sb.append(str);
                }
            }

        }

    }

    @Override
    public void onArduinoOpened() {

    }

    public void display(final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(message+"\n");
                final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
                if (scrollAmount > 0)
                    textView.scrollTo(0, scrollAmount);
                else
                    textView.scrollTo(0, 0);
            }
        });
    }
}
