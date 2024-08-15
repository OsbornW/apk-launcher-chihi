package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.CityAdapter;
import com.soya.launcher.adapter.TextWatcherAdapter;
import com.soya.launcher.bean.City;
import com.soya.launcher.decoration.HSlideMarginDecoration;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.ui.dialog.KeyboardDialog;
import com.soya.launcher.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class CityFragment extends AbsFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    public static CityFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CityFragment fragment = new CityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    private HorizontalGridView mRecommendGrid;
    private VerticalGridView mContentGrid;
    private TextView mTitleView;
    private TextView mEditView;
    private View mDivSearch;
    private View mProgressView;

    private Call call;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_city;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mRecommendGrid = view.findViewById(R.id.recommend);
        mTitleView = view.findViewById(R.id.recommend_title);
        mEditView = view.findViewById(R.id.edit_query);
        mContentGrid = view.findViewById(R.id.content);
        mDivSearch = view.findViewById(R.id.div_search);
        mProgressView = view.findViewById(R.id.progressBar);

        mRecommendGrid.addItemDecoration(new HSlideMarginDecoration(getResources().getDimension(com.shudong.lib_dimen.R.dimen.qb_px_10), getResources().getDimension(com.shudong.lib_dimen.R.dimen.qb_px_2)));
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mDivSearch.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);

        setRecommenContent(getPlaceholdings());
        HttpRequest.getHotCitys(newCitySearchCallback(0));
        mEditView.setOnEditorActionListener(this);
        mEditView.addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) return;
                mEditView.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mContentGrid);
        showKeyboard();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void setRecommenContent(List<City> list){
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CityAdapter(getActivity(), getLayoutInflater(), R.layout.holder_city, newCityClickCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mRecommendGrid.setAdapter(itemBridgeAdapter);

        mTitleView.setText(getString(R.string.recommend_city, list.size()));
        arrayObjectAdapter.addAll(0, list);
        requestFocus(mDivSearch);
    }

    private void setSearchContent(List<City> list){
        mContentGrid.setVisibility(View.VISIBLE);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CityAdapter(getActivity(), getLayoutInflater(), R.layout.holder_city_2, newCityClickCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setNumColumns(3);
        arrayObjectAdapter.addAll(0, list);
    }

    private CityAdapter.Callback newCityClickCallback(){
        return new CityAdapter.Callback() {
            @Override
            public void onClick(City bean) {
                if (TextUtils.isEmpty(bean.getCityName())) return;
                PreferencesUtils.setProperty(Atts.CITY, bean.getCityName());
                getActivity().sendBroadcast(new Intent(IntentAction.ACTION_REFRESH_WEATHER));
                getActivity().onBackPressed();
            }
        };
    }

    private ServiceRequest.Callback<City[]> newCitySearchCallback(int type){
        return new ServiceRequest.Callback<City[]>() {
            @Override
            public void onCallback(Call call, int status, City[] result) {
                if (!isAdded() || call.isCanceled()) return;
                mProgressView.setVisibility(View.GONE);
                if (result == null) return;

                switch (type){
                    case 0:
                        setRecommenContent(Arrays.asList(result));
                        break;
                    case 1:
                        setSearchContent(Arrays.asList(result));
                        break;
                }
            }
        };
    }

    private List<City> getPlaceholdings(){
        List<City> movices = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            movices.add(new City());
        }
        return movices;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = v.getText().toString();
        if (call != null) call.cancel();
        if (!TextUtils.isEmpty(text)){
            mProgressView.setVisibility(View.VISIBLE);
            mContentGrid.setVisibility(View.GONE);
            call = HttpRequest.searchCity(newCitySearchCallback(1), text);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mDivSearch)){
            showKeyboard();
        }
    }

    private void showKeyboard(){
        KeyboardDialog dialog = KeyboardDialog.newInstance();
        dialog.setTargetView(mEditView);
        dialog.show(getChildFragmentManager(), KeyboardDialog.TAG);
    }
}
