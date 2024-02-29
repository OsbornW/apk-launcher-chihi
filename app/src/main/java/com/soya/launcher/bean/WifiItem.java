package com.soya.launcher.bean;

import android.net.wifi.ScanResult;

public class WifiItem {
    private ScanResult item;
    private boolean isSave;

    public WifiItem(ScanResult item, boolean isSave){
        this.item = item;
        this.isSave = isSave;
    }

    public ScanResult getItem() {
        return item;
    }

    public void setItem(ScanResult item) {
        this.item = item;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }
}
