package com.soya.launcher.ui.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.soya.launcher.R;
import com.soya.launcher.config.Config;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.ui.fragment.MainFragment;
import com.soya.launcher.ui.fragment.WelcomeFragment;

public class MainActivity extends AbsActivity {
    private boolean canBackPressed = true;
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
        if (PreferencesManager.isGuide() == 0 && Config.COMPANY == 0){
            canBackPressed = true;
            return WelcomeFragment.newInstance();
        }else {
            canBackPressed = false;
            return MainFragment.newInstance();
        }
    }

    @Override
    public void onBackPressed() {
        if (canBackPressed)
            super.onBackPressed();
        else
            sendBroadcast(new Intent(IntentAction.ACTION_RESET_SELECT_HOME));
    }
}
