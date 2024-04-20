package com.soya.launcher.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.TimeZoneAdapter;
import com.soya.launcher.bean.SimpleTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneDialog extends SingleDialogFragment{
    public static final String TAG = "TimeZoneDialog";
    public static TimeZoneDialog newInstance() {

        Bundle args = new Bundle();

        TimeZoneDialog fragment = new TimeZoneDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private VerticalGridView mContentGrid;

    private TimeZoneAdapter mTimeZoneAdapter;

    private ImageView mBlur;
    private View mRootView;

    private Callback callback;


    @Override
    protected int getLayout() {
        return R.layout.dialog_time_zone;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mContentGrid = view.findViewById(R.id.content);
        mBlur = view.findViewById(R.id.blur);
        mRootView = view.findViewById(R.id.root);

        mTimeZoneAdapter = new TimeZoneAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);

        TimeZone aDefault = TimeZone.getDefault();
        String[] ids = TimeZone.getAvailableIDs();
        List<SimpleTimeZone> list = new ArrayList<>();
        int select = 0;
        for (int i = 0; i < ids.length; i++){
            String id = ids[i];
            if (id.equals(aDefault.getID())) select = i;
            TimeZone zone = TimeZone.getTimeZone(id);
            list.add(new SimpleTimeZone(zone, id, zone.getDisplayName()));
        }
        mTimeZoneAdapter.setSelect(aDefault);
        mTimeZoneAdapter.replace(list);
        mContentGrid.setAdapter(mTimeZoneAdapter);
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentGrid.setSelectedPosition(select);
        blur(mRootView, mBlur);

        mTimeZoneAdapter.setCallback(new TimeZoneAdapter.Callback() {
            @Override
            public void onClick(SimpleTimeZone bean) {
                if (callback != null) callback.onClick(bean);
            }
        });
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(SimpleTimeZone bean);
    }
}
