package com.ricardo.proyecto.conandard;

import android.hardware.usb.UsbDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class HomeActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private TextView logTextView;
    private EditText requestEditText;
    private Button enviarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        logTextView = (TextView) findViewById(R.id.logTextView);
        requestEditText = (EditText) findViewById(R.id.requestEditText);
        enviarButton = (Button) findViewById(R.id.enviarButton);

        arduino = new Arduino(this);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arduino.send(requestEditText.getText().toString().getBytes());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }


    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino unido!");
        arduino.open(device);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextView.append(message+"\n");
                final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
                if (scrollAmount > 0)
                    logTextView.scrollTo(0, scrollAmount);
                else
                    logTextView.scrollTo(0, 0);
            }
        });
    }

}
