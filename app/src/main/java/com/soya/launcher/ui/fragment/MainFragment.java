package com.soya.launcher.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.open.system.SystemUtils;
import com.soya.launcher.App;
import com.soya.launcher.BuildConfig;
import com.soya.launcher.R;
import com.soya.launcher.adapter.AdsAdapter;
import com.soya.launcher.adapter.AppListAdapter;
import com.soya.launcher.adapter.MainContentAdapter;
import com.soya.launcher.adapter.MainHeaderAdapter;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.adapter.StoreAdapter;
import com.soya.launcher.bean.Ads;
import com.soya.launcher.bean.AppInfo;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.HomeItem;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MoviceData;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.MyRunnable;
import com.soya.launcher.bean.Projector;
import com.soya.launcher.bean.PushApp;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.bean.Version;
import com.soya.launcher.bean.WeatherData;
import com.soya.launcher.config.Config;
import com.soya.launcher.decoration.HSlideMarginDecoration;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.enums.Types;
import com.soya.launcher.http.AppServiceRequest;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.http.Url;
import com.soya.launcher.http.response.AppListResponse;
import com.soya.launcher.http.response.HomeResponse;
import com.soya.launcher.http.response.VersionResponse;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.ui.activity.AboutActivity;
import com.soya.launcher.ui.activity.AppsActivity;
import com.soya.launcher.ui.activity.GradientActivity;
import com.soya.launcher.ui.activity.LoginActivity;
import com.soya.launcher.ui.activity.MoviceListActivity;
import com.soya.launcher.ui.activity.ScaleScreenActivity;
import com.soya.launcher.ui.activity.SearchActivity;
import com.soya.launcher.ui.activity.SettingActivity;
import com.soya.launcher.ui.activity.WeatherActivity;
import com.soya.launcher.ui.activity.WifiListActivity;
import com.soya.launcher.ui.dialog.AppDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.AppUtils;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.PreferencesUtils;
import com.soya.launcher.utils.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import retrofit2.Call;

public class MainFragment extends AbsFragment implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final int MAX_WEATHER_TIME = 90 * 1000;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private HorizontalGridView mHeaderGrid;
    private HorizontalGridView mHorizontalContentGrid;
    private VerticalGridView mVerticalContentGrid;
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBarLayout;
    private View mRootView;
    private View mSettingView;
    private ImageView mWeatherView;
    private View mSearchView;
    private ImageView mWifiView;
    private View mLoginView;
    private TextView mTimeView;
    private TextView mSegmentView;
    private View mHelpView;
    private TextView mTestView;

    private View mBluetoothView;
    private View mSdCardView;
    private View mAPView;

    private Handler uiHandler;
    private String uuid;
    private Call call;
    private InnerReceiver receiver;
    private WallpaperReceiver wallpaperReceiver;
    private float maxVerticalOffset;
    private final List<TypeItem> items = new ArrayList<>();
    private final List<TypeItem> targetMenus = new ArrayList<>();
    private MyRunnable timeRunnable;
    private boolean isConnectFirst = false;
    private boolean isNetworkAvailable;
    private long lastWeatherTime = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxVerticalOffset = getResources().getDimension(R.dimen.until_collapsed_height);
        uiHandler = new Handler();
        receiver = new InnerReceiver();
        wallpaperReceiver = new WallpaperReceiver();

        items.addAll(Arrays.asList(new TypeItem[]{
                new TypeItem(getString(R.string.app_store), R.drawable.store, 0, Types.TYPE_APP_STORE, TypeItem.TYPE_ICON_IMAGE_RES, TypeItem.TYPE_LAYOUT_STYLE_UNKNOW),
                new TypeItem(getString(R.string.apps), R.drawable.app_list, 0, Types.TYPE_MY_APPS, TypeItem.TYPE_ICON_IMAGE_RES, TypeItem.TYPE_LAYOUT_STYLE_UNKNOW),
        }));

        if (Config.COMPANY == 0){
            items.add(new TypeItem(getString(R.string.pojector), R.drawable.projector, 0, Types.TYPE_PROJECTOR, TypeItem.TYPE_ICON_IMAGE_RES, TypeItem.TYPE_LAYOUT_STYLE_UNKNOW));
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(receiver, filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(IntentAction.ACTION_UPDATE_WALLPAPER);
        filter1.addAction(Intent.ACTION_SCREEN_ON);
        filter1.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction("android.hardware.usb.action.USB_STATE");
        getActivity().registerReceiver(wallpaperReceiver, filter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(wallpaperReceiver);
        exec.shutdownNow();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLoopTime();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoopTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        syncWeather();
        syncTime();
        syncNotify();
        startLoopTime();
    }

    @Override
    public void onStart() {
        super.onStart();
        syncWeather();
        syncTime();
        syncNotify();
        startLoopTime();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mHeaderGrid, 350);
        checkVersion();
        uidPull();
    }

    @Override
    protected void init(View view, LayoutInflater inflater){
        super.init(view, inflater);
        mHeaderGrid = view.findViewById(R.id.header);
        mHorizontalContentGrid = view.findViewById(R.id.horizontal_content);
        mVerticalContentGrid = view.findViewById(R.id.vertical_content);
        mAppBarLayout = view.findViewById(R.id.app_bar);
        mRootView = view.findViewById(R.id.root);
        mSettingView = view.findViewById(R.id.setting);
        mWeatherView = view.findViewById(R.id.weather);
        mSearchView = view.findViewById(R.id.search);
        mWifiView = view.findViewById(R.id.wifi);
        mLoginView = view.findViewById(R.id.login);
        mRecyclerView = view.findViewById(R.id.recycler);
        mSegmentView = view.findViewById(R.id.loop_segment);
        mTimeView = view.findViewById(R.id.loop_time);
        mHelpView = view.findViewById(R.id.help);
        mTestView = view.findViewById(R.id.test);
        mBluetoothView = view.findViewById(R.id.bluetooth);
        mSdCardView = view.findViewById(R.id.sd_card);
        mAPView = view.findViewById(R.id.ap);

        mHorizontalContentGrid.addItemDecoration(new HSlideMarginDecoration(getResources().getDimension(R.dimen.margin_decoration_max), getResources().getDimension(R.dimen.margin_decoration_min)));
        mHeaderGrid.addItemDecoration(new HSlideMarginDecoration(getResources().getDimension(R.dimen.margin_decoration_max), getResources().getDimension(R.dimen.margin_decoration_min)));

        mHeaderGrid.setPivotY(maxVerticalOffset);
        mTestView.setText("CHIHI Test Version: "+BuildConfig.VERSION_NAME);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mAppBarLayout.addOnOffsetChangedListener(this);

        mSettingView.setOnClickListener(this);
        mWeatherView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mWifiView.setOnClickListener(this);
        mLoginView.setOnClickListener(this);
        mHelpView.setOnClickListener(this);
        mBluetoothView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater){
        super.initBind(view, inflater);
        fillHeader();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void uidPull(){
        if (!Config.IS_TEST) HttpRequest.uidPull(AppInfo.newInfo(getActivity()));
    }

    private void setHeader(List<TypeItem> items){
        targetMenus.clear();
        targetMenus.addAll(items);
        MainHeaderAdapter adapter = new MainHeaderAdapter(getActivity(), getLayoutInflater(), items, newHeaderCallback());
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(adapter);
        adapter.setAdapter(arrayObjectAdapter);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_LARGE, false);
        mHeaderGrid.setAdapter(itemBridgeAdapter);
        arrayObjectAdapter.addAll(0, items);
        mHeaderGrid.setSelectedPosition(0);
    }

    private void setMoviceContent(List<Movice> list, int direction, int columns, int layoutId){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new MainContentAdapter(getActivity(), getLayoutInflater(), layoutId, newContentCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_SMALL, false);
        switch (direction){
            case 1:
                mHorizontalContentGrid.setAdapter(itemBridgeAdapter);
                mVerticalContentGrid.setVisibility(View.GONE);
                mHorizontalContentGrid.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                break;
            case 0:
                if (list.size() > columns * 2){
                    list = list.subList(0, columns * 2);
                }
                mVerticalContentGrid.setAdapter(itemBridgeAdapter);
                mVerticalContentGrid.setVisibility(View.VISIBLE);
                mHorizontalContentGrid.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mVerticalContentGrid.setNumColumns(columns);
                mVerticalContentGrid.setVerticalSpacing((int) getResources().getDimension(R.dimen.main_page_vertical_spacing));
                break;
        }

        arrayObjectAdapter.addAll(0, list);
    }

    private void stopLoopTime(){
        if (timeRunnable != null) timeRunnable.interrupt();
        timeRunnable = null;
    }

    private void startLoopTime(){
        if (timeRunnable != null) return;
        timeRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    SystemClock.sleep(2000);
                    syncNotify();
                    if (lastWeatherTime <= 0 || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastWeatherTime) > MAX_WEATHER_TIME){
                        HttpRequest.getCityWeather(newWeatherCallback(), PreferencesManager.getCityName());
                        lastWeatherTime = System.currentTimeMillis();
                    }
                }
            }
        };
        exec.execute(timeRunnable);
    }

    private void syncNotify(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                syncTime();
                boolean old = isNetworkAvailable;
                isNetworkAvailable = AndroidSystem.isNetworkAvailable(getActivity());
                if (isNetworkAvailable != old && isNetworkAvailable) uidPull();
                if (AndroidSystem.isEthernetConnected(getActivity())){
                    mWifiView.setImageResource(R.drawable.baseline_lan_100);
                }else {
                    mWifiView.setImageResource(isNetworkAvailable ? R.drawable.baseline_wifi_100 : R.drawable.baseline_wifi_off_100);
                }
                mBluetoothView.setVisibility(bluetoothAdapter != null && bluetoothAdapter.isEnabled() ? View.VISIBLE : View.GONE);
                HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getActivity().getSystemService(Context.USB_SERVICE)).getDeviceList();
                mSdCardView.setVisibility(!deviceHashMap.isEmpty() ? View.VISIBLE : View.GONE);
                mAPView.setVisibility(SystemUtils.isApEnable(getActivity()) ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean isKeyBoard(InputDevice device) {
        int sourceMask = device.getSources();
        return (sourceMask & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD;
    }

    private boolean isMouse(InputDevice device) {
        int sourceMask = device.getSources();
        return (sourceMask & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE;
    }

    private ServiceRequest.Callback<WeatherData> newWeatherCallback(){
        return new ServiceRequest.Callback<WeatherData>() {
            @Override
            public void onCallback(Call call, int status, WeatherData result) {
                if (!isAdded()) return;
                if (call.isCanceled() || result == null || result.getDays() == null || result.getDays().length == 0) return;
                App.WEATHER.setWeather(result);
                syncWeather();
            }
        };
    }

    private void syncWeather(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (App.WEATHER.getWeather() == null || !isAdded()) return;
                WeatherData result = App.WEATHER.getWeather();
                mWeatherView.setImageBitmap(AndroidSystem.getImageForAssets(getActivity(), FilePathMangaer.getWeatherIcon(result.getDays()[0].getIcon())));
            }
        });
    }

    private void syncTime(){
        boolean is24 = AppUtils.is24Display(getActivity());
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(is24 ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
        int m = calendar.get(Calendar.MINUTE);
        String time = getString(R.string.hour_minute_second, h, m);
        String segment = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
        mSegmentView.setVisibility(is24 ? View.GONE : View.VISIBLE);
        mSegmentView.setText(segment);
        mTimeView.setText(time);
    }

    private void setAdsContent(List<Ads> dataList, Map<Integer, Integer> layoutMap) {
        mVerticalContentGrid.setVisibility(View.GONE);
        mHorizontalContentGrid.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        AdsAdapter adapter = new AdsAdapter(getActivity(), getLayoutInflater(), dataList, layoutMap);
        GridLayoutManager lm = new GridLayoutManager(getActivity(), 2, RecyclerView.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(adapter);
        setSpanSizeLookup(lm, new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return dataList.get(position).getSpanSize();
            }
        });
    }

    private void setAppContent(List<ApplicationInfo> list){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AppListAdapter(getActivity(), getLayoutInflater(), R.layout.holder_app, newAppListCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mHorizontalContentGrid.setAdapter(itemBridgeAdapter);
        mVerticalContentGrid.setVisibility(View.GONE);
        mHorizontalContentGrid.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        arrayObjectAdapter.addAll(0, list);
    }

    private void setProjectorContent(){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), getLayoutInflater(), newProjectorCallback(), R.layout.holder_setting_3));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mHorizontalContentGrid.setAdapter(itemBridgeAdapter);
        mVerticalContentGrid.setVisibility(View.GONE);
        mHorizontalContentGrid.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        List<SettingItem> list = new ArrayList<>();
        list.add(new SettingItem(Projector.TYPE_SETTING, getString(R.string.project_crop), R.drawable.baseline_crop_100));
        list.add(new SettingItem(Projector.TYPE_PROJECTOR_MODE, getString(R.string.project_mode), R.drawable.baseline_model_training_100));
        list.add(new SettingItem(Projector.TYPE_HDMI, getString(R.string.project_hdmi), R.drawable.baseline_settings_input_hdmi_100));
        list.add(new SettingItem(Projector.TYPE_SCREEN, getString(R.string.project_gradient), R.drawable.baseline_screenshot_monitor_100));

        arrayObjectAdapter.addAll(0, list);
    }

    private SettingAdapter.Callback newProjectorCallback(){
        return new SettingAdapter.Callback() {
            @Override
            public void onSelect(boolean selected, SettingItem bean) {
                if (selected) setExpanded(false);
            }

            @Override
            public void onClick(SettingItem bean) {
                switch (bean.getType()){
                    case Projector.TYPE_SETTING:{
                        startActivity(new Intent(getActivity(), ScaleScreenActivity.class));
                    }
                        break;
                    case Projector.TYPE_PROJECTOR_MODE:{
                        boolean success = AndroidSystem.openProjectorMode(getActivity());
                        if (!success) toastInstall();
                    }
                        break;
                    case Projector.TYPE_HDMI:{
                        boolean success = AndroidSystem.openProjectorHDMI(getActivity());
                        if (!success) toastInstall();
                    }
                        break;
                    case Projector.TYPE_SCREEN:{
                        startActivity(new Intent(getActivity(), GradientActivity.class));
                    }
                        break;
                }
            }
        };
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float value = 1f - Math.abs(verticalOffset / 2f) / maxVerticalOffset;
        mHeaderGrid.setScaleX(value);
        mHeaderGrid.setScaleY(value);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mSettingView)){
            startActivity(new Intent(getActivity(), SettingActivity.class));
            //AndroidSystem.openSystemSetting(getActivity());
        }else if (v.equals(mWeatherView)){
            startActivity(new Intent(getActivity(), WeatherActivity.class));
        }else if (v.equals(mSearchView)){
            startActivity(new Intent(getActivity(), SearchActivity.class));
        }else if (v.equals(mWifiView)){
            if (Config.COMPANY == 3) {
                AndroidSystem.openWifiSetting(getActivity());
            }else {
                startActivity(new Intent(getActivity(), WifiListActivity.class));
            }
        }else if (v.equals(mLoginView)){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }else if (v.equals(mHelpView)){
            startActivity(new Intent(getActivity(), AboutActivity.class));
        }
    }

    private void work(final String flag, final TypeItem bean){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag.equals(uuid)){
                    selectWork(bean);
                }
            }
        }, 120);
    }

    private void selectWork(TypeItem bean){
        switch (bean.getType()){
            case Types.TYPE_MY_APPS:{
                fillApps();
            }
            break;
            case Types.TYPE_APP_STORE:{
                fillAppStore();
            }
            break;
            case Types.TYPE_PROJECTOR:{
                setProjectorContent();
            }
            break;
            case Types.TYPE_ADS:{
                List<Ads> dataList = new ArrayList<>();
                dataList.add(new Ads(2, R.drawable.ads_3));
                dataList.add(new Ads(1, R.drawable.ads_5));
                dataList.add(new Ads(1, R.drawable.ads_6));
                dataList.add(new Ads(1, R.drawable.ads_7));
                dataList.add(new Ads(1, R.drawable.ads_5));
                dataList.add(new Ads(1, R.drawable.ads_6));
                dataList.add(new Ads(1, R.drawable.ads_7));
                dataList.add(new Ads(1, R.drawable.ads_5));
                dataList.add(new Ads(1, R.drawable.ads_6));
                dataList.add(new Ads(1, R.drawable.ads_7));
                Map<Integer, Integer> map = new HashMap<>();
                map.put(AdsAdapter.TYPE_MAX, R.layout.holder_ads_max);
                map.put(AdsAdapter.TYPE_MIN, R.layout.holder_ads);
                setAdsContent(dataList, map);
            }
            break;
            case Types.TYPE_ADS2:{
                List<Ads> dataList = new ArrayList<>();
                dataList.add(new Ads(2, R.drawable.ads_4));
                dataList.add(new Ads(2, R.drawable.ads_3));
                dataList.add(new Ads(2, R.drawable.ads_1));
                dataList.add(new Ads(2, R.drawable.ads_3));
                dataList.add(new Ads(2, R.drawable.ads_4));
                dataList.add(new Ads(2, R.drawable.ads_1));
                Map<Integer, Integer> map = new HashMap<>();
                map.put(AdsAdapter.TYPE_MAX, R.layout.holder_ads_3);
                map.put(AdsAdapter.TYPE_MIN, R.layout.holder_ads_3);
                setAdsContent(dataList, map);
                setAdsContent(dataList, map);
            }
            break;
            default:
                switchMovice(bean);
        }
    }

    private MainHeaderAdapter.Callback newHeaderCallback(){
        return new MainHeaderAdapter.Callback() {
            @Override
            public void onClick(TypeItem bean) {
                switch (bean.getType()){
                    case Types.TYPE_MOVICE:{
                        if (!App.MOVIE_MAP.containsKey(bean.getId())){
                            toast(getString(R.string.place_waiting), ToastDialog.MODE_CONFIRM,null);
                            break;
                        }
                        Intent intent = new Intent(getActivity(), MoviceListActivity.class);
                        intent.putExtra(Atts.BEAN, bean);
                        startActivity(intent);
                    }
                        break;
                    case Types.TYPE_APP_STORE:
                        boolean success = AndroidSystem.jumpAppStore(getActivity());
                        if (!success) toastInstall();
                        break;
                    case Types.TYPE_MY_APPS:{
                        Intent intent = new Intent(getActivity(), AppsActivity.class);
                        intent.putExtra(Atts.TYPE, bean.getType());
                        startActivity(intent);
                    }
                        break;
                }
            }

            @Override
            public void onSelect(boolean selected, TypeItem bean) {
                if (selected){
                    setExpanded(true);
                    try {
                        if (call != null) call.cancel();
                        uuid = UUID.randomUUID().toString();
                        work(uuid, bean);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void toastInstall(){
        toast(getString(R.string.place_install_app), ToastDialog.MODE_DEFAULT, null);
    }

    private void toastInstallApp(String name, ToastDialog.Callback callback){
        toast(getString(R.string.place_install, name), ToastDialog.MODE_DEFAULT, callback);
    }

    private void toast(String title, int mode, ToastDialog.Callback callback){
        ToastDialog dialog = ToastDialog.newInstance(title, mode);
        dialog.setCallback(callback);
        dialog.show(getChildFragmentManager(), ToastDialog.TAG);
    }

    private void fillMovice(TypeItem bean){
        int layoutId = R.layout.holder_content;
        int columns = 1;
        switch (bean.getLayoutType()){
            case 1:
                columns = 0;
                layoutId = R.layout.holder_content;
                break;
            case 0:
                columns = 4;
                layoutId = R.layout.holder_content_4;
                break;
        }
        fillMovice(bean.getId(), bean.getLayoutType(), columns, layoutId);
    }

    private void fillMovice(long id, int dirction, int columns, int layoutId){
        if (App.MOVIE_MAP.containsKey(id) && isConnectFirst){
            setMoviceContent(App.MOVIE_MAP.get(id), dirction, columns, layoutId);
        }else {
            setMoviceContent(App.MOVIE_MAP.containsKey(id) ? App.MOVIE_MAP.get(id) : getPlaceholdings(), dirction, columns, layoutId);
            call = HttpRequest.getHomeContents(newMoviceListCallback());
        }
    }


    private void fillApps(){
        List<ApplicationInfo> infos = AndroidSystem.getUserApps(getActivity());
        if (infos.size() > 8){
            infos = infos.subList(0, 8);
        }
        setAppContent(infos);
    }

    private void fillAppStore(){
        if (App.APP_STORE_ITEMS.isEmpty()){
            List<AppItem> emptys = new ArrayList<>();
            for (int i = 0; i < 10; i++) emptys.add(new AppItem());
            setStoreContent(emptys);
            call = HttpRequest.getAppList(new AppServiceRequest.Callback<AppListResponse>() {
                @Override
                public void onCallback(Call call, int status, AppListResponse result) {
                    if (!isAdded() || call.isCanceled() || result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().isEmpty()) return;
                    if (mHeaderGrid.getSelectedPosition() == -1 || mHeaderGrid.getSelectedPosition() > targetMenus.size() - 1 || targetMenus.get(mHeaderGrid.getSelectedPosition()).getType() != Types.TYPE_APP_STORE) return;

                    App.APP_STORE_ITEMS.addAll(result.getResult().getAppList());
                    setStoreContent(App.APP_STORE_ITEMS);
                }
            }, Config.USER_ID, null, "hot", null, 1, 20);
        }else {
            setStoreContent(App.APP_STORE_ITEMS);
        }
    }

    private void setStoreContent(List<AppItem> list){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new StoreAdapter(getActivity(), getLayoutInflater(), newStoreClickCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mHorizontalContentGrid.setAdapter(itemBridgeAdapter);
        mVerticalContentGrid.setVisibility(View.GONE);
        mHorizontalContentGrid.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        arrayObjectAdapter.addAll(0, list);
    }

    private void local(String filePath, int direction, int columns, int layoutId){
        String[] files = AndroidSystem.getAssetsFileNames(getActivity(), filePath);
        List<Movice> list = new ArrayList<>(files.length);
        for (String item : files){
            list.add(new Movice(Types.TYPE_UNKNOW, null, filePath + "/" + item, Movice.PIC_ASSETS));
        }

        setMoviceContent(list, direction, columns, layoutId);
    }

    private void fillHeader(){
        try {
            String path = FilePathMangaer.getJsonPath(getActivity()) + "/Home.json";
            if (new File(path).exists()){
                HomeResponse result = new Gson().fromJson(new JsonReader(new FileReader(path)), HomeResponse.class);
                List<TypeItem> header = fillData(result);
                header.addAll(items);
                setHeader(header);
            }else {
                List<TypeItem> header = new ArrayList<>();
                header.add(new TypeItem(getString(R.string.netflix), R.drawable.netflix, UUID.randomUUID().getLeastSignificantBits(), Types.TYPE_MOVICE, TypeItem.TYPE_ICON_IMAGE_RES, TypeItem.TYPE_LAYOUT_STYLE_2));
                header.add(new TypeItem(getString(R.string.disney), R.drawable.disney, UUID.randomUUID().getLeastSignificantBits(), Types.TYPE_MOVICE, TypeItem.TYPE_ICON_IMAGE_RES, TypeItem.TYPE_LAYOUT_STYLE_2));
                header.addAll(items);
                setHeader(header);
            }
        }catch (Exception e){}
    }

    private List<TypeItem> fillData(HomeResponse result){
        List<HomeItem> homeItems = result.getData().getMovies();
        List<TypeItem> menus = new ArrayList<>();
        for (HomeItem bean : homeItems){
            List<Movice> movices = new ArrayList<>(bean.getDatas().length);
            for (Movice movice : bean.getDatas()){
                movice.setPicType(Movice.PIC_NETWORD);
                movice.setAppName(bean.getName());
                movice.setAppPackage(bean.getPackageNames());
                movices.add(movice);
            }
            TypeItem item = new TypeItem(bean.getName(), bean.getIcon(), UUID.randomUUID().getLeastSignificantBits(), Types.TYPE_MOVICE, TypeItem.TYPE_ICON_IMAGE_URL, bean.getType());
            App.MOVIE_MAP.put(item.getId(), movices);
            menus.add(item);
        }
        return menus;
    }

    private StoreAdapter.Callback newStoreClickCallback(){
        return new StoreAdapter.Callback() {
            @Override
            public void onSelect(boolean selected, AppItem bean) {
                if (selected) setExpanded(false);
            }

            @Override
            public void onClick(AppItem bean) {
                if (!TextUtils.isEmpty(bean.getAppDownLink())) AndroidSystem.jumpAppStore(getActivity(), new Gson().toJson(bean));
            }
        };
    }

    private MainContentAdapter.Callback newContentCallback(){
        return new MainContentAdapter.Callback() {
            @Override
            public void onClick(Movice bean) {
                boolean success = AndroidSystem.jumpVideoApp(getActivity(), bean.getAppPackage(), bean.getUrl());
                if (!success) {
                    toastInstallApp(bean.getAppName(), new ToastDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1){
                                Intent intent = new Intent(getActivity(), SearchActivity.class);
                                intent.putExtra(Atts.WORD, bean.getAppName());
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFouces(boolean hasFocus, Movice bean) {
                if (hasFocus){
                    setExpanded(false);
                }
            }
        };
    }

    private ServiceRequest.Callback<HomeResponse> newMoviceListCallback(){
        return new ServiceRequest.Callback<HomeResponse>() {
            @Override
            public void onCallback(Call call, int status, HomeResponse result) {
                try {
                    if (!isAdded() || call.isCanceled() || result == null) return;

                    FileUtils.writeFile(new Gson().toJson(result).getBytes(StandardCharsets.UTF_8), FilePathMangaer.getJsonPath(getActivity()), "Home.json");

                    List<TypeItem> header = fillData(result);
                    header.addAll(items);
                    if (!isConnectFirst) {

                        boolean isChanger = false;
                        Set<Integer> types = new HashSet<>();
                        for (TypeItem item : header) types.add(item.getType());

                        for (TypeItem item : targetMenus) {
                            if (!types.contains(item.getType())){
                                isChanger = true;
                                break;
                            }
                        }

                        if (header.size() != targetMenus.size()) isChanger = true;

                        if (isChanger) setHeader(header);
                    }
                    isConnectFirst = true;

                    TypeItem item = header.get(0);
                    fillMovice(item);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void switchMovice(TypeItem bean){
        fillMovice(bean);
    }

    private AppListAdapter.Callback newAppListCallback(){
        return new AppListAdapter.Callback() {

            @Override
            public void onSelect(boolean selected) {
                if (selected) setExpanded(false);
            }

            @Override
            public void onClick(ApplicationInfo bean) {
                openApp(bean);
            }
        };
    }

    private void openApp(ApplicationInfo bean){
        if (Config.COMPANY == 1 || Config.COMPANY == 2 || Config.COMPANY == 3){
            AndroidSystem.openPackageName(getActivity(), bean.packageName);
        }else {
            AppDialog dialog = AppDialog.newInstance(bean);
            dialog.setCallback(new AppDialog.Callback() {
                @Override
                public void onDelete() {
                    AndroidSystem.openApplicationDetials(getActivity(), bean.packageName);
                }

                @Override
                public void onOpen() {
                    AndroidSystem.openPackageName(getActivity(), bean.packageName);
                }
            });
            dialog.show(getChildFragmentManager(), AppDialog.TAG);
        }
    }

    private void setExpanded(boolean isExpanded){
        mAppBarLayout.setExpanded(isExpanded);
    }

    private List<Movice> getPlaceholdings(){
        List<Movice> movices = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            movices.add(new Movice(Types.TYPE_UNKNOW, null, "", Movice.PIC_PLACEHOLDING));
        }
        return movices;
    }

    private void setSpanSizeLookup(GridLayoutManager lm, GridLayoutManager.SpanSizeLookup spanSizeLookup){
        lm.setSpanSizeLookup(spanSizeLookup);
    }

    private void checkVersion(){
        HttpRequest.checkVersion(new ServiceRequest.Callback<VersionResponse>() {
            @Override
            public void onCallback(Call call, int status, VersionResponse result) {
                if (!isAdded() || call.isCanceled() || result == null || result.getData() == null) return;
                Version version = result.getData();
                if (version.getVersion() > BuildConfig.VERSION_CODE && Config.CHANNEL.equals(version.getChannel())) {
                    PreferencesUtils.setProperty(Atts.UPGRADE_VERSION, (int) version.getVersion());
                    AndroidSystem.jumpUpgrade(getActivity(), version);
                }
            }
        });
    }

    public class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case IntentAction.ACTION_UPDATE_WALLPAPER:
                    updateWallpaper();
                    break;
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REMOVED:
                case Intent.ACTION_PACKAGE_REPLACED:
                    if (mHeaderGrid.getSelectedPosition() != -1 && targetMenus.get(mHeaderGrid.getSelectedPosition()).getType() == Types.TYPE_MY_APPS){
                        fillApps();
                        requestFocus(mHorizontalContentGrid, 150);
                    }
                    break;
            }
        }
    }

    public class WallpaperReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case IntentAction.ACTION_UPDATE_WALLPAPER:
                    updateWallpaper();
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    break;
            }
        }
    }
}
