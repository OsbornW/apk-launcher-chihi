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
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MoviceData;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.Types;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.http.ServiceRequest;
import com.soya.launcher.http.Url;
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

    private int type = Types.TYPE_NETFLIX;
    private int layoutId = R.layout.holder_content;
    private int columns = 6;
    private String filePath;
    private String title;

    public static MoviceListFragment newInstance(int type) {
        
        Bundle args = new Bundle();
        args.putInt(Atts.TYPE, type);
        MoviceListFragment fragment = new MoviceListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(Atts.TYPE, Types.TYPE_NETFLIX);
        title = StringUtils.getMoviceCollectionTitle(getActivity(), type);
        switch (type){
            case Types.TYPE_NETFLIX:
                columns = 6;
                layoutId = R.layout.holder_content_5;
                filePath = FilePathMangaer.getLocalNetflixPictures(getActivity());
                break;
            case Types.TYPE_YOUTUBE:
                columns = 4;
                layoutId = R.layout.holder_content_3;
                filePath = FilePathMangaer.getLocalYouTubePictures(getActivity());
                break;
            case Types.TYPE_DISNEY:
                columns = 4;
                layoutId = R.layout.holder_content_3;
                filePath = FilePathMangaer.getLocalDisneyPictures(getActivity());
                break;
            case Types.TYPE_MAX:
                columns = 6;
                layoutId = R.layout.holder_content_5;
                filePath = FilePathMangaer.getLocalNetflixPictures(getActivity());
                break;
            case Types.TYPE_HULU:
                columns = 4;
                layoutId = R.layout.holder_content_3;
                filePath = FilePathMangaer.getLocalDisneyPictures(getActivity());
                break;
            case Types.TYPE_PRIME_VIDEO:
                columns = 4;
                layoutId = R.layout.holder_content_3;
                filePath = FilePathMangaer.getLocalDisneyPictures(getActivity());
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

        mTitleView.setText(title);
        fillMovice(type);
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

    private void fillMovice(int type){
        if (App.MOVIE_MAP.containsKey(type)){
            setMoviceContent(App.MOVIE_MAP.get(type));
        }else {
            setMoviceContent(getPlaceholdings());
            call = HttpRequest.getMoviceList(newMoviceListCallback(type));
        }
    }

    private MainContentAdapter.Callback newContentCallback(){
        return new MainContentAdapter.Callback() {
            @Override
            public void onClick(Movice bean) {
                boolean success = AndroidSystem.jumpVideoApp(getActivity(), bean.getType(), bean.getUrl());
                if (!success) {
                    ToastDialog dialog = ToastDialog.newInstance(getString(R.string.place_install, title));
                    dialog.setCallback(new ToastDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1){
                                Intent intent = new Intent(getActivity(), SearchActivity.class);
                                intent.putExtra(Atts.WORD, title);
                                startActivity(intent);
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

    private ServiceRequest.Callback<MoviceList[]> newMoviceListCallback(int type){
        return new ServiceRequest.Callback<MoviceList[]>() {
            @Override
            public void onCallback(Call call, int status, MoviceList[] result) {
                if (!isAdded() || call.isCanceled() || result == null) return;

                fillData(result);

                fillMovice(type);
            }
        };
    }

    private List<TypeItem> fillData(MoviceList[] result){
        Map<Integer, TypeItem> itemMap = new LinkedHashMap<>();
        for (MoviceList child : result){
            List<Movice> youtubes = new ArrayList<>();
            List<Movice> netflixs = new ArrayList<>();
            List<Movice> disneys = new ArrayList<>();
            List<Movice> hulus = new ArrayList<>();
            List<Movice> maxs = new ArrayList<>();
            List<Movice> primes = new ArrayList<>();

            if (child.getYoutube() != null){
                itemMap.put(Types.TYPE_YOUTUBE, new TypeItem(getString(R.string.youtube), R.drawable.youtube, Types.TYPE_YOUTUBE));
                for (MoviceData item : child.getYoutube()){
                    youtubes.add(new Movice(Types.TYPE_YOUTUBE, Url.conformity(Url.YOUTUBE_PLAY, item.getVideoId()), item.getThumbnailMap().get("high"), Movice.PIC_NETWORD));
                }
            }

            MoviceList.Inner inner = child.getOthers();
            if (inner != null){
                if (inner.getDisney() != null){
                    itemMap.put(Types.TYPE_DISNEY, new TypeItem(getString(R.string.disney), R.drawable.disney, Types.TYPE_DISNEY));
                    for (MoviceData item : inner.getDisney()){
                        disneys.add(new Movice(Types.TYPE_DISNEY, Url.conformity(Url.DISNEY_PLAY, item.getDisney_id()), item.getMain_img(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getMax() != null){
                    itemMap.put(Types.TYPE_MAX, new TypeItem(getString(R.string.max), R.drawable.max, Types.TYPE_MAX));
                    for (MoviceData item : inner.getMax()){
                        maxs.add(new Movice(Types.TYPE_MAX, Url.conformity(Url.MAX_PLAY, item.getMax_id()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getHulu() != null){
                    itemMap.put(Types.TYPE_HULU, new TypeItem(getString(R.string.hulu), R.drawable.hulu, Types.TYPE_HULU));
                    for (MoviceData item : inner.getHulu()){
                        hulus.add(new Movice(Types.TYPE_HULU, Url.conformity(Url.HULU_PLAY, item.getHulu_id()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getPrime_video() != null){
                    itemMap.put(Types.TYPE_PRIME_VIDEO, new TypeItem(getString(R.string.prime_video), R.drawable.prime_video, Types.TYPE_PRIME_VIDEO));
                    for (MoviceData item : inner.getPrime_video()){
                        primes.add(new Movice(Types.TYPE_PRIME_VIDEO, Url.conformity(Url.PRIME_VIDEO_PLAY, item.getPrime_videoid()), item.getDefault_image(), Movice.PIC_NETWORD));
                    }
                }

                if (inner.getNetflix() != null){
                    itemMap.put(Types.TYPE_NETFLIX, new TypeItem(getString(R.string.netflix), R.drawable.netflix, Types.TYPE_NETFLIX));
                    for (MoviceData item : inner.getNetflix()){
                        netflixs.add(new Movice(Types.TYPE_NETFLIX, Url.conformity(Url.NETFLIX_PLAY, item.getNetflix_id()), item.getLarge_image(), Movice.PIC_NETWORD));
                    }
                }
            }

            if (netflixs.size() != 0) App.MOVIE_MAP.put(Types.TYPE_NETFLIX, netflixs);
            if (disneys.size() != 0) App.MOVIE_MAP.put(Types.TYPE_DISNEY, disneys);
            if (youtubes.size() != 0) App.MOVIE_MAP.put(Types.TYPE_YOUTUBE, youtubes);
            if (maxs.size() != 0) App.MOVIE_MAP.put(Types.TYPE_MAX, maxs);
            if (hulus.size() != 0) App.MOVIE_MAP.put(Types.TYPE_HULU, hulus);
            if (primes.size() != 0) App.MOVIE_MAP.put(Types.TYPE_PRIME_VIDEO, primes);
        }

        List<TypeItem> menus = new ArrayList<>();
        for (Map.Entry<Integer, TypeItem> entry : itemMap.entrySet()) {
            if (entry.getKey() == Types.TYPE_NETFLIX || entry.getKey() == Types.TYPE_MAX){
                menus.add(0, entry.getValue());
            }else {
                menus.add(entry.getValue());
            }
        }
        return menus;
    }

    private List<Movice> getPlaceholdings(){
        List<Movice> movices = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            movices.add(new Movice(Types.TYPE_UNKNOW, null, "empty", Movice.PIC_PLACEHOLDING));
        }
        return movices;
    }
}
