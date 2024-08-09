package com.soya.launcher.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.open.system.ASystemProperties
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.softwinner.keystone.KeystoneCorrection
import com.softwinner.keystone.KeystoneCorrectionManager
import com.soya.launcher.R
import com.soya.launcher.ext.isH6
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.h6.H6Manager
import com.soya.launcher.rk3326.KeystoneVertex
import com.soya.launcher.view.KeyEventFrameLayout
import com.soya.launcher.view.KeyEventFrameLayout.KeyEventCallback
import java.util.Arrays

class ScaleScreenFragment : AbsFragment(), KeyEventCallback {
    private val keystone = KeystoneCorrectionManager()
    private var mRootView: KeyEventFrameLayout? = null
    private var mSurfaceView: View? = null

    private var screenWidth = 0
    private var screenHeigh = 0

    private var pressKeyState = upKey

    private var mDirL: ImageView? = null
    private var mDirR: ImageView? = null
    private var mDirT: ImageView? = null
    private var mDirB: ImageView? = null

    private var AX = 0
    private var AY = 0 //A translate

    private var BX = 0
    private var BY = 0 //B translate

    private var CX = 0
    private var CY = 0 //C translate

    private var DX = 0
    private var DY = 0 //D translate
    var mHwOffsetA: Point? = null
    var mHwOffsetB: Point? = null
    var mHwOffsetC: Point? = null
    var mHwOffsetD: Point? = null

    var unitX: Double = 0.0
    var unitY: Double = 0.0
    private var mWindowManager: WindowManager? = null
    private var screenXaxis = 0
    private var screenYaxis = 0
    private var mScaleTextView: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_scale_screen
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mRootView = view.findViewById(R.id.root)
        mSurfaceView = view.findViewById(R.id.surface)
        mDirB = view.findViewById(R.id.dir_b)
        mDirL = view.findViewById(R.id.dir_l)
        mDirT = view.findViewById(R.id.dir_t)
        mDirR = view.findViewById(R.id.dir_r)
        mScaleTextView = view.findViewById(R.id.tb_text)
    }

    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mRootView!!.setKeyEventCallback(this)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mWindowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        screenWidth = mWindowManager!!.defaultDisplay.width
        screenHeigh = mWindowManager!!.defaultDisplay.height
        unitX = screenWidth / 250.0
        unitY = screenHeigh / 250.0
        screenXaxis = screenWidth
        screenYaxis = 0

        hwOffset
        loadSettings()
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private val hwOffset: Unit
        get() {
            mHwOffsetA = Point()
            mHwOffsetB = Point()
            mHwOffsetC = Point()
            mHwOffsetD = Point()

            val res = ASystemProperties.get("sys.fb.size", "")
            if (res.length <= 0) {
                mHwOffsetA!![0] = 0
                mHwOffsetB!![0] = 0
                mHwOffsetC!![0] = 0
                mHwOffsetD!![0] = 0
                return
            }

            var propVal = ASystemProperties.get("persist.display.keystone_ltx.$res", "0")
            mHwOffsetA!!.x = (propVal.toDouble() / unitX).toInt()
            propVal = ASystemProperties.get("persist.display.keystone_lty.$res", "0")
            mHwOffsetA!!.y = (propVal.toDouble() / unitY).toInt()

            propVal = ASystemProperties.get("persist.display.keystone_lbx.$res", "0")
            mHwOffsetB!!.x = (propVal.toDouble() / unitX).toInt()
            propVal = ASystemProperties.get("persist.display.keystone_lby.$res", "0")
            mHwOffsetB!!.y = (propVal.toDouble() / unitY).toInt()

            propVal = ASystemProperties.get("persist.display.keystone_rbx.$res", "0")
            mHwOffsetC!!.x = (propVal.toDouble() / unitX).toInt()
            propVal = ASystemProperties.get("persist.display.keystone_rby.$res", "0")
            mHwOffsetC!!.y = (propVal.toDouble() / unitY).toInt()

            propVal = ASystemProperties.get("persist.display.keystone_rtx.$res", "0")
            mHwOffsetD!!.x = (propVal.toDouble() / unitX).toInt()
            propVal = ASystemProperties.get("persist.display.keystone_rty.$res", "0")
            mHwOffsetD!!.y = (propVal.toDouble() / unitY).toInt()
        }

    private fun reloadSettingsByRes() {
        val res = ASystemProperties.get("sys.fb.size", "")
        val propList = arrayOf(
            "persist.display.keystone_ltx",
            "persist.display.keystone_lty",
            "persist.display.keystone_lbx",
            "persist.display.keystone_lby",
            "persist.display.keystone_rbx",
            "persist.display.keystone_rby",
            "persist.display.keystone_rtx",
            "persist.display.keystone_rty"
        )

        for (propName in propList) {
            val propVal = ASystemProperties.get("$propName.$res", "0")
            ASystemProperties.set(propName, propVal)

        }

        loadSettings()
    }

    private fun loadSettings() {
        var propVal = ASystemProperties.get("persist.display.keystone_ltx", "0")
        AX = (propVal.toDouble() / unitX).toInt()
        propVal = ASystemProperties.get("persist.display.keystone_lty", "0")
        AY = (propVal.toDouble() / unitY / 2).toInt()

        propVal = ASystemProperties.get("persist.display.keystone_lbx", "0")
        BX = (propVal.toDouble() / unitX).toInt()
        propVal = ASystemProperties.get("persist.display.keystone_lby", "0")
        BY = (propVal.toDouble() / unitY / 2).toInt()

        propVal = ASystemProperties.get("persist.display.keystone_rbx", "0")
        CX = (propVal.toDouble() / unitX).toInt()
        propVal = ASystemProperties.get("persist.display.keystone_rby", "0")
        CY = (propVal.toDouble() / unitY / 2).toInt()

        propVal = ASystemProperties.get("persist.display.keystone_rtx", "0")
        DX = (propVal.toDouble() / unitX).toInt()
        propVal = ASystemProperties.get("persist.display.keystone_rty", "0")
        DY = (propVal.toDouble() / unitY / 2).toInt()

        syncScale()
    }

    private fun reset() {
        AX = 0
        AY = 0
        BX = 0
        BY = 0
        CX = 0
        CY = 0
        DX = 0
        DY = 0
        isH6().yes {
            H6Manager.getInstance(requireContext())?.zoomValue = 100
        }.otherwise {
            isRK3326().yes {
                kv.getAllKeystoneVertex()
                kv.vTopLeft.x = 0
                kv.vTopLeft.y = 0
                kv.vTopRight.x = 0
                kv.vTopRight.y = 0
                kv.vBottomLeft.x = 0
                kv.vBottomLeft.y = 0
                kv.vBottomRight.x = 0
                kv.vBottomRight.y = 0
                kv.updateAllKeystoneVertex()
            }.otherwise {
                val correction = KeystoneCorrection(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                keystone.setKeystoneCorrection(correction)
            }
        }

        mSurfaceView!!.invalidate()
        syncScale()
    }

    override fun onKeyDown(event: KeyEvent) {
        val keyCode = event.keyCode
        val action = event.action

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && action == 0) {
            if (AX > mHwOffsetA!!.x && AX <= 50 + mHwOffsetA!!.x) {
                AX--
            }
            if (BX > mHwOffsetB!!.x && BX <= 50 + mHwOffsetB!!.x) {
                BX--
            }
            if (CX > mHwOffsetC!!.x && CX <= 50 + mHwOffsetC!!.x) {
                CX--
            }
            if (DX > mHwOffsetD!!.x && DX <= 50 + mHwOffsetD!!.x) {
                DX--
            }

            if (AY > mHwOffsetA!!.y && AY <= 50 + mHwOffsetA!!.y) {
                AY--
            }
            if (BY > mHwOffsetB!!.y && BY <= 50 + mHwOffsetB!!.y) {
                BY--
            }
            if (CY > mHwOffsetC!!.y && CY <= 50 + mHwOffsetC!!.y) {
                CY--
            }
            if (DY > mHwOffsetD!!.y && DY <= 50 + mHwOffsetD!!.y) {
                DY--
            }

            pressKeyState = upKey
            setDirImage(keyCode, true)
            isH6().yes {
                var zoom = H6Manager.getInstance(requireContext())?.zoomValue
                H6Manager.getInstance(requireContext())?.zoomValue = (zoom?:0)+1
                mSurfaceView!!.invalidate()
                syncScale()
            }.otherwise {
                setValue()
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && action == 0) {
            if (AX >= mHwOffsetA!!.x && AX < 50 + mHwOffsetA!!.x) {
                AX++
            }
            if (BX >= mHwOffsetB!!.x && BX < 50 + mHwOffsetB!!.x) {
                BX++
            }
            if (CX >= mHwOffsetC!!.x && CX < 50 + mHwOffsetC!!.x) {
                CX++
            }
            if (DX >= mHwOffsetD!!.x && DX < 50 + mHwOffsetD!!.x) {
                DX++
            }

            if (AY >= mHwOffsetA!!.y && AY < 50 + mHwOffsetA!!.y) {
                AY++
            }
            if (BY >= mHwOffsetB!!.y && BY < 50 + mHwOffsetB!!.y) {
                BY++
            }
            if (CY >= mHwOffsetC!!.y && CY < 50 + mHwOffsetC!!.y) {
                CY++
            }
            if (DY >= mHwOffsetD!!.y && DY < 50 + mHwOffsetD!!.y) {
                DY++
            }

            pressKeyState = downKey
            setDirImage(keyCode, true)
            isH6().yes {
                var zoom = H6Manager.getInstance(requireContext())?.zoomValue
                if (zoom != null) {
                    if(zoom>(H6Manager.getInstance(requireContext())?.zoomMinValue)?:0){
                        H6Manager.getInstance(requireContext())?.zoomValue = zoom -1
                        mSurfaceView!!.invalidate()
                        syncScale()
                    }
                }

            }.otherwise {
                setValue()
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && action == 0) {
            if (AX > mHwOffsetA!!.x && AX <= 50 + mHwOffsetA!!.x) {
                AX--
            }
            if (BX > mHwOffsetB!!.x && BX <= 50 + mHwOffsetB!!.x) {
                BX--
            }
            if (CX > mHwOffsetC!!.x && CX <= 50 + mHwOffsetC!!.x) {
                CX--
            }
            if (DX > mHwOffsetD!!.x && DX <= 50 + mHwOffsetD!!.x) {
                DX--
            }

            if (AY > mHwOffsetA!!.y && AY <= 50 + mHwOffsetA!!.y) {
                AY--
            }
            if (BY > mHwOffsetB!!.y && BY <= 50 + mHwOffsetB!!.y) {
                BY--
            }
            if (CY > mHwOffsetC!!.y && CY <= 50 + mHwOffsetC!!.y) {
                CY--
            }
            if (DY > mHwOffsetD!!.y && DY <= 50 + mHwOffsetD!!.y) {
                DY--
            }
            pressKeyState = leftKey
            setDirImage(keyCode, true)
            isH6().yes {
                var zoom = H6Manager.getInstance(requireContext())?.zoomValue
                H6Manager.getInstance(requireContext())?.zoomValue = (zoom?:0)+1
                mSurfaceView!!.invalidate()
                syncScale()
            }.otherwise {
                setValue()
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && action == 0) {
            if (AX >= mHwOffsetA!!.x && AX < 50 + mHwOffsetA!!.x) {
                AX++
            }
            if (BX >= mHwOffsetB!!.x && BX < 50 + mHwOffsetB!!.x) {
                BX++
            }
            if (CX >= mHwOffsetC!!.x && CX < 50 + mHwOffsetC!!.x) {
                CX++
            }
            if (DX >= mHwOffsetD!!.x && DX < 50 + mHwOffsetD!!.x) {
                DX++
            }

            if (AY >= mHwOffsetA!!.y && AY < 50 + mHwOffsetA!!.y) {
                AY++
            }
            if (BY >= mHwOffsetB!!.y && BY < 50 + mHwOffsetB!!.y) {
                BY++
            }
            if (CY >= mHwOffsetC!!.y && CY < 50 + mHwOffsetC!!.y) {
                CY++
            }
            if (DY >= mHwOffsetD!!.y && DY < 50 + mHwOffsetD!!.y) {
                DY++
            }
            pressKeyState = rightKey
            setDirImage(keyCode, true)
            isH6().yes {
                var zoom = H6Manager.getInstance(requireContext())?.zoomValue
                H6Manager.getInstance(requireContext())?.zoomValue = (zoom?:0)-1
                mSurfaceView!!.invalidate()
                syncScale()
            }.otherwise {
                setValue()
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            pressKeyState = menuKey
            reset()
        }
        if (action == 1) resetIcon()
    }

    private fun setDirImage(code: Int, isSelect: Boolean) {
        val drawable = resources.getDrawable(
            R.drawable.baseline_arrow_left_100,
            Resources.getSystem().newTheme()
        )
        DrawableCompat.setTint(
            drawable,
            if (isSelect) resources.getColor(
                R.color.ico_style_3,
                Resources.getSystem().newTheme()
            ) else resources.getColor(R.color.ico_style_1, Resources.getSystem().newTheme())
        )

        when (code) {
            KeyEvent.KEYCODE_DPAD_UP -> mDirT!!.setImageDrawable(drawable)
            KeyEvent.KEYCODE_DPAD_DOWN -> mDirB!!.setImageDrawable(drawable)
            KeyEvent.KEYCODE_DPAD_LEFT -> mDirL!!.setImageDrawable(drawable)
            KeyEvent.KEYCODE_DPAD_RIGHT -> mDirR!!.setImageDrawable(drawable)
        }
    }

    private fun resetIcon() {
        val isSelect = false
        val drawable = resources.getDrawable(
            R.drawable.baseline_arrow_left_100,
            Resources.getSystem().newTheme()
        )
        DrawableCompat.setTint(
            drawable,
            if (isSelect) resources.getColor(
                R.color.ico_style_3,
                Resources.getSystem().newTheme()
            ) else resources.getColor(R.color.ico_style_1, Resources.getSystem().newTheme())
        )

        mDirB!!.setImageDrawable(drawable)
        mDirT!!.setImageDrawable(drawable)
        mDirL!!.setImageDrawable(drawable)
        mDirR!!.setImageDrawable(drawable)
    }

    private val kv = KeystoneVertex()

    private fun setValue() {

        isRK3326().yes {
            kv.getAllKeystoneVertex()
            kv.vTopLeft.x = (unitX * AX).toInt()
            kv.vTopLeft.y = (unitY * AY * 2).toInt()
            kv.vTopRight.x = (unitX * DX).toInt()
            kv.vTopRight.y = (unitY * DY * 2).toInt()
            kv.vBottomLeft.x = (unitX * BX).toInt()
            kv.vBottomLeft.y = (unitY * BY * 2).toInt()
            kv.vBottomRight.x = (unitX * CX).toInt()
            kv.vBottomRight.y = (unitY * CY * 2).toInt()
            kv.updateAllKeystoneVertex()
        }.otherwise {
            val kc = KeystoneCorrection(
                (unitX * BX), (unitY * BY * 2),
                (unitX * AX), (unitY * AY * 2),
                (unitX * CX), (unitY * CY * 2),
                (unitX * DX), (unitY * DY * 2)
            )
            keystone.setKeystoneCorrection(kc)
        }


        mSurfaceView!!.invalidate()
        syncScale()
    }

    private fun syncScale() {
        val doubles = doubleArrayOf(
            AX.toDouble(),
            AY.toDouble(),
            BX.toDouble(),
            BY.toDouble(),
            CX.toDouble(),
            CY.toDouble(),
            DX.toDouble(),
            DY.toDouble()
        )
        Arrays.sort(doubles)
        val num = H6Manager.getInstance(requireContext())?.zoomValue
        isH6().yes {
            if (num != null) {
                mScaleTextView!!.text = num.toString()
            }
        }.otherwise {
            mScaleTextView!!.text = doubles[0].toInt().toString()

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ScaleScreenFragment {
            val args = Bundle()

            val fragment = ScaleScreenFragment()
            fragment.arguments = args
            return fragment
        }

        private const val TAG = "ScaleScreenFragment"
        private const val KEYSTONE_LBX = "persist.display.keystone_lbx"
        private const val KEYSTONE_LBY = "persist.display.keystone_lby"
        private const val KEYSTONE_RBX = "persist.display.keystone_rbx"
        private const val KEYSTONE_RBY = "persist.display.keystone_rby"
        private const val KEYSTONE_LTX = "persist.display.keystone_ltx"
        private const val KEYSTONE_LTY = "persist.display.keystone_lty"
        private const val KEYSTONE_RTX = "persist.display.keystone_rtx"
        private const val KEYSTONE_RTY = "persist.display.keystone_rty"

        private const val upKey = 0
        private const val downKey = 1
        private const val leftKey = 2
        private const val rightKey = 3
        private const val menuKey = 4
    }
}
