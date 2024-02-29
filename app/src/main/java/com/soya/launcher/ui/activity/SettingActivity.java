package com.soya.launcher.ui.activity;

import androidx.fragment.app.Fragment;

import com.soya.launcher.R;
import com.soya.launcher.ui.fragment.SettingFragment;

public class SettingActivity extends AbsActivity{
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getContainerId() {
        return R.id.main_browse_fragment;
    }

    @Override
    public Fragment getFragment() {
        return SettingFragment.newInstance();
    }
}
