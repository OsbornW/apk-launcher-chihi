package com.soya.launcher.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

import java.util.List;

public class AppItemAdapter extends RecyclerView.Adapter<AppItemAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<AppItem> dataList;
    private Callback callback;

    private int layoutId;

    public AppItemAdapter(Context context, LayoutInflater inflater, List<AppItem> dataList, int layoutId, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.layoutId = layoutId;
        this.callback = callback;
        this.dataList = dataList;
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

    public void replace(List<AppItem> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mIV;
        private ImageView mIVSmall;
        private TextView mTitle;
        private MyFrameLayout mRootView;

        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mTitle = view.findViewById(R.id.title);
            mIVSmall = view.findViewById(R.id.image_small);
            mRootView = (MyFrameLayout) view;
        }

        public void bind(AppItem bean){
            View root = itemView.getRootView();
            mIV.setVisibility(View.GONE);
            mIVSmall.setVisibility(View.VISIBLE);
            GlideUtils.bind(context, mIVSmall, bean.getAppIcon());
            mTitle.setText(bean.getAppName());

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mRootView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in_middle : R.anim.zoom_out_middle);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });

            mRootView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onSelect(selected);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        public void unbind(){

        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onSelect(boolean selected);
        void onClick(AppItem bean);
    }
}
