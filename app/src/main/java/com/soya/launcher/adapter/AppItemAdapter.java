package com.soya.launcher.adapter;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

public class AppItemAdapter extends Presenter {
    private Context context;
    private LayoutInflater inflater;

    private Callback callback;

    private int layoutId;

    public AppItemAdapter(Context context, LayoutInflater inflater, int layoutId, Callback callback){
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
        holder.bind((AppItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private ImageView mIV;
        private ImageView mIVSmall;
        private TextView mTitle;
        private MyFrameLayout mRootView;

        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mTitle = view.findViewById(R.id.title);
            mIVSmall = view.findViewById(R.id.image_small);
            mRootView = (MyFrameLayout) view;
        }

        public void bind(AppItem bean){
            mIV.setVisibility(View.GONE);
            mIVSmall.setVisibility(View.VISIBLE);
            GlideUtils.bind(context, mIVSmall, bean.getAppIcon());
            mTitle.setText(bean.getAppName());

            mRootView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onSelect(selected);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        public void unbind(){

        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onSelect(boolean selected);
        void onClick(AppItem bean);
    }
}
