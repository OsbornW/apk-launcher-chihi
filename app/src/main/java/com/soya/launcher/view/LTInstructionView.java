package com.soya.launcher.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class LTInstructionView extends MyInstructionView{
    public LTInstructionView(@NonNull Context context) {
        super(context);
    }

    public LTInstructionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.include_instructions_lt;
    }
}
