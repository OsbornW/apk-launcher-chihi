package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.adapter.WallpaperAdapter;
import com.soya.launcher.bean.Wallpaper;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.utils.PreferencesUtils;

public class WallpaperFragment extends AbsFragment{

    public static WallpaperFragment newInstance() {

        Bundle args = new Bundle();

        WallpaperFragment fragment = new WallpaperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private VerticalGridView mContentGrid;
    private ImageView mWallpaperView;

    private ArrayObjectAdapter mArrayObjectAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mContentGrid = view.findViewById(R.id.content);
        mWallpaperView = view.findViewById(R.id.wallpaper);

        mTitleView.setText(getString(R.string.wallpaper));
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

    private void setContent(){
        mArrayObjectAdapter = new ArrayObjectAdapter(new WallpaperAdapter(getActivity(), getLayoutInflater(), newWallpaperCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(mArrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(3);
        mArrayObjectAdapter.addAll(0, App.WALLPAPERS);
        mContentGrid.requestFocus();
    }

    public WallpaperAdapter.Callback newWallpaperCallback(){
        return new WallpaperAdapter.Callback() {
            @Override
            public void onSelect(boolean select, Wallpaper bean) {
                if (select) GlideUtils.bindBlurCross(getActivity(), mWallpaperView, bean.getPicture(), 800);
            }

            @Override
            public void onClick(Wallpaper bean) {
                PreferencesUtils.setProperty(Atts.WALLPAPER, bean.getId());
                mArrayObjectAdapter.notifyArrayItemRangeChanged(0, mArrayObjectAdapter.size());
                getActivity().sendBroadcast(new Intent(IntentAction.ACTION_UPDATE_WALLPAPER));
            }
        };
    }
}
