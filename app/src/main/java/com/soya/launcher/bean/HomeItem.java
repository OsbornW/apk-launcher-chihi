package com.soya.launcher.bean;

import java.io.Serializable;
import java.util.List;

public class HomeItem implements Serializable {
    private int type;
    private String name;
    private String icon;
    private AppPackage[] packageNames;
    private Movice[] datas;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AppPackage[] getPackageNames() {
        return packageNames;
    }

    public Movice[] getDatas() {
        return datas;
    }

    public String getIcon() {
        return icon;
    }
}
