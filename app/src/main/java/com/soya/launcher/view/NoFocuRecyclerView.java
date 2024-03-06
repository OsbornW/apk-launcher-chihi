package com.soya.launcher.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NoFocuRecyclerView extends RecyclerView {
    public NoFocuRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NoFocuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setEnabled(false);
    }
}
