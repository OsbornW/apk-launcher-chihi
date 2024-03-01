package com.soya.launcher.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.DivSearch;
import com.soya.launcher.bean.WebItem;

import java.util.List;

public class FullSearchAdapter extends RecyclerView.Adapter<FullSearchAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private Callback callback;

    private List<DivSearch> dataList;

    public FullSearchAdapter(Context context, LayoutInflater inflater, List<DivSearch> dataList, Callback callback){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_full_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void sync(DivSearch bean){
        int index = dataList.indexOf(bean);
        if (index != -1) notifyItemChanged(index);
    }

    public void replace(List<DivSearch> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(int index, DivSearch bean){
        dataList.add(index, bean);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private HorizontalGridView mContentGrid;
        private TextView mTitleView;
        private View mNoneView;
        private View mProgressView;

        public Holder(View view) {
            super(view);
            mContentGrid = view.findViewById(R.id.content);
            mTitleView = view.findViewById(R.id.title);
            mNoneView = view.findViewById(R.id.mask);
            mProgressView = view.findViewById(R.id.progressBar);
        }

        public void bind(DivSearch bean){
            mTitleView.setText(bean.getTitle());
            switch (bean.getType()){
                case 0:
                    mContentGrid.setVisibility(View.VISIBLE);
                    mNoneView.setVisibility(View.GONE);
                    mProgressView.setVisibility(View.GONE);
                    setAppContent(bean);
                    break;
                case 1:
                    if (bean.getState() == 0){
                        mContentGrid.setVisibility(View.GONE);
                        mProgressView.setVisibility(View.VISIBLE);
                        mNoneView.setVisibility(View.GONE);
                    }else if (bean.getState() == 1){
                        mContentGrid.setVisibility(View.GONE);
                        mProgressView.setVisibility(View.GONE);
                        mNoneView.setVisibility(View.VISIBLE);
                    }else {
                        mContentGrid.setVisibility(View.VISIBLE);
                        mProgressView.setVisibility(View.GONE);
                        mNoneView.setVisibility(View.GONE);
                        setAppStore(bean);
                    }
                    break;
                case 2:
                    mContentGrid.setVisibility(View.VISIBLE);
                    mNoneView.setVisibility(View.GONE);
                    mProgressView.setVisibility(View.GONE);
                    web(bean);
                    break;
            }
        }

        private void setAppContent(DivSearch bean){
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AppListAdapter(context, inflater, R.layout.holder_app_3, new AppListAdapter.Callback() {
                @Override
                public void onSelect(boolean selected) {

                }

                @Override
                public void onClick(ApplicationInfo child) {
                    if (callback != null) callback.onClick(bean.getType(), child);
                }
            }));
            ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
            FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
            mContentGrid.setAdapter(itemBridgeAdapter);
            arrayObjectAdapter.addAll(0, bean.getList());
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void setAppStore(DivSearch bean){
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new AppItemAdapter(context, inflater, R.layout.holder_app_3, new AppItemAdapter.Callback() {
                @Override
                public void onSelect(boolean selected) {

                }

                @Override
                public void onClick(AppItem child) {
                    if (callback != null) callback.onClick(bean.getType(), child);
                }
            }));
            ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
            FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
            mContentGrid.setAdapter(itemBridgeAdapter);
            arrayObjectAdapter.addAll(0, bean.getList());
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void web(DivSearch bean){
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new WebAdapter(context, inflater, new WebAdapter.Callback() {
                @Override
                public void onClick(WebItem child) {
                    if (callback != null) callback.onClick(bean.getType(), child);
                }
            }));
            ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
            FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
            mContentGrid.setAdapter(itemBridgeAdapter);
            arrayObjectAdapter.addAll(0, bean.getList());
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(int type, Object bean);
    }
}
