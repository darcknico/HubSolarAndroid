package com.ricardo.proyecto.conandard.repositorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by karen on 23/9/2017.
 */

public class DBManager {
    private HubSolarDBHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new HubSolarDBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String humedad, String temperatura, String indice_calor, Long latitud, Long longitud,String fecha_hora) {
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String creado = s.format(new Date());
        String uuid = UUID.randomUUID().toString();
        ContentValues contentValue = new ContentValues();
        contentValue.put(HubSolarDBHelper.HUMEDAD, humedad);
        contentValue.put(HubSolarDBHelper.TEMPERATURA, temperatura);
        contentValue.put(HubSolarDBHelper.INDICE_CALOR, indice_calor);
        contentValue.put(HubSolarDBHelper.LATITUD, latitud);
        contentValue.put(HubSolarDBHelper.LONGITUD, longitud);
        contentValue.put(HubSolarDBHelper.FECHA_HORA, fecha_hora);
        contentValue.put(HubSolarDBHelper.SUBIDO, 0);
        contentValue.put(HubSolarDBHelper.UUID, uuid);
        contentValue.put(HubSolarDBHelper.CREADO, creado);
        database.insert(HubSolarDBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] {
                HubSolarDBHelper._ID,
                HubSolarDBHelper.HUMEDAD,
                HubSolarDBHelper.TEMPERATURA,
                HubSolarDBHelper.INDICE_CALOR,
                HubSolarDBHelper.LATITUD,
                HubSolarDBHelper.LONGITUD,
                HubSolarDBHelper.FECHA_HORA,
                HubSolarDBHelper.UUID,
        };
        Cursor cursor = database.query(HubSolarDBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String fecha_hora_subido, int id_subido) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HubSolarDBHelper.FECHA_HORA_SUBIDO, fecha_hora_subido);
        contentValues.put(HubSolarDBHelper.ID_SUBIDO, id_subido);
        contentValues.put(HubSolarDBHelper.SUBIDO, 1);
        int i = database.update(HubSolarDBHelper.TABLE_NAME, contentValues, HubSolarDBHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(HubSolarDBHelper.TABLE_NAME, HubSolarDBHelper._ID + "=" + _id, null);
    }

}
