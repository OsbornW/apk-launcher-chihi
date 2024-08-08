package com.soya.launcher.bean;

import java.io.Serializable;

public class TypeItem implements Serializable {
    public static final int TYPE_ICON_IMAGE_RES = 0;
    public static final int TYPE_ICON_IMAGE_URL = 1;
    public static final int TYPE_ICON_ASSETS = 2;
    public static final int TYPE_LAYOUT_STYLE_UNKNOW = -1;
    public static final int TYPE_LAYOUT_STYLE_1 = 0;
    public static final int TYPE_LAYOUT_STYLE_2 = 1;

    private int iconType = TYPE_ICON_IMAGE_RES;
    private int layoutType = TYPE_LAYOUT_STYLE_UNKNOW;
    private final String name;
    private Object icon;
    private String iconName;
    private final long id;
    private final int type;
    private String data;

    public TypeItem(String name, Object icon, long id, int type, int iconType, int layoutType){
        this.name = name;
        this.icon = icon;
        this.id = id;
        this.type = type;
        this.iconType = iconType;
        this.layoutType = layoutType;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public Object getIcon() {
        return icon;
    }

    public int getIconType() {
        return iconType;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getLayoutType() {
        return layoutType;
    }
}
