package com.soya.launcher.ui.fragment;

import android.os.Bundle;

public class HomeGuideGroupGradientFragment extends AbsGroupGradientFragment{
    public static HomeGuideGroupGradientFragment newInstance() {

        Bundle args = new Bundle();

        HomeGuideGroupGradientFragment fragment = new HomeGuideGroupGradientFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isGuide() {
        return false;
    }
}
