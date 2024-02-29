package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.Ads;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Ads> dataList;

    public BannerAdapter(Context context, LayoutInflater inflater, List<Ads> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
