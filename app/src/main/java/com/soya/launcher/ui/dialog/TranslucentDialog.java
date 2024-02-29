package com.soya.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class TranslucentDialog extends Dialog {

    protected Context context;
    protected LayoutInflater inflater;
    protected View decorView;
    protected Handler uiHandler;
    protected int layoutId;

    public TranslucentDialog(@NonNull Context context, int layoutId) {
        super(context);
        this.layoutId = layoutId;
        this.context = context;
        inflater = LayoutInflater.from(context);
        uiHandler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                        | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        window.setDimAmount(getDimAmount());
        if (getAnimation() != -1) window.setWindowAnimations(getAnimation());

        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        lp.type =WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        lp.width = getWidth();
        lp.height = getHeight();
        window.setAttributes(lp);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        decorView = window.getDecorView();
    }

    protected int getAnimation(){
        return -1;
    }

    protected int getWidth(){
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    protected int getHeight(){
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    protected float getDimAmount(){
        return 0.5f;
    }
}
