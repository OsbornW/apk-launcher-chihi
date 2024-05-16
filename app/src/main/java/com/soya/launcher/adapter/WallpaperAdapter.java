package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.Wallpaper;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.view.MyFrameLayout;

public class WallpaperAdapter extends Presenter {

    private final Context context;
    private final LayoutInflater inflater;
    private final Callback callback;

    public WallpaperAdapter(Context context, LayoutInflater inflater, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_wallpaper, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((Wallpaper) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {

        private final MyFrameLayout mRootView;
        private final ImageView mIV;
        private final View mCheckView;

        public Holder(View view) {
            super(view);
            mRootView = (MyFrameLayout) view;
            mIV = view.findViewById(R.id.image);
            mCheckView = view.findViewById(R.id.check);
        }

        public void bind(Wallpaper bean){
            mRootView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onSelect(selected, bean);
                }
            });

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
            mIV.setImageResource(bean.getPicture());
            mCheckView.setVisibility(bean.getId() == PreferencesManager.getWallpaper() ? View.VISIBLE : View.GONE);
        }

        public void unbind(){}
    }

    public interface Callback{
        void onSelect(boolean select, Wallpaper bean);
        void onClick(Wallpaper bean);
    }
}
