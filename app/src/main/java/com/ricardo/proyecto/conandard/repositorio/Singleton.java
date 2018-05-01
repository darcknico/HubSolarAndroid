package com.ricardo.proyecto.conandard.repositorio;

import android.widget.ImageButton;

/**
 * Created by karen on 9/10/2017.
 */

public class Singleton {
    private static Singleton instance = null;
    private boolean LOG = false;
    private boolean query = false;
    private boolean importar = false;
    private boolean backup = false;

    private ImageButton logButton = null;
    private ImageButton importButton = null;
    private ImageButton backupButton = null;
    private ImageButton queryButton = null;

    private ImageButton usbButton = null;
    private ImageButton bluethootButton = null;
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

    public boolean isQuery() {
        return query;
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

    public static void setInstance(Singleton instance) {
        Singleton.instance = instance;
    }

    public ImageButton getLogButton() {
        return logButton;
    }

    public void setLogButton(ImageButton logButton) {
        this.logButton = logButton;
    }

    public ImageButton getImportButton() {
        return importButton;
    }

    public void setImportButton(ImageButton importButton) {
        this.importButton = importButton;
    }

    public ImageButton getBackupButton() {
        return backupButton;
    }

    public void setBackupButton(ImageButton backupButton) {
        this.backupButton = backupButton;
    }

    public ImageButton getQueryButton() {
        return queryButton;
    }

    public void setQueryButton(ImageButton queryButton) {
        this.queryButton = queryButton;
    }

    public ImageButton getUsbButton() {
        return usbButton;
    }

    public void setUsbButton(ImageButton usbButton) {
        if(this.usbButton == null) {
            this.usbButton = usbButton;
        }
    }

    public ImageButton getBluethootButton() {
        return bluethootButton;
    }

    public void setBluethootButton(ImageButton bluethootButton) {
        if(this.bluethootButton == null) {
            this.bluethootButton = bluethootButton;
        }
    }
}
