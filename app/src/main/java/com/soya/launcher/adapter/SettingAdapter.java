package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.view.MyFrameLayout;

public class SettingAdapter extends Presenter {
    private final Context context;
    private final LayoutInflater inflater;

    private Callback callback;

    private final int layoutId;

    public SettingAdapter(Context context, LayoutInflater inflater, Callback callback, int layoutId){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
        this.layoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((SettingItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private final MyFrameLayout mRootView;
        private final ImageView mIV;
        private final TextView mTitleView;
        public Holder(View view) {
            super(view);
            mRootView = (MyFrameLayout) view;
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
        }

        public void bind(SettingItem bean){
            mIV.setImageResource(bean.getIco());
            mTitleView.setText(bean.getName());

           mRootView.setCallback(new SelectedCallback() {
               @Override
               public void onSelect(boolean selected) {
                   if (callback != null) callback.onSelect(selected, bean);
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
        void onSelect(boolean selected, SettingItem bean);
        void onClick(SettingItem bean);
    }
}
