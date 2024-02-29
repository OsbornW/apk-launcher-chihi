package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.Projector;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.view.MyFrameLayout;

public class ProjectorAdapter extends Presenter {
    private Context context;
    private LayoutInflater inflater;

    private Callback callback;
    private int layoutId;

    public ProjectorAdapter(Context context, LayoutInflater inflater, int layoutId, Callback callback){
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
        holder.bind((Projector) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    public class Holder extends ViewHolder {
        private ImageView mIV;
        private MyFrameLayout mRootView;
        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mRootView = (MyFrameLayout) view;
            view.setNextFocusUpId(R.id.header);
        }

        public void bind(Projector bean){
            mIV.setImageResource(bean.getIcon());
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
    }

    public interface Callback{
        void onSelect(boolean selected, Projector bean);
        void onClick(Projector bean);
    }
}
