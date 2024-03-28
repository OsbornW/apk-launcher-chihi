package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.App;
import com.soya.launcher.R;
import com.soya.launcher.adapter.AboutAdapter;
import com.soya.launcher.adapter.NotifyBarAdapter;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.MyRunnable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotifyFragment extends AbsFragment{

    public static NotifyFragment newInstance() {

        Bundle args = new Bundle();

        NotifyFragment fragment = new NotifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final ExecutorService exec = Executors.newCachedThreadPool();

    private VerticalGridView mContentGrid;
    private TextView mTitleView;
    private View mMaskView;

    private NotifyBarAdapter mAdapter;
    private MyRunnable runnable;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable != null) runnable.interrupt();
        exec.shutdownNow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_notify;
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mTitleView = view.findViewById(R.id.title);
        mContentGrid = view.findViewById(R.id.content);
        mMaskView = view.findViewById(R.id.masked);

        mAdapter = new NotifyBarAdapter(getActivity(), inflater, new CopyOnWriteArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mAdapter.setCallback(new NotifyBarAdapter.Callback() {
            @Override
            public void onClick(AppItem bean) {
                if (bean.getStatus() == AppItem.STATU_DOWNLOAD_FAIL || bean.getStatus() == AppItem.STATU_INSTALL_FAIL){
                    bean.setStatus(AppItem.STATU_IDLE);
                }
            }
        });
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mTitleView.setText(getString(R.string.notice));

        mContentGrid.setAdapter(mAdapter);
        mContentGrid.setNumColumns(1);
        mContentGrid.requestFocus();
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        mAdapter.replace(App.PUSH_APPS);

        time();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.replace(App.PUSH_APPS);
        mMaskView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mContentGrid.setVisibility(mAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private void time(){
        if (runnable != null) runnable.interrupt();
        runnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    SystemClock.sleep(500);
                    mContentGrid.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) return;
                            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount(), new Object());
                        }
                    });
                }
            }
        };
        exec.execute(runnable);
    }
}
