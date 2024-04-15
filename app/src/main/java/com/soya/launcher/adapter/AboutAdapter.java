package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.AboutItem;
import com.soya.launcher.bean.SettingItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.view.MyFrameLayout;

public class AboutAdapter extends Presenter {

    private Context context;
    private LayoutInflater inflater;

    private Callback callback;

    public AboutAdapter(Context context, LayoutInflater inflater){
        this.context = context;
        this.inflater = inflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_about, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((AboutItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private MyFrameLayout mRootView;
        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mDescView;


        public Holder(View view) {
            super(view);
            mRootView = (MyFrameLayout) view;
            mIconView = view.findViewById(R.id.icon);
            mTitleView = view.findViewById(R.id.title);
            mDescView = view.findViewById(R.id.desc);
        }

        public void bind(AboutItem bean){
            mIconView.setImageResource(bean.getIcon());
            mTitleView.setText(bean.getTitle());
            mDescView.setText(bean.getDescription());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });

            mRootView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {

                }
            });
        }

        public void unbind(){

        }
    }

    public AboutAdapter setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback{
        void onClick(AboutItem bean);
    }
}
