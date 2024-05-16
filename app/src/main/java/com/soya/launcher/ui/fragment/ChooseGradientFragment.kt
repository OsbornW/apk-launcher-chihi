package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.open.system.ASystemProperties;
import com.open.system.SystemUtils;
import com.soya.launcher.R;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.bean.Projector;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.ui.activity.GradientActivity;
import com.soya.launcher.ui.activity.ScaleScreenActivity;
import com.soya.launcher.utils.AndroidSystem;

import java.util.ArrayList;
import java.util.List;

public class ChooseGradientFragment extends AbsFragment{

    public static ChooseGradientFragment newInstance() {

        Bundle args = new Bundle();

        ChooseGradientFragment fragment = new ChooseGradientFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private final List<SettingItem> dataList = new ArrayList<>();
    private ItemBridgeAdapter itemBridgeAdapter;
    private TextView mTitleView;
    private VerticalGridView mContentGrid;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_choose_gradient;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mContentGrid = view.findViewById(R.id.content);

        mTitleView.setText(getString(R.string.project_gradient));
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
        dataList.clear();
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), getLayoutInflater(), newProjectorCallback(), R.layout.holder_setting_5));
        itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_LARGE, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(2);
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1;
        dataList.add(new SettingItem(Projector.TYPE_AUTO_MODE, isEnalbe ? getString(R.string.auto) : getString(R.string.close), R.drawable.auto));
        dataList.add(new SettingItem(Projector.TYPE_SCREEN, isEnalbe ? getString(R.string.auto_calibration) : getString(R.string.manual), R.drawable.baseline_screenshot_monitor_100));

        arrayObjectAdapter.addAll(0, dataList);
    }

    private SettingAdapter.Callback newProjectorCallback(){
        return new SettingAdapter.Callback() {

            @Override
            public void onSelect(boolean selected, SettingItem bean) {

            }

            @Override
            public void onClick(SettingItem bean) {
                switch (bean.getType()){
                    case Projector.TYPE_SCREEN:{
                        boolean isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1;
                        if (isEnalbe){
                            AndroidSystem.openActivityName(getActivity(), "com.hxdevicetest", "com.hxdevicetest.CheckGsensorActivity");
                        }else {
                            startActivity(new Intent(getActivity(), GradientActivity.class));
                        }
                    }
                    break;
                    case Projector.TYPE_AUTO_MODE:{
                        boolean isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1;
                        isEnalbe = !isEnalbe;
                        ASystemProperties.set("persist.vendor.gsensor.enable", isEnalbe ? "1" : "0");
                        dataList.get(0).setName(isEnalbe ? getString(R.string.auto) : getString(R.string.close));
                        dataList.get(1).setName(isEnalbe ? getString(R.string.auto_calibration) : getString(R.string.manual));
                        itemBridgeAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        };
    }
}
