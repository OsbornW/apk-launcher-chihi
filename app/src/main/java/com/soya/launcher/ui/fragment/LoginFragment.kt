package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class LoginFragment extends AbsFragment{

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View mLoginView;
    private TextView mTitleVoew;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mLoginView = view.findViewById(R.id.login);
        mTitleVoew = view.findViewById(R.id.title);

        mTitleVoew.setText(getString(R.string.login));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mLoginView);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }
}
