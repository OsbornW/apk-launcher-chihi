package com.soya.launcher.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.BuildConfig;
import com.soya.launcher.R;
import com.soya.launcher.adapter.AboutAdapter;
import com.soya.launcher.bean.AboutItem;
import com.soya.launcher.bean.Version;
import com.soya.launcher.config.Config;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.http.response.VersionResponse;
import com.soya.launcher.ui.dialog.ProgressDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class AboutFragment extends AbsFragment implements View.OnClickListener {

    public static AboutFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private VerticalGridView mContentGrid;
    private View mUpgradeView;
    
    private Handler uiHandler;

    private Call call;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mContentGrid = view.findViewById(R.id.content);
        mUpgradeView = view.findViewById(R.id.upgrade);

        mTitleView.setText(getString(R.string.about));
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mUpgradeView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        setContent();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mContentGrid);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void setContent(){
        List<AboutItem> list = new ArrayList<>();
        //list.add(new AboutItem(0, R.drawable.baseline_storage_100, getString(R.string.storage), getString(R.string.storage_total_mask, AndroidSystem.getTotalInternalMemorySize() / 1024000000.0F)));
        list.add(new AboutItem(0, R.drawable.baseline_tv_100, getString(R.string.android_tv_os_version), Build.VERSION.RELEASE));
        list.add(new AboutItem(0, R.drawable.baseline_translate_100, getString(R.string.language), AndroidSystem.getSystemLanguage(getActivity())));
        list.add(new AboutItem(0, R.drawable.baseline_apps_100, getString(R.string.apps), String.valueOf(AndroidSystem.getUserApps(getActivity()).size())));
        list.add(new AboutItem(0, R.drawable.baseline_workspaces_100, getString(R.string.software_version), BuildConfig.VERSION_NAME));
        if (Config.COMPANY == 0)
            list.add(new AboutItem(2, R.drawable.baseline_settings_backup_restore_100, getString(R.string.factory_reset), Build.MODEL));
        else
            list.add(new AboutItem(0, R.drawable.baseline_token_100, getString(R.string.device_id), AndroidSystem.getDeviceId(getActivity())));

        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AboutAdapter(getActivity(), getLayoutInflater()).setCallback(new AboutAdapter.Callback() {
            @Override
            public void onClick(AboutItem bean) {
                switch (bean.getType()){
                    case 2:
                        AndroidSystem.restoreFactory(getActivity());
                        break;
                }
            }
        }));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(1);
        arrayObjectAdapter.addAll(0, list);
        mContentGrid.requestFocus();
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    @Override
    public void onClick(View v) {
        if (v.equals(mUpgradeView)){
            ProgressDialog dialog = ProgressDialog.newInstance();
            dialog.show(getChildFragmentManager(), ProgressDialog.TAG);
            call = HttpRequest.checkVersion((call, status, response) -> {
                dialog.dismiss();
                if (!isAdded() || call.isCanceled() || response == null || response.getData() == null){

                    ToastDialog toastDialog = ToastDialog.newInstance(getString(R.string.already_latest_version), ToastDialog.MODE_CONFIRM);
                    toastDialog.show(getChildFragmentManager(), ToastDialog.TAG);
                    return;
                }
                Version result = response.getData();
                if (result.getVersion() > BuildConfig.VERSION_CODE && Config.CHANNEL.equals(result.getChannel())) {
                    PreferencesUtils.setProperty(Atts.UPGRADE_VERSION, (int) result.getVersion());
                    AndroidSystem.jumpUpgrade(getActivity(), result);
                } else {

                    ToastDialog toastDialog = ToastDialog.newInstance(getString(R.string.already_latest_version), ToastDialog.MODE_CONFIRM);
                    toastDialog.show(getChildFragmentManager(), ToastDialog.TAG);
                }
            });
        }
    }
}
