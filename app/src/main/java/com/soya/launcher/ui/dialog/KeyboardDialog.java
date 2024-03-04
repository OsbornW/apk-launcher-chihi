package com.soya.launcher.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.KeyboardAdapter;
import com.soya.launcher.bean.KeyItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardDialog extends SingleDialogFragment implements KeyboardAdapter.Callback {
    public static final String TAG = "KeyboardDialog";
    public static KeyboardDialog newInstance() {

        Bundle args = new Bundle();

        KeyboardDialog fragment = new KeyboardDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private KeyboardAdapter mAdapter;
    private Callback callback;

    private final List<KeyItem> ens = new ArrayList<>();
    private final List<KeyItem> nums = new ArrayList<>();

    private TextView mTargetView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillEn();
        fillNums();
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_keyboard;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mRecyclerView = view.findViewById(R.id.recycler);

        mAdapter = new KeyboardAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(LayoutInflater inflater, View view) {
        super.initBefore(inflater, view);
        mAdapter.setCallback(this);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP){
                    if (event.getUnicodeChar() == 0){
                        switch (keyCode){
                            case KeyEvent.KEYCODE_DEL:
                                del();
                                break;
                        }
                    }else {
                        mTargetView.append(String.valueOf(event.getDisplayLabel()));
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);
        mAdapter.replace(ens, KeyboardAdapter.TYPE_ENG);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 10));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    }

    @Override
    protected int[] getWidthAndHeight() {
        return new int[]{RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    public void setTargetView(TextView targetView) {
        this.mTargetView = targetView;
    }

    private void fillEn(){
        String[] array = "q w e r t y u i o p".split(" ");
        Log.e(TAG, "fillEn: "+ Arrays.toString(array));
        for (String item : array) ens.add(new KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false));

        array = "a s d f g h j k l ,".split(" ");
        for (String item : array) ens.add(new KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false));

        ens.add(new KeyItem(KeyItem.TYPE_UPCAST, "", R.drawable.baseline_lower_cast_100, 1, true));
        array = "z x c v b n m .".split(" ");
        for (String item : array) ens.add(new KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false));
        ens.add(new KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true));

        ens.add(new KeyItem(KeyItem.TYPE_SWITCH, "?123", -1, 2, false));
        ens.add(new KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false));
        ens.add(new KeyItem(KeyItem.TYPE_SEARCH, "", R.drawable.baseline_search_100_2, 2, true));
    }

    private void fillNums(){

        String[] array = "1 2 3 4 5 6 7 8 9 0".split(" ");
        for (String item : array) nums.add(new KeyItem(KeyItem.TYPE_NUM, item, -1, 1, false));

        array = "! @ # $ % ^ & * ( )".split(" ");
        for (String item : array) nums.add(new KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false));

        array = "~ , . ? / \" - + =".split(" ");
        for (String item : array) nums.add(new KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false));
        nums.add(new KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true));

        nums.add(new KeyItem(KeyItem.TYPE_SWITCH, "ABC", -1, 2, false));
        nums.add(new KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false));
        nums.add(new KeyItem(KeyItem.TYPE_SEARCH, "", R.drawable.baseline_search_100_2, 2, true));
    }

    @Override
    public void onClick(KeyItem bean, String text) {
        switch (bean.getType()){
            case KeyItem.TYPE_SWITCH:
                if (mAdapter.getType() == KeyboardAdapter.TYPE_ENG){
                    mAdapter.replace(nums, KeyboardAdapter.TYPE_NUM);
                }else if (mAdapter.getType() == KeyboardAdapter.TYPE_NUM){
                    mAdapter.replace(ens, KeyboardAdapter.TYPE_ENG);
                }
                break;
            case KeyItem.TYPE_UPCAST:
                mAdapter.setUPCase(!mAdapter.isUPCase());
                break;
            case KeyItem.TYPE_DEL:
                del();
                break;
            case KeyItem.TYPE_SEARCH:
                mTargetView.onEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            default:
                mTargetView.append(text);
        }
    }

    private void del(){
        int len = mTargetView.getText().length();
        if (len != 0) mTargetView.setText(mTargetView.getText().subSequence(0, len - 1));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) callback.onClose();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClose();
    }
}
