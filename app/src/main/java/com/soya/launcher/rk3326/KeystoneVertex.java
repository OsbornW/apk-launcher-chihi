package com.soya.launcher.rk3326;

import android.util.Log;

public class KeystoneVertex {

    public static final String PROP_PROJECTION_TYPE = "persist.vendor.projection.type";
    public static final String PROJECTION_TYPE_MIPI_480P = "mipi_480p";
    public static final String PROJECTION_TYPE_MIPI_1080P = "mipi_1080p";
    public static final String PROJECTION_TYPE_LVDS = "lvds";
    public static final String PROJECTION_TYPE_LVDS_720P_SPI = "lvds_720p_spi";

    public static final String PROP_KEYSTONE_TOP_LEFT = "persist.sys.keystone.lt";
    public static final String PROP_KEYSTONE_TOP_RIGHT = "persist.sys.keystone.rt";
    public static final String PROP_KEYSTONE_BOTTOM_LEFT = "persist.sys.keystone.lb";
    public static final String PROP_KEYSTONE_BOTTOM_RIGHT = "persist.sys.keystone.rb";
    public static final String PROP_KEYSTONE_MIRROR_X = "persist.sys.keystone.mirror_x";
	public static final String PROP_KEYSTONE_MIRROR_Y = "persist.sys.keystone.mirror_y";
    public static final String PROP_KEYSTONE_UPDATE = "persist.sys.keystone.update";

    public static final String PROP_PROJECTION_MODE = "persist.sys.projection.mode";

    public static final String PROP_DISPLAY_WIDTH = "persist.sys.keystone.width";
	public static final String PROP_DISPLAY_HEIGHT = "persist.sys.keystone.height";

    public Vertex vTopLeft;
    public Vertex vTopRight;
    public Vertex vBottomLeft;
    public Vertex vBottomRight;

    public void getAllKeystoneVertex() {
            String sTopLeft = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_LEFT, "0,0");
            String sTopRight = ReflectUtils.getProperty(PROP_KEYSTONE_TOP_RIGHT, "0,0");
            String sBottomLeft = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_LEFT, "0,0");
            String sBottomRight = ReflectUtils.getProperty(PROP_KEYSTONE_BOTTOM_RIGHT, "0,0");
				
			Log.d("keystone", sTopLeft + " " + sTopRight + " " + sBottomLeft + " " + sBottomRight);
				
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
}


