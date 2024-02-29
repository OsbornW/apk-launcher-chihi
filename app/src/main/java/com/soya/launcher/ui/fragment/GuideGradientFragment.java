package com.soya.launcher.ui.fragment;

import android.os.Bundle;

public class GuideGradientFragment extends AbsGradientFragment{
    public static GuideGradientFragment newInstance() {
        
        Bundle args = new Bundle();
        
        GuideGradientFragment fragment = new GuideGradientFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
