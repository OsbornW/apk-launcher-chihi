package com.soya.launcher.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soya.launcher.R;

public class WifiPassDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "WifiPassDialog";
    public static WifiPassDialog newInstance() {

        Bundle args = new Bundle();

        WifiPassDialog fragment = new WifiPassDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mEditText;
    private View mCloseView;
    private View mCleanView;
    private View mConfirmView;
    private View mRootView;
    private ImageView mBlur;

    private Callback callback;

    @Override
    protected int getLayout() {
        return R.layout.dialog_wifi_pass;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mEditText = view.findViewById(R.id.edit_pass);
        mCleanView = view.findViewById(R.id.clean);
        mCloseView = view.findViewById(R.id.close);
        mConfirmView = view.findViewById(R.id.confirm);
        mRootView = view.findViewById(R.id.root);
        mBlur = view.findViewById(R.id.blur);
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mCleanView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);
        mEditText.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText.requestFocus();
        blur(mRootView, mBlur);
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean isMaterial() {
        return false;
    }

    protected int[] getWidthAndHeight() {
        return new int[]{
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        };
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCloseView)){
            dismiss();
        }else if (v.equals(mCleanView)){
            mEditText.setText("");
        }else if (v.equals(mConfirmView)){
            String text = mEditText.getText().toString();
            if (callback != null && !TextUtils.isEmpty(text)) {
                callback.onConfirm(text);
                dismiss();
            }
        }else if (v.equals(mEditText)){
            KeyboardDialog dialog = KeyboardDialog.newInstance();
            dialog.setTargetView(mEditText);
            dialog.setCallback(new KeyboardDialog.Callback() {
                @Override
                public void onClose() {
                    setRootGravity(Gravity.CENTER);
                }
            });
            setRootGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            dialog.show(getChildFragmentManager(), KeyboardDialog.TAG);
        }
    }

    public void setRootGravity(int gravity){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
        lp.gravity = gravity;
        mRootView.setLayoutParams(lp);
        blur(mRootView, mBlur);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onConfirm(String text);
    }
}
