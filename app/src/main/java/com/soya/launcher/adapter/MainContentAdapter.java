package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

import org.w3c.dom.Text;

import java.util.List;

public class MainContentAdapter extends Presenter {
    private Context context;
    private LayoutInflater inflater;

    private Callback callback;
    private int layoutId;

    public MainContentAdapter(Context context, LayoutInflater inflater, int layoutId, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.layoutId = layoutId;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((Movice) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private MyFrameLayout mCardView;
        private ImageView mIV;
        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mCardView = (MyFrameLayout) view;
        }

        public void bind(Movice item){
            switch (item.getPicType()){
                case Movice.PIC_ASSETS:
                    GlideUtils.bind(context, mIV, FileUtils.readAssets(context, (String) item.getImageUrl()));
                    break;
                case Movice.PIC_NETWORD:
                    Object image = item.getImageUrl();
                    if (!item.isLocal() && !TextUtils.isEmpty(item.getUrl()) && App.MOVIE_IMAGE.containsKey(item.getUrl())){
                        Object obj = App.MOVIE_IMAGE.get(item.getUrl());
                        if (obj != null) image = obj;
                    }
                    GlideUtils.bind(context, mIV, TextUtils.isEmpty((CharSequence) image) ? R.drawable.transparent : image);
                    break;
                default:
                    GlideUtils.bind(context, mIV, R.drawable.transparent);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(item.getUrl())) return;
                    if (callback != null) callback.onClick(item);
                }
            });

            mCardView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onFouces(selected, item);
                }
            });
        }

        public void unbind(){

        }
    }

    public interface Callback{
        void onClick(Movice bean);
        void onFouces(boolean hasFocus, Movice bean);
    }
}
