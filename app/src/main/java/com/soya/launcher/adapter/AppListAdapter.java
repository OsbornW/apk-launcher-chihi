package com.soya.launcher.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
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
import com.soya.launcher.callback.SelectedCallback;
import com.soya.launcher.config.Config;
import com.soya.launcher.view.AppLayout;
import com.soya.launcher.view.MyFrameLayout;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<ApplicationInfo> dataList;
    private Callback callback;

    private int layoutId;

    public AppListAdapter(Context context, LayoutInflater inflater, List<ApplicationInfo> dataList, int layoutId, Callback callback){
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

    public void replace(List<ApplicationInfo> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mIV;
        private ImageView mIVSmall;
        private TextView mTitle;
        private MyFrameLayout mRootView;
        private AppLayout mAppLayout;

        public Holder(View view) {
            super(view);
            view.setNextFocusUpId(R.id.header);
            mIV = view.findViewById(R.id.image);
            mTitle = view.findViewById(R.id.title);
            mIVSmall = view.findViewById(R.id.image_small);
            mAppLayout = view.findViewById(R.id.root);
            mRootView = (MyFrameLayout) view;
        }

        public void bind(ApplicationInfo bean){
            View root = itemView.getRootView();
            PackageManager pm = context.getPackageManager();
            //Drawable banner = bean.activityInfo.loadBanner(pm);
            Drawable banner = null;
            if (banner != null){
                mIV.setImageDrawable(banner);
            }else {
                mIVSmall.setImageDrawable(bean.loadIcon(pm));
            }

            if (Config.COMPANY == 4 && bean.packageName.equals("com.mediatek.tvinput")){
                mTitle.setText("HDMI");
            }else {
                mTitle.setText(bean.loadLabel(pm));
            }

            mIV.setVisibility(banner == null ? View.GONE : View.VISIBLE);
            mIVSmall.setVisibility(banner == null ? View.VISIBLE : View.GONE);

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

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mRootView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in_middle : R.anim.zoom_out_middle);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });

            mAppLayout.setListener(new AppLayout.EventListener() {
                @Override
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                    if (callback != null && event.getKeyCode() == KeyEvent.KEYCODE_MENU){
                        callback.onMenuClick(bean);
                        return false;
                    }
                    return true;
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
        void onClick(ApplicationInfo bean);
        void onMenuClick(ApplicationInfo bean);
    }
}
