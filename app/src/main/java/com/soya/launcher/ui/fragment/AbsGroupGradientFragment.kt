package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.open.system.ASystemProperties;
import com.soya.launcher.R;

public class AbsGroupGradientFragment extends AbsFragment {

    private View mManualView;
    private TextView mSkipView;
    private TextView mSkipTipView;

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
        mSkipTipView = view.findViewById(R.id.skip_tip);
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
                if (isGuide())
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideDateFragment.newInstance()).addToBackStack(null).commit();
                else
                    getActivity().finish();
            }
        });
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mSkipView.setText(isGuide() ? getString(R.string.next) : getString(R.string.done));
        mSkipTipView.setText(isGuide() ? getString(R.string.guide_group_gradient_tip_next) : getString(R.string.guide_group_gradient_tip_done));
    }

    protected boolean isGuide(){
        return true;
    }

    private void setEnable(boolean isEnalbe){
        ASystemProperties.set("persist.vendor.gsensor.enable", isEnalbe ? "1" : "0");
    }
}
