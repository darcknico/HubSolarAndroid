package com.ricardo.proyecto.conandard;

import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.repositorio.DBManager;
import com.ricardo.proyecto.conandard.repositorio.Singleton;
import com.ricardo.proyecto.conandard.utils.HeatIndexCalculator;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;
import me.aflak.arduino.ArduinoListener;

public class ConsoleActivity extends AppCompatActivity {

    private TextView logTextView;
    private EditText requestEditText;

    private ImageButton logImportButton;
    private ImageButton logLogButton;
    private ImageButton logQueryButton;
    private ImageButton logClearImport;

    private ArduinoManager arduinoManager;

    private View v;
    private ConstraintLayout frameLayout;
    private StringBuilder sb;
    private Singleton singleton;

    private long mLastClickTime = 0;

    private DBManager dbManager;
    private ImageButton requestHelpButton;

    private String scopeTerminal = "&: ";
    private String scopeUSB = "$: ";
    private String scopeBluetooth = "#: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        frameLayout = (ConstraintLayout) findViewById(R.id.fragmentLog);

        logImportButton = (ImageButton) findViewById(R.id.logImportButton);
        logLogButton = (ImageButton) findViewById(R.id.logLogButton);
        logQueryButton = (ImageButton) findViewById(R.id.logQueryButton);
        logClearImport = (ImageButton) findViewById(R.id.logClearImport);
        logTextView = (TextView) findViewById(R.id.logTextView);
        requestEditText = (EditText) findViewById(R.id.requestEditText);
        requestHelpButton = (ImageButton) findViewById(R.id.requestHelpButton);


        dbManager = (new DBManager(getApplicationContext())).open();

        logTextView.setMovementMethod(new ScrollingMovementMethod());

        arduinoManager = ArduinoManager.getInstance();

        singleton = Singleton.getInstance();
        /*
        SimpleDateFormat formatoGlobal = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        dbManager.insert(
                "53",
                "27",
                "9",
                "9",
                "0",
                "0",
                "0",
                0l,
                0l,
                formatoGlobal.format(new Date())
        );
        */

        logLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(arduinoManager.getUsbHelper().isOpened() || arduinoManager.getBluetoothHelper().isConnected()) {
                    if (!singleton.isQuery() && !singleton.isImportar() && !singleton.isLOG() && !singleton.isBackup()) {
                        singleton.setImportar(true);
                        logImportButton.setPressed(true);
                        arduinoManager.enviar(ArduinoManager.INSERT);
                    }
                }
            }
        });
        logClearImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logTextView.setText("");
            }
        });

        arduinoManager.getBluetoothHelper().setBluetoothHelperListener(new BluetoothHelper.BluetoothHelperListener() {
            @Override
            public void onBluetoothHelperMessageReceived(BluetoothHelper bluetoothhelper, String message) {
                capturador(scopeBluetooth,message);
            }

            @Override
            public void onBluetoothHelperConnectionStateChanged(BluetoothHelper bluetoothhelper, boolean isConnected) {
                if(isConnected){
                    snackbar("Dispositivo conectado por Bluetooth.");
                    if(singleton.getBluethootButton()!=null){
                        singleton.getBluethootButton().setPressed(true);
                    }

                    singleton.setImportar(true);
                    logImportButton.setPressed(true);
                    arduinoManager.enviar(ArduinoManager.INSERT);

                } else {
                    singleton.setImportar(false);
                    singleton.setLOG(false);
                    singleton.setQuery(false);
                    snackbar("Dispositivo desconectado por Bluetooth.");
                    if(singleton.getBluethootButton()!=null){
                        singleton.getBluethootButton().setPressed(false);
                    }
                    finish();
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

                singleton.setImportar(true);
                logImportButton.setPressed(true);
                arduinoManager.enviar(ArduinoManager.INSERT);
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
                finish();
            }

            @Override
            public void onArduinoMessage(byte[] bytes) {
                sb.append( new String(bytes));
                if(sb.toString().contains("\n")){
                    String parte[]=sb.toString().split("\n");
                    capturador(scopeUSB,parte[0]);
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

        requestEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return true;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(i == KeyEvent.KEYCODE_ENDCALL || i == KeyEvent.KEYCODE_ENTER){
                    String mensaje = textView.getText().toString();
                    String comando = "";
                    switch (mensaje){
                        case ArduinoManager.LOG:
                            comando = " - LOG";
                            break;
                        case ArduinoManager.SELECTALL:
                            comando = " - SELECTALL";
                            break;
                        case ArduinoManager.DELETE:
                            comando = " - DELETE";
                            break;
                        case ArduinoManager.INSERT:
                            comando = " - INSERT";
                            break;
                        case ArduinoManager.COUNT:
                            comando = " - COUNT";
                            break;
                        case ArduinoManager.BACKUP:
                            comando = " - BACKUP";
                            break;
                        default:
                            comando = " - desconocido";
                    }
                    display(scopeTerminal+mensaje+comando);
                    arduinoManager.enviar(mensaje);
                    requestEditText.setText("");
                }
                return true;
            }
        });

        requestHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                display(scopeTerminal+ArduinoManager.LOG+" - Realiza una lectura");
                display(scopeTerminal+ArduinoManager.SELECTALL+" - Recupera todos los registros del archivo activo");
                display(scopeTerminal+ArduinoManager.DELETE+" - Elimina los registros del archivo activo");
                display(scopeTerminal+ArduinoManager.INSERT+" - Recupera e Inserta una lectura en el archivo activo");
                display(scopeTerminal+ArduinoManager.COUNT+" - Cantidad de registros en el archivo activo");
                display(scopeTerminal+ArduinoManager.BACKUP+" - Copia los registros en un nuevo archivo y elimina elimina los registros en el archivo activo");
            }
        });

    }
    public void capturador(String referencia, String message){
        Log.i("ARDUINO",message);
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
            String[] messageSplit = message.split(";");
            if(messageSplit.length>3){
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
            display(referencia + message);
            if(!message.contains("ERROR") || message.contains("FIN")){
                singleton.notImportar();
                logImportButton.setPressed(false);
            }
        } else if(singleton.isBackup()){
            display(referencia + message);
            if (message.contains("BACKUP")){
                singleton.setBackup(false);
                if(singleton.getBackupButton()!=null){
                    singleton.getBackupButton().setPressed(false);
                }
            } else {
                String[] messageSplit = message.split(";");
                if(messageSplit.length>3){
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

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }
}
