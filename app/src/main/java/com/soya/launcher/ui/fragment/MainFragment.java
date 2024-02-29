package com.soya.launcher.ui.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.soya.launcher.App;
import com.soya.launcher.BuildConfig;
import com.soya.launcher.R;
import com.soya.launcher.adapter.AdsAdapter;
import com.soya.launcher.adapter.AppListAdapter;
import com.soya.launcher.adapter.BannerAdapter;
import com.soya.launcher.adapter.MainContentAdapter;
import com.soya.launcher.adapter.MainHeaderAdapter;
import com.soya.launcher.adapter.ProjectorAdapter;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.adapter.StoreAdapter;
import com.soya.launcher.bean.Ads;
import com.soya.launcher.bean.AppInfo;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MoviceData;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.MyRunnable;
import com.soya.launcher.bean.Projector;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.bean.Version;
import com.soya.launcher.bean.WeatherData;
import com.soya.launcher.decoration.HSlideMarginDecoration;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.enums.Types;
import com.soya.launcher.http.AppServiceRequest;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.http.Url;
import com.soya.launcher.http.response.AppListResponse;
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
import com.soya.launcher.ui.activity.UpgradeActivity;
import com.soya.launcher.ui.activity.WallpaperActivity;
import com.soya.launcher.ui.activity.WeatherActivity;
import com.soya.launcher.ui.activity.WifiListActivity;
import com.soya.launcher.ui.dialog.AppDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.AppUtils;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.PreferencesUtils;
import com.soya.launcher.utils.StringUtils;
import com.soya.launcher.view.BannerLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class MainFragment extends AbsFragment implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
    private Handler uiHandler;
    private String uuid;
    private Call call;
    private InnerReceiver receiver;
    private WallpaperReceiver wallpaperReceiver;
    private float maxVerticalOffset;
    private final List<TypeItem> items = new ArrayList<>();
    private final List<TypeItem> targetMenus = new ArrayList<>();
    private MyRunnable timeRunnable;
    private MyRunnable weatherRunnable;
    private boolean isConnectFirst = false;
    private boolean isNetworkAvailable;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxVerticalOffset = getResources().getDimension(R.dimen.until_collapsed_height);
        uiHandler = new Handler();
        receiver = new InnerReceiver();
        wallpaperReceiver = new WallpaperReceiver();

        items.addAll(Arrays.asList(new TypeItem[]{
                new TypeItem(getString(R.string.app_store), R.drawable.store, Types.TYPE_APP_STORE),
                new TypeItem(getString(R.string.apps), R.drawable.app_list, Types.TYPE_MY_APPS),
        }));

        if (App.COMPANY == 0){
            items.add(new TypeItem(getString(R.string.pojector), R.drawable.projector, Types.TYPE_PROJECTOR));
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
        getActivity().registerReceiver(wallpaperReceiver, filter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weatherRunnable != null) weatherRunnable.interrupt();
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
    public void onStart() {
        super.onStart();
        syncWeather();
        syncTime();
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
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater){
        super.initBind(view, inflater);
        fillHeader();
        fillMovice(Types.TYPE_NETFLIX, 0, 0, R.layout.holder_content);
        startWeather();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void uidPull(){
        if (!App.IS_TEST) HttpRequest.uidPull(AppInfo.newInfo(getActivity()));
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
            case 0:
                mHorizontalContentGrid.setAdapter(itemBridgeAdapter);
                mVerticalContentGrid.setVisibility(View.GONE);
                mHorizontalContentGrid.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                break;
            case 1:
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
    }

    private void startLoopTime(){
        stopLoopTime();
        timeRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    SystemClock.sleep(2000);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) return;
                            syncTime();
                            boolean old = isNetworkAvailable;
                            isNetworkAvailable = AndroidSystem.isNetworkAvailable(getActivity());
                            if (isNetworkAvailable != old && isNetworkAvailable) uidPull();
                            mWifiView.setImageResource(isNetworkAvailable ? R.drawable.baseline_wifi_100 : R.drawable.baseline_wifi_off_100);
                        }
                    });
                }
            }
        };
        exec.execute(timeRunnable);
    }

    private void startWeather(){
        if (weatherRunnable != null) weatherRunnable.interrupt();
        weatherRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    HttpRequest.getCityWeather(newWeatherCallback(), PreferencesManager.getCityName());
                    SystemClock.sleep(90 * 1000);
                }
            }
        };
        exec.execute(weatherRunnable);
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
                        /*boolean success = AndroidSystem.openProjectorSetting(getActivity());
                        if (!success) Toast.makeText(getActivity(), getString(R.string.place_install_app), Toast.LENGTH_SHORT).show();*/
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
            startActivity(new Intent(getActivity(), WifiListActivity.class));
        }else if (v.equals(mLoginView)){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }else if (v.equals(mHelpView)){
            startActivity(new Intent(getActivity(), AboutActivity.class));
        }
    }

    private void work(final String flag, final int type){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag.equals(uuid)){
                    selectWork(type);
                }
            }
        }, 120);
    }

    private void selectWork(int type){
        switch (type){
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
                switchMovice(type);
        }
    }

    private MainHeaderAdapter.Callback newHeaderCallback(){
        return new MainHeaderAdapter.Callback() {
            @Override
            public void onClick(TypeItem bean) {
                switch (bean.getType()){
                    case Types.TYPE_HULU:
                    case Types.TYPE_MAX:
                    case Types.TYPE_PRIME_VIDEO:
                    case Types.TYPE_DISNEY:
                    case Types.TYPE_NETFLIX:
                    case Types.TYPE_YOUTUBE:{
                        Intent intent = new Intent(getActivity(), MoviceListActivity.class);
                        intent.putExtra(Atts.TYPE, bean.getType());
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
                        work(uuid, bean.getType());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void toastInstall(){
        toastInstall(getString(R.string.place_install_app), null);
    }

    private void toastInstallApp(String name, ToastDialog.Callback callback){
        toastInstall(getString(R.string.place_install, name), callback);
    }

    private void toastInstall(String title, ToastDialog.Callback callback){
        ToastDialog dialog = ToastDialog.newInstance(title);
        dialog.setCallback(callback);
        dialog.show(getChildFragmentManager(), ToastDialog.TAG);
    }

    private void fillMovice(int type, int dirction, int columns, int layoutId){
        if (App.MOVIE_MAP.containsKey(type) && isConnectFirst){
            setMoviceContent(App.MOVIE_MAP.get(type), dirction, columns, layoutId);
        }else {
            setMoviceContent(App.MOVIE_MAP.containsKey(type) ? App.MOVIE_MAP.get(type) : getPlaceholdings(), dirction, columns, layoutId);
            call = HttpRequest.getMoviceList(newMoviceListCallback(type));
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
            }, App.USER_ID, null, "hot", null, 1, 20);
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
                MoviceList[] result = new Gson().fromJson(new JsonReader(new FileReader(path)), MoviceList[].class);
                List<TypeItem> header = fillData(result);
                header.addAll(items);
                setHeader(header);
            }else {
                List<TypeItem> header = new ArrayList<>();
                header.add(new TypeItem(getString(R.string.netflix), R.drawable.netflix, Types.TYPE_NETFLIX));
                header.add(new TypeItem(getString(R.string.disney), R.drawable.disney, Types.TYPE_DISNEY));
                header.addAll(items);
                setHeader(header);
            }
        }catch (Exception e){}
    }

    private List<TypeItem> fillData(MoviceList[] result){
        Map<Integer, TypeItem> itemMap = new LinkedHashMap<>();
        for (MoviceList child : result){
            List<Movice> youtubes = new ArrayList<>();
            List<Movice> netflixs = new ArrayList<>();
            List<Movice> disneys = new ArrayList<>();
            List<Movice> hulus = new ArrayList<>();
            List<Movice> maxs = new ArrayList<>();
            List<Movice> primes = new ArrayList<>();

            if (child.getYoutube() != null){
                itemMap.put(Types.TYPE_YOUTUBE, new TypeItem(getString(R.string.youtube), R.drawable.youtube, Types.TYPE_YOUTUBE));
                for (MoviceData item : child.getYoutube()){
                    youtubes.add(new Movice(Types.TYPE_YOUTUBE, Url.conformity(Url.YOUTUBE_PLAY, item.getVideoId()), item.getThumbnailMap().get("high"), Movice.PIC_NETWORD));
                }
            }

            MoviceList.Inner inner = child.getOthers();
            if (inner != null){
               if (inner.getDisney() != null){
                   itemMap.put(Types.TYPE_DISNEY, new TypeItem(getString(R.string.disney), R.drawable.disney, Types.TYPE_DISNEY));
                   for (MoviceData item : inner.getDisney()){
                       disneys.add(new Movice(Types.TYPE_DISNEY, Url.conformity(Url.DISNEY_PLAY, item.getDisney_id()), item.getMain_img(), Movice.PIC_NETWORD));
                   }
               }

                if (inner.getMax() != null){
                    itemMap.put(Types.TYPE_MAX, new TypeItem(getString(R.string.max), R.drawable.max, Types.TYPE_MAX));
                    for (MoviceData item : inner.getMax()){
                        maxs.add(new Movice(Types.TYPE_MAX, Url.conformity(Url.MAX_PLAY, item.getMax_id()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getHulu() != null){
                    itemMap.put(Types.TYPE_HULU, new TypeItem(getString(R.string.hulu), R.drawable.hulu, Types.TYPE_HULU));
                    for (MoviceData item : inner.getHulu()){
                        hulus.add(new Movice(Types.TYPE_HULU, Url.conformity(Url.HULU_PLAY, item.getHulu_id()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getPrime_video() != null){
                    itemMap.put(Types.TYPE_PRIME_VIDEO, new TypeItem(getString(R.string.prime_video), R.drawable.prime_video, Types.TYPE_PRIME_VIDEO));
                    for (MoviceData item : inner.getPrime_video()){
                        primes.add(new Movice(Types.TYPE_PRIME_VIDEO, Url.conformity(Url.PRIME_VIDEO_PLAY, item.getPrime_videoid()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getNetflix() != null){
                    itemMap.put(Types.TYPE_NETFLIX, new TypeItem(getString(R.string.netflix), R.drawable.netflix, Types.TYPE_NETFLIX));
                    for (MoviceData item : inner.getNetflix()){
                        netflixs.add(new Movice(Types.TYPE_NETFLIX, Url.conformity(Url.NETFLIX_PLAY, item.getNetflix_id()), item.getLarge_image(), Movice.PIC_NETWORD));
                    }
                }
            }

            if (netflixs.size() != 0) App.MOVIE_MAP.put(Types.TYPE_NETFLIX, netflixs);
            if (disneys.size() != 0) App.MOVIE_MAP.put(Types.TYPE_DISNEY, disneys);
            if (youtubes.size() != 0) App.MOVIE_MAP.put(Types.TYPE_YOUTUBE, youtubes);
            if (maxs.size() != 0) App.MOVIE_MAP.put(Types.TYPE_MAX, maxs);
            if (hulus.size() != 0) App.MOVIE_MAP.put(Types.TYPE_HULU, hulus);
            if (primes.size() != 0) App.MOVIE_MAP.put(Types.TYPE_PRIME_VIDEO, primes);
        }

        List<TypeItem> menus = new ArrayList<>();
        for (Map.Entry<Integer, TypeItem> entry : itemMap.entrySet()) {
            if (entry.getKey() == Types.TYPE_NETFLIX || entry.getKey() == Types.TYPE_MAX){
                menus.add(0, entry.getValue());
            }else {
                menus.add(entry.getValue());
            }
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
                boolean success = AndroidSystem.jumpVideoApp(getActivity(), bean.getType(), bean.getUrl());
                if (!success) {
                    String title = StringUtils.getMoviceCollectionTitle(getActivity(), bean.getType());
                    toastInstallApp(title, new ToastDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1){
                                Intent intent = new Intent(getActivity(), SearchActivity.class);
                                intent.putExtra(Atts.WORD, title);
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

    private ServiceRequest.Callback<MoviceList[]> newMoviceListCallback(final int type){
        return new ServiceRequest.Callback<MoviceList[]>() {
            @Override
            public void onCallback(Call call, int status, MoviceList[] result) {
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

                    switchMovice(type);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void switchMovice(int type){
        switch (type) {
            case Types.TYPE_NETFLIX: {
                fillMovice(type, 0, 0, R.layout.holder_content);
            }
            break;
            case Types.TYPE_DISNEY: {
                fillMovice(type, 1, 4, R.layout.holder_content_4);
            }
            break;
            case Types.TYPE_YOUTUBE: {
                fillMovice(type, 1, 4, R.layout.holder_content_4);
            }
            break;
            case Types.TYPE_MAX: {
                fillMovice(type, 0, 0, R.layout.holder_content);
            }
            break;
            case Types.TYPE_HULU: {
                fillMovice(type, 1, 4, R.layout.holder_content_4);
            }
            break;
            case Types.TYPE_PRIME_VIDEO: {
                fillMovice(type, 1, 4, R.layout.holder_content_4);
            }
            break;
        }
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
        if (App.COMPANY == 1 || App.COMPANY == 2){
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
                if (version.getVersion() > BuildConfig.VERSION_CODE && App.CHANNEL.equals(version.getChannel())) {
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
            }
        }
    }
}
