package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.Weather;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.utils.AndroidSystem;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Weather> dataList;

    public WeatherAdapter(Context context, LayoutInflater inflater, List<Weather> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<Weather> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mDateView;
        private ImageView mIconView;
        private TextView mRangeView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mDateView = itemView.findViewById(R.id.date);
            mIconView = itemView.findViewById(R.id.icon);
            mRangeView = itemView.findViewById(R.id.range);
        }

        public void bind(Weather bean){
            mDateView.setText(bean.getDateDes());
            mIconView.setImageBitmap(AndroidSystem.getImageForAssets(context, FilePathMangaer.getWeatherIcon(bean.getIcon())));
            if (!TextUtils.isEmpty(bean.getDatetime())) mRangeView.setText(String.format("%.0f℉  %.0f℉", bean.getTempmin(), bean.getTempmax()));
        }
    }
}
