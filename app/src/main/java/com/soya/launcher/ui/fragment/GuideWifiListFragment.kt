package com.soya.launcher.ui.fragment;

import android.os.Bundle;

public class GuideWifiListFragment extends AbsWifiListFragment {
    public static GuideWifiListFragment newInstance() {

        Bundle args = new Bundle();

        GuideWifiListFragment fragment = new GuideWifiListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
