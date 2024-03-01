package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class KeyView extends AppCompatImageView {
    public KeyView(@NonNull Context context) {
        this(context, null);
    }

    public KeyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return event.getKeyCode() == 21 ? true : super.onKeyDown(keyCode, event);
    }
}
