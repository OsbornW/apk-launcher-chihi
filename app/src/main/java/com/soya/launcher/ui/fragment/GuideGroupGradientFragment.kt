package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.open.system.ASystemProperties;
import com.soya.launcher.R;
import com.soya.launcher.ui.activity.ChooseGradientActivity;

public class GuideGroupGradientFragment extends AbsGroupGradientFragment {

    public static GuideGroupGradientFragment newInstance() {

        Bundle args = new Bundle();

        GuideGroupGradientFragment fragment = new GuideGroupGradientFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isGuide() {
        return true;
    }
}
