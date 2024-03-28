package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.callback.SelectedCallback;

public class AppLayout extends FrameLayout {

    private SelectedCallback callback;
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

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (callback != null) callback.onSelect(selected);
    }

    public void setCallback(SelectedCallback callback) {
        this.callback = callback;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public interface EventListener{
        boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
