package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.ProjectorAdapter;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.Projector;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.ui.activity.GradientActivity;
import com.soya.launcher.ui.activity.ScaleScreenActivity;
import com.soya.launcher.utils.AndroidSystem;

import java.util.ArrayList;
import java.util.List;

public class ProjectorFragment extends AbsFragment{

    public static ProjectorFragment newInstance() {

        Bundle args = new Bundle();

        ProjectorFragment fragment = new ProjectorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private VerticalGridView mContentGrid;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_projector;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mContentGrid = view.findViewById(R.id.content);

        mTitleView.setText(getString(R.string.pojector));
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
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), getLayoutInflater(), newProjectorCallback(), R.layout.holder_setting_4));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_LARGE, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(4);
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
                        if (!success) Toast.makeText(getActivity(), getString(R.string.place_install_app), Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case Projector.TYPE_HDMI:{
                        boolean success = AndroidSystem.openProjectorHDMI(getActivity());
                        if (!success) Toast.makeText(getActivity(), getString(R.string.place_install_app), Toast.LENGTH_SHORT).show();
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
}
