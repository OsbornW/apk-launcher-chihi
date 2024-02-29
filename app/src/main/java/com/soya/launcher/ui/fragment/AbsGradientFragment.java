package com.soya.launcher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.softwinner.keystone.KeystoneCorrection;
import com.softwinner.keystone.KeystoneCorrectionManager;
import com.soya.launcher.R;
import com.soya.launcher.view.KeyEventFrameLayout;
import com.soya.launcher.view.MyInstructionView;

import java.util.concurrent.TimeUnit;

public abstract class AbsGradientFragment extends AbsFragment implements View.OnClickListener, KeyEventFrameLayout.KeyEventCallback {

    private static final int DIR_X = 0;
    private static final int DIR_Y = 1;

    private static final String KEYSTONE_LBX = "persist.display.keystone_lbx";
    private static final String KEYSTONE_LBY = "persist.display.keystone_lby";
    private static final String KEYSTONE_RBX = "persist.display.keystone_rbx";
    private static final String KEYSTONE_RBY = "persist.display.keystone_rby";
    private static final String KEYSTONE_LTX = "persist.display.keystone_ltx";
    private static final String KEYSTONE_LTY = "persist.display.keystone_lty";
    private static final String KEYSTONE_RTX = "persist.display.keystone_rtx";
    private static final String KEYSTONE_RTY = "persist.display.keystone_rty";

    public static final int TYPE_LT = 0;
    public static final int TYPE_LB = 1;
    public static final int TYPE_RB = 2;
    public static final int TYPE_RT = 3;

    private float x;
    private float y;

    private float centerX;
    private float centerY;
    private float detialX;
    private float detialY;
    private KeyEventFrameLayout mRootView;
    private KeystoneCorrectionManager keystone;
    private View mSurfaceView;
    private View mContentView;
    private View mLT;
    private View mRT;
    private View mLB;
    private View mRB;
    private View mLongTipView;
    private TextView mLRTextView;
    private TextView mTBTextView;

    private View mLTA;
    private View mLBA;
    private View mRTA;
    private View mRBA;

    private View mLT_LR;
    private View mLT_TB;
    private View mLB_LR;
    private View mLB_TB;
    private View mRT_LR;
    private View mRT_TB;
    private View mRB_LR;
    private View mRB_TB;
    private ImageView mDirLR;
    private ImageView mDirTB;

    private final int MAX_LOOP = 4;
    private final int MAX_PROGRESS = 100;
    private int type = TYPE_LT;
    private int index = 0;

    private long time = -1;
    private boolean isOut = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keystone = new KeystoneCorrectionManager();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_gradient;
    }

    @Override
    public void onStart() {
        super.onStart();
        time = -1;
        isOut = false;
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                syncDetial();
                setDefaultXY(type);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        time = -1;
        isOut = true;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mRootView = view.findViewById(R.id.root);
        mSurfaceView = view.findViewById(R.id.surface);
        mContentView = view.findViewById(R.id.content);
        mLT = view.findViewById(R.id.lt);
        mRT = view.findViewById(R.id.rt);
        mLB = view.findViewById(R.id.lb);
        mRB = view.findViewById(R.id.rb);
        mLTA = view.findViewById(R.id.lt_anchors);
        mLBA = view.findViewById(R.id.lb_anchors);
        mRTA = view.findViewById(R.id.rt_anchors);
        mRBA = view.findViewById(R.id.rb_anchors);
        mLT_LR = view.findViewById(R.id.lt_lr);
        mLT_TB = view.findViewById(R.id.lt_tb);
        mLB_LR = view.findViewById(R.id.lb_lr);
        mLB_TB = view.findViewById(R.id.lb_tb);
        mRT_LR = view.findViewById(R.id.rt_lr);
        mRT_TB = view.findViewById(R.id.rt_tb);
        mRB_LR = view.findViewById(R.id.rb_lr);
        mRB_TB = view.findViewById(R.id.rb_tb);
        mLongTipView = view.findViewById(R.id.long_click_tip);
        mTBTextView = view.findViewById(R.id.tb_text);
        mLRTextView = view.findViewById(R.id.lr_text);
        mDirLR = view.findViewById(R.id.dir_lr);
        mDirTB = view.findViewById(R.id.dir_tb);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mRootView.setKeyEventCallback(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mLongTipView.setVisibility(useLongClick() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mContentView);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    public void onKeyDown(KeyEvent event) {
        if (mRootView.getMeasuredWidth() == 0 || mRootView.getMeasuredHeight() == 0) return;
        if (centerX == 0 || centerY == 0){
            syncDetial();
        }

        int keyCode = event.getKeyCode();
        int action = event.getAction();
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
               if (action == 0){
                   if (type == TYPE_LT || type == TYPE_RT){
                       y -= detialY;
                   }else {
                       y += detialY;
                   }

                   if (y < 0) y = 0;
                   if (y > centerY) y = centerY;

                   setValue(type, DIR_Y, y);
               }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (action == 0){
                    if (type == TYPE_LT || type == TYPE_RT){
                        y += detialY;
                    }else {
                        y -= detialY;
                    }

                    if (y > centerY) y = centerY;
                    if (y < 0) y = 0;

                    setValue(type, DIR_Y, y);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (action == 0){
                    if (type == TYPE_LT || type == TYPE_LB){
                        x -= detialX;
                    }else {
                        x += detialX;
                    }

                    if (x < 0) x = 0;
                    if (x > centerX) x = centerX;

                    setValue(type, DIR_X, x);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (action == 0){
                    if (type == TYPE_LT || type == TYPE_LB){
                        x += detialX;
                    }else {
                        x -= detialX;
                    }

                    if (x > centerX) x = centerX;
                    if (x < 0) x = 0;

                    setValue(type, DIR_X, x);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (event.getAction() == 0){
                    if (time == -1) time = System.currentTimeMillis();
                    if (TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - time) >= 2000 && !isOut && useLongClick()){
                        isOut = true;
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, GuideDateFragment.newInstance()).addToBackStack(null).commit();
                    }
                }else if (event.getAction() == 1 && !isOut) {
                    time = -1;
                    type = ++index % MAX_LOOP;
                    setDefaultXY(type);
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                reset();
                break;
        }
    }

    private void syncDetial(){
        centerX = (float) (mRootView.getMeasuredWidth() / 2);
        centerY = (float) (mRootView.getMeasuredHeight() / 2);
        detialX = centerX / MAX_PROGRESS;
        detialY = centerY / MAX_PROGRESS;
    }

    private void setInstructionVisible(boolean lt, boolean rt, boolean lb, boolean rb){
        mLTA.setVisibility(lt ? View.VISIBLE : View.GONE);
        mRTA.setVisibility(rt ? View.VISIBLE : View.GONE);
        mLBA.setVisibility(lb ? View.VISIBLE : View.GONE);
        mRBA.setVisibility(rb ? View.VISIBLE : View.GONE);

        mLT_LR.setVisibility(lt ? View.VISIBLE : View.GONE);
        mLT_TB.setVisibility(lt ? View.VISIBLE : View.GONE);

        mRT_LR.setVisibility(rt ? View.VISIBLE : View.GONE);
        mRT_TB.setVisibility(rt ? View.VISIBLE : View.GONE);

        mLB_LR.setVisibility(lb ? View.VISIBLE : View.GONE);
        mLB_TB.setVisibility(lb ? View.VISIBLE : View.GONE);

        mRB_LR.setVisibility(rb ? View.VISIBLE : View.GONE);
        mRB_TB.setVisibility(rb ? View.VISIBLE : View.GONE);
    }

    private void setDefaultXY(int type) {
        switch (type){
            case TYPE_LT:
                setInstructionVisible(true, false, false, false);
                x = getValue(KEYSTONE_LTX, 0);
                y = getValue(KEYSTONE_LTY, 0);
                break;
            case TYPE_LB:
                setInstructionVisible(false, false, true, false);
                x = getValue(KEYSTONE_LBX, 0);
                y = getValue(KEYSTONE_LBY, 0);
                break;
            case TYPE_RT:
                setInstructionVisible(false, true, false, false);
                x = getValue(KEYSTONE_RTX, 0);
                y = getValue(KEYSTONE_RTY, 0);
                break;
            case TYPE_RB:
                setInstructionVisible(false, false, false, true);
                x = getValue(KEYSTONE_RBX, 0);
                y = getValue(KEYSTONE_RBY, 0);
                break;
        }

        int xP = (int) (x / centerX * 100);
        int yP = (int) (y / centerY * 100);

        switch (type){
            case TYPE_LT:
                mLRTextView.setText(String.valueOf(xP));
                mTBTextView.setText(String.valueOf(yP));
                break;
            case TYPE_LB:
                mTBTextView.setText(String.valueOf(xP));
                mLRTextView.setText(String.valueOf(yP));
                break;
            case TYPE_RT:
                mLRTextView.setText(String.valueOf(xP));
                mTBTextView.setText(String.valueOf(yP));
                break;
            case TYPE_RB:
                mTBTextView.setText(String.valueOf(xP));
                mLRTextView.setText(String.valueOf(yP));
                break;
        }

        switchDir(type);
    }

    private void switchDir(int type){
        switch (type){
            case TYPE_LT:
                mDirLR.setRotation(180);
                mDirTB.setRotation(270);
                break;
            case TYPE_LB:
                mDirLR.setRotation(90);
                mDirTB.setRotation(180);
                break;
            case TYPE_RT:
                mDirLR.setRotation(360);
                mDirTB.setRotation(270);
                break;
            case TYPE_RB:
                mDirLR.setRotation(90);
                mDirTB.setRotation(360);
                break;
        }
    }

    private void setValue(int type, int direction, float value){
        if (value < 0) return;
        float ltx = type == TYPE_LT && direction == DIR_X ? value : getValue(KEYSTONE_LTX, 0);
        float lty = type == TYPE_LT && direction == DIR_Y ? value : getValue(KEYSTONE_LTY, 0);

        float lbx = type == TYPE_LB && direction == DIR_X ? value : getValue(KEYSTONE_LBX, 0);
        float lby = type == TYPE_LB && direction == DIR_Y ? value : getValue(KEYSTONE_LBY, 0);

        float rtx = type == TYPE_RT && direction == DIR_X ? value : getValue(KEYSTONE_RTX, 0);
        float rty = type == TYPE_RT && direction == DIR_Y ? value : getValue(KEYSTONE_RTY, 0);

        float rbx = type == TYPE_RB && direction == DIR_X ? value : getValue(KEYSTONE_RBX, 0);
        float rby = type == TYPE_RB && direction == DIR_Y ? value : getValue(KEYSTONE_RBY, 0);

        KeystoneCorrection correction = new KeystoneCorrection(lbx, lby, ltx, lty, rbx, rby, rtx, rty);
        boolean success = keystone.setKeystoneCorrection(correction);
        mSurfaceView.invalidate();

        switch (type){
            case TYPE_LT:
                mLRTextView.setText(String.valueOf((int) (ltx / centerX * 100)));
                mTBTextView.setText(String.valueOf((int) (lty / centerY * 100)));
                break;
            case TYPE_LB:
                mTBTextView.setText(String.valueOf((int) (lbx / centerX * 100)));
                mLRTextView.setText(String.valueOf((int) (lby / centerY * 100)));
                break;
            case TYPE_RT:
                mLRTextView.setText(String.valueOf((int) (rtx / centerX * 100)));
                mTBTextView.setText(String.valueOf((int) (rty / centerY * 100)));
                break;
            case TYPE_RB:
                mTBTextView.setText(String.valueOf((int) (rbx / centerX * 100)));
                mLRTextView.setText(String.valueOf((int) (rby / centerY * 100)));
                break;
        }
    }

    private void reset(){
        x = 0;
        y = 0;
        KeystoneCorrection correction = new KeystoneCorrection(0, 0, 0, 0, 0, 0, 0, 0);
        keystone.setKeystoneCorrection(correction);
        mSurfaceView.invalidate();

        mLRTextView.setText(String.valueOf(0));
        mTBTextView.setText(String.valueOf(0));
    }

    private float getValue(String key, float def){
        KeystoneCorrection correction = keystone.getKeystoneCorrection();
        double value = def;
        switch (key){
            case KEYSTONE_LBX:
                value = correction.leftBottomX;
                break;
            case KEYSTONE_LBY:
                value = correction.leftBottomY;
                break;
            case KEYSTONE_LTX:
                value = correction.leftTopX;
                break;
            case KEYSTONE_LTY:
                value = correction.leftTopY;
                break;
            case KEYSTONE_RBX:
                value = correction.rightBottomX;
                break;
            case KEYSTONE_RBY:
                value = correction.rightBottomY;
                break;
            case KEYSTONE_RTX:
                value = correction.rightTopX;
                break;
            case KEYSTONE_RTY:
                value = correction.rightTopY;
                break;
        }
        return (float) value;
    }

    protected boolean useLongClick(){
        return true;
    }
}
