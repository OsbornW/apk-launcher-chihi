package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class FocusFragment extends AbsFragment implements View.OnClickListener {

    public static FocusFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FocusFragment fragment = new FocusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View mNextView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_focus;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mNextView = view.findViewById(R.id.next);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mNextView.setOnClickListener(this::onClick);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mNextView);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextView)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideGroupGradientFragment.newInstance()).addToBackStack(null).commit();
        }
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }
}
