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

public class DatePickerDialog extends SingleDialogFragment implements View.OnClickListener {
    public static final String TAG = "DatePickerDialog";
    public static DatePickerDialog newInstance() {

        Bundle args = new Bundle();

        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private final Calendar calendar = Calendar.getInstance();

    private HorizontalGridView mYearGrid;
    private HorizontalGridView mMonthGrid;
    private HorizontalGridView mDayGrid;
    private View mCancelView;
    private View mConfirmView;
    private ImageView mBlur;
    private View mRootView;

    private DateSelectAdapter mYearAdapter;
    private DateSelectAdapter mMonthAdapter;
    private DateSelectAdapter mDayAdapter;

    private Callback callback;

    @Override
    protected int getLayout() {
        return R.layout.dialog_date_picker;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mYearGrid = view.findViewById(R.id.year);
        mMonthGrid = view.findViewById(R.id.month);
        mDayGrid = view.findViewById(R.id.day);
        mCancelView = view.findViewById(R.id.cancel);
        mConfirmView = view.findViewById(R.id.confirm);
        mBlur = view.findViewById(R.id.blur);
        mRootView = view.findViewById(R.id.root);

        List<Integer> years = new ArrayList<>();
        for (int i = 0; i < 300; i++){
            years.add(1900 + i);
        }
        List<Integer> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++){
            months.add(i);
        }
        mYearAdapter = new DateSelectAdapter(getActivity(), inflater, years, newYearCallback());
        mMonthAdapter = new DateSelectAdapter(getActivity(), inflater, months, newMonthCallback());
        mDayAdapter = new DateSelectAdapter(getActivity(), inflater, new ArrayList<>(), newDayCallback());
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
        int yearIndex = calendar.get(Calendar.YEAR) - 1900;
        int monthIndex = calendar.get(Calendar.MONTH);
        int dayIndex = calendar.get(Calendar.DAY_OF_MONTH) - 1;

        mYearGrid.setAdapter(mYearAdapter);
        mMonthGrid.setAdapter(mMonthAdapter);
        mDayGrid.setAdapter(mDayAdapter);

        mYearGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMonthGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mDayGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mYearGrid.setSelectedPosition(yearIndex);
        mMonthGrid.setSelectedPosition(monthIndex);
        syncDay();
        mDayGrid.setSelectedPosition(dayIndex);

        mYearAdapter.setSelect(mYearAdapter.getDataList().get(mYearGrid.getSelectedPosition()));
        mMonthAdapter.setSelect(mMonthAdapter.getDataList().get(mMonthGrid.getSelectedPosition()));
        mDayAdapter.setSelect(mDayAdapter.getDataList().get(mDayGrid.getSelectedPosition()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //blur(mRootView, mBlur);
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
    protected int[] getWidthAndHeight() {
        return new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    private DateSelectAdapter.Callback newYearCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.YEAR, bean);
                syncDay();
            }
        };
    }

    private DateSelectAdapter.Callback newMonthCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.MONTH, bean - 1);
                syncDay();
            }
        };
    }

    private DateSelectAdapter.Callback newDayCallback(){
        return new DateSelectAdapter.Callback() {
            @Override
            public void onClick(Integer bean) {
                calendar.set(Calendar.DAY_OF_MONTH, bean);
            }
        };
    }

    private void syncDay(){
        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Integer> days = new ArrayList<>();
        for (int i = 1; i <= max; i++){
            days.add(i);
        }
        mDayAdapter.replace(days);
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
