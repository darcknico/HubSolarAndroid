package com.ricardo.proyecto.conandard.tabs;

import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.Singleton;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.ArduinoListener;

public class LogFragment extends Fragment {

    private TextView logTextView;
    private EditText requestEditText;

    private ImageButton logImportButton;
    private ImageButton logLogButton;
    private ImageButton logQueryButton;

    private Drawable logImportDrawable;
    private Drawable logLogDrawable;
    private Drawable logQueryDrawable;

    private ArduinoManager arduinoManager;

    private View v;
    private FrameLayout frameLayout;
    private StringBuilder sb;
    private Singleton singleton;

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
        v = inflater.inflate(R.layout.fragment_log, container, false);

        frameLayout = (FrameLayout) v.findViewById(R.id.fragmentLog);

        logImportButton = (ImageButton) v.findViewById(R.id.logImportButton);
        logLogButton = (ImageButton) v.findViewById(R.id.logLogButton);
        logQueryButton = (ImageButton) v.findViewById(R.id.logQueryButton);
        logTextView = (TextView) v.findViewById(R.id.logTextView);
        requestEditText = (EditText) v.findViewById(R.id.requestEditText);

        logImportDrawable = logImportButton.getBackground();
        logLogDrawable = logLogButton.getBackground();
        logQueryDrawable = logQueryButton.getBackground();

        logTextView.setMovementMethod(new ScrollingMovementMethod());

        arduinoManager = ArduinoManager.getInstance();

        singleton = Singleton.getInstance();

        logLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setLOG(true);
                        if (singleton.isLOG()) {
                            singleton.setEnviado(true);
                            logLogButton.setBackgroundResource(R.color.imageButtonPress);
                        } else {
                            logLogButton.setBackground(logLogDrawable);
                        }
                    } else {
                        logLogButton.setBackground(logLogDrawable);
                        singleton.setLOG(false);
                    }
                }
            }
        });

        logQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setQuery(true);
                    }
                    if (singleton.isQuery()) {
                        singleton.setEnviado(true);
                        logQueryButton.setBackgroundResource(R.color.imageButtonPress);
                    }
                }
            }
        });

        logImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setImportar(true);
                    }
                    if (singleton.isImportar()) {
                        enviar(ArduinoManager.LOG);
                        logImportButton.setBackgroundResource(R.color.imageButtonPress);
                    }
                }
            }
        });

        arduinoManager.getBluetoothHelper().setBluetoothHelperListener(new BluetoothHelper.BluetoothHelperListener() {
            @Override
            public void onBluetoothHelperMessageReceived(BluetoothHelper bluetoothhelper, String message) {
                capturador("#: ",message);
            }

            @Override
            public void onBluetoothHelperConnectionStateChanged(BluetoothHelper bluetoothhelper, boolean isConnected) {
                if(isConnected){
                    snackbar("Dispositivo conectado por Bluetooth.");
                } else {
                    singleton.setImportar(false);
                    singleton.setLOG(false);
                    singleton.setQuery(false);
                    snackbar("Dispositivo desconectado por Bluetooth.");
                }
            }
        });

        arduinoManager.getUsbHelper().setArduinoListener(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
                snackbar("Dispositivo conectado por USB exitosamente.");
                arduinoManager.getUsbHelper().open(device);
            }

            @Override
            public void onArduinoDetached() {
                singleton.setImportar(false);
                singleton.setLOG(false);
                singleton.setQuery(false);
                snackbar("Dispositivo desconectado por USB.");
            }

            @Override
            public void onArduinoMessage(byte[] bytes) {
                sb.append( new String(bytes));
                if(sb.toString().contains("\n")){
                    String parte[]=sb.toString().split("\n");
                    capturador("$: ",parte[0]);
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
                snackbar("Dispositivo conectado por USB, abierto.");
            }
        });

        return v;
    }

    public void capturador(String referencia,String message){
        if(singleton.isLOG()) {
            display(referencia + message);
            enviar(ArduinoManager.LOG);
        }
        if(singleton.isQuery()){
            display(referencia + message);
            if (message.contains("FIN")){
                logQueryButton.setBackground(logQueryDrawable);
                singleton.notQuery();
            }
        }
        if(singleton.isImportar()){
            display(referencia + message);
            logImportButton.setBackground(logImportDrawable);
            singleton.notImportar();
        }
        if(singleton.isBackup()){
            display(referencia + message);
            if (message.contains("FIN")){
                logQueryButton.setBackground(logQueryDrawable);
                singleton.notQuery();
            }
        }
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

    public void enviar(String texto){
        if(arduinoManager.getUsbHelper().isOpened()) {
            arduinoManager.getUsbHelper().send(texto.getBytes());
        } else if(arduinoManager.getBluetoothHelper().isConnected()){
            arduinoManager.getBluetoothHelper().SendMessage(texto);
        }
    }

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }

}
