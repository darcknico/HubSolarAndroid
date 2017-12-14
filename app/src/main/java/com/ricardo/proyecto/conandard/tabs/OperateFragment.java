package com.ricardo.proyecto.conandard.tabs;


import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ricardo.proyecto.conandard.R;
import com.ricardo.proyecto.conandard.repositorio.DBManager;
import com.ricardo.proyecto.conandard.repositorio.HubSolarDBHelper;
import com.ricardo.proyecto.conandard.tabla.FranjaHoraria;
import com.ricardo.proyecto.conandard.tabla.MedicionTableDataAdapter;
import com.ricardo.proyecto.conandard.tabla.MedicionTableHeaderAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.providers.TableDataRowBackgroundProvider;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class OperateFragment extends Fragment {

    private RadioButton radioPerfil1Button;
    private RadioButton radioPerfil2Button;
    private TableView tableView;
    private NestedScrollView operateScrollView;

    private MedicionTableDataAdapter medicionTableDataAdapter;
    private ArrayList<FranjaHoraria> list;
    private DBManager dbManager;
    private TextView operateFechaTextVew;
    private ImageView operatePreviousButton;
    private ImageView operateNextButton;
    private Date fechaModificable,fechaActual;
    private SimpleDateFormat formatoLocal;
    private SimpleDateFormat formatoGlobal;
    private SimpleDateFormat formatoDB;

    public OperateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_operate, container, false);
        operateScrollView = (NestedScrollView) view.findViewById(R.id.operateScrollView);
        operateFechaTextVew = (TextView) view.findViewById(R.id.operateFechaTextView);
        operatePreviousButton = (ImageView) view.findViewById(R.id.operatePreviousButton);
        operateNextButton = (ImageView) view.findViewById(R.id.operateNextButton);
        tableView = (TableView) view.findViewById(R.id.tableView);
        tableView.setColumnCount(5);

        list = new ArrayList<>();
        list.add(new FranjaHoraria("7-8","12.42","0.02","15.68","0.26"));
        list.add(new FranjaHoraria("8-9","88.25","0.06","16.26","0.91"));
        list.add(new FranjaHoraria("9-10","285.72","0.15","16.59","2.46"));
        list.add(new FranjaHoraria("10-11","454.33","0.24","16.76","4.04"));
        list.add(new FranjaHoraria("11-12","595.83","0.34","16.87","5,82"));
        list.add(new FranjaHoraria("12-13","676.00","0.39","16.99","6.56"));
        list.add(new FranjaHoraria("13-14","706.00","0.40","17.04","6.84"));
        list.add(new FranjaHoraria("14-15","674.17","0.39","16.99","6.55"));
        list.add(new FranjaHoraria("15-16","573.92","0.33","16.85","5.54"));
        list.add(new FranjaHoraria("16-17","369.08","0.19","16.68","3.11"));
        list.add(new FranjaHoraria("17-18","191.42","0.10","16.49","1.72"));
        list.add(new FranjaHoraria("18-19","24.33","0.02","15.77","0.36"));
        medicionTableDataAdapter = new MedicionTableDataAdapter(getActivity().getApplicationContext(),list);
        tableView.setColumnCount(5);
        tableView.setDataAdapter(medicionTableDataAdapter);
        tableView.setHeaderAdapter(new MedicionTableHeaderAdapter(getActivity().getApplicationContext()));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        radioPerfil1Button = (RadioButton)view.findViewById(R.id.radioPerfil1Button);
        radioPerfil2Button = (RadioButton)view.findViewById(R.id.radioPerfil2Button);

        fechaModificable = new Date();
        fechaActual = new Date();
        formatoLocal = new SimpleDateFormat("dd/MM/yyyy");
        formatoDB = new SimpleDateFormat("dd-MM-yyyy");
        formatoGlobal = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        operateFechaTextVew.setText(formatoLocal.format(fechaModificable));
        operateNextButton.setVisibility(View.INVISIBLE);

        dbManager = (new DBManager(getActivity().getApplicationContext())).open();
        datosTabla();

        radioPerfil1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioPerfil2Button.setChecked(false);
                tableView.setDataRowBackgroundProvider(new FranjaHorariaRowColorProvider(0.15));
                medicionTableDataAdapter.notifyDataSetChanged();
                operateScrollView.post(new Runnable() {
                    public void run() {
                        operateScrollView.fullScroll(operateScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        radioPerfil2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioPerfil1Button.setChecked(false);
                tableView.setDataRowBackgroundProvider(new FranjaHorariaRowColorProvider(0.35));
                medicionTableDataAdapter.notifyDataSetChanged();
                operateScrollView.post(new Runnable() {
                    public void run() {
                        operateScrollView.fullScroll(operateScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        operatePreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operateNextButton.setVisibility(View.VISIBLE);
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaModificable);
                cal.add(Calendar.DATE, -1);
                fechaModificable = cal.getTime();
                operateFechaTextVew.setText(formatoLocal.format(fechaModificable));
                datosTabla(operateFechaTextVew.getText().toString());
                medicionTableDataAdapter.notifyDataSetChanged();
            }
        });
        operateNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fechaModificable.before(fechaActual)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fechaModificable);
                    cal.add(Calendar.DATE, 1);
                    fechaModificable = cal.getTime();
                    operateFechaTextVew.setText(formatoLocal.format(fechaModificable));
                    if(fechaActual.equals(fechaModificable)) {
                        operateNextButton.setVisibility(View.INVISIBLE);
                        datosTabla();
                    } else {
                        datosTabla(operateFechaTextVew.getText().toString());
                    }
                    medicionTableDataAdapter.notifyDataSetChanged();
                }
            }
        });

        datosTabla();
        medicionTableDataAdapter.notifyDataSetChanged();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        radioPerfil1Button.setChecked(false);
        radioPerfil2Button.setChecked(false);
        operateScrollView.post(new Runnable() {
            public void run() {
                operateScrollView.fullScroll(operateScrollView.FOCUS_UP);
            }
        });
        medicionTableDataAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void datosTabla(){
        Cursor cursor = dbManager.fetch();
        try {
            FranjaHoraria franjaHoraria78 = list.get(0);
            franjaHoraria78.setCount(0);
            franjaHoraria78.setIntensidadCorrienteDouble(0);
            franjaHoraria78.setPotenciaDouble(0);
            franjaHoraria78.setRadiacionSolarDouble(0);
            franjaHoraria78.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria89 = list.get(1);
            franjaHoraria89.setCount(0);
            franjaHoraria89.setIntensidadCorrienteDouble(0);
            franjaHoraria89.setPotenciaDouble(0);
            franjaHoraria89.setRadiacionSolarDouble(0);
            franjaHoraria89.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria910 = list.get(2);
            franjaHoraria910.setCount(0);
            franjaHoraria910.setIntensidadCorrienteDouble(0);
            franjaHoraria910.setPotenciaDouble(0);
            franjaHoraria910.setRadiacionSolarDouble(0);
            franjaHoraria910.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1011 = list.get(3);
            franjaHoraria1011.setCount(0);
            franjaHoraria1011.setIntensidadCorrienteDouble(0);
            franjaHoraria1011.setPotenciaDouble(0);
            franjaHoraria1011.setRadiacionSolarDouble(0);
            franjaHoraria1011.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1112 = list.get(4);
            franjaHoraria1112.setCount(0);
            franjaHoraria1112.setIntensidadCorrienteDouble(0);
            franjaHoraria1112.setPotenciaDouble(0);
            franjaHoraria1112.setRadiacionSolarDouble(0);
            franjaHoraria1112.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1213 = list.get(5);
            franjaHoraria1213.setCount(0);
            franjaHoraria1213.setIntensidadCorrienteDouble(0);
            franjaHoraria1213.setPotenciaDouble(0);
            franjaHoraria1213.setRadiacionSolarDouble(0);
            franjaHoraria1213.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1314 = list.get(6);
            franjaHoraria1314.setCount(0);
            franjaHoraria1314.setIntensidadCorrienteDouble(0);
            franjaHoraria1314.setPotenciaDouble(0);
            franjaHoraria1314.setRadiacionSolarDouble(0);
            franjaHoraria1314.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1415 = list.get(7);
            franjaHoraria1415.setCount(0);
            franjaHoraria1415.setIntensidadCorrienteDouble(0);
            franjaHoraria1415.setPotenciaDouble(0);
            franjaHoraria1415.setRadiacionSolarDouble(0);
            franjaHoraria1415.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1516 = list.get(8);
            franjaHoraria1516.setCount(0);
            franjaHoraria1516.setIntensidadCorrienteDouble(0);
            franjaHoraria1516.setPotenciaDouble(0);
            franjaHoraria1516.setRadiacionSolarDouble(0);
            franjaHoraria1516.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1617 = list.get(9);
            franjaHoraria1617.setCount(0);
            franjaHoraria1617.setIntensidadCorrienteDouble(0);
            franjaHoraria1617.setPotenciaDouble(0);
            franjaHoraria1617.setRadiacionSolarDouble(0);
            franjaHoraria1617.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1718 = list.get(10);
            franjaHoraria1718.setCount(0);
            franjaHoraria1718.setIntensidadCorrienteDouble(0);
            franjaHoraria1718.setPotenciaDouble(0);
            franjaHoraria1718.setRadiacionSolarDouble(0);
            franjaHoraria1718.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1819 = list.get(11);
            franjaHoraria1819.setCount(0);
            franjaHoraria1819.setIntensidadCorrienteDouble(0);
            franjaHoraria1819.setPotenciaDouble(0);
            franjaHoraria1819.setRadiacionSolarDouble(0);
            franjaHoraria1819.setVoltajeDouble(0);
            while (cursor.moveToNext()) {
                String fecha_hora = cursor.getString(cursor.getColumnIndex(HubSolarDBHelper.FECHA_HORA));
                Double radiacion_solar = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.RADIACION_SOLAR));
                Double intesidad_corriente = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.INTESIDAD_CORRIENTE));
                Double voltaje = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.VOLTAJE));
                Double potencia = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.POTENCIA));
                Date date = formatoGlobal.parse(fecha_hora);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                switch (hora){
                    case 7:
                        franjaHoraria78.plusCount();
                        franjaHoraria78.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria78.plusPotencia(potencia);
                        franjaHoraria78.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria78.plusVoltaje(voltaje);
                        break;
                    case 8:
                        franjaHoraria89.plusCount();
                        franjaHoraria89.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria89.plusPotencia(potencia);
                        franjaHoraria89.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria89.plusVoltaje(voltaje);
                        break;
                    case 9:
                        franjaHoraria910.plusCount();
                        franjaHoraria910.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria910.plusPotencia(potencia);
                        franjaHoraria910.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria910.plusVoltaje(voltaje);
                        break;
                    case 10:
                        franjaHoraria1011.plusCount();
                        franjaHoraria1011.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1011.plusPotencia(potencia);
                        franjaHoraria1011.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1011.plusVoltaje(voltaje);
                        break;
                    case 11:
                        franjaHoraria1112.plusCount();
                        franjaHoraria1112.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1112.plusPotencia(potencia);
                        franjaHoraria1112.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1112.plusVoltaje(voltaje);
                        break;
                    case 12:
                        franjaHoraria1213.plusCount();
                        franjaHoraria1213.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1213.plusPotencia(potencia);
                        franjaHoraria1213.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1213.plusVoltaje(voltaje);
                        break;
                    case 13:
                        franjaHoraria1314.plusCount();
                        franjaHoraria1314.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1314.plusPotencia(potencia);
                        franjaHoraria1314.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1314.plusVoltaje(voltaje);
                        break;
                    case 14:
                        franjaHoraria1415.plusCount();
                        franjaHoraria1415.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1415.plusPotencia(potencia);
                        franjaHoraria1415.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1415.plusVoltaje(voltaje);
                        break;
                    case 15:
                        franjaHoraria1516.plusCount();
                        franjaHoraria1516.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1516.plusPotencia(potencia);
                        franjaHoraria1516.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1516.plusVoltaje(voltaje);
                        break;
                    case 16:
                        franjaHoraria1617.plusCount();
                        franjaHoraria1617.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1617.plusPotencia(potencia);
                        franjaHoraria1617.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1617.plusVoltaje(voltaje);
                        break;
                    case 17:
                        franjaHoraria1718.plusCount();
                        franjaHoraria1718.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1718.plusPotencia(potencia);
                        franjaHoraria1718.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1718.plusVoltaje(voltaje);
                        break;
                    case 18:
                        franjaHoraria1819.plusCount();
                        franjaHoraria1819.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1819.plusPotencia(potencia);
                        franjaHoraria1819.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1819.plusVoltaje(voltaje);
                        break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void datosTabla(String fecha){
        Cursor cursor = null;
        try {
            Date fechaBuscar = formatoLocal.parse(fecha);
            cursor = dbManager.fetch(formatoDB.format(fechaBuscar));
            FranjaHoraria franjaHoraria78 = list.get(0);
            franjaHoraria78.setCount(0);
            franjaHoraria78.setIntensidadCorrienteDouble(0);
            franjaHoraria78.setPotenciaDouble(0);
            franjaHoraria78.setRadiacionSolarDouble(0);
            franjaHoraria78.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria89 = list.get(1);
            franjaHoraria89.setCount(0);
            franjaHoraria89.setIntensidadCorrienteDouble(0);
            franjaHoraria89.setPotenciaDouble(0);
            franjaHoraria89.setRadiacionSolarDouble(0);
            franjaHoraria89.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria910 = list.get(2);
            franjaHoraria910.setCount(0);
            franjaHoraria910.setIntensidadCorrienteDouble(0);
            franjaHoraria910.setPotenciaDouble(0);
            franjaHoraria910.setRadiacionSolarDouble(0);
            franjaHoraria910.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1011 = list.get(3);
            franjaHoraria1011.setCount(0);
            franjaHoraria1011.setIntensidadCorrienteDouble(0);
            franjaHoraria1011.setPotenciaDouble(0);
            franjaHoraria1011.setRadiacionSolarDouble(0);
            franjaHoraria1011.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1112 = list.get(4);
            franjaHoraria1112.setCount(0);
            franjaHoraria1112.setIntensidadCorrienteDouble(0);
            franjaHoraria1112.setPotenciaDouble(0);
            franjaHoraria1112.setRadiacionSolarDouble(0);
            franjaHoraria1112.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1213 = list.get(5);
            franjaHoraria1213.setCount(0);
            franjaHoraria1213.setIntensidadCorrienteDouble(0);
            franjaHoraria1213.setPotenciaDouble(0);
            franjaHoraria1213.setRadiacionSolarDouble(0);
            franjaHoraria1213.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1314 = list.get(6);
            franjaHoraria1314.setCount(0);
            franjaHoraria1314.setIntensidadCorrienteDouble(0);
            franjaHoraria1314.setPotenciaDouble(0);
            franjaHoraria1314.setRadiacionSolarDouble(0);
            franjaHoraria1314.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1415 = list.get(7);
            franjaHoraria1415.setCount(0);
            franjaHoraria1415.setIntensidadCorrienteDouble(0);
            franjaHoraria1415.setPotenciaDouble(0);
            franjaHoraria1415.setRadiacionSolarDouble(0);
            franjaHoraria1415.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1516 = list.get(8);
            franjaHoraria1516.setCount(0);
            franjaHoraria1516.setIntensidadCorrienteDouble(0);
            franjaHoraria1516.setPotenciaDouble(0);
            franjaHoraria1516.setRadiacionSolarDouble(0);
            franjaHoraria1516.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1617 = list.get(9);
            franjaHoraria1617.setCount(0);
            franjaHoraria1617.setIntensidadCorrienteDouble(0);
            franjaHoraria1617.setPotenciaDouble(0);
            franjaHoraria1617.setRadiacionSolarDouble(0);
            franjaHoraria1617.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1718 = list.get(10);
            franjaHoraria1718.setCount(0);
            franjaHoraria1718.setIntensidadCorrienteDouble(0);
            franjaHoraria1718.setPotenciaDouble(0);
            franjaHoraria1718.setRadiacionSolarDouble(0);
            franjaHoraria1718.setVoltajeDouble(0);
            FranjaHoraria franjaHoraria1819 = list.get(11);
            franjaHoraria1819.setCount(0);
            franjaHoraria1819.setIntensidadCorrienteDouble(0);
            franjaHoraria1819.setPotenciaDouble(0);
            franjaHoraria1819.setRadiacionSolarDouble(0);
            franjaHoraria1819.setVoltajeDouble(0);
            while (cursor.moveToNext()) {
                String fecha_hora = cursor.getString(cursor.getColumnIndex(HubSolarDBHelper.FECHA_HORA));
                Double radiacion_solar = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.RADIACION_SOLAR));
                Double intesidad_corriente = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.INTESIDAD_CORRIENTE));
                Double voltaje = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.VOLTAJE));
                Double potencia = cursor.getDouble(cursor.getColumnIndex(HubSolarDBHelper.POTENCIA));
                Date date = formatoGlobal.parse(fecha_hora);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                switch (hora){
                    case 7:
                        franjaHoraria78.plusCount();
                        franjaHoraria78.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria78.plusPotencia(potencia);
                        franjaHoraria78.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria78.plusVoltaje(voltaje);
                        break;
                    case 8:
                        franjaHoraria89.plusCount();
                        franjaHoraria89.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria89.plusPotencia(potencia);
                        franjaHoraria89.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria89.plusVoltaje(voltaje);
                        break;
                    case 9:
                        franjaHoraria910.plusCount();
                        franjaHoraria910.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria910.plusPotencia(potencia);
                        franjaHoraria910.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria910.plusVoltaje(voltaje);
                        break;
                    case 10:
                        franjaHoraria1011.plusCount();
                        franjaHoraria1011.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1011.plusPotencia(potencia);
                        franjaHoraria1011.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1011.plusVoltaje(voltaje);
                        break;
                    case 11:
                        franjaHoraria1112.plusCount();
                        franjaHoraria1112.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1112.plusPotencia(potencia);
                        franjaHoraria1112.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1112.plusVoltaje(voltaje);
                        break;
                    case 12:
                        franjaHoraria1213.plusCount();
                        franjaHoraria1213.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1213.plusPotencia(potencia);
                        franjaHoraria1213.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1213.plusVoltaje(voltaje);
                        break;
                    case 13:
                        franjaHoraria1314.plusCount();
                        franjaHoraria1314.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1314.plusPotencia(potencia);
                        franjaHoraria1314.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1314.plusVoltaje(voltaje);
                        break;
                    case 14:
                        franjaHoraria1415.plusCount();
                        franjaHoraria1415.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1415.plusPotencia(potencia);
                        franjaHoraria1415.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1415.plusVoltaje(voltaje);
                        break;
                    case 15:
                        franjaHoraria1516.plusCount();
                        franjaHoraria1516.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1516.plusPotencia(potencia);
                        franjaHoraria1516.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1516.plusVoltaje(voltaje);
                        break;
                    case 16:
                        franjaHoraria1617.plusCount();
                        franjaHoraria1617.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1617.plusPotencia(potencia);
                        franjaHoraria1617.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1617.plusVoltaje(voltaje);
                        break;
                    case 17:
                        franjaHoraria1718.plusCount();
                        franjaHoraria1718.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1718.plusPotencia(potencia);
                        franjaHoraria1718.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1718.plusVoltaje(voltaje);
                        break;
                    case 18:
                        franjaHoraria1819.plusCount();
                        franjaHoraria1819.plusIntensidadCorriente(intesidad_corriente);
                        franjaHoraria1819.plusPotencia(potencia);
                        franjaHoraria1819.plusRadiacionSolar(radiacion_solar);
                        franjaHoraria1819.plusVoltaje(voltaje);
                        break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private class FranjaHorariaRowColorProvider implements TableDataRowBackgroundProvider<FranjaHoraria> {

        private double intensidadCorrienteInferior;

        public FranjaHorariaRowColorProvider(double intensidadCorrienteInferior){
            this.intensidadCorrienteInferior = intensidadCorrienteInferior;
        }

        @Override
        public Drawable getRowBackground(int rowIndex, FranjaHoraria rowData) {
            int rowColor = getActivity().getApplicationContext().getResources().getColor(R.color.md_white_1000);
            if(Double.parseDouble(rowData.getIntensidadCorriente())>intensidadCorrienteInferior){
                rowColor = getActivity().getApplicationContext().getResources().getColor(R.color.md_amber_50);
            }
            return new ColorDrawable(rowColor);
        }
    }
}
