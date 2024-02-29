package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class GuideWifiListFragment extends AbsWifiListFragment {
    public static GuideWifiListFragment newInstance() {

        Bundle args = new Bundle();

        GuideWifiListFragment fragment = new GuideWifiListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
