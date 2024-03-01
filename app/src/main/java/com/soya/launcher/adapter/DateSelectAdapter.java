package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;

import java.util.List;

public class DateSelectAdapter extends RecyclerView.Adapter<DateSelectAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Integer> dataList;

    private Callback callback;
    private Integer select;

    public DateSelectAdapter(Context context, LayoutInflater inflater, List<Integer> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_date_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public Integer getSelect() {
        return select;
    }

    public void setSelect(Integer select) {
        this.select = select;
        notifyItemRangeChanged(0, dataList.size());
    }

    public List<Integer> getDataList() {
        return dataList;
    }

    public void replace(List<Integer> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTitleView;

        public Holder(View view) {
            super(view);
            mTitleView = (TextView) view;
        }

        public void bind(Integer bean){
            mTitleView.setSelected(bean.equals(select));
            mTitleView.setText(String.valueOf(bean));

            mTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = bean;
                    notifyItemRangeChanged(0, dataList.size());
                    if (callback != null) callback.onClick(bean);
                }
            });
        }
    }

    public interface Callback{
        void onClick(Integer bean);
    }
}
