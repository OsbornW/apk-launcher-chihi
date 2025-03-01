package com.chihihx.launcher.bean;

public class PushApp {
    private String name;
    private String packageName;
    private String version;
    private String url;
    private Boolean installFlag;

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Boolean getInstallFlag() {
        return installFlag;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
