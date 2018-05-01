package com.ricardo.proyecto.conandard.repositorio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by karen on 23/9/2017.
 */

public class HubSolarDBHelper extends SQLiteOpenHelper {
    // Table Name
    public static final String TABLE_NAME = "COUNTRIES";

    // Table columns
    public static final String _ID = "_id";
    public static final String TEMPERATURA = "temperatura";
    public static final String HUMEDAD = "humedad";
    public static final String INDICE_CALOR = "indice_calor";
    public static final String RADIACION_SOLAR = "radiacion_solar";
    public static final String INTESIDAD_CORRIENTE = "intencidad_corriente";
    public static final String VOLTAJE = "voltaje";
    public static final String POTENCIA = "potencia";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String FECHA_HORA = "fecha_hora";
    public static final String SUBIDO = "subido";
    public static final String FECHA_HORA_SUBIDO = "fecha_hora_subido";
    public static final String ID_SUBIDO = "id_subido";
    public static final String UUID = "uuid";
    public static final String CREADO = "creado";

    // Database Information
    static final String DB_NAME = "HUBSOLAR.DB";

    // database version
    static final int DB_VERSION = 1;

    public HubSolarDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating table query
    private static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" +
                    _ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TEMPERATURA + " REAL NOT NULL, " +
                    HUMEDAD + " REAL NOT NULL," +
                    INDICE_CALOR + " REAL NOT NULL," +
                    RADIACION_SOLAR + " REAL NOT NULL," +
                    INTESIDAD_CORRIENTE + " REAL NOT NULL," +
                    VOLTAJE + " REAL NOT NULL," +
                    POTENCIA + " REAL NOT NULL," +
                    LATITUD + " INTEGER," +
                    LONGITUD + " INTEGER," +
                    SUBIDO + " INTEGER," +
                    FECHA_HORA + " TEXT," +
                    FECHA_HORA_SUBIDO + " TEXT," +
                    ID_SUBIDO + " INTEGER," +
                    UUID + " TEXT," +
                    CREADO + " TEXT" +
                    ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
