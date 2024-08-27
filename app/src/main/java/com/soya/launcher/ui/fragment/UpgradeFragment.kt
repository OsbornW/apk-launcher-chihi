package com.soya.launcher.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.soya.launcher.R;
import com.soya.launcher.bean.Version;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.handler.PermissionHandler;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.ui.activity.WifiListActivity;
import com.soya.launcher.ui.dialog.ProgressDialog;
import com.soya.launcher.utils.AppUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpgradeFragment extends AbsFragment implements ActivityResultCallback<ActivityResult>, View.OnClickListener {

    public static UpgradeFragment newInstance() {

        Bundle args = new Bundle();

        UpgradeFragment fragment = new UpgradeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final ExecutorService exec = Executors.newCachedThreadPool();

    private TextView mTitleView;
    private TextView mMesView;
    private ProgressBar mProgressBar;
    private TextView mTipView;
    private View mDivOpt;
    private View mCloseView;
    private View mInstallView;
    private View mRetryView;
    private View mWifiView;

    private Version version;
    private Handler uiHandler;
    private ActivityResultLauncher resultLauncher;
    private String fileName;
    private String savePath;
    private ProgressDialog mDialog;
    private long taskId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        version = (Version) getActivity().getIntent().getSerializableExtra(Atts.BEAN);
        uiHandler = new Handler();
        fileName = String.format("%s.apk", System.currentTimeMillis());
        savePath = FilePathMangaer.getAppUpgradePath(getActivity()) + "/" + fileName;
        mDialog = ProgressDialog.newInstance();
        Aria.download(this).register();
        initLauncher();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exec.shutdownNow();
        if (taskId != -1) Aria.download(this).load(taskId).stop();
        Aria.download(this).unRegister();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_upgrade;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mMesView = view.findViewById(R.id.mes);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTipView = view.findViewById(R.id.tip);
        mDivOpt = view.findViewById(R.id.div_oper);
        mCloseView = view.findViewById(R.id.close);
        mInstallView = view.findViewById(R.id.install);
        mRetryView = view.findViewById(R.id.retry);
        mWifiView = view.findViewById(R.id.wifi);

        mDivOpt.setVisibility(View.GONE);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mCloseView.setOnClickListener(this);
        mRetryView.setOnClickListener(this);
        mInstallView.setOnClickListener(this);
        mWifiView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mTitleView.setText(getString(R.string.upgrade));
        mTipView.setText(version.getDesc());
        syncProgress(0);
        download();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void initLauncher(){
        resultLauncher = PermissionHandler.createPermissionsWithIntent(this, this);
    }

    private void download(){
        syncProgress(0);
        showButtom(false, true, true, true);
        String url = version.getDownLink();
        taskId = Aria.download(this).load(url).setFilePath(savePath).create();
    }

    private void check(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (!getActivity().getPackageManager().canRequestPackageInstalls()){
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                resultLauncher.launch(intent);
            }else {
                install();
            }
        }else {
            install();
        }
    }

    private void install(){
        mDialog.show(getChildFragmentManager(), ProgressDialog.TAG);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppUtils.adbInstallApk(savePath);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void syncProgress(float progress){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mMesView.setText(getString(R.string.download_progress, progress));
                mProgressBar.setProgress((int) progress);
            }
        });
    }

    private void syncMes(String mes){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mMesView.setText(mes);
            }
        });
    }

    private void showButtom(boolean div, boolean close, boolean retry, boolean install){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mDivOpt.setVisibility(div ? View.VISIBLE : View.GONE);
                mCloseView.setVisibility(close ? View.VISIBLE : View.GONE);
                mInstallView.setVisibility(install ? View.VISIBLE : View.GONE);
                mRetryView.setVisibility(retry ? View.VISIBLE : View.GONE);
                mCloseView.setVisibility(View.GONE);
                mDivOpt.requestFocus();
            }
        });
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            install();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCloseView)) {
            getActivity().finish();
        }else if (v.equals(mRetryView)){
            download();
        }else if (v.equals(mInstallView)){
            check();
        }else if (v.equals(mWifiView)){
            startActivity(new Intent(getActivity(), WifiListActivity.class));
        }
    }

    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        if (!isAdded()) return;
        int p = task.getPercent();
        syncProgress(p);
    }

    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
        syncProgress(100);
        syncMes(getString(R.string.upgrade_now));
        showButtom(true, true, false, true);
    }

    @Download.onTaskFail
    protected void taskFail(DownloadTask task){
        if (!isAdded()) return;
        syncMes(getString(R.string.download_fail));
        showButtom(true, true, true, false);
    }
}
