package com.soya.launcher.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.Language;
import com.soya.launcher.bean.SimpleTimeZone;

import java.util.List;
import java.util.TimeZone;

public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.Holder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<SimpleTimeZone> dataList;

    private TimeZone select;
    private Callback callback;

    public TimeZoneAdapter(Context context, LayoutInflater inflater, List<SimpleTimeZone> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_time_zone, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setSelect(TimeZone select) {
        this.select = select;
        notifyDataSetChanged();
    }

    public void replace(List<SimpleTimeZone> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView mTitleView;
        private final TextView mDescView;
        private final ImageView mCheckView;

        public Holder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mDescView = view.findViewById(R.id.desc);
            mCheckView = view.findViewById(R.id.check);
        }

        public void bind(SimpleTimeZone bean){
            boolean isSelect = select != null && bean.getZone().getID().equals(select.getID());
            if(isSelect){
                Log.e("zengyue1", "bind: 当前名字"+bean.getZone().getID()+"=="+bean.getName()+"=="+select.getDisplayName() );
            }
            mTitleView.setText(bean.getName());
            mDescView.setText(bean.getDesc());
            mCheckView.setVisibility(isSelect ? View.VISIBLE : View.GONE);
            mDescView.setVisibility(isSelect ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = bean.getZone();
                    if (callback != null) callback.onClick(bean);
                }
            });
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(SimpleTimeZone bean);
    }
}
