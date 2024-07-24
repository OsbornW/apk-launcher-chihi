package com.soya.launcher.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.ext.GlideExtKt;
import com.soya.launcher.h27002.H27002ExtKt;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyFrameLayout;

import org.w3c.dom.Text;

import java.util.List;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.Holder> {
    private final Context context;
    private final LayoutInflater inflater;
    private final List<Movice> dataList;

    private final Callback callback;
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
        holder.bind(dataList.get(position),position);
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
        private final MyFrameLayout mCardView;
        private final ImageView mIV;
        private final TextView tvName;
        private final TextView tvLoadding;
        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mCardView = (MyFrameLayout) view;
            tvName = view.findViewById(R.id.tv_name);
            tvLoadding = view.findViewById(R.id.tv_loadding);
        }

        public void bind(Movice item,int position){
            View root = itemView.getRootView();
            switch (item.getPicType()){
                case Movice.PIC_ASSETS:
                    Log.d("zy1996", "bind: 当前要显示的本地图片是------"+item.getImageUrl());
                    GlideUtils.bind(context, mIV, FileUtils.readAssets(context, (String) item.getImageUrl()));
                    break;
                case Movice.PIC_NETWORD:
                    Object image = item.getImageUrl();
                    if(tvName!=null){
                        tvName.setText(item.getId());
                    }
                    if (!item.isLocal() && !TextUtils.isEmpty(item.getUrl()) && App.MOVIE_IMAGE.containsKey(item.getUrl())){
                        Object obj = App.MOVIE_IMAGE.get(item.getUrl());
                        if (obj != null) image = obj;
                    }

                    //String path = FilePathMangaer.getMoviePath(context) + "/" + item.getPlaceHolderList().get(position).path;
                    //Drawable drawable = H27002ExtKt.getDrawableFromPath(context,path);

                    Log.e("zengyue", "bind: 当前要加载的路径是"+ image );
                    GlideExtKt.bindImageView( mIV, TextUtils.isEmpty((CharSequence) image) ? R.drawable.transparent : image,H27002ExtKt.getDrawableByName(context,(String) item.getImageName()));
                    if(tvLoadding!=null){
                        if(item.getId().isEmpty()){
                            tvLoadding.setVisibility(View.GONE);
                        }
                    }
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
