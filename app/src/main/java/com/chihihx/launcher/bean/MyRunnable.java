package com.chihihx.launcher.bean;

public abstract class MyRunnable implements Runnable{
    protected boolean interrupt;

    public void interrupt(){
        interrupt = true;
    }

    public boolean isInterrupt() {
        return interrupt;
    }
}
