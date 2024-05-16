package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.WebItem;
import com.soya.launcher.utils.GlideUtils;

public class WebAdapter extends Presenter {
    private final Context context;
    private final LayoutInflater inflater;

    private Callback callback;

    public WebAdapter(Context context, LayoutInflater inflater, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_web, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((WebItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private final ImageView mImageView;

        public Holder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image);
        }

        public void bind(WebItem bean){
            GlideUtils.bind(context, mImageView, bean.getIcon());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        public void unbind(){}
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(WebItem bean);
    }
}
