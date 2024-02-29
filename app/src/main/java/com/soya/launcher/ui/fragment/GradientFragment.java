package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class GradientFragment extends AbsGradientFragment{
    public static GradientFragment newInstance() {
        
        Bundle args = new Bundle();
        
        GradientFragment fragment = new GradientFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
    }

    @Override
    protected boolean useLongClick() {
        return false;
    }
}
