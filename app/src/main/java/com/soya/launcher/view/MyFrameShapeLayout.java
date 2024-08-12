package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.shape.layout.ShapeFrameLayout;
import com.soya.launcher.callback.SelectedCallback;

import org.jetbrains.annotations.NotNull;

public class MyFrameShapeLayout extends ShapeFrameLayout {
    private SelectedCallback callback;

    public MyFrameShapeLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyFrameShapeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameShapeLayout(@NotNull Context context, @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context,attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (callback != null) callback.onSelect(selected);
    }

    public void setCallback(SelectedCallback callback) {
        this.callback = callback;
    }
}
