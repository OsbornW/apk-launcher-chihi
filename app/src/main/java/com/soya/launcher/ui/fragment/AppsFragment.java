package com.soya.launcher.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.soya.launcher.adapter.AppListAdapter;
import com.soya.launcher.ui.dialog.AppDialog;
import com.soya.launcher.utils.AndroidSystem;

import java.util.List;

public class AppsFragment extends AbsFragment{

    public static AppsFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AppsFragment fragment = new AppsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private VerticalGridView mContentGrid;
    private TextView mTitleView;

    private InnerReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_apps;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mTitleView = view.findViewById(R.id.title);

        mTitleView.setText(getString(R.string.apps));
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        fillApps();
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

    private void fillApps(){
        setContent(AndroidSystem.getUserApps(getActivity()));
    }

    private void setContent(List<ApplicationInfo> list){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AppListAdapter(getActivity(), getLayoutInflater(), R.layout.holder_app_2, newAppListCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(4);
        arrayObjectAdapter.addAll(0, list);
        requestFocus(mContentGrid);
    }

    private AppListAdapter.Callback newAppListCallback(){
        return new AppListAdapter.Callback() {

            @Override
            public void onSelect(boolean selected) {
            }

            @Override
            public void onClick(ApplicationInfo bean) {
                if (App.COMPANY == 1 || App.COMPANY == 2 || App.COMPANY == 3){
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
        };
    }

    public class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REMOVED:
                case Intent.ACTION_PACKAGE_REPLACED:
                    fillApps();
                    break;
            }
        }
    }
}
