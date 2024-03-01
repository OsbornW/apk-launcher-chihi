package com.soya.launcher.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.open.system.ASystemProperties;
import com.softwinner.keystone.KeystoneCorrection;
import com.softwinner.keystone.KeystoneCorrectionManager;
import com.soya.launcher.R;
import com.soya.launcher.view.KeyEventFrameLayout;

import java.util.Arrays;

public class ScaleScreenFragment extends AbsFragment implements KeyEventFrameLayout.KeyEventCallback {

    public static ScaleScreenFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ScaleScreenFragment fragment = new ScaleScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private static final String TAG = "ScaleScreenFragment";
    private static final String KEYSTONE_LBX = "persist.display.keystone_lbx";
    private static final String KEYSTONE_LBY = "persist.display.keystone_lby";
    private static final String KEYSTONE_RBX = "persist.display.keystone_rbx";
    private static final String KEYSTONE_RBY = "persist.display.keystone_rby";
    private static final String KEYSTONE_LTX = "persist.display.keystone_ltx";
    private static final String KEYSTONE_LTY = "persist.display.keystone_lty";
    private static final String KEYSTONE_RTX = "persist.display.keystone_rtx";
    private static final String KEYSTONE_RTY = "persist.display.keystone_rty";

    private final KeystoneCorrectionManager keystone = new KeystoneCorrectionManager();
    private KeyEventFrameLayout mRootView;
    private View mSurfaceView;

    private int screenWidth= 0;
    private int screenHeigh= 0;

    private static final int upKey = 0;
    private static final int downKey = 1;
    private static final int leftKey = 2;
    private static final int rightKey = 3;
    private static final int menuKey = 4;
    private int pressKeyState = upKey;

    private ImageView mDirL;
    private ImageView mDirR;
    private ImageView mDirT;
    private ImageView mDirB;

    private int AX;
    private int AY;       //A translate

    private int BX;
    private int BY;       //B translate

    private int CX;
    private int CY;       //C translate

    private int DX;
    private int DY;       //D translate
    Point mHwOffsetA, mHwOffsetB, mHwOffsetC, mHwOffsetD;

    double unitX;
    double unitY;
    private WindowManager mWindowManager;
    private int screenXaxis= 0;
    private int screenYaxis= 0;
    private TextView mScaleTextView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_scale_screen;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mRootView = view.findViewById(R.id.root);
        mSurfaceView = view.findViewById(R.id.surface);
        mDirB = view.findViewById(R.id.dir_b);
        mDirL = view.findViewById(R.id.dir_l);
        mDirT = view.findViewById(R.id.dir_t);
        mDirR = view.findViewById(R.id.dir_r);
        mScaleTextView = view.findViewById(R.id.tb_text);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mRootView.setKeyEventCallback(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mWindowManager = (WindowManager) getActivity().getSystemService (Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        screenHeigh = mWindowManager.getDefaultDisplay().getHeight();
        unitX = screenWidth / 250.0;
        unitY = screenHeigh / 250.0;
        screenXaxis = screenWidth;
        screenYaxis = 0;

        getHwOffset();
        loadSettings();
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void getHwOffset() {
        mHwOffsetA = new Point();
        mHwOffsetB = new Point();
        mHwOffsetC = new Point();
        mHwOffsetD = new Point();

        String res = ASystemProperties.get("sys.fb.size", "");
        if (res.length() <= 0) {
            mHwOffsetA.set(0, 0);
            mHwOffsetB.set(0, 0);
            mHwOffsetC.set(0, 0);
            mHwOffsetD.set(0, 0);
            return;
        }

        String propVal = ASystemProperties.get("persist.display.keystone_ltx." + res, "0");
        mHwOffsetA.x = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_lty." + res, "0");
        mHwOffsetA.y = (int)(Double.parseDouble(propVal) / unitY);

        propVal = ASystemProperties.get("persist.display.keystone_lbx." + res, "0");
        mHwOffsetB.x = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_lby." + res, "0");
        mHwOffsetB.y = (int)(Double.parseDouble(propVal) / unitY);

        propVal = ASystemProperties.get("persist.display.keystone_rbx." + res, "0");
        mHwOffsetC.x = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_rby." + res, "0");
        mHwOffsetC.y = (int)(Double.parseDouble(propVal) / unitY);

        propVal = ASystemProperties.get("persist.display.keystone_rtx." + res, "0");
        mHwOffsetD.x = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_rty." + res, "0");
        mHwOffsetD.y = (int)(Double.parseDouble(propVal) / unitY);
    }

    private void reloadSettingsByRes() {
        String res = ASystemProperties.get("sys.fb.size", "");
        String propList[] = {"persist.display.keystone_ltx",
                "persist.display.keystone_lty",
                "persist.display.keystone_lbx",
                "persist.display.keystone_lby",
                "persist.display.keystone_rbx",
                "persist.display.keystone_rby",
                "persist.display.keystone_rtx",
                "persist.display.keystone_rty"};

        for (String propName : propList) {
            String propVal = ASystemProperties.get(propName + "." + res, "0");
            ASystemProperties.set(propName, propVal);
            Log.i(TAG, "Set [" + propName + "] to " + propVal);
        }

        loadSettings();
    }

    private void loadSettings() {
        String propVal = ASystemProperties.get("persist.display.keystone_ltx", "0");
        AX = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_lty", "0");
        AY = (int)(Double.parseDouble(propVal) / unitY / 2);

        propVal = ASystemProperties.get("persist.display.keystone_lbx", "0");
        BX = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_lby", "0");
        BY = (int)(Double.parseDouble(propVal) / unitY / 2);

        propVal = ASystemProperties.get("persist.display.keystone_rbx", "0");
        CX = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_rby", "0");
        CY = (int)(Double.parseDouble(propVal) / unitY / 2);

        propVal = ASystemProperties.get("persist.display.keystone_rtx", "0");
        DX = (int)(Double.parseDouble(propVal) / unitX);
        propVal = ASystemProperties.get("persist.display.keystone_rty", "0");
        DY = (int)(Double.parseDouble(propVal) / unitY / 2);

        syncScale();
    }

    private void reset(){
        AX = 0;
        AY = 0;
        BX = 0;
        BY = 0;
        CX = 0;
        CY = 0;
        DX = 0;
        DY = 0;
        KeystoneCorrection correction = new KeystoneCorrection(0, 0, 0, 0, 0, 0, 0, 0);
        keystone.setKeystoneCorrection(correction);
        mSurfaceView.invalidate();
        syncScale();
    }

    @Override
    public void onKeyDown(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && action == 0) {
            if (AX > mHwOffsetA.x && AX <= 50 + mHwOffsetA.x) {
                AX--;
            }
            if (BX > mHwOffsetB.x && BX <= 50 + mHwOffsetB.x) {
                BX--;
            }
            if (CX > mHwOffsetC.x && CX <= 50 + mHwOffsetC.x) {
                CX--;
            }
            if (DX > mHwOffsetD.x && DX <= 50 + mHwOffsetD.x) {
                DX--;
            }

            if (AY > mHwOffsetA.y && AY <= 50 + mHwOffsetA.y) {
                AY--;
            }
            if (BY > mHwOffsetB.y && BY <= 50 + mHwOffsetB.y) {
                BY--;
            }
            if (CY > mHwOffsetC.y && CY <= 50 + mHwOffsetC.y) {
                CY--;
            }
            if (DY > mHwOffsetD.y && DY <= 50 + mHwOffsetD.y) {
                DY--;
            }

            pressKeyState = upKey;
            setDirImage(keyCode, true);
            setValue();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && action == 0) {
            if (AX >= mHwOffsetA.x && AX < 50 + mHwOffsetA.x) {
                AX++;
            }
            if (BX >= mHwOffsetB.x && BX < 50 + mHwOffsetB.x) {
                BX++;
            }
            if (CX >= mHwOffsetC.x && CX < 50 + mHwOffsetC.x) {
                CX++;
            }
            if (DX >= mHwOffsetD.x && DX < 50 + mHwOffsetD.x) {
                DX++;
            }

            if (AY >= mHwOffsetA.y && AY < 50 + mHwOffsetA.y) {
                AY++;
            }
            if (BY >= mHwOffsetB.y && BY < 50 + mHwOffsetB.y) {
                BY++;
            }
            if (CY >= mHwOffsetC.y && CY < 50 + mHwOffsetC.y) {
                CY++;
            }
            if (DY >= mHwOffsetD.y && DY < 50 + mHwOffsetD.y) {
                DY++;
            }

            pressKeyState = downKey;
            setValue();
            setDirImage(keyCode, true);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && action == 0) {
            if (AX > mHwOffsetA.x && AX <= 50 + mHwOffsetA.x) {
                AX--;
            }
            if (BX > mHwOffsetB.x && BX <= 50 + mHwOffsetB.x) {
                BX--;
            }
            if (CX > mHwOffsetC.x && CX <= 50 + mHwOffsetC.x) {
                CX--;
            }
            if (DX > mHwOffsetD.x && DX <= 50 + mHwOffsetD.x) {
                DX--;
            }

            if (AY > mHwOffsetA.y && AY <= 50 + mHwOffsetA.y) {
                AY--;
            }
            if (BY > mHwOffsetB.y && BY <= 50 + mHwOffsetB.y) {
                BY--;
            }
            if (CY > mHwOffsetC.y && CY <= 50 + mHwOffsetC.y) {
                CY--;
            }
            if (DY > mHwOffsetD.y && DY <= 50 + mHwOffsetD.y) {
                DY--;
            }
            pressKeyState = leftKey;
            setValue();
            setDirImage(keyCode, true);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && action == 0) {
            if (AX >= mHwOffsetA.x && AX < 50 + mHwOffsetA.x) {
                AX++;
            }
            if (BX >= mHwOffsetB.x && BX < 50 + mHwOffsetB.x) {
                BX++;
            }
            if (CX >= mHwOffsetC.x && CX < 50 + mHwOffsetC.x) {
                CX++;
            }
            if (DX >= mHwOffsetD.x && DX < 50 + mHwOffsetD.x) {
                DX++;
            }

            if (AY >= mHwOffsetA.y && AY < 50 + mHwOffsetA.y) {
                AY++;
            }
            if (BY >= mHwOffsetB.y && BY < 50 + mHwOffsetB.y) {
                BY++;
            }
            if (CY >= mHwOffsetC.y && CY < 50 + mHwOffsetC.y) {
                CY++;
            }
            if (DY >= mHwOffsetD.y && DY < 50 + mHwOffsetD.y) {
                DY++;
            }
            pressKeyState = rightKey;
            setValue();
            setDirImage(keyCode, true);
        } else if(keyCode == KeyEvent.KEYCODE_MENU){
            pressKeyState = menuKey;
            reset();
        }
        if (action == 1) resetIcon();
    }

    private void setDirImage(int code, boolean isSelect){
        Drawable drawable = getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme());
        DrawableCompat.setTint(drawable, isSelect ? getResources().getColor(R.color.ico_style_3, Resources.getSystem().newTheme()) : getResources().getColor(R.color.ico_style_1, Resources.getSystem().newTheme()));

        switch (code){
            case KeyEvent.KEYCODE_DPAD_UP:
                mDirT.setImageDrawable(drawable);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mDirB.setImageDrawable(drawable);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mDirL.setImageDrawable(drawable);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mDirR.setImageDrawable(drawable);
                break;
        }
    }

    private void resetIcon(){
        boolean isSelect = false;
        Drawable drawable = getResources().getDrawable(R.drawable.baseline_arrow_left_100, Resources.getSystem().newTheme());
        DrawableCompat.setTint(drawable, isSelect ? getResources().getColor(R.color.ico_style_3, Resources.getSystem().newTheme()) : getResources().getColor(R.color.ico_style_1, Resources.getSystem().newTheme()));

        mDirB.setImageDrawable(drawable);
        mDirT.setImageDrawable(drawable);
        mDirL.setImageDrawable(drawable);
        mDirR.setImageDrawable(drawable);
    }

    private void setValue(){
        KeystoneCorrection kc = new KeystoneCorrection((double)(unitX * BX ),(double)(unitY * BY * 2),
                (double)(unitX * AX ),(double)(unitY * AY * 2),
                (double)( unitX * CX),(double)(unitY * CY * 2),
                (double)(unitX * DX),(double)(unitY * DY * 2)

        );

        keystone.setKeystoneCorrection(kc);
        mSurfaceView.invalidate();
        syncScale();
    }

    private void syncScale(){
        double[] doubles = {AX, AY, BX, BY, CX, CY, DX, DY};
        Arrays.sort(doubles);
        mScaleTextView.setText(String.valueOf((int) doubles[0]));
    }
}
