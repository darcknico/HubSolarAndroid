package com.ricardo.proyecto.conandard.repositorio;

/**
 * Created by karen on 9/10/2017.
 */

public class Singleton {
    private static Singleton instance = null;
    private boolean LOG = false;
    private boolean query = false;
    private boolean importar = false;

    protected Singleton() {
    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
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
}
