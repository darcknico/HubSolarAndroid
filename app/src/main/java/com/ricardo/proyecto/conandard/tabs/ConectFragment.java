package com.ricardo.proyecto.conandard.tabs;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.Singleton;


public class ConectFragment extends Fragment {


    private ImageButton conectBluetoohButton;
    private ImageButton conectUsbButton;
    private ImageButton conectCloudButton;
    private ImageButton conectImportButton;

    private ArduinoManager arduinoManager;

    private View v;
    private FrameLayout frameLayout;
    private Singleton singleton;

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

        frameLayout = (FrameLayout) v.findViewById(R.id.fragmentConect);

        arduinoManager = ArduinoManager.getInstance();

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
            }
        });



        return v;
    }

    @Override
    public void onResume() {
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

}
