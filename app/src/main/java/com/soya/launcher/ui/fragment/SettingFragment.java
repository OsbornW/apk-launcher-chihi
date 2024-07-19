package com.soya.launcher.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.BuildConfig;
import com.soya.launcher.ConstantsKt;
import com.soya.launcher.R;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.config.Config;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.ext.AndroidExtKt;
import com.soya.launcher.ext.FlavoursUtilsKt;
import com.soya.launcher.handler.PermissionHandler;
import com.soya.launcher.ui.activity.AboutActivity;
import com.soya.launcher.ui.activity.LanguageActivity;
import com.soya.launcher.ui.activity.ProjectorActivity;
import com.soya.launcher.ui.activity.RemoteControlActivity;
import com.soya.launcher.ui.activity.SetDateActivity;
import com.soya.launcher.ui.activity.WallpaperActivity;
import com.soya.launcher.ui.activity.WifiListActivity;
import com.soya.launcher.utils.AndroidSystem;

import java.util.Arrays;

public class SettingFragment extends AbsFragment {

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private VerticalGridView mContentGrid;
    private TextView mTitleView;
    private ActivityResultLauncher launcher;

    private WallpaperReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLauncher();

        receiver = new WallpaperReceiver();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(IntentAction.ACTION_UPDATE_WALLPAPER);
        getActivity().registerReceiver(receiver, filter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mTitleView = view.findViewById(R.id.title);

        mTitleView.setText(getString(R.string.setting));
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        setContent(view, inflater);
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

    private void initLauncher() {
        launcher = PermissionHandler.createPermissionsWithIntent(this, new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

            }
        });
    }

    private void setContent(View view, LayoutInflater inflater) {
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), inflater, newCallback(), R.layout.holder_setting));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_LARGE, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(4);

        if (Config.COMPANY == 0) {
            arrayObjectAdapter.addAll(0, Arrays.asList(new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(10, getString(R.string.bluetooth), R.drawable.baseline_bluetooth_100),
                    new SettingItem(2, getString(R.string.pojector), R.drawable.baseline_cast_connected_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    new SettingItem(4, getString(R.string.date), R.drawable.baseline_calendar_month_100),
                    new SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        }else if (Config.COMPANY==9){
            arrayObjectAdapter.addAll(0, Arrays.asList(new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(10, getString(R.string.bluetooth), R.drawable.baseline_bluetooth_100),
                    new SettingItem(2, getString(R.string.pojector), R.drawable.baseline_cast_connected_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    new SettingItem(4, getString(R.string.date), R.drawable.baseline_calendar_month_100),
                    new SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        } else if (Config.COMPANY == 1) {
            arrayObjectAdapter.addAll(0, Arrays.asList(new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(2, getString(R.string.pojector), R.drawable.baseline_cast_connected_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    new SettingItem(4, getString(R.string.date), R.drawable.baseline_calendar_month_100),
                    new SettingItem(5, getString(R.string.bluetooth), R.drawable.baseline_bluetooth_100),
                    new SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        } else if (Config.COMPANY == 2) {
            arrayObjectAdapter.addAll(0, Arrays.asList(
                    new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(8, getString(R.string.sound), R.drawable.baseline_settings_voice_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    new SettingItem(4, getString(R.string.date), R.drawable.baseline_calendar_month_100),
                    new SettingItem(5, getString(R.string.bluetooth), R.drawable.baseline_bluetooth_100),
                    new SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        } else if (Config.COMPANY==5) {
            arrayObjectAdapter.addAll(0, Arrays.asList(
                    new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        } else {
            arrayObjectAdapter.addAll(0, Arrays.asList(new SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    new SettingItem(8, getString(R.string.sound), R.drawable.baseline_settings_voice_100),
                    new SettingItem(1, getString(R.string.wallpaper), R.drawable.baseline_wallpaper_100),
                    new SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    new SettingItem(4, getString(R.string.date), R.drawable.baseline_calendar_month_100),
                    new SettingItem(9, getString(R.string.keyboard), R.drawable.baseline_keyboard_100),
                    new SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    new SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)));
        }
    }

    public SettingAdapter.Callback newCallback() {
        return new SettingAdapter.Callback() {
            @Override
            public void onSelect(boolean selected, SettingItem bean) {

            }

            @Override
            public void onClick(SettingItem bean) {
                switch (bean.getType()) {
                    case 0:
                        if (Config.COMPANY == 3) {
                            AndroidSystem.openWifiSetting(getActivity());
                        } else {
                            startActivity(new Intent(getActivity(), WifiListActivity.class));
                        }
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), WallpaperActivity.class));
                        break;
                    case 2:
                        if (Config.COMPANY == 0) {
                            startActivity(new Intent(getActivity(), ProjectorActivity.class));
                        } else if (Config.COMPANY == 1) {
                            AndroidSystem.openActivityName(getActivity(), "com.qf.keystone.AllActivity");
                        }
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), LanguageActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(), SetDateActivity.class));
                        break;
                    case 5:
                        if (Config.COMPANY == 1 || Config.COMPANY == 2) {
                            AndroidSystem.openBluetoothSetting2(getActivity());
                        } else if (Config.COMPANY == 3) {
                            AndroidSystem.openBluetoothSetting3(getActivity());
                        }
                        //startActivity(new Intent(getActivity(), BluetoothActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getActivity(), AboutActivity.class));
                        break;
                    case 7:
                        if (Config.COMPANY == 0 || Config.COMPANY == 1) {
                            AndroidSystem.openSystemSetting(getActivity());
                        } else {
                            AndroidSystem.openSystemSetting2(getActivity());
                        }
                        break;
                    case 8:
                        AndroidSystem.openVoiceSetting(getActivity());
                        break;
                    case 9:
                        AndroidSystem.openInputSetting(getActivity());
                        break;
                    case 10:
                        if(Config.COMPANY==9){
                            AndroidExtKt.openBluetoothSettings(getContext());
                        }else {
                            AndroidSystem.openBluetoothSetting4(getContext());
                        }

                        //startActivity(new Intent(getActivity(), RemoteControlActivity.class));
                        break;
                }
            }
        };
    }

    public class WallpaperReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case IntentAction.ACTION_UPDATE_WALLPAPER:
                    updateWallpaper();
                    break;
            }
        }
    }
}
