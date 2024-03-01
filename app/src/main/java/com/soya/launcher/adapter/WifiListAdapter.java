package com.soya.launcher.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.WifiItem;
import com.soya.launcher.utils.AndroidSystem;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<WifiItem> dataList;

    private Callback callback;
    private boolean useNext;

    public WifiListAdapter(Context context, LayoutInflater inflater, List<WifiItem> dataList, boolean useNext, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.callback = callback;
        this.useNext = useNext;
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
        return new Holder(inflater.inflate(R.layout.holder_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public List<WifiItem> getDataList() {
        return dataList;
    }

    public void replace(List<WifiItem> results){
        dataList.clear();
        dataList.addAll(results);
        notifyDataSetChanged();
    }

    public void add(int position, List<WifiItem> list){
        dataList.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }

    public void remove(List<WifiItem> list){
        for (WifiItem item : list){
            int index = dataList.indexOf(item);
            if (index != -1) {
                dataList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private ImageView mLockView;
        private ImageView mSignalView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            if (useNext){
                itemView.setNextFocusLeftId(R.id.next);
                itemView.setNextFocusRightId(R.id.next);
            }
            mTitleView = itemView.findViewById(R.id.title);
            mLockView = itemView.findViewById(R.id.lock);
            mSignalView = itemView.findViewById(R.id.signal);
        }

        public void bind(WifiItem bean){
            ScanResult result = bean.getItem();
            boolean usePass = AndroidSystem.isUsePassWifi(result);

            mTitleView.setText(result.SSID);
            mLockView.setVisibility(usePass ? View.VISIBLE : View.GONE);
            switch (WifiManager.calculateSignalLevel(bean.getItem().level, 5)){
                case 1:
                case 2:
                    mSignalView.setImageResource(R.drawable.baseline_wifi_1_bar_100);
                    break;
                case 3:
                    mSignalView.setImageResource(R.drawable.baseline_wifi_2_bar_100);
                    break;
                default:
                    mSignalView.setImageResource(R.drawable.baseline_wifi_100);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(result);
                }
            });
        }
    }

    public interface Callback{
        void onClick(ScanResult bean);
    }
}
