package com.soya.launcher.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.soya.launcher.R;

public abstract class MyInstructionView extends FrameLayout {

    protected abstract int getLayoutId();

    private TextView mLRText;
    private TextView mTBText;

    private ImageView mL;
    private ImageView mR;
    private ImageView mT;
    private ImageView mB;

    public MyInstructionView(@NonNull Context context) {
        this(context, null);
    }

    public MyInstructionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(getLayoutId(), this);
        mLRText = findViewById(R.id.lr);
        mTBText = findViewById(R.id.tb);
        mL = findViewById(R.id.l);
        mR = findViewById(R.id.r);
        mT = findViewById(R.id.t);
        mB = findViewById(R.id.b);
    }

    public void setLR(int value){
        mLRText.setText(String.valueOf(value));
    }

    public void setTB(int value){
        mTBText.setText(String.valueOf(value));
    }

    public void setIconColor(int l, int r, int t, int b){
        Drawable licon = DrawableCompat.wrap(getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme()));
        DrawableCompat.setTintList(licon, ColorStateList.valueOf(getResources().getColor(l, Resources.getSystem().newTheme())));
        mL.setImageDrawable(licon);

        Drawable ricon = DrawableCompat.wrap(getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme()));
        DrawableCompat.setTintList(ricon, ColorStateList.valueOf(getResources().getColor(r, Resources.getSystem().newTheme())));
        mR.setImageDrawable(ricon);

        Drawable ticon = DrawableCompat.wrap(getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme()));
        DrawableCompat.setTintList(ticon, ColorStateList.valueOf(getResources().getColor(t, Resources.getSystem().newTheme())));
        mT.setImageDrawable(ticon);

        Drawable bicon = DrawableCompat.wrap(getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme()));
        DrawableCompat.setTintList(bicon, ColorStateList.valueOf(getResources().getColor(b, Resources.getSystem().newTheme())));
        mB.setImageDrawable(bicon);
    }

    public void resetICON(){
        setIconColor(R.color.ico_style_1, R.color.ico_style_1, R.color.ico_style_1, R.color.ico_style_1);
    }

    public void resetZERO(){
        mLRText.setText(String.valueOf(0));
        mTBText.setText(String.valueOf(0));
    }
}
