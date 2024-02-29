package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.soya.launcher.R;
import com.soya.launcher.bean.Language;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.ui.activity.MainActivity;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.PreferencesUtils;

import java.util.Locale;

public class SetLanguageFragment extends AbsLanguageFragment {
    public static SetLanguageFragment newInstance() {

        Bundle args = new Bundle();

        SetLanguageFragment fragment = new SetLanguageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        showNext(false);
    }

    @Override
    protected void onSelectLanguage(Language bean) {
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.getLanguage().toLanguageTag());
        AndroidSystem.setSystemLanguage(getActivity(), Locale.forLanguageTag(PreferencesManager.getLanguage()));
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }
}
