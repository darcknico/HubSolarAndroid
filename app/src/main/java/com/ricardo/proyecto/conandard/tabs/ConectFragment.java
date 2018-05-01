package com.ricardo.proyecto.conandard.tabs;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ricardo.proyecto.conandard.ConsoleActivity;
import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.Singleton;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.ArduinoListener;


public class ConectFragment extends Fragment {


    private ImageButton conectBluetoohButton;
    private ImageButton conectUsbButton;
    private ImageButton conectCloudButton;
    private ImageButton conectImportButton;

    private Drawable initialUsbButton;
    private Drawable initialBluetoothButton;

    private ArduinoManager arduinoManager;
    private StringBuilder sb;
    private Singleton singleton;

    private View v;
    private FrameLayout frameLayout;

    public ConectFragment() {
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
        v = inflater.inflate(R.layout.fragment_conect, container, false);

        conectBluetoohButton = (ImageButton) v.findViewById(R.id.conectBluetoohButton);
        conectUsbButton = (ImageButton) v.findViewById(R.id.conectUsbButton);
        conectCloudButton = (ImageButton) v.findViewById(R.id.conectCloudButton);
        conectImportButton = (ImageButton) v.findViewById(R.id.conectImportButton);

        frameLayout = (FrameLayout) v.findViewById(R.id.fragmentConect);

        arduinoManager = ArduinoManager.getInstance();
        singleton = Singleton.getInstance();

        //guardando el background inicial
        initialUsbButton = conectUsbButton.getBackground();
        initialBluetoothButton = conectBluetoohButton.getBackground();

        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setBackgroundResource(R.color.imageButtonPress);
        }

        if(arduinoManager.getBluetoothHelper().isConnected()){
            conectBluetoohButton.setBackgroundColor(Color.BLUE);
        }

        conectBluetoohButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    snackbar("El dispositivo no dispone de bluetooth.");
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        snackbar("El bluetooth no esta prendido.");
                    } else {
                        if(!arduinoManager.getBluetoothHelper().isConnected() && !arduinoManager.getUsbHelper().isOpened()){
                            arduinoManager.getBluetoothHelper().Connect("HubSolar");
                        } else {
                            arduinoManager.getBluetoothHelper().Disconnect();
                            conectBluetoohButton.setBackground(initialBluetoothButton);
                            snackbar("Bluetooth desconectado.");
                        }

                    }
                }
            }
        });

        conectUsbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened()){
                    arduinoManager.getUsbHelper().close();
                    conectUsbButton.setBackground(initialUsbButton);
                }
            }
        });

        conectImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsoleActivity.class);
                startActivity(intent);
            }
        });

        conectarArduino();

        return v;
    }

    @Override
    public void onResume() {
        conectarArduino();

        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setBackgroundResource(R.color.imageButtonPress);
            if(arduinoManager.getBluetoothHelper().isConnected()) {
                arduinoManager.getBluetoothHelper().Disconnect();
            }
            conectBluetoohButton.setBackground(initialBluetoothButton);
            snackbar("Dispositivo conectado por USB exitosamente");
        } else {
            conectUsbButton.setBackground(initialUsbButton);
        }
        if(arduinoManager.getBluetoothHelper().isConnected()) {
            conectBluetoohButton.setBackgroundResource(R.color.imageButtonPress);
        }
        super.onResume();
    }

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }

    public void capturador(String referencia,String message){
        if(singleton.isLOG()) {
            enviar(ArduinoManager.LOG);
        }
        if(singleton.isQuery()){
            if (message.contains("FIN")){
                singleton.notQuery();
            }
        }
        if(singleton.isImportar()){
            singleton.notImportar();
        }
        if(singleton.isBackup()){
            if (message.contains("FIN")){
                singleton.notQuery();
            }
        }
    }

    public void enviar(String texto){
        if(arduinoManager.getUsbHelper().isOpened()) {
            arduinoManager.getUsbHelper().send(texto.getBytes());
        } else if(arduinoManager.getBluetoothHelper().isConnected()){
            arduinoManager.getBluetoothHelper().SendMessage(texto);
        }
    }

    public void conectarArduino () {
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
    }
}
