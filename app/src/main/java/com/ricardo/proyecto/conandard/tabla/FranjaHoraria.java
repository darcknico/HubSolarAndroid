package com.ricardo.proyecto.conandard.tabla;

/**
 * Created by nico on 23/11/17.
 */

public class FranjaHoraria {
    private String hora;
    private String radiacionSolar;
    private String intensidadCorriente;
    private String voltaje;
    private String potencia;

    private double radiacionSolarDouble = 0;
    private double intensidadCorrienteDouble = 0;
    private double voltajeDouble = 0;
    private double potenciaDouble = 0;


    public FranjaHoraria(String hora, String radiacionSolar, String intensidadCorriente, String voltaje, String potencia) {
        this.hora = hora;
        this.radiacionSolar = radiacionSolar;
        this.intensidadCorriente = intensidadCorriente;
        this.voltaje = voltaje;
        this.potencia = potencia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getRadiacionSolar() {
        if(radiacionSolarDouble>0){
            return String.valueOf(getRadiacionSolarDouble());
        }
        return radiacionSolar;
    }

    public void setRadiacionSolar(String radiacionSolar) {
        this.radiacionSolar = radiacionSolar;
    }

    public String getIntensidadCorriente() {
        if(intensidadCorrienteDouble>0){
            return String.valueOf(getIntensidadCorrienteDouble());
        }
        return intensidadCorriente;
    }

    public void setIntensidadCorriente(String intensidadCorriente) {
        this.intensidadCorriente = intensidadCorriente;
    }

    public String getVoltaje() {
        if(getVoltajeDouble()>0){
            return String.valueOf(getVoltajeDouble());
        }
        return voltaje;
    }

    public void setVoltaje(String voltaje) {
        this.voltaje = voltaje;
    }

    public String getPotencia() {
        if(potenciaDouble>0){
            return String.valueOf(getPotenciaDouble());
        }
        return potencia;
    }

    public void setPotencia(String potencia) {
        this.potencia = potencia;
    }

    public double getRadiacionSolarDouble() {
        return radiacionSolarDouble;
    }

    public void setRadiacionSolarDouble(double radiacionSolarDouble) {
        this.radiacionSolarDouble = radiacionSolarDouble;
    }

    public double getIntensidadCorrienteDouble() {
        return intensidadCorrienteDouble;
    }

    public void setIntensidadCorrienteDouble(double intensidadCorrienteDouble) {
        this.intensidadCorrienteDouble = intensidadCorrienteDouble;
    }

    public double getVoltajeDouble() {
        return voltajeDouble;
    }

    public void setVoltajeDouble(double voltajeDouble) {
        this.voltajeDouble = voltajeDouble;
    }

    public double getPotenciaDouble() {
        return potenciaDouble;
    }

    public void setPotenciaDouble(double potenciaDouble) {
        this.potenciaDouble = potenciaDouble;
    }

    public void plusRadiacionSolar(double radiacionSolarDouble){
        this.radiacionSolarDouble +=radiacionSolarDouble;
    }
    public void plusIntensidadCorriente(double intensidadCorrienteDouble){
        this.intensidadCorrienteDouble +=intensidadCorrienteDouble;
    }public void plusVoltaje(double voltajeDouble){
        this.voltajeDouble +=voltajeDouble;
    }
    public void plusPotencia(double potenciaDouble){
        this.potenciaDouble +=potenciaDouble;
    }
}
