package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.Ads;
import com.soya.launcher.utils.GlideUtils;

import java.util.List;
import java.util.Map;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.Holder> {
    public static final int TYPE_MIN = 0;
    public static final int TYPE_MAX = 1;
    private Context context;
    private LayoutInflater inflater;
    private List<Ads> dataList;
    private Map<Integer, Integer> layoutMap;

    public AdsAdapter(Context context, LayoutInflater inflater, List<Ads> dataList, Map<Integer, Integer> layoutMap){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.layoutMap = layoutMap;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(layoutMap.get(viewType), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getSpanSize() == 2 ? TYPE_MAX : TYPE_MIN;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mImagView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mImagView = itemView.findViewById(R.id.image);
        }

        public void bind(Ads bean){
            GlideUtils.bind(context, mImagView, bean.getPicture());
        }
    }
}
