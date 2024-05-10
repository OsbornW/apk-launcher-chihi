package com.soya.launcher.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.WifiListAdapter;
import com.soya.launcher.bean.WifiItem;
import com.soya.launcher.ui.dialog.ProgressDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.ui.dialog.WifiPassDialog;
import com.soya.launcher.ui.dialog.WifiSaveDialog;
import com.soya.launcher.utils.AndroidSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbsWifiListFragment extends AbsFragment {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    private View mWifiEnableView;
    private VerticalGridView mContentGrid;
    private TextView mWifiNameView;
    private TextView mNetwordConnectedView;
    private TextView mIPView;
    private TextView mSignalView;
    private Switch mWifiSwitch;
    private View mOffView;
    private View mProgressBar;
    private View mNextView;
    private View mLoopProgress;

    private WifiManager mWifiManager;

    private WifiListAdapter mAdapter;
    private WifiReceiver receiver;

    private ProgressDialog mDialog;
    private Handler uiHandler;
    private ScanResult mTarget;
    private long connectedTime = -1;
    private TextView tvConnectedName;
    //private TextView tvConnectStatus;
    private ImageView ivConectedSignal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiManager = getActivity().getSystemService(WifiManager.class);
        mDialog = ProgressDialog.newInstance();
        uiHandler = new Handler();

        receiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exec.shutdownNow();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wifi_list;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);

        tvConnectedName = view.findViewById(R.id.tv_connected_wifiname);
        //tvConnectStatus = view.findViewById(R.id.tv_connected_status);
        ivConectedSignal = view.findViewById(R.id.tv_connected_signal);

        mWifiEnableView = view.findViewById(R.id.wifi_enable);
        mContentGrid = view.findViewById(R.id.content);
        mWifiNameView = view.findViewById(R.id.wifi_name);
        mNetwordConnectedView = view.findViewById(R.id.netword_connected_mes);
        mIPView = view.findViewById(R.id.ip);
        mSignalView = view.findViewById(R.id.signal);
        mWifiSwitch = view.findViewById(R.id.switch_item);
        mOffView = view.findViewById(R.id.off);
        mProgressBar = view.findViewById(R.id.progressBar);
        mNextView = view.findViewById(R.id.next);
        mLoopProgress = view.findViewById(R.id.loop);

        mAdapter = new WifiListAdapter(getActivity(), inflater, new CopyOnWriteArrayList<>(), useNext(), newCallback());
        mAdapter.replace(fillterWifi(mWifiManager.getScanResults()));
        mOffView.setVisibility(mWifiManager.isWifiEnabled() ? View.GONE : View.VISIBLE);
        mContentGrid.setVisibility(mWifiManager.isWifiEnabled() ? View.VISIBLE : View.GONE);
        if (mWifiManager.isWifiEnabled())
            mProgressBar.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        if (useNext()) {
            mWifiEnableView.setNextFocusLeftId(R.id.next);
            mWifiEnableView.setNextFocusRightId(R.id.next);
        }
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mWifiEnableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnable = !mWifiManager.isWifiEnabled();
                mWifiManager.setWifiEnabled(isEnable);
                mWifiSwitch.setChecked(isEnable);
                mOffView.setVisibility(isEnable ? View.GONE : View.VISIBLE);
                mProgressBar.setVisibility(isEnable && mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                mContentGrid.setVisibility(isEnable ? View.VISIBLE : View.GONE);
            }
        });

        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, ChooseWallpaperFragment.newInstance()).addToBackStack(null).commit();
            }
        });

        mDialog.setCallback(new ProgressDialog.Callback() {
            @Override
            public void onDismiss() {
                connectedTime = -1;
            }
        });
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mContentGrid.setAdapter(mAdapter);
        mWifiSwitch.setChecked(mWifiManager.isWifiEnabled());

        exec.execute(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                while (isAdded()) {
                    final WifiInfo info = mWifiManager.getConnectionInfo();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) return;

                            if (mTarget != null && connectedTime != -1 && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - connectedTime) >= 30) {
                                toastFail();
                                closeDialog();
                                mTarget = null;
                                connectedTime = -1;
                            }

                            if (!TextUtils.isEmpty(info.getSSID()) && info.getIpAddress() != 0 && !info.getSSID().equals("<unknown ssid>")) {
                                /*if (mTarget != null && connectedTime != -1 && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - connectedTime) >= 8 && !mTarget.SSID.equals(cleanSSID(info.getSSID()))){
                                    toastFail();
                                    closeDialog();
                                    mTarget = null;
                                    connectedTime = -1;
                                }*/

                                if (mTarget != null && connectedTime != -1 && mTarget.SSID.equals(cleanSSID(info.getSSID()))) {
                                    closeDialog();
                                    mTarget = null;
                                    connectedTime = -1;
                                }

                                if (mTarget == null && connectedTime == -1) {
                                    closeDialog();
                                    mTarget = null;
                                    connectedTime = -1;
                                }
                            }

                            String old = mAdapter.connectSSID;
                            String ssid = cleanSSID(info.getSSID());
                            mAdapter.connectSSID = ssid;
                            for (WifiItem item : mAdapter.getDataList()) {
                                if (item.getItem().SSID.equals(ssid)) {
                                    int index = mAdapter.getDataList().indexOf(item);
                                    if (index != -1) {
                                        mAdapter.notifyItemChanged(index);
                                    }
                                }

                                if (item.getItem().SSID.equals(old)) {
                                    int index = mAdapter.getDataList().indexOf(item);
                                    if (index != -1) {
                                        mAdapter.notifyItemChanged(index);
                                    }
                                }
                            }
                            syncWifi(info);
                        }
                    });
                    if (time++ % 5 == 0) mWifiManager.startScan();
                    SystemClock.sleep(1000);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mWifiEnableView);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void toastFail() {
        if (!isAdded()) return;
        ToastDialog dialog = ToastDialog.newInstance(getString(R.string.unable_to_wifi, mTarget.SSID), ToastDialog.MODE_CONFIRM);
        dialog.show(getChildFragmentManager(), ToastDialog.TAG);
    }

    private void syncWifi(WifiInfo info) {
        int level = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        String levelStr = getString(R.string.signal_low);
        switch (level) {
            case 1:
            case 2:
                levelStr = getString(R.string.signal_low);
                break;
            case 3:
                levelStr = getString(R.string.signal_middle);
                break;
            default:
                levelStr = getString(R.string.signal_strong);
        }
        boolean isConnected = !TextUtils.isEmpty(info.getSSID()) && info.getIpAddress() != 0;
        mWifiNameView.setText(isConnected ? getNoDoubleQuotationSSID(info.getSSID()) : "-  -  -");
        mIPView.setText(isConnected ? Formatter.formatIpAddress(info.getIpAddress()) : "-");
        mSignalView.setText(isConnected ? levelStr : "-");
        mNetwordConnectedView.setText(isConnected ? getString(R.string.connected) : "-");

        tvConnectedName.setText(getNoDoubleQuotationSSID(info.getSSID()));

        if (level == 1 || level == 2) {
            ivConectedSignal.setImageResource(R.drawable.baseline_wifi_1_bar_100);
        } else if (level == 3) {
            ivConectedSignal.setImageResource(R.drawable.baseline_wifi_2_bar_100);
        } else {
            ivConectedSignal.setImageResource(R.drawable.baseline_wifi_100);
        }
    }

    public String getNoDoubleQuotationSSID(String ssid) {

        //获取Android版本号
        int deviceVersion = Build.VERSION.SDK_INT;

        if (deviceVersion >= 17) {

            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {

                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }
        return ssid;
    }

    private String cleanSSID(String ssid) {
        return ssid.replaceFirst("^\"", "").replaceFirst("\"$", "");
    }

    private WifiListAdapter.Callback newCallback() {
        return new WifiListAdapter.Callback() {
            @Override
            public void onClick(WifiItem wifiItem) {
                ScanResult bean = wifiItem.getItem();
                boolean usePass = AndroidSystem.isUsePassWifi(bean);

                List<WifiConfiguration> saves = mWifiManager.getConfiguredNetworks();
                Map<String, WifiConfiguration> map = new HashMap<>();
                for (WifiConfiguration item : saves) {
                    String ssd = cleanSSID(item.SSID);
                    map.put(ssd, item);
                }
                WifiConfiguration item = map.get(bean.SSID);
                boolean isSave = item != null;

                if (isSave) {
                    WifiSaveDialog dialog = WifiSaveDialog.newInstance(bean.SSID);
                    dialog.setCallback(new WifiSaveDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            int index = mAdapter.getDataList().indexOf(wifiItem);
                            switch (type) {
                                case 0:
                                    connect(bean, item.networkId);
                                    wifiItem.setSave(true);
                                    break;
                                case -1:
                                    mWifiManager.removeNetwork(item.networkId);
                                    wifiItem.setSave(false);
                                    break;
                            }
                            if (index != -1) mAdapter.notifyItemChanged(index);
                        }
                    });
                    dialog.show(getChildFragmentManager(), WifiSaveDialog.TAG);
                } else if (usePass) {
                    WifiPassDialog dialog = WifiPassDialog.newInstance();
                    dialog.setCallback(new WifiPassDialog.Callback() {
                        @Override
                        public void onConfirm(String text) {
                            wifiItem.setSave(true);
                            int index = mAdapter.getDataList().indexOf(wifiItem);
                            if (index != -1) mAdapter.notifyItemChanged(index);
                            connect(bean, text);
                        }
                    });
                    dialog.show(getChildFragmentManager(), WifiPassDialog.TAG);
                } else {
                    wifiItem.setSave(true);
                    int index = mAdapter.getDataList().indexOf(wifiItem);
                    if (index != -1) mAdapter.notifyItemChanged(index);
                    connect(bean, "");
                }
            }
        };
    }

    private void showDialog() {
        mLoopProgress.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.loop_translate);
        mLoopProgress.startAnimation(animation);
        //mDialog.show(getChildFragmentManager(), ProgressDialog.TAG);
    }

    private void closeDialog() {
        mLoopProgress.setVisibility(View.GONE);
        if (mLoopProgress.getAnimation() != null) mLoopProgress.getAnimation().cancel();
        //if (mDialog.isAdded() && mDialog.isVisible()) mDialog.dismiss();
    }

    private void connect(ScanResult bean, int networkId) {
        target(bean);
        mWifiManager.enableNetwork(networkId, true);
        showDialog();
    }

    private void connect(ScanResult bean, String password) {
        target(bean);
        WifiConfiguration configuration = newWifiConfiguration(bean, password);
        int id = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(id, true);
        showDialog();
    }

    private void target(ScanResult bean) {
        mTarget = bean;
        connectedTime = System.currentTimeMillis();
    }

    protected boolean useNext() {
        return true;
    }

    private void availableAction(Intent intent) {
        boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
        List<ScanResult> results = mWifiManager.getScanResults();
        List<WifiItem> items = fillterWifi(results);

        List<WifiItem> adds = new ArrayList<>();
        List<WifiItem> removes = new ArrayList<>();

        Map<String, WifiItem> resultSet = new HashMap<>();
        Map<String, WifiItem> sourceSet = new HashMap<>();
        for (WifiItem result : items) resultSet.put(result.getItem().SSID, result);
        for (WifiItem item : mAdapter.getDataList()) sourceSet.put(item.getItem().SSID, item);

        for (Map.Entry<String, WifiItem> entry : resultSet.entrySet()) {
            if (!sourceSet.containsKey(entry.getKey())) adds.add(entry.getValue());
        }

        for (Map.Entry<String, WifiItem> entry : sourceSet.entrySet()) {
            if (!resultSet.containsKey(entry.getKey())) {
                removes.add(entry.getValue());
            }
        }

        for (Map.Entry<String, WifiItem> entry : resultSet.entrySet()) {
            if (sourceSet.containsKey(entry.getKey())) {
                sourceSet.get(entry.getKey()).setItem(entry.getValue().getItem());
            }
        }

        List<WifiItem> a = new ArrayList<>();
        List<WifiItem> b = new ArrayList<>();
        for (WifiItem item : adds) {
            if (WifiManager.calculateSignalLevel(item.getItem().level, 5) >= 4) {
                a.add(item);
            } else {
                b.add(item);
            }
        }

        mAdapter.remove(removes);
        mAdapter.add(0, a);
        mAdapter.add(mAdapter.getItemCount(), b);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
        mProgressBar.setVisibility(mAdapter.getItemCount() == 0 && mOffView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("MissingPermission")
    private List<WifiItem> fillterWifi(List<ScanResult> results) {
        List<WifiItem> list = new ArrayList<>();
        List<WifiConfiguration> saves = mWifiManager.getConfiguredNetworks();

        Map<String, WifiConfiguration> map = new HashMap<>();
        for (WifiConfiguration item : saves) map.put(cleanSSID(item.SSID), item);

        for (ScanResult result : results) {
            if (!TextUtils.isEmpty(result.SSID)) {
                WifiConfiguration item = map.get(result.SSID);
                boolean isSave = item != null;
                list.add(new WifiItem(result, isSave));
            }
        }
        Collections.sort(list, new Comparator<WifiItem>() {
            @Override
            public int compare(WifiItem o1, WifiItem o2) {
                return WifiManager.calculateSignalLevel(o1.getItem().level, 5) > WifiManager.calculateSignalLevel(o2.getItem().level, 5) ? -1 : 1;
            }
        });
        return list;
    }

    private WifiConfiguration newWifiConfiguration(ScanResult result, String password) {
        WifiConfiguration configuration = new WifiConfiguration();

        if (AndroidSystem.isUsePassWifi(result)) {
            configuration.SSID = "\"" + result.SSID + "\"";
            configuration.preSharedKey = "\"" + password + "\"";
        } else {
            configuration.SSID = "\"" + result.SSID + "\"";
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return configuration;
    }

    public void showNext(boolean show) {
        mNextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    availableAction(intent);
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    break;
            }
        }
    }
}
