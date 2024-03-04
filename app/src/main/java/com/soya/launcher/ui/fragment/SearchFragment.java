package com.soya.launcher.ui.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import com.google.gson.Gson;
import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.adapter.AppItemAdapter;
import com.soya.launcher.adapter.FullSearchAdapter;
import com.soya.launcher.adapter.RecommendAdapter;
import com.soya.launcher.adapter.TextWatcherAdapter;
import com.soya.launcher.adapter.WebAdapter;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.DivSearch;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.Recommend;
import com.soya.launcher.bean.RecommendData;
import com.soya.launcher.bean.WebItem;
import com.soya.launcher.config.Config;
import com.soya.launcher.decoration.HSlideMarginDecoration;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.Types;
import com.soya.launcher.http.AppServiceRequest;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.RecommendServiceRequest;
import com.soya.launcher.http.Url;
import com.soya.launcher.http.response.AppListResponse;
import com.soya.launcher.ui.dialog.KeyboardDialog;
import com.soya.launcher.ui.dialog.ToastDialog;
import com.soya.launcher.utils.AndroidSystem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SearchFragment extends AbsFragment implements View.OnClickListener, TextView.OnEditorActionListener, FullSearchAdapter.Callback {

    public static SearchFragment newInstance(String word) {

        Bundle args = new Bundle();
        args.putString(Atts.WORD, word);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private HorizontalGridView mRecommendGrid;
    private VerticalGridView mContentGrid;
    private TextView mTitleView;
    private TextView mEditView;
    private View mDivSearch;

    private FullSearchAdapter mAdapter;

    private DivSearch store;
    private String searchText;
    private Call call;
    private Call appCall;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
        if (appCall != null) appCall.cancel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mRecommendGrid = view.findViewById(R.id.recommend);
        mTitleView = view.findViewById(R.id.recommend_title);
        mEditView = view.findViewById(R.id.edit_query);
        mContentGrid = view.findViewById(R.id.content);
        mDivSearch = view.findViewById(R.id.div_search);

        mAdapter = new FullSearchAdapter(getActivity(), inflater, new ArrayList<>(), this);
        mRecommendGrid.addItemDecoration(new HSlideMarginDecoration(getResources().getDimension(R.dimen.margin_decoration_max), getResources().getDimension(R.dimen.margin_decoration_min)));
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mDivSearch.setOnClickListener(this);
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
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        fillAppStore();

        mContentGrid.setAdapter(mAdapter);
        String word = getArguments().getString(Atts.WORD);
        if (!TextUtils.isEmpty(word)) {
            mEditView.setText(word);
            mEditView.onEditorAction(EditorInfo.IME_ACTION_DONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mDivSearch);
        showKeyboard();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void replace(){
        if (call != null) call.cancel();
        mContentGrid.setVisibility(View.VISIBLE);
        List<DivSearch> list = new ArrayList<>();

        store = new DivSearch(1, getString(R.string.app_store), new ArrayList(), 0);
        list.add(store);

        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> infos = AndroidSystem.getUserApps(getActivity());
        List<Object> apps = new ArrayList<>();
        for (ApplicationInfo info : infos){
            if (info.loadLabel(pm).toString().toLowerCase().contains(searchText.toLowerCase())){
                apps.add(info);
            }
        }
        if (!apps.isEmpty()) list.add(new DivSearch(0, getString(R.string.app_list), apps, 3));

        List<Object> webItems = new ArrayList<>();
        webItems.add(new WebItem(0, "Bing", R.drawable.edge, "https://cn.bing.com"));
        webItems.add(new WebItem(1, "Google", R.drawable.chrome, "https://www.google.com"));
        webItems.add(new WebItem(2, "Baidu", R.drawable.baidu, "https://www.baidu.com"));
        webItems.add(new WebItem(3, "Yandex", R.drawable.yandex, "https://yandex.com"));
        list.add(new DivSearch(2, getString(R.string.use_website_search), webItems, 3));

        mAdapter.replace(list);

        call = HttpRequest.getAppList(new AppServiceRequest.Callback<AppListResponse>() {
            @Override
            public void onCallback(Call call, int status, AppListResponse result) {
                if (!isAdded() || call.isCanceled() || store == null) return;
                if (result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().isEmpty()) {
                    store.setState(1);
                    mAdapter.sync(store);
                }else {
                    store.getList().addAll(result.getResult().getAppList());
                    store.setState(2);
                    mAdapter.sync(store);
                }

            }
        }, Config.USER_ID, null, null, searchText, 1, 50);
    }

    private WebAdapter.Callback newWebCallback(){
        return new WebAdapter.Callback() {
            @Override
            public void onClick(WebItem bean) {
                String url = bean.getLink();
                switch (bean.getType()){
                    case 0:
                        url += "/search?q="+searchText;
                        break;
                    case 1:
                        url += "/search?q="+searchText;
                        break;
                    case 2:
                        url += "/s?wd="+searchText;
                        break;
                    case 3:
                        url += "/search/?text="+searchText;
                        break;
                }

                List<ResolveInfo> infos = AndroidSystem.queryBrowareLauncher(getActivity(), url);
                if (infos.isEmpty()) {
                    toastInstall();
                    return;
                }
                Intent intent = AndroidSystem.openWebUrl(url);
                getActivity().startActivity(intent);
            }
        };
    }

    private void fillAppStore(){
        if (App.APP_STORE_ITEMS.isEmpty()){
            List<AppItem> emptys = new ArrayList<>();
            for (int i = 0; i < 10; i++) emptys.add(new AppItem());
            setStoreContent(emptys);
            appCall = HttpRequest.getAppList(new AppServiceRequest.Callback<AppListResponse>() {
                @Override
                public void onCallback(Call call, int status, AppListResponse result) {
                    if (!isAdded() || call.isCanceled() || result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().isEmpty()) return;

                    App.APP_STORE_ITEMS.addAll(result.getResult().getAppList());
                    setStoreContent(App.APP_STORE_ITEMS);
                }
            }, Config.USER_ID, "81", null, null, 1, 20);
        }else {
            setStoreContent(App.APP_STORE_ITEMS);
        }
    }

    private void setStoreContent(List<AppItem> list){
        mTitleView.setText(getString(R.string.recommend_for_you, list.size()));
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AppItemAdapter(getActivity(), getLayoutInflater(), R.layout.holder_app_3, newAppClickCallback()));
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
        mRecommendGrid.setAdapter(itemBridgeAdapter);
        arrayObjectAdapter.addAll(0, list);
    }

    private void toastInstall(){
        ToastDialog dialog = ToastDialog.newInstance(getString(R.string.place_install_app));
        dialog.show(getChildFragmentManager(), ToastDialog.TAG);
    }

    private AppItemAdapter.Callback newAppClickCallback(){
        return new AppItemAdapter.Callback() {
            @Override
            public void onSelect(boolean selected) {

            }

            @Override
            public void onClick(AppItem bean) {
                if (!TextUtils.isEmpty(bean.getAppDownLink())) AndroidSystem.jumpAppStore(getActivity(), new Gson().toJson(bean));
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = v.getText().toString();
        if (call != null) call.cancel();
        if (!TextUtils.isEmpty(text)) {
            searchText = text;
            replace();
        }
        return false;
    }

    @Override
    public void onClick(int type, Object bean) {
        switch (type){
            case 0:
                AndroidSystem.openPackageName(getActivity(), ((ApplicationInfo) bean).packageName);
                break;
            case 1:
                AndroidSystem.jumpAppStore(getActivity(), new Gson().toJson((AppItem) bean));
                break;
            case 2:
                webClick((WebItem) bean);
                break;
        }
    }

    private void webClick(WebItem bean){
        String url = bean.getLink();
        switch (bean.getType()){
            case 0:
                url += "/search?q="+searchText;
                break;
            case 1:
                url += "/search?q="+searchText;
                break;
            case 2:
                url += "/s?wd="+searchText;
                break;
            case 3:
                url += "/search/?text="+searchText;
                break;
        }

        List<ResolveInfo> infos = AndroidSystem.queryBrowareLauncher(getActivity(), url);
        if (infos.isEmpty()) {
            toastInstall();
            return;
        }
        Intent intent = AndroidSystem.openWebUrl(url);
        getActivity().startActivity(intent);
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
