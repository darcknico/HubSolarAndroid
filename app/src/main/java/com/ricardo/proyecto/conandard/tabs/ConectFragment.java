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

    private Drawable initialUsbButton;
    private Drawable initialBluetoothButton;

    private ArduinoManager arduinoManager;

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

            }
        });



        return v;
    }

    @Override
    public void onResume() {
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

}
