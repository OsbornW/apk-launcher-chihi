package com.chihihx.launcher.bean;

import android.bluetooth.BluetoothDevice;

public class BluetoothItem {
    private BluetoothDevice device;

    public BluetoothItem(BluetoothDevice device){
        this.device = device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
