package com.chihihx.launcher.bean;

import java.io.Serializable;

public class HomeItem implements Serializable {
    private int type;
    private String name;
    private String icon;
    private String iconName;
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

    public String getIconName() {
       /* if (iconName == null||iconName.isEmpty()) {
            return "icon_media_center";
        }*/
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIcon() {
        return icon;
    }
}
