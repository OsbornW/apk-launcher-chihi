package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MyTextView(@NonNull Context context) {
        this(context, null);
    }

    public MyTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {

    }

    public void setSelected2(boolean selected){
        super.setSelected(selected);
    }
}
