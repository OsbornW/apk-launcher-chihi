package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.callback.SelectedCallback;

public class KeyEventFrameLayout extends MyFrameLayout {
    private KeyEventCallback keyEventCallback;

    public KeyEventFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public KeyEventFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (keyEventCallback != null) keyEventCallback.onKeyDown(event);
        return super.dispatchKeyEvent(event);
    }

    public void setKeyEventCallback(KeyEventCallback keyEventCallback) {
        this.keyEventCallback = keyEventCallback;
    }

    public interface KeyEventCallback{
        void onKeyDown(KeyEvent event);
    }
}
