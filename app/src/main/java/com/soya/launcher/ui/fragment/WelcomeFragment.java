package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class WelcomeFragment extends AbsFragment{

    public static WelcomeFragment newInstance() {

        Bundle args = new Bundle();

        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Handler uiHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler = new Handler();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_welcome;
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideLanguageFragment.newInstance()).commit();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, RemoteControlFragment.newInstance(true)).commit();
            }
        }, 2000);
    }
}
