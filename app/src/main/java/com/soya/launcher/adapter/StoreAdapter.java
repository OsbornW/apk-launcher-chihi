package com.soya.launcher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import com.soya.launcher.h27002.H27002ExtKt;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.Holder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<AppItem> dataList;

    private final Callback callback;

    public StoreAdapter(Context context, LayoutInflater inflater, List<AppItem> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.callback = callback;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_app_store, parent, false));
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
        private final MyFrameLayout mRootView;
        private final ImageView mIV;
        private final TextView mTitleView;
        private final TextView mMesView;

        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
            mMesView = view.findViewById(R.id.mes);

            mRootView = (MyFrameLayout) view;
            view.setNextFocusUpId(R.id.header);
        }

        public void bind(AppItem bean){
            View root = itemView.getRootView();
            if(TextUtils.isEmpty(bean.getLocalIcon())){
                GlideUtils.bind(context, mIV,  bean.getAppIcon());
            }else {
                mIV.setImageDrawable(H27002ExtKt.getDrawableByName(context,bean.getLocalIcon()));
            }
            mTitleView.setText(bean.getAppName());
            if (!TextUtils.isEmpty(bean.getAppSize()))
                mMesView.setText(String.format("%.01f★ | %s", bean.getScore(), bean.getAppSize()));
            else
                mMesView.setText("");

            mRootView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onSelect(selected, bean);
                }
            });

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mRootView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in_middle : R.anim.zoom_out_middle);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
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

    public interface Callback{
        void onSelect(boolean selected, AppItem bean);
        void onClick(AppItem bean);
    }
}
