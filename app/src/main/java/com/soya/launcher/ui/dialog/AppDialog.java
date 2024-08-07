package com.soya.launcher.ui.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.utils.AndroidSystem;

public class AppDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "AppDialog";
    public static AppDialog newInstance(ApplicationInfo info) {

        Bundle args = new Bundle();
        args.putParcelable(Atts.BEAN, info);
        AppDialog fragment = new AppDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private View mCloseView;
    private View mDeleteView;
    private View mOpenView;

    private ImageView mIV;
    private TextView mNameView;
    private TextView mVersionView;
    private ImageView mBlur;
    private View mRootView;

    private ApplicationInfo info;

    private Callback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getParcelable(Atts.BEAN);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_app;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOpenView.requestFocus();
        //blur(mRootView, mBlur);
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mCloseView = view.findViewById(R.id.close);
        mDeleteView = view.findViewById(R.id.delete);
        mOpenView = view.findViewById(R.id.open);
        mIV = view.findViewById(R.id.icon);
        mNameView = view.findViewById(R.id.name);
        mVersionView = view.findViewById(R.id.version);
        mBlur = view.findViewById(R.id.blur);
        mRootView = view.findViewById(R.id.root);
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mDeleteView.setVisibility(AndroidSystem.isSystemApp(info.flags) ? View.GONE : View.VISIBLE);
        mCloseView.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
        mOpenView.setOnClickListener(this);
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);
        try {
            PackageManager pm = getActivity().getPackageManager();
            mIV.setImageDrawable(info.loadIcon(pm));
            mNameView.setText(info.loadLabel(pm));
            mVersionView.setText(pm.getPackageInfo(info.packageName, 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int[] getWidthAndHeight() {
        return new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean isMaterial() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCloseView)){
            dismiss();
        }else if (v.equals(mDeleteView)){
            //UninstallDialog.newInstance(info).show(getActivity().getSupportFragmentManager(), UninstallDialog.TAG);

            AndroidSystem.uninstallPackage(getActivity(), info.packageName);
            dismiss();
        }else if (v.equals(mOpenView)){
            if (callback != null) callback.onOpen();
            dismiss();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onOpen();
    }
}
