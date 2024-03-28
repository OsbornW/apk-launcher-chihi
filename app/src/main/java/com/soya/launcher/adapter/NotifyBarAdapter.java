package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.utils.GlideUtils;

import java.util.List;

public class NotifyBarAdapter extends RecyclerView.Adapter<NotifyBarAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<AppItem> dataList;

    private Callback callback;

    public NotifyBarAdapter(Context context, LayoutInflater inflater, List<AppItem> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_notify_bar, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position, @NonNull List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.upset(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void remove(AppItem bean){
        int index = dataList.indexOf(bean);
        if (index != -1){
            dataList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void replace(List<AppItem> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView mIV;
        private TextView mTileView;
        private TextView mActionView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mIV = itemView.findViewById(R.id.icon);
            mTileView = itemView.findViewById(R.id.title);
            mActionView = itemView.findViewById(R.id.action);
        }

        public void bind(AppItem bean){
            GlideUtils.bind(context, mIV, TextUtils.isEmpty(bean.getLocalIcon()) ? bean.getAppIcon() : bean.getLocalIcon());
            mTileView.setText(bean.getAppName());
            upset(bean);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        private void upset(AppItem bean){
            switch (bean.getStatus()){
                case AppItem.STATU_IDLE:
                    mActionView.setText(context.getString(R.string.waiting));
                    break;
                case AppItem.STATU_DOWNLOAD_FAIL:
                    mActionView.setText(context.getString(R.string.download_fail_mask));
                    break;
                case AppItem.STATU_DOWNLOADING:
                    mActionView.setText(String.format("%.01f%%", bean.getProgress() * 100f));
                    break;
                case AppItem.STATU_INSTALL_FAIL:
                    mActionView.setText(context.getString(R.string.install_failed));
                    break;
                case AppItem.STATU_INSTALL_SUCCESS:
                    mActionView.setText(context.getString(R.string.installed));
                    break;
                case AppItem.STATU_INSTALLING:
                    mActionView.setText(context.getString(R.string.installing));
                    break;
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(AppItem bean);
    }
}
