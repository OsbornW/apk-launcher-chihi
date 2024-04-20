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

public class GuideGroupGradientFragment extends AbsFragment {

    public static GuideGroupGradientFragment newInstance() {

        Bundle args = new Bundle();

        GuideGroupGradientFragment fragment = new GuideGroupGradientFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View mManualView;
    private View mSkipView;

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_guide_group_gradient;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mManualView = view.findViewById(R.id.manual);
        mSkipView = view.findViewById(R.id.skip);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mManualView);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mManualView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(false);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GradientFragment.newInstance()).addToBackStack(null).commit();
            }
        });

        mSkipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideDateFragment.newInstance()).addToBackStack(null).commit();
            }
        });
    }

    private void setEnable(boolean isEnalbe){
        ASystemProperties.set("persist.vendor.gsensor.enable", isEnalbe ? "1" : "0");
    }
}
