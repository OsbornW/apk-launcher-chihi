package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soya.launcher.R;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.GlideUtils;
import com.soya.launcher.view.MyCardView;

import java.util.List;

public class MainHeaderAdapter extends Presenter {
    private Context context;
    private LayoutInflater inflater;
    private int selectItem = -1;

    private Callback callback;
    private List<TypeItem> items;
    private ArrayObjectAdapter adapter;

    public MainHeaderAdapter(Context context, LayoutInflater inflater, List<TypeItem> items,  Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.items = items;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_header, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((TypeItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public void setAdapter(ArrayObjectAdapter adapter) {
        this.adapter = adapter;
    }

    public class Holder extends ViewHolder {
        private ImageView mIV;
        private TextView mTitleView;
        private MyCardView mCardView;

        public Holder(View view) {
            super(view);
            mIV = view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
            mCardView = (MyCardView) view;
        }

        public void bind(TypeItem item){
            mTitleView.setBackgroundResource(R.drawable.light_item);
            mTitleView.setTextColor(context.getColorStateList(R.color.text_selector_color_1));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(item);
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
