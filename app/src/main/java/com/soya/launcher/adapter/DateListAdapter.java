package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.DateItem;

import java.util.List;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.Holder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<DateItem> dataList;

    private final Callback callback;

    public DateListAdapter(Context context, LayoutInflater inflater, List<DateItem> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_date_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView mTitleView;
        private final TextView mDescView;
        private final Switch mSwitch;

        public Holder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mDescView = view.findViewById(R.id.desc);
            mSwitch = view.findViewById(R.id.switch_item);
        }

        public void bind(DateItem bean){
            int index = dataList.indexOf(bean);
            mTitleView.setText(bean.getTitle());
            mDescView.setText(bean.getDescription());
            mSwitch.setChecked(bean.isSwitch());
            mSwitch.setVisibility(bean.isUseSwitch() ? View.VISIBLE : View.GONE);
            mDescView.setVisibility(bean.isUseSwitch() ? View.GONE : View.VISIBLE);
            mTitleView.setEnabled(!dataList.get(0).isSwitch() || (index != 1 && index != 2));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        public void unbind(){}
    }

    public interface Callback{
        void onClick(DateItem bean);
    }
}
