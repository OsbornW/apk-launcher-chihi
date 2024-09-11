package com.soya.launcher.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.soya.launcher.R;

public class ProgressDialog extends SingleDialogFragment{
    public static final String TAG = "ProgressDialog";

    public static ProgressDialog newInstance() {

        Bundle args = new Bundle();

        ProgressDialog fragment = new ProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private Callback callback;

    @Override
    protected int getLayout() {
        return R.layout.dialog_progress;
    }

    @Override
    protected boolean canOutSide() {
        return false;
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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) callback.onDismiss();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onDismiss();
    }
}
