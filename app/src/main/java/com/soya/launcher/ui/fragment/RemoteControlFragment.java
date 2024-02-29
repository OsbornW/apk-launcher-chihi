package com.soya.launcher.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.open.system.SystemUtils;
import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.bean.BluetoothItem;
import com.soya.launcher.bean.MyRunnable;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.BluetoothScannerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteControlFragment extends AbsFragment implements View.OnClickListener {

    public static RemoteControlFragment newInstance(boolean useControl) {

        Bundle args = new Bundle();
        args.putBoolean(Atts.SHOW_BOTTOM, useControl);
        RemoteControlFragment fragment = new RemoteControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final Map<String, BluetoothDevice> deviceMap = new ConcurrentHashMap<>();
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private BluetoothAdapter mBluetoothAdapter;
    private MyRunnable runnable;
    private MyRunnable connRunnable;
    private View mNextView;
    private ImageView mConnectedIconView;
    private View mProgressView;
    private TextView mContentMessageView;
    private BluetoothDevice mCacheDevice;
    private Handler uiHandler;
    private boolean isConnected = false;
    private boolean isConnecting = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().sendBroadcast(new Intent(IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG));
        uiHandler = new Handler();
        AndroidSystem.setEnableBluetooth(getActivity(), true);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothScannerUtils.startListening(getActivity(), new BluetoothScannerUtils.Listener(){
            @Override
            public void onFound(BluetoothAdapter adapter, List<BluetoothDevice> devices) {
                super.onFound(adapter, devices);
                BluetoothDevice device = devices.get(0);
                if (isRemote(device.getName()) && !SystemUtils.isConnected(device) && !isConnecting){
                    isConnecting = true;
                    mCacheDevice = device;
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_remote_control;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().sendBroadcast(new Intent(IntentAction.ACTION_SHOW_REMOTE_DIALOG));
        if (connRunnable != null) connRunnable.interrupt();
        if (runnable != null) runnable.interrupt();
        BluetoothScannerUtils.removeListener(getActivity());
        exec.shutdownNow();
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mNextView = view.findViewById(R.id.next);
        mConnectedIconView = view.findViewById(R.id.connected_icon);
        mContentMessageView = view.findViewById(R.id.search_message);
        mProgressView = view.findViewById(R.id.progressBar);

        mNextView.setVisibility(getArguments().getBoolean(Atts.SHOW_BOTTOM, true) ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mNextView.setOnClickListener(this::onClick);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        check();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mNextView);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextView)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideLanguageFragment.newInstance()).addToBackStack(null).commit();
        }
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void check(){
        if (mBluetoothAdapter == null) return;
        if (runnable != null) runnable.interrupt();
        runnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    if (!isAdded()) return;

                    String name = "";
                    for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()){
                        if (!device.getBluetoothClass().doesClassMatch(BluetoothClass.PROFILE_HID) || !isRemote(device.getName())) continue;
                        isConnected = SystemUtils.isConnected(device);
                        name = device.getName();
                        break;
                    }

                    if (isConnected) isConnecting = false;
                    if (mCacheDevice != null){
                        BluetoothProfile profile = BluetoothScannerUtils.getProfileProxy(getActivity());
                        if (profile != null){
                            name = mCacheDevice.getName();
                            syncIcon(1, name);
                            boolean success = AndroidSystem.connect(getActivity(), profile, mCacheDevice);
                            mCacheDevice = null;
                            isConnecting = false;
                            if (!success) {
                                syncIcon(2, name);
                                SystemClock.sleep(3000);
                            }else {
                                SystemClock.sleep(1000);
                                continue;
                            }
                        }
                    }
                    syncIcon(isConnected ? 3 : 0, name);
                    SystemClock.sleep(300);
                }
            }
        };
        exec.execute(runnable);
    }

    private boolean isRemote(String name){
        return "BlueX-Remote1".equals(name);
    }

    public void syncIcon(int connectedType, String name){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                //mConnectedIconView.setImageResource(connected ? R.drawable.baseline_bluetooth_connected_100 : R.drawable.baseline_bluetooth_searching_100);
                mProgressView.setVisibility(connectedType == 0 ? View.VISIBLE : View.GONE);
                if (connectedType == 1){
                    mContentMessageView.setText(getString(R.string.pairing_mark, name));
                }else if (connectedType == 2){
                    mContentMessageView.setText(getString(R.string.pairing_fail, name));
                }else if (connectedType == 3){
                    mContentMessageView.setText(getString(R.string.pairing_success, name));
                }else {
                    mContentMessageView.setText("");
                }
                //if (isConnecting) mConnectedIconView.setImageResource(R.drawable.baseline_router_100);
            }
        });
    }

    private void connectDevice(final BluetoothDevice device){
        if (isConnecting) return;
        isConnecting = true;
        if (connRunnable != null) connRunnable.interrupt();
        connRunnable = new MyRunnable() {
            @Override
            public void run() {
                try {
                    int count = 0;
                    //AndroidSystem.connect(getActivity(), device);
                    while (!isInterrupt()){
                        SystemClock.sleep(300);
                        if (SystemUtils.isConnected(device) || count ++ >= 10){
                            return;
                        }
                    }
                }finally {
                    isConnecting = false;
                }
            }
        };
        exec.execute(connRunnable);
    }
}
