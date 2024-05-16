package com.soya.launcher.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.adapter.WeatherAdapter;
import com.soya.launcher.bean.Weather;
import com.soya.launcher.bean.WeatherData;
import com.soya.launcher.bean.Weatherhour;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.ui.activity.CityActivity;
import com.soya.launcher.ui.dialog.ProgressDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class WeatherFragment extends AbsFragment implements View.OnClickListener {

    public static WeatherFragment newInstance() {

        Bundle args = new Bundle();

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ProgressDialog dialog;

    private RecyclerView mContentGrid;
    private View mSearchView;
    private TextView mCityNameView;
    private TextView mTimeView;
    private View mUpdateView;
    private TextView mDateView;
    private TextView mCurrentTempView;
    private TextView mIconNameView;
    private TextView mRangeView;
    private ImageView mTargetIconView;

    private WeatherAdapter mAdapter;
    private InnerReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new InnerReceiver();
        dialog = ProgressDialog.newInstance();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.ACTION_REFRESH_WEATHER);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mSearchView = view.findViewById(R.id.search);
        mCityNameView = view.findViewById(R.id.city_name);
        mTimeView = view.findViewById(R.id.time);
        mUpdateView = view.findViewById(R.id.update);
        mDateView = view.findViewById(R.id.date);
        mCurrentTempView = view.findViewById(R.id.current_temperature);
        mIconNameView = view.findViewById(R.id.icon_name);
        mRangeView = view.findViewById(R.id.range);
        mTargetIconView = view.findViewById(R.id.target_icon);

        mAdapter = new WeatherAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mSearchView.setOnClickListener(this);
        mUpdateView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);

        updataMessage(view, inflater);

        mContentGrid.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mContentGrid.setAdapter(mAdapter);

        refresh();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mSearchView);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mSearchView)){
            startActivity(new Intent(getActivity(), CityActivity.class));
        }else if (v.equals(mUpdateView)){
            updateWeather();
        }
    }

    private void refresh(){
        HttpRequest.getCityWeather(newWeatherCallback(), PreferencesManager.getCityName());
    }

    private void updataMessage(View view, LayoutInflater inflater){
        String cityName = PreferencesManager.getCityName();
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        String language = Locale.getDefault().getLanguage();

        mCityNameView.setText(StringUtils.firstCharToUpperCase(cityName));
        mTimeView.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        mDateView.setText(language.equals("zh") ? String.format(getString(R.string.calendar_format), calendar.get(Calendar.YEAR), month, day) : String.format(getString(R.string.calendar_format), day, month,calendar.get(Calendar.YEAR)));

        boolean isCache = false;
        if (App.WEATHER.getTime() > 0 && App.WEATHER.getWeather() != null){
            calendar.setTimeInMillis(App.WEATHER.getTime());
            if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.DAY_OF_MONTH) == day){
                update(App.WEATHER.getWeather());
                isCache = true;
            }
        }
        if (!isCache){
            List<Weather> weathers = new ArrayList<>();
            Locale locale = Locale.getDefault();
            for (int i = 0; i < 7; i++){
                String des = language.equals("zh") ? getString(R.string.date_des, StringUtils.dayToWeek(getActivity(), dayWeek + i), month, day + i) : getString(R.string.date_des, StringUtils.dayToWeek(getActivity(), dayWeek + i), day + i, month);
                weathers.add(new Weather(des));
            }
            mAdapter.replace(weathers);
        }
    }

    public ServiceRequest.Callback<WeatherData> newWeatherCallback(){
        return new ServiceRequest.Callback<WeatherData>() {
            @Override
            public void onCallback(Call call, int status, WeatherData result) {
                if (!isAdded()) return;

                if (dialog.isAdded()) dialog.dismiss();

                if (call.isCanceled() || result == null || result.getDays() == null || result.getDays().length == 0) return;
                App.WEATHER.setWeather(result);
                update(result);
            }
        };
    }

    private void update(WeatherData result){
        int size = result.getDays().length > 7 ? 7 : result.getDays().length;
        List<Weather> list = new ArrayList<>(size);
        Calendar calendar = Calendar.getInstance();
        String language = Locale.getDefault().getLanguage();
        for (int i = 0; i < size; i++){
            Weather item = result.getDays()[i];
            String[] array = item.getDatetime().split("-");
            int year = Integer.valueOf(array[0]);
            int month = Integer.valueOf(array[1]);
            int dayWeek = Integer.valueOf(array[2]);
            calendar.set(year, month - 1, dayWeek);
            String des = language.equals("zh") ? getString(R.string.date_des, StringUtils.dayToWeek(getActivity(), calendar.get(Calendar.DAY_OF_WEEK) - 1), month, dayWeek) : getString(R.string.date_des, StringUtils.dayToWeek(getActivity(), calendar.get(Calendar.DAY_OF_WEEK) - 1), dayWeek, month);
            item.setDateDes(des);
            list.add(item);
        }

        calendar.setTimeInMillis(System.currentTimeMillis());
        Weather item = list.get(0);
        String[] array = item.getDatetime().split("-");
        int year = Integer.valueOf(array[0]);
        int month = Integer.valueOf(array[1]);
        int day = Integer.valueOf(array[2]);
        mDateView.setText(String.format(getString(R.string.calendar_format), year, month, day));
        mIconNameView.setText(StringUtils.firstCharToUpperCase(item.getIcon().replace("-", " ")));
        mRangeView.setText(String.format("%.0f℉ ~ %.0f℉", item.getTempmin(), item.getTempmax()));
        mTargetIconView.setImageBitmap(AndroidSystem.getImageForAssets(getActivity(), FilePathMangaer.getWeatherIcon(item.getIcon())));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (item.getHours() != null) {
            item.getHours();
            for (Weatherhour weatherhour : item.getHours()) {
                String[] split = weatherhour.getDatetime().split(":");
                if (hour == Integer.valueOf(split[0])) {
                    mCurrentTempView.setText(String.format("%.0f℉", weatherhour.getTemp()));
                    break;
                }
            }
        }
        mAdapter.replace(list);
    }

    private void updateWeather(){
        dialog.show(getChildFragmentManager(), ProgressDialog.TAG);
        mCityNameView.setText(StringUtils.firstCharToUpperCase(PreferencesManager.getCityName()));
        refresh();
    }

    public class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            updateWeather();
        }
    }
}
