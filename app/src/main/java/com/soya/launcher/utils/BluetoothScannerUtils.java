package com.soya.launcher.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.soya.launcher.bean.MyRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothScannerUtils {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();
    private static final Map<Context, Inner> MAP = new ConcurrentHashMap<>();
    private static MyRunnable scanRunnable;
    private static BluetoothProfile bluetoothProfile;

    public static void init(Context context){
        if (BLUETOOTH_ADAPTER == null) return;
        registProfileProxy(context);
    }

    public static void startListening(Context context, Listener listener){
        if (BLUETOOTH_ADAPTER == null) return;
        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filter);
        MAP.put(context, new Inner(context, receiver, listener));
        listener.onStartScan(BLUETOOTH_ADAPTER);
        if (scanRunnable == null){
            scanRunnable = new MyRunnable() {
                @Override
                public void run() {
                    while (!isInterrupt()){
                        if (MAP.isEmpty()) continue;
                        scanNow();
                        SystemClock.sleep(20 * 1000);
                    }
                }
            };
            EXECUTOR_SERVICE.execute(scanRunnable);
        }
    }

    public static void removeListener(Context context){
        if (MAP.containsKey(context)){
            Inner inner = MAP.get(context);
            context.unregisterReceiver(inner.receiver);
            MAP.remove(context);
        }
    }

    private static void registProfileProxy(Context context){
        if (BLUETOOTH_ADAPTER == null) return;
        BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
                    @Override
                    public void onServiceDisconnected(int profile) {
                    }
                    @Override
                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
                        bluetoothProfile = proxy;
                    }
                };

        BLUETOOTH_ADAPTER.getProfileProxy(context, serviceListener, 4);
    }

    public static BluetoothProfile getProfileProxy(Context context){
        return bluetoothProfile;
    }

    public static class Listener{
        public void onStartScan(BluetoothAdapter adapter){};
        public void onFound(BluetoothAdapter adapter, List<BluetoothDevice> devices){};
        public void onRemove(BluetoothAdapter adapter, List<BluetoothDevice> devices){};
    }

    public static final class Inner{
        private Context context;
        private Receiver receiver;
        private Listener listener;
        private BluetoothProfile profile;

        public Inner(Context context, Receiver receiver, Listener listener){
            this.context = context;
            this.receiver = receiver;
            this.listener = listener;
        }

        public void setProfile(BluetoothProfile profile) {
            this.profile = profile;
        }

        public BluetoothProfile getProfile() {
            return profile;
        }
    }

    public static final class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    for (Map.Entry<Context, Inner> entry : MAP.entrySet()){
                        entry.getValue().listener.onFound(BLUETOOTH_ADAPTER, Arrays.asList(device));
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    break;
            }
        }
    }

    public static void scanNow() {
        if (BLUETOOTH_ADAPTER == null) return;
        if (BLUETOOTH_ADAPTER.isDiscovering()) {
            BLUETOOTH_ADAPTER.cancelDiscovery();
        }
        BLUETOOTH_ADAPTER.startDiscovery();
    }

    public static void scanStop(){
        if (BLUETOOTH_ADAPTER == null) return;
        BLUETOOTH_ADAPTER.cancelDiscovery();
    }
}
