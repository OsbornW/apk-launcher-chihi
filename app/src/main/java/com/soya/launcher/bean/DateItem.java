package com.soya.launcher.bean;

public class DateItem {
    private int type;
    private String title;
    private String description;
    private boolean isSwitch;

    private boolean useSwitch;

    public DateItem(int type, String title, String description, boolean isSwitch, boolean useSwitch){
        this.type = type;
        this.title = title;
        this.description = description;
        this.isSwitch = isSwitch;
        this.useSwitch = useSwitch;
    }

    public boolean isUseSwitch() {
        return useSwitch;
    }

    public void setUseSwitch(boolean useSwitch) {
        this.useSwitch = useSwitch;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSwitch(boolean aSwitch) {
        isSwitch = aSwitch;
    }
}
