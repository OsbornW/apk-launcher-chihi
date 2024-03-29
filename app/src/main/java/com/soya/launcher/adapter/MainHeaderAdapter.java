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
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soya.launcher.R;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyCardView;

import java.util.List;

public class MainHeaderAdapter extends RecyclerView.Adapter<MainHeaderAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private int selectItem = -1;

    private Callback callback;
    private List<TypeItem> items;

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
        holder.bind(items.get(position));
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
        private ImageView mIV;
        private TextView mTitleView;
        private MyCardView mCardView;


        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
            mCardView = view.findViewById(R.id.root);
        }

        public void bind(TypeItem item){
            View root = itemView.getRootView();
            mTitleView.setBackgroundResource(R.drawable.light_item);
            mTitleView.setTextColor(context.getColorStateList(R.color.text_selector_color_1));
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

            switch (item.getIconType()){
                case TypeItem.TYPE_ICON_IMAGE_RES:
                    mIV.setImageResource((Integer) item.getIcon());
                    break;
                case TypeItem.TYPE_ICON_ASSETS:
                    GlideUtils.bind(context, mIV, FileUtils.readAssets(context, (String) item.getIcon()));
                    break;
                default:
                    GlideUtils.bind(context, mIV, item.getIcon());
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
