package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;

import com.soya.launcher.R;
import com.soya.launcher.adapter.SettingAdapter;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.utils.AndroidSystem;

import java.util.Arrays;

public class SetNetwordFragment extends AbsFragment implements View.OnClickListener {

    public static SetNetwordFragment newInstance() {

        Bundle args = new Bundle();

        SetNetwordFragment fragment = new SetNetwordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private HorizontalGridView mContentGrid;
    private View mNextView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_set_netword;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mNextView = view.findViewById(R.id.next);
        setContent(view, inflater);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mNextView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        AndroidSystem.openWifiSetting(getActivity());
    }

    private void setContent(View view, LayoutInflater inflater){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SettingAdapter(getActivity(), inflater, newCallback(), R.layout.holder_setting_2));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        arrayObjectAdapter.addAll(0, Arrays.asList(new SettingItem[]{
                new SettingItem(1, getString(R.string.network), R.drawable.baseline_wifi_100),
        }));
        mContentGrid.setSelectedPosition(0);
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
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, ChooseWallpaperFragment.newInstance()).commit();
        }
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }
}
