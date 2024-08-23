package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.soya.launcher.R;
import com.soya.launcher.bean.Language;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.PreferencesUtils;

public class GuideLanguageFragment extends AbsLanguageFragment {
    public static GuideLanguageFragment newInstance() {

        Bundle args = new Bundle();

        GuideLanguageFragment fragment = new GuideLanguageFragment();
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
        AndroidSystem.setSystemLanguage(getActivity(), bean.getLanguage());
        jump();
    }

    @Override
    protected void onNext() {
        super.onNext();
        jump();
    }

    private void jump(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideGroupGradientFragment.newInstance()).addToBackStack(null).commit();
    }
}
