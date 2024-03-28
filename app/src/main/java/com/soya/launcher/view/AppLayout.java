package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLayout extends MyFrameLayout {

    private EventListener listener;

    public AppLayout(@NonNull Context context) {
        super(context, null);
    }

    public AppLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (listener != null) listener.onKeyDown(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public interface EventListener{
        boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
