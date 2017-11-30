package com.ricardo.proyecto.conandard.tabs;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.DBManager;
import com.ricardo.proyecto.conandard.repositorio.HubSolarDBHelper;
import com.ricardo.proyecto.conandard.repositorio.Singleton;
import com.ricardo.proyecto.conandard.utils.HeatIndexCalculator;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.ArduinoListener;

public class LogFragment extends Fragment {

    private TextView logTextView;
    private EditText requestEditText;

    private ImageButton logImportButton;
    private ImageButton logLogButton;
    private ImageButton logQueryButton;

    private ArduinoManager arduinoManager;

    private View v;
    private FrameLayout frameLayout;
    private StringBuilder sb;
    private Singleton singleton;

    private DBManager dbManager;
    public LogFragment() {
        // Required empty public constructor
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

        dbManager = (new DBManager(getActivity().getApplicationContext())).open();

        logTextView.setMovementMethod(new ScrollingMovementMethod());

        arduinoManager = ArduinoManager.getInstance();

        singleton = Singleton.getInstance();

        logLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setLOG(true);
                        arduinoManager.enviar(ArduinoManager.LOG);
                        logLogButton.setPressed(true);
                    } else {
                        logLogButton.setPressed(false);
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
                        logQueryButton.setPressed(true);
                        arduinoManager.enviar(ArduinoManager.SELECTALL);
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
                        logImportButton.setPressed(true);
                        arduinoManager.enviar(ArduinoManager.INSERT);
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
                    if(singleton.getBluethootButton()!=null){
                        singleton.getBluethootButton().setPressed(true);
                    }
                } else {
                    singleton.setImportar(false);
                    singleton.setLOG(false);
                    singleton.setQuery(false);
                    snackbar("Dispositivo desconectado por Bluetooth.");
                    if(singleton.getBluethootButton()!=null){
                        singleton.getBluethootButton().setPressed(false);
                    }
                }
            }
        });

        arduinoManager.getUsbHelper().setArduinoListener(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
                snackbar("Dispositivo conectado por USB exitosamente.");
                if(singleton.getUsbButton()!=null){
                    singleton.getUsbButton().setPressed(true);
                }
                arduinoManager.getUsbHelper().open(device);
            }

            @Override
            public void onArduinoDetached() {
                singleton.setImportar(false);
                singleton.setLOG(false);
                singleton.setQuery(false);
                snackbar("Dispositivo desconectado por USB.");
                if(singleton.getUsbButton()!=null){
                    singleton.getUsbButton().setPressed(false);
                }
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
            arduinoManager.enviar(ArduinoManager.LOG);
        } else if(singleton.isQuery()){
            display(referencia + message);
            if (message.contains("FIN")){
                logQueryButton.setPressed(false);
                singleton.setQuery(false);
            }
        } else if(singleton.isImportar()){
            display(referencia + message);
            logImportButton.setPressed(false);
            singleton.notImportar();
        } else if(singleton.isBackup()){
            display(referencia + message);
            if (message.contains("BACKUP")){
                singleton.setBackup(false);
                if(singleton.getBackupButton()!=null){
                    singleton.getBackupButton().setPressed(false);
                }
            } else {
                String[] messageSplit = message.split(";");
                if(messageSplit.length>4){
                    String id = messageSplit[0];
                    String temperatura = messageSplit[1];
                    String humedad = messageSplit[2];
                    String indice_calor = String.valueOf(HeatIndexCalculator.calculateHeatIndex(Integer.valueOf(temperatura),Double.valueOf(humedad)));
                    String radiacion_solar = "40";
                    String intensidad_corriente = "40";
                    String voltaje = "6";
                    String potencia = "2";
                    Long latitud = 0l;
                    Long longitud = 0l;
                    String fecha_hora = messageSplit[3]+" "+messageSplit[4];

                    dbManager.insert(
                            humedad,
                            temperatura,
                            indice_calor,
                            radiacion_solar,
                            intensidad_corriente,
                            voltaje,
                            potencia,
                            latitud,
                            longitud,
                            fecha_hora
                    );
                }
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

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }

}
