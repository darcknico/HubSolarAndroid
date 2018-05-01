package com.ricardo.proyecto.conandard.tabs;

import android.bluetooth.BluetoothAdapter;
import android.database.Cursor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.ConsoleActivity;
import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.DBManager;
import com.ricardo.proyecto.conandard.repositorio.HubSolarDBHelper;
import com.ricardo.proyecto.conandard.repositorio.Singleton;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.ArduinoListener;


public class ConectFragment extends Fragment {


    private ImageButton conectBluetoohButton;
    private ImageButton conectUsbButton;
    private ImageButton conectCloudButton;
    private ImageButton conectImportButton;

    private ArduinoManager arduinoManager;
    private StringBuilder sb;
    private Singleton singleton;

    private View v;
    private FrameLayout frameLayout;
    private Singleton singleton;

    private long mLastClickTime = 0;
    private TextView conectFechaText;
    private TextView conectHumedadText;
    private TextView conectPotenciaText;
    private TextView conectRadiacionText;
    private TextView conectTemperaturaText;
    private DBManager dbManager;

    public ConectFragment() {
        // Required empty public constructor
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
        conectFechaText =(TextView) v.findViewById(R.id.conectFechaText);
        conectHumedadText =(TextView) v.findViewById(R.id.conectHumedadText);
        conectPotenciaText =(TextView) v.findViewById(R.id.conectPotenciaText);
        conectRadiacionText =(TextView) v.findViewById(R.id.conectRadiacionText);
        conectTemperaturaText =(TextView) v.findViewById(R.id.conectTemperaturaText);


        frameLayout = (FrameLayout) v.findViewById(R.id.fragmentConect);

        arduinoManager = ArduinoManager.getInstance();
        singleton = Singleton.getInstance();

        singleton = Singleton.getInstance();

        singleton.setUsbButton(conectUsbButton);
        singleton.setBluethootButton(conectBluetoohButton);
        singleton.setBackupButton(conectImportButton);

        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setPressed(true);
        }

        if(arduinoManager.getBluetoothHelper().isConnected()){
            conectBluetoohButton.setPressed(true);
        }

        conectBluetoohButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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
                            snackbar("Bluetooth desconectado.");
                        }

                    }
                }
            }
        });

        conectUsbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(arduinoManager.getUsbHelper().isOpened()){
                    arduinoManager.getUsbHelper().close();
                    conectUsbButton.setPressed(true);
                }
            }
        });

        conectImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setBackup(true);
                        arduinoManager.enviar(ArduinoManager.BACKUP);
                        conectImportButton.setPressed(true);
                    }
                }
                Intent intent = new Intent(getActivity(), ConsoleActivity.class);
                startActivity(intent);
            }
        });

        conectarArduino();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dbManager = (new DBManager(getActivity().getApplicationContext())).open();
        actualizarUltimoInsert();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        actualizarUltimoInsert();

        conectarArduino();

        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setPressed(true);
            if(arduinoManager.getBluetoothHelper().isConnected()) {
                arduinoManager.getBluetoothHelper().Disconnect();
            }
            conectBluetoohButton.setPressed(false);
        } else {
            conectUsbButton.setPressed(false);
        }
        if(arduinoManager.getBluetoothHelper().isConnected()) {
            conectBluetoohButton.setPressed(true);
        }
        super.onResume();
    }

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }

    public void actualizarUltimoInsert(){
        Cursor cursor = dbManager.lastInsert();
        try {
            while (cursor.moveToNext()) {
                String fecha_hora = cursor.getString(cursor.getColumnIndex(HubSolarDBHelper.FECHA_HORA));
                Double radiacion_solar = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.RADIACION_SOLAR));
                Double temperatura = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.TEMPERATURA));
                Double humedad = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.HUMEDAD));
                Double potencia = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.POTENCIA));

                conectFechaText.setText(fecha_hora);
                conectRadiacionText.setText(radiacion_solar.toString());
                conectPotenciaText.setText(potencia.toString());
                conectTemperaturaText.setText(temperatura.toString());
                conectHumedadText.setText(humedad.toString());
            }
        } finally {
            cursor.close();
        }
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
