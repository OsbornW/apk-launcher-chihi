package com.soya.launcher.bean;

import java.io.Serializable;

public class TypeItem implements Serializable {
    public static final int TYPE_ICON_IMAGE_RES = 0;
    public static final int TYPE_ICON_IMAGE_URL = 1;
    public static final int TYPE_LAYOUT_STYLE_UNKNOW = -1;
    public static final int TYPE_LAYOUT_STYLE_1 = 0;
    public static final int TYPE_LAYOUT_STYLE_2 = 1;

    private int iconType = TYPE_ICON_IMAGE_RES;
    private int layoutType = TYPE_LAYOUT_STYLE_UNKNOW;
    private String name;
    private Object icon;
    private long id;
    private int type;

    public TypeItem(String name, Object icon, long id, int type, int iconType, int layoutType){
        this.name = name;
        this.icon = icon;
        this.id = id;
        this.type = type;
        this.iconType = iconType;
        this.layoutType = layoutType;
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
