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

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

import org.w3c.dom.Text;

import java.util.List;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Movice> dataList;

    private Callback callback;
    private int layoutId;

    public MainContentAdapter(Context context, LayoutInflater inflater, List<Movice> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.layoutId = layoutId;
        this.dataList = dataList;
        this.callback = callback;
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

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void replace(List<Movice> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private MyFrameLayout mCardView;
        private ImageView mIV;
        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mCardView = (MyFrameLayout) view;
        }

        public void bind(Movice item){
            View root = itemView.getRootView();
            switch (item.getPicType()){
                case Movice.PIC_ASSETS:
                    GlideUtils.bind(context, mIV, FileUtils.readAssets(context, (String) item.getImageUrl()));
                    break;
                case Movice.PIC_NETWORD:
                    Object image = item.getImageUrl();
                    if (!item.isLocal() && !TextUtils.isEmpty(item.getUrl()) && App.MOVIE_IMAGE.containsKey(item.getUrl())){
                        Object obj = App.MOVIE_IMAGE.get(item.getUrl());
                        if (obj != null) image = obj;
                    }
                    GlideUtils.bind(context, mIV, TextUtils.isEmpty((CharSequence) image) ? R.drawable.transparent : image);
                    break;
                default:
                    GlideUtils.bind(context, mIV, R.drawable.transparent);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(item.getUrl())) return;
                    if (callback != null) callback.onClick(item);
                }
            });

            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mCardView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in_middle : R.anim.zoom_out_middle);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });

            mCardView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onFouces(selected, item);
                }
            });
        }

        public void unbind(){

        }
    }

    public interface Callback{
        void onClick(Movice bean);
        void onFouces(boolean hasFocus, Movice bean);
    }
}
