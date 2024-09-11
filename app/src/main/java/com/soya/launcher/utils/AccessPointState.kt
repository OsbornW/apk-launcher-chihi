package com.soya.launcher.utils;

import android.net.wifi.ScanResult;

public class AccessPointState {
    public static final String WPA2 = "WPA2";
    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String OPEN = "Open";
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";

    public static String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] securityModes = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return OPEN;
    }
}
