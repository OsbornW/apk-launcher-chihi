package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class GuideDateFragment extends AbsDateFragment{
    public static GuideDateFragment newInstance() {

        Bundle args = new Bundle();

        GuideDateFragment fragment = new GuideDateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        showNext(true);
        showWifi(false);
    }
}
