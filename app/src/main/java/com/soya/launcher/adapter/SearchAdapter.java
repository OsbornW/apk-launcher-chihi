package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.GlideUtils;

public class SearchAdapter extends Presenter {

    private final Context context;
    private final LayoutInflater inflater;
    private final Callback callback;

    public SearchAdapter(Context context, LayoutInflater inflater, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_search, parent, false));
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
        private final ImageView mIV;
        private final TextView mTitleView;

        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
        }

        public void bind(Movice bean){
            switch (bean.getPicType()){
                case Movice.PIC_ASSETS:
                    GlideUtils.bind(context, mIV, AndroidSystem.getImageFromAssetsFile(context, bean.getPicture()));
                    break;
                case Movice.PIC_NETWORD:
                    GlideUtils.bind(context, mIV, bean.getPicture());
                    break;
                default:
                    GlideUtils.bind(context, mIV, R.drawable.transparent);
            }

            mTitleView.setText(bean.getTitle());
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
        void onClick(Movice bean);
    }
}
