package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
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

public class StoreAdapter extends Presenter {

    private Context context;
    private LayoutInflater inflater;

    private Callback callback;

    public StoreAdapter(Context context, LayoutInflater inflater, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_app_store, parent, false));
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
        private MyFrameLayout mRootView;
        private ImageView mIV;
        private TextView mTitleView;
        private TextView mMesView;

        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
            mMesView = view.findViewById(R.id.mes);

            mRootView = (MyFrameLayout) view;
            view.setNextFocusUpId(R.id.header);
        }

        public void bind(AppItem bean){

            GlideUtils.bind(context, mIV, TextUtils.isEmpty(bean.getLocalIcon()) ? bean.getAppIcon() : bean.getLocalIcon());
            mTitleView.setText(bean.getAppName());
            if (!TextUtils.isEmpty(bean.getAppSize()))
                mMesView.setText(String.format("%.01f★ | %s", bean.getScore(), bean.getAppSize()));
            else
                mMesView.setText("");

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

    public interface Callback{
        void onSelect(boolean selected, AppItem bean);
        void onClick(AppItem bean);
    }
}
