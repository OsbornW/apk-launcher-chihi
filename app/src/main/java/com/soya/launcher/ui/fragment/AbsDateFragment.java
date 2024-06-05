package com.soya.launcher.ui.fragment;

import android.app.AlarmManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.DateListAdapter;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.bean.DateItem;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.bean.SimpleTimeZone;
import com.soya.launcher.ui.dialog.DatePickerDialog;
import com.soya.launcher.ui.dialog.TimePickerDialog;
import com.soya.launcher.ui.dialog.TimeZoneDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public abstract class AbsDateFragment extends AbsFragment implements View.OnClickListener {

    private HorizontalGridView mContentGrid;
    private VerticalGridView mSlideGrid;
    private View mNextView;
    private TextView mTitleView;

    private DateListAdapter mItemAdapter;

    private final List<DateItem> itemList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean is24 = AppUtils.is24Display(getActivity());
        itemList.addAll(Arrays.asList(new DateItem(0, getString(R.string.auto_time_title), isAutoTime() ? getString(R.string.open) : getString(R.string.close), isAutoTime(), true),
                new DateItem(1, getString(R.string.set_date_title), getDate(), false, false),
                new DateItem(2, getString(R.string.set_time_title), getTime(), false, false),
                new DateItem(3, getString(R.string.time_display), is24 ? getString(R.string.open) : getString(R.string.close), is24, true),
                new DateItem(4, getString(R.string.time_zone), TimeZone.getDefault().getDisplayName(), false, false)));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_set_date;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mNextView = view.findViewById(R.id.next);
        mSlideGrid = view.findViewById(R.id.slide);
        mTitleView = view.findViewById(R.id.title);

        mSlideGrid.post(() -> {
            mSlideGrid.requestFocus();
        });
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mNextView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        setContent(view, inflater);
        setSlide(view, inflater);
    }

    private void setContent(View view, LayoutInflater inflater){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), inflater, newCallback(), R.layout.holder_setting));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        arrayObjectAdapter.addAll(0, Collections.singletonList(new SettingItem(1, getString(R.string.network), R.drawable.baseline_wifi_100)));
    }

    private void setSlide(View view, LayoutInflater inflater){
        mItemAdapter = new DateListAdapter(getActivity(), inflater, itemList, new DateListAdapter.Callback() {
            @Override
            public void onClick(DateItem bean) {
                switch (bean.getType()){
                    case 0:
                        changAutoTime(bean);
                        break;
                    case 1:
                        if (isAutoTime()){
                            ToastDialog dialog = ToastDialog.newInstance(getString(R.string.is_auto_time_toast), ToastDialog.MODE_CONFIRM);
                            dialog.show(getChildFragmentManager(), ToastDialog.TAG);
                        }else {
                            openDatePicker();
                        }
                        break;
                    case 2:
                        if (isAutoTime()){
                            ToastDialog dialog = ToastDialog.newInstance(getString(R.string.is_auto_time_toast), ToastDialog.MODE_CONFIRM);
                            dialog.show(getChildFragmentManager(), ToastDialog.TAG);
                        }else {
                            openTimePicker();
                        }
                        break;
                    case 3:
                        chang24Display(bean);
                        break;
                    case 4:
                        openTimeZone(bean);
                        break;
                }
            }
        });
        mSlideGrid.setAdapter(mItemAdapter);
        mSlideGrid.setSelectedPosition(0);
    }

    private void openTimeZone(DateItem item){
        TimeZoneDialog dialog = TimeZoneDialog.newInstance();
        dialog.setCallback(new TimeZoneDialog.Callback() {
            @Override
            public void onClick(SimpleTimeZone bean) {
                AlarmManager alarmManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager = getActivity().getSystemService(AlarmManager.class);
                }else {
                    alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                }

                alarmManager.setTimeZone(bean.getZone().getID());
                item.setDescription(bean.getDesc());
                itemList.get(1).setDescription(getDate());
                itemList.get(2).setDescription(getTime());
                mItemAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show(getChildFragmentManager(), TimeZoneDialog.TAG);
    }

    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String name = Locale.getDefault().getLanguage();
        return name.equals("zh") ? getString(R.string.year_month_day, year, month, day) : getString(R.string.year_month_day, day, month, year);
    }

    private String getTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return getString(R.string.hour_minute_second, hour, minute);
    }

    private void openDatePicker(){
        DatePickerDialog dialog = DatePickerDialog.newInstance();
        dialog.setCallback(new DatePickerDialog.Callback() {
            @Override
            public void onConfirm(long timeMills) {
                AppUtils.setTime(timeMills);
                itemList.get(1).setDescription(getDate());
                mItemAdapter.notifyItemRangeChanged(0, itemList.size());
            }
        });
        dialog.show(getChildFragmentManager(), DatePickerDialog.TAG);
    }

    private void changAutoTime(DateItem bean){
        try {
            boolean isAuto = isAutoTime();
            AppUtils.setAutoDate(getActivity(), !isAuto);
            bean.setDescription(!isAuto ? getString(R.string.open) : getString(R.string.close));
            bean.setSwitch(!isAuto);
            mItemAdapter.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void chang24Display(DateItem bean){
        try {
            boolean isAuto = AppUtils.is24Display(getActivity());
            AppUtils.set24Display(getActivity(), !isAuto);
            bean.setDescription(!isAuto ? getString(R.string.open) : getString(R.string.close));
            bean.setSwitch(!isAuto);
            mItemAdapter.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isAutoTime(){
        try {
            return Settings.Global.getInt(getActivity().getContentResolver(), Settings.Global.AUTO_TIME) == 1;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }


    private void openTimePicker(){
        TimePickerDialog dialog = TimePickerDialog.newInstance();
        dialog.setCallback(new TimePickerDialog.Callback() {
            @Override
            public void onConfirm(long timeMills) {
                AppUtils.setTime(timeMills);
                itemList.get(2).setDescription(getTime());
                mItemAdapter.notifyItemRangeChanged(0, itemList.size());
            }
        });
        dialog.show(getChildFragmentManager(), TimePickerDialog.TAG);
    }

    public SettingAdapter.Callback newCallback(){
        return new SettingAdapter.Callback() {
            @Override
            public void onSelect(boolean selected, SettingItem bean) {

            }

            @Override
            public void onClick(SettingItem bean) {
                switch (bean.getType()){
                    case 0:
                        AndroidSystem.openDateSetting(getActivity());
                        break;
                    case 1:
                        AndroidSystem.openWifiSetting(getActivity());
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextView)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideWifiListFragment.newInstance()).addToBackStack(null).commit();
        }
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    protected void showWifi(boolean show){
        mContentGrid.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void showNext(boolean show){
        mNextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
