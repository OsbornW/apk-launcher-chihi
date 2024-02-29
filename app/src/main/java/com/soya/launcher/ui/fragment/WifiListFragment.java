package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class WifiListFragment extends AbsWifiListFragment {
    public static WifiListFragment newInstance() {

        Bundle args = new Bundle();

        WifiListFragment fragment = new WifiListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        showNext(false);
    }

    @Override
    protected boolean useNext() {
        return false;
    }
}
