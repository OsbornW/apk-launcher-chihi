package com.soya.launcher.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soya.launcher.R;
import com.soya.launcher.utils.AndroidSystem;

public class RemoteDialog extends TranslucentDialog{
    private TextView mNameView;

    public RemoteDialog(@NonNull Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNameView = decorView.findViewById(R.id.blu_name);
    }

    public void setName(String name){
        if (mNameView != null) mNameView.setText(name);
    }
}
