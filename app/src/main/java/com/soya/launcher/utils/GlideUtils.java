package com.soya.launcher.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.soya.launcher.App;
import com.soya.launcher.bean.GlideApp;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtils {
    public static void bind(Context context, ImageView view, Object load){
        bind(context, view, load, DiskCacheStrategy.ALL);
    }

    public static void bind(Context context, ImageView view, Object load, DiskCacheStrategy strategy){
        GlideApp.with(App.getInstance())
                .load(load)
                .diskCacheStrategy(strategy)
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bind(Context context, ImageView view, Object load, int duration){
        Glide.with(App.getInstance())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bindCrop(Context context, ImageView view, Object load){
        Glide.with(App.getInstance())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bindBlur(Context context, ImageView view, Object load){
        bindBlur(context, view, load, null, 25,8);
    }

    public static void bindBlur(Context context, ImageView view, Object load, Drawable placeholder, int radius, int sampling){
        Glide.with(App.getInstance())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholder)
                .override(640, 360)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(radius,sampling)))
                .into(view);
    }

    public static void bindBlurCross(Context context, ImageView view, Object load, int duration){
        Glide.with(App.getInstance())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,8)))
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void download(Context context, Object url, DownloadListener listener) {
        Glide.with(context).downloadOnly().load(url).diskCacheStrategy(DiskCacheStrategy.DATA).addListener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                if (listener != null) listener.onCallback(null);
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null) listener.onCallback(resource);
                return false;
            }
        }).preload();
    }

    public interface DownloadListener {
        void onCallback(File resource);
    }
}
