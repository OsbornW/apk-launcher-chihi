package com.soya.launcher.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.HomeItem;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.ext.GlideExtKt;
import com.soya.launcher.h27002.H27002ExtKt;
import com.soya.launcher.http.response.HomeResponse;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyCardView;

import java.util.List;
import java.util.function.Consumer;

public class MainHeaderAdapter extends RecyclerView.Adapter<MainHeaderAdapter.Holder> {
    private final Context context;
    private final LayoutInflater inflater;
    private final int selectItem = -1;

    private final Callback callback;
    private final List<TypeItem> items;

    public MainHeaderAdapter(Context context, LayoutInflater inflater, List<TypeItem> items,  Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position),position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replace(List<TypeItem> list){
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView mIV;
        private final TextView mTitleView;
        private final MyCardView mCardView;


        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
            mCardView = view.findViewById(R.id.root);
        }

        public void bind(TypeItem item,int position){
            View root = itemView.getRootView();
            mTitleView.setBackgroundResource(R.drawable.light_item);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTitleView.setTextColor(context.getColorStateList(R.color.text_selector_color_1));
            }else {
                ColorStateList colorStateList = ResourcesCompat.getColorStateList(context.getResources(), R.color.text_selector_color_1, null);
                mTitleView.setTextColor(colorStateList);

            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(item);
                }
            });

            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mCardView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in_max : R.anim.zoom_out_max);
                    animation.setFillAfter(true);
                    itemView.startAnimation(animation);
                }
            });

            mCardView.setCallback(new SelectedCallback() {
                @Override
                public void onSelect(boolean selected) {
                    if (callback != null) callback.onSelect(selected, item);
                }
            });

            /*if(item.getLayoutType()==2){
                mIV.setScaleType(ImageView.ScaleType.FIT_XY);
            }else {
                mIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }*/

            switch (item.getIconType()){
                case TypeItem.TYPE_ICON_IMAGE_RES:

                    mIV.setImageResource((Integer) item.getIcon());
                    break;
                case TypeItem.TYPE_ICON_ASSETS:

                    GlideUtils.bind(context, mIV, FileUtils.readAssets(context, (String) item.getIcon()));
                    break;
                default:
                    if(item.getIconName()==null||item.getIconName().isEmpty()){
                        GlideExtKt.bindImageView(mIV, item.getIcon(),null);

                    }else {
                        GlideExtKt.bindImageView(mIV, item.getIcon(),H27002ExtKt.getDrawableByName(context,item.getIconName()));

                    }


            }
            mTitleView.setText(item.getName());
        }

        public void unbind(){
        }
    }

    public interface Callback{
        void onClick(TypeItem bean);
        void onSelect(boolean selected, TypeItem bean);
    }
}
