package com.soya.launcher.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.open.system.SystemUtils;
import com.soya.launcher.R;
import com.soya.launcher.bean.BluetoothItem;

import java.util.List;

public class BluetoothItemAdapter extends RecyclerView.Adapter<BluetoothItemAdapter.Holder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<BluetoothItem> dataList;

    private final Callback callback;

    public BluetoothItemAdapter(Context context, LayoutInflater inflater, List<BluetoothItem> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.callback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_bluetooth, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public List<BluetoothItem> getDataList() {
        return dataList;
    }

    public void replace(List<BluetoothItem> results){
        dataList.clear();
        dataList.addAll(results);
        notifyDataSetChanged();
    }

    public void add(int position, List<BluetoothItem> list){
        dataList.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }

    public void add(List<BluetoothItem> list){
        int size = dataList.size();
        dataList.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    public void remove(List<BluetoothItem> list){
        for (BluetoothItem item : list){
            int index = dataList.indexOf(item);
            if (index != -1) {
                dataList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView mTitleView;
        private final TextView mDescView;
        private final ImageView mCheckView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mCheckView = itemView.findViewById(R.id.check);
            mDescView = itemView.findViewById(R.id.desc);
        }

        public void bind(BluetoothItem bean){
            BluetoothDevice device = bean.getDevice();
            mTitleView.setText(device.getName());
            mDescView.setVisibility(View.GONE);
            mCheckView.setVisibility(SystemUtils.isConnected(bean.getDevice()) ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }
    }

    public interface Callback{
        void onClick(BluetoothItem bean);
    }
}
