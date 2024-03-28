package com.soya.launcher.ui.fragment;

import android.content.Intent;
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
import com.soya.launcher.adapter.MainContentAdapter;
import com.soya.launcher.bean.HomeItem;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MoviceData;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.Types;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.http.Url;
import com.soya.launcher.http.response.HomeResponse;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.ui.activity.SearchActivity;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MoviceListFragment extends AbsFragment {
    private VerticalGridView mContentGrid;
    private TextView mTitleView;

    private MainContentAdapter mMainContentAdapter;

    private Call call;

    private TypeItem mTypeItem;
    private int layoutId = R.layout.holder_content;
    private int columns = 6;

    public static MoviceListFragment newInstance(TypeItem bean) {
        
        Bundle args = new Bundle();
        args.putSerializable(Atts.BEAN, bean);
        MoviceListFragment fragment = new MoviceListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeItem = (TypeItem) getArguments().getSerializable(Atts.BEAN);
        switch (mTypeItem.getLayoutType()){
            case 1:
                columns = 6;
                layoutId = R.layout.holder_content_5;
                break;
            case 0:
                columns = 4;
                layoutId = R.layout.holder_content_3;
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_movice_list;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mTitleView = view.findViewById(R.id.title);

        mMainContentAdapter = new MainContentAdapter(getActivity(), inflater, layoutId, newContentCallback());
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);

        mTitleView.setText(mTypeItem.getName());
        fillMovice();
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

    private void setMoviceContent(List<Movice> list){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(mMainContentAdapter);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(columns);
        arrayObjectAdapter.addAll(0, list);
    }

    private void fillMovice(){
        if (App.MOVIE_MAP.containsKey(mTypeItem.getId())){
            setMoviceContent(App.MOVIE_MAP.get(mTypeItem.getId()));
        }else {
            setMoviceContent(getPlaceholdings());
        }
    }

    private MainContentAdapter.Callback newContentCallback(){
        return new MainContentAdapter.Callback() {
            @Override
            public void onClick(Movice bean) {
                boolean success = AndroidSystem.jumpVideoApp(getActivity(), bean.getAppPackage(), bean.getUrl());
                if (!success) {
                    ToastDialog dialog = ToastDialog.newInstance(getString(R.string.place_install, mTypeItem.getName()));
                    dialog.setCallback(new ToastDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1){
                                String[] pns = new String[bean.getAppPackage().length];
                                for (int i = 0; i < pns.length; i++){
                                    pns[i] = bean.getAppPackage()[i].getPackageName();
                                }
                                AndroidSystem.jumpAppStore(getActivity(), null, pns);
                            }
                        }
                    });
                    dialog.show(getChildFragmentManager(), ToastDialog.TAG);
                }
            }

            @Override
            public void onFouces(boolean hasFocus, Movice bean) {

            }
        };
    }

    private List<Movice> getPlaceholdings(){
        List<Movice> movices = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            movices.add(new Movice(Types.TYPE_UNKNOW, null, "empty", Movice.PIC_PLACEHOLDING));
        }
        return movices;
    }
}
