package com.soya.launcher.rk3326;

import android.util.Log;

public class KeystoneVertex {


    public static final String PROP_KEYSTONE_TOP_LEFT = "persist.sys.keystone.lt";
    public static final String PROP_KEYSTONE_TOP_RIGHT = "persist.sys.keystone.rt";
    public static final String PROP_KEYSTONE_BOTTOM_LEFT = "persist.sys.keystone.lb";
    public static final String PROP_KEYSTONE_BOTTOM_RIGHT = "persist.sys.keystone.rb";
    public static final String PROP_KEYSTONE_UPDATE = "persist.sys.keystone.update";


    public Vertex vTopLeft;
    public Vertex vTopRight;
    public Vertex vBottomLeft;
    public Vertex vBottomRight;

    public void getAllKeystoneVertex() {
            String sTopLeft = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_LEFT, "0,0");
            String sTopRight = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_RIGHT, "0,0");
            String sBottomLeft = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_LEFT, "0,0");
            String sBottomRight = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_RIGHT, "0,0");

        vTopLeft = new Vertex(sTopLeft);
            vTopRight = new Vertex(sTopRight);
            vBottomLeft = new Vertex(sBottomLeft);
            vBottomRight = new Vertex(sBottomRight);
    }

    public void getAllKeystoneVertexForH6() {
        String sTopLeft = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_LEFT, "0,0");
        String sTopRight = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_RIGHT, "0,0");
        String sBottomLeft = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_LEFT, "0,0");
        String sBottomRight = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_RIGHT, "0,0");

        vTopLeft = new Vertex(sTopLeft);
        vTopRight = new Vertex(sTopRight);
        vBottomLeft = new Vertex(sBottomLeft);
        vBottomRight = new Vertex(sBottomRight);
    }


    public void updateAllKeystoneVertex() {
            ReflectUtils.setProperty(PROP_KEYSTONE_TOP_LEFT, vTopLeft.toString());
            ReflectUtils.setProperty(PROP_KEYSTONE_TOP_RIGHT, vTopRight.toString());
            ReflectUtils.setProperty(PROP_KEYSTONE_BOTTOM_LEFT, vBottomLeft.toString());
            ReflectUtils.setProperty(PROP_KEYSTONE_BOTTOM_RIGHT, vBottomRight.toString());
            ReflectUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");

    }

    public void updateAllKeystoneVertexForH6() {
        ReflectUtils.setProperty(PROP_KEYSTONE_BOTTOM_LEFT, vTopLeft.toString());
        ReflectUtils.setProperty(PROP_KEYSTONE_BOTTOM_RIGHT, vTopRight.toString());
        ReflectUtils.setProperty(PROP_KEYSTONE_TOP_LEFT, vBottomLeft.toString());
        ReflectUtils.setProperty(PROP_KEYSTONE_TOP_RIGHT, vBottomRight.toString());
        ReflectUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");

    }

}


