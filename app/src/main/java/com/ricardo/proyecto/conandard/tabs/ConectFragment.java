package com.ricardo.proyecto.conandard.tabs;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;


public class ConectFragment extends Fragment {


    private ImageButton conectBluetoohButton;
    private ImageButton conectUsbButton;
    private ImageButton conectCloudButton;
    private ImageButton conectImportButton;

    private Drawable initialUsbButton;
    private Drawable initialBluetoothButton;

    private ArduinoManager arduinoManager;

    private View v;

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

        arduinoManager = ArduinoManager.getInstance();

        //guardando el background inicial
        initialUsbButton = conectUsbButton.getBackground();
        initialBluetoothButton = conectBluetoohButton.getBackground();

        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setBackgroundResource(R.color.md_blue_50);
        }

        if(arduinoManager.getBluetoothHelper().isConnected()){
            conectBluetoohButton.setBackgroundColor(Color.BLUE);
        }

        conectBluetoohButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!arduinoManager.getBluetoothHelper().isConnected() && !arduinoManager.getUsbHelper().isOpened() ){
                    arduinoManager.getBluetoothHelper().Connect("HubSolar");
                    conectBluetoohButton.setBackgroundResource(R.color.md_blue_50);
                    Snackbar.make(v,"Dispositivo conectado por Bluetooth exitosamente",Snackbar.LENGTH_SHORT);
                } else {
                    Snackbar.make(v,"No pudo realizar la conexcion por bluetooth",Snackbar.LENGTH_SHORT);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        if(arduinoManager.getUsbHelper().isOpened()){
            conectUsbButton.setBackgroundResource(R.color.md_blue_50);
            if(arduinoManager.getBluetoothHelper().isConnected()) {
                arduinoManager.getBluetoothHelper().Disconnect();
            }
            conectBluetoohButton.setBackground(initialBluetoothButton);
            Snackbar.make(v,"Dispositivo conectado por USB exitosamente",Snackbar.LENGTH_SHORT);
        } else {
            conectUsbButton.setBackground(initialUsbButton);
        }
        super.onResume();
    }
}
