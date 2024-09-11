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

public class UninstallDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "UninstallDialog";
    public static UninstallDialog newInstance(ApplicationInfo info,Boolean isSuccess) {

        Bundle args = new Bundle();
        args.putParcelable(Atts.BEAN, info);
        args.putBoolean("issuccess",isSuccess);
        UninstallDialog fragment = new UninstallDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mOptView;
    private TextView mMsgView;
    private ImageView mBlur;
    private View mRootView;

    private ApplicationInfo info;
    private Boolean isSuccess;
    //private InnerBroadcast mBroadcast;
    private boolean isEnd = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getParcelable(Atts.BEAN);
        isSuccess = getArguments().getBoolean("issuccess");
        //mBroadcast = new InnerBroadcast();
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(IntentAction.ACTION_DELETE_PACKAGE);
        //getActivity().registerReceiver(mBroadcast, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // getActivity().unregisterReceiver(mBroadcast);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_uninstall;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOptView.requestFocus();
        //blur(mRootView, mBlur);
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mOptView = view.findViewById(R.id.opt);
        mMsgView = view.findViewById(R.id.msg);
        mBlur = view.findViewById(R.id.blur);
        mRootView = view.findViewById(R.id.root);
        blur(mRootView, mBlur);
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mOptView.setOnClickListener(this);
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);
        if(isSuccess){
            mMsgView.setText(getString(R.string.uninstalled));
        }else {
            mMsgView.setText(getString(R.string.uninstall_failed));
        }
        mOptView.setText(getString(R.string.close));
        isEnd = true;
        //mMsgView.setText(getString(R.string.uninstalling));
        //AndroidSystem.uninstallPackage(getActivity(), info.packageName);
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
        if (v.equals(mOptView)){
            if (isEnd) dismiss();
        }
    }

/*    public final class InnerBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ;
            int code = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1);
            boolean success = code == PackageInstaller.STATUS_SUCCESS;
            mMsgView.setText(getString(success ? R.string.uninstalled : R.string.uninstall_failed));
            mOptView.setText(getString(R.string.close));
            isEnd = true;
        }
    }*/
}
