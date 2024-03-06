package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.Notify;
import com.soya.launcher.utils.GlideUtils;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.Holder> {

    private Context context;
    private List<Notify> dataList;
    private LayoutInflater inflater;
    private int layoutId;

    public NotifyAdapter(Context context, LayoutInflater inflater, List<Notify> dataList, int layoutId){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<Notify> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mIV;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mIV = itemView.findViewById(R.id.image);
        }

        public void bind(Notify bean){
            itemView.setFocusable(false);
            itemView.setFocusableInTouchMode(false);
            itemView.setEnabled(false);
            GlideUtils.bind(context, mIV, bean.getIcon());
        }
    }
}
