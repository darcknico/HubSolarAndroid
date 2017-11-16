package com.ricardo.proyecto.conandard.repositorio;

/**
 * Created by karen on 9/10/2017.
 */

public class Singleton {
    private static Singleton instance = null;
    private boolean enviado = false;
    private boolean LOG = false;
    private boolean query = false;
    private boolean importar = false;
    private boolean backup = false;
    private Thread hiloConnecion = null;

    protected Singleton() {
    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }

    public boolean isLOG() {
        return LOG;
    }

    public void notLOG() {
        this.LOG = !this.isLOG();
    }

    public boolean isQuery() {
        return query;
    }

    public void notQuery() {
        this.query = !this.query;
    }

    public boolean isImportar() {
        return importar;
    }

    public void notImportar() {
        this.importar = !this.importar;
    }

    public void setLOG(boolean LOG) {
        this.LOG = LOG;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public void setImportar(boolean importar) {
        this.importar = importar;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public Thread getHiloConnecion() {
        return hiloConnecion;
    }

    public void setHiloConnecion(Thread hiloConnecion) {
        this.hiloConnecion = hiloConnecion;
    }
}
