package com.soya.launcher.ui.dialog;

import android.os.Bundle;
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

public class WifiSaveDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "WifiSaveDialog";
    public static WifiSaveDialog newInstance(String name) {

        Bundle args = new Bundle();
        args.putString(Atts.BEAN, name);
        WifiSaveDialog fragment = new WifiSaveDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private View mCloseView;
    private View mCleanView;
    private View mConfirmView;
    private TextView mNameView;
    private View mRootView;
    private ImageView mBlur;

    private Callback callback;

    @Override
    protected int getLayout() {
        return R.layout.dialog_wifi_save;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mCleanView = view.findViewById(R.id.clean);
        mCloseView = view.findViewById(R.id.close);
        mConfirmView = view.findViewById(R.id.confirm);
        mNameView = view.findViewById(R.id.wifi_name);
        mRootView = view.findViewById(R.id.root);
        mBlur = view.findViewById(R.id.blur);

        mNameView.setText(getArguments().getString(Atts.BEAN));
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mCleanView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mConfirmView.requestFocus();
        blur(mRootView, mBlur);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCloseView)){
            dismiss();
        }else if (v.equals(mCleanView)){
            if (callback != null) {
                callback.onClick(-1);
                dismiss();
            }
        }else if (v.equals(mConfirmView)){
            if (callback != null) {
                callback.onClick(0);
                dismiss();
            }
        }
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
    protected int[] getWidthAndHeight() {
        return new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    public interface Callback{
        void onClick(int type);
    }
}
