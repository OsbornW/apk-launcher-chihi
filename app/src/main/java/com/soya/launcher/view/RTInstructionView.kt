package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class RTInstructionView extends MyInstructionView{
    public RTInstructionView(@NonNull Context context) {
        super(context);
    }

    public RTInstructionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.include_instructions_rt;
    }
}
