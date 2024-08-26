package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.bean.Wallpaper;
import com.soya.launcher.cache.AppCache;
import com.soya.launcher.config.Config;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.utils.GlideUtils;

public abstract class AbsFragment extends Fragment {
    public abstract int getLayoutId();

     public ImageView mWallpaperView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        init(view, inflater);
        initBefore(view, inflater);
        initBind(view, inflater);
        return view;
    }

    protected void init(View view, LayoutInflater inflater){
        if (getWallpaperView() != -1) {
            mWallpaperView = view.findViewById(getWallpaperView());
            setWallpaper(mWallpaperView);
        }
    }

    protected void initBefore(View view, LayoutInflater inflater){}

    protected void initBind(View view, LayoutInflater inflater){}

    protected void requestFocus(View view){
        requestFocus(view, 0);
    }

    protected void requestFocus(View view, int delayed){
        if (delayed > 0){
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            }, delayed);
        }else {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            });
        }
    }

    protected int getWallpaperView(){
        return -1;
    }

    protected void updateWallpaper(){
        setWallpaper(mWallpaperView);
    }

    protected void setWallpaper(ImageView view){
        int id = Config.COMPANY == 0 ? R.drawable.wallpaper_22 : R.drawable.wallpaper_1;
        for (Wallpaper wallpaper : AppCache.INSTANCE.getWALLPAPERS()){
            if (wallpaper.id == PreferencesManager.getWallpaper()){
                id = wallpaper.picture;
                break;
            }
        }
        GlideUtils.bindBlur(getActivity(), mWallpaperView, id);
    }
}
