package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.City;

public class CityAdapter extends Presenter {

    private final Context context;
    private final LayoutInflater inflater;
    private final int layoutId;
    private final Callback callback;

    public CityAdapter(Context context, LayoutInflater inflater, int layoutId, Callback callback){
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
        holder.bind((City) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public class Holder extends ViewHolder {
        private final TextView mTitleView;
        private final TextView mDesView;

        public Holder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mDesView = view.findViewById(R.id.des);
        }

        public void bind(City bean){
            mTitleView.setText(bean.getCityName());
            if (!TextUtils.isEmpty(bean.getISO_Code()))
                mDesView.setText(String.format("%s : (%s, %s)", bean.getISO_Code(), bean.getLatitude(), bean.getLongitude()));
            else
                mDesView.setText("");

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
        void onClick(City bean);
    }
}
