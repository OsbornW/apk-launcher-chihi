package com.soya.launcher.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.BluetoothItemAdapter;
import com.soya.launcher.bean.BluetoothItem;
import com.soya.launcher.utils.BluetoothScannerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothFragment extends AbsFragment implements BluetoothItemAdapter.Callback {

    public static BluetoothFragment newInstance() {

        Bundle args = new Bundle();

        BluetoothFragment fragment = new BluetoothFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BluetoothItemAdapter mAdapter;
    private VerticalGridView mContentGrid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothScannerUtils.startListening(getActivity(), new BluetoothScannerUtils.Listener(){
            @Override
            public void onFound(BluetoothAdapter adapter, List<BluetoothDevice> devices) {
                super.onFound(adapter, devices);

                Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
                Map<String, BluetoothDevice> map = new HashMap<>();
                for (BluetoothDevice device : deviceSet){
                    if (!TextUtils.isEmpty(device.getName())) map.put(device.getName(), device);
                }
                for (BluetoothDevice device : devices){
                    if (!TextUtils.isEmpty(device.getName())) map.put(device.getName(), device);
                }

                Map<String, BluetoothItem> strings = new HashMap<>();
                for (BluetoothItem item : mAdapter.getDataList()) strings.put(item.getDevice().getName(), item);
                List<BluetoothItem> items = new ArrayList<>();

                for (Map.Entry<String, BluetoothDevice> entry : map.entrySet()){
                    if (!strings.containsKey(entry.getKey()))
                        items.add(new BluetoothItem(entry.getValue()));
                    else
                        strings.get(entry.getKey()).setDevice(entry.getValue());

                }
                mAdapter.add(items);
                mAdapter.notifyItemChanged(0, mAdapter.getItemCount());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothScannerUtils.removeListener(getActivity());
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);

        mAdapter = new BluetoothItemAdapter(getActivity(), inflater, new ArrayList<>(), this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mContentGrid.setAdapter(mAdapter);
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    public void onClick(BluetoothItem bean) {
        //AndroidSystem.connect(bean.getDevice());
    }
}
