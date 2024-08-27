package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.soya.launcher.R;

public class SetDateFragment extends AbsDateFragment{

    public static SetDateFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SetDateFragment fragment = new SetDateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        showNext(false);
        showWifi(false);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }
}
