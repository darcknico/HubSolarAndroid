package com.ricardo.proyecto.conandard.tabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.arduino.ArduinoManager;
import com.ricardo.proyecto.conandard.arduino.LogArduinoListener;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.Arduino;

public class LogFragment extends Fragment {


    private Arduino arduino;
    private TextView logTextView;
    private EditText requestEditText;
    private Button enviarButton;
    private Button obtenerButton;
    private BluetoothHelper mBluetoothHelper;
    private Button conBluetoothButton;

    private Boolean guardarDatos = false;
    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log, container, false);
        logTextView = (TextView) v.findViewById(R.id.logTextView);
        requestEditText = (EditText) v.findViewById(R.id.requestEditText);
        enviarButton = (Button) v.findViewById(R.id.enviarButton);
        obtenerButton = (Button) v.findViewById(R.id.obtenerButton);
        conBluetoothButton = (Button) v.findViewById(R.id.conBluetoothButton);

        ArduinoManager.getInstance().getArduino().setArduinoListener(new LogArduinoListener(logTextView,getActivity()));
        mBluetoothHelper = new BluetoothHelper();

        logTextView.setMovementMethod(new ScrollingMovementMethod());

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arduino.isOpened()) {
                    arduino.send(requestEditText.getText().toString().getBytes());
                }
                if(mBluetoothHelper.isConnected()){
                    mBluetoothHelper.SendMessage(requestEditText.getText().toString());
                }
            }
        });

        mBluetoothHelper.setBluetoothHelperListener(new BluetoothHelper.BluetoothHelperListener() {
            @Override
            public void onBluetoothHelperMessageReceived(BluetoothHelper bluetoothhelper, String message) {
                display("#"+message);
                if(guardarDatos){
                    String[] dato =message.split(";");
                }
            }

            @Override
            public void onBluetoothHelperConnectionStateChanged(BluetoothHelper bluetoothhelper, boolean isConnected) {

            }
        });

        conBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothHelper.Connect("HubSolar");
            }
        });

        obtenerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos = true;
                if(arduino.isOpened()) {
                    arduino.send("1".getBytes());
                }
                if(mBluetoothHelper.isConnected()){
                    mBluetoothHelper.SendMessage("1");
                }
            }
        });
        return v;
    }

    public void display(final String message){
        getActivity().runOnUiThread(new Runnable() {
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
