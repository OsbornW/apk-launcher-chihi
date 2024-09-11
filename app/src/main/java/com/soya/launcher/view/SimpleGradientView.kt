package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class SimpleGradientView extends FrameLayout {
    public SimpleGradientView(@NonNull Context context) {
        this(context, null);
    }

    public SimpleGradientView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_instructions, this);
    }
}
