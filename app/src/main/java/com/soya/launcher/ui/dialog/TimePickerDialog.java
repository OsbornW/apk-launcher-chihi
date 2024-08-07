package com.soya.launcher.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.HorizontalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.DateSelectAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "TimePickerDialog";
    public static TimePickerDialog newInstance() {

        Bundle args = new Bundle();

        TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private final Calendar calendar = Calendar.getInstance();

    private HorizontalGridView mHourGrid;
    private HorizontalGridView mMinthGrid;
    private HorizontalGridView mSencondGrid;
    private View mCancelView;
    private View mConfirmView;
    private ImageView mBlur;
    private View mRootView;

    private DateSelectAdapter mHourAdapter;
    private DateSelectAdapter mMinuteAdapter;
    private DateSelectAdapter mSecondAdapter;

    private Callback callback;

    @Override
    protected int getLayout() {
        return R.layout.dialog_time_picker;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mHourGrid = view.findViewById(R.id.hour);
        mMinthGrid = view.findViewById(R.id.minute);
        mSencondGrid = view.findViewById(R.id.second);
        mCancelView = view.findViewById(R.id.cancel);
        mConfirmView = view.findViewById(R.id.confirm);
        mBlur = view.findViewById(R.id.blur);
        mRootView = view.findViewById(R.id.root);

        List<Integer> years = new ArrayList<>();
        for (int i = 0; i < 24; i++){
            years.add(i);
        }
        List<Integer> months = new ArrayList<>();
        for (int i = 0; i <= 59; i++){
            months.add(i);
        }
        List<Integer> sencods = new ArrayList<>();
        for (int i = 0; i <= 59; i++){
            sencods.add(i);
        }
        mHourAdapter = new DateSelectAdapter(getActivity(), inflater, years, newHourCallback());
        mMinuteAdapter = new DateSelectAdapter(getActivity(), inflater, months, newMinthCallback());
        mSecondAdapter = new DateSelectAdapter(getActivity(), inflater, sencods, newSecondCallback());
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mCancelView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);
        int hourIndex = calendar.get(Calendar.HOUR_OF_DAY);
        int minthIndex = calendar.get(Calendar.MINUTE);
        int secondIndex = calendar.get(Calendar.SECOND);

        mHourGrid.setAdapter(mHourAdapter);
        mMinthGrid.setAdapter(mMinuteAdapter);
        mSencondGrid.setAdapter(mSecondAdapter);

        mHourGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMinthGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSencondGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mHourGrid.setSelectedPosition(hourIndex);
        mMinthGrid.setSelectedPosition(minthIndex);
        mSencondGrid.setSelectedPosition(secondIndex);

        mHourAdapter.setSelect(mHourAdapter.getDataList().get(mHourGrid.getSelectedPosition()));
        mMinuteAdapter.setSelect(mMinuteAdapter.getDataList().get(mMinthGrid.getSelectedPosition()));
        mSecondAdapter.setSelect(mSecondAdapter.getDataList().get(mSencondGrid.getSelectedPosition()));
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean isMaterial() {
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //blur(mRootView, mBlur);
    }

    @Override
    protected int[] getWidthAndHeight() {
        return new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    private DateSelectAdapter.Callback newHourCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.HOUR_OF_DAY, bean);
            }
        };
    }

    private DateSelectAdapter.Callback newMinthCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.MINUTE, bean - 1);
            }
        };
    }

    private DateSelectAdapter.Callback newSecondCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.SECOND, bean - 1);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCancelView)){
            dismiss();
        }else if (v.equals(mConfirmView)){
            if (callback != null) callback.onConfirm(calendar.getTimeInMillis());
            dismiss();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onConfirm(long timeMills);
    }
}
