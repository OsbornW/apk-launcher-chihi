package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.softwinner.keystone.KeystoneCorrection
import com.softwinner.keystone.KeystoneCorrectionManager
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentGradientBinding
import com.soya.launcher.ext.isH6
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.rk3326.KeystoneVertex
import com.soya.launcher.view.KeyEventFrameLayout
import com.soya.launcher.view.KeyEventFrameLayout.KeyEventCallback
import java.util.concurrent.TimeUnit

abstract class AbsGradientFragment<VDB : FragmentGradientBinding, VM : BaseViewModel> 
    : BaseWallPaperFragment<VDB,VM>(), View.OnClickListener, KeyEventCallback {
    private var x = 0f
    private var y = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var detialX = 0f
    private var detialY = 0f
    private var keystone: KeystoneCorrectionManager? = null
    private val MAX_LOOP = 4
    private val MAX_PROGRESS = 100
    private var type = TYPE_LT
    private var index = 0
    private var time: Long = -1
    private var isOut = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keystone = KeystoneCorrectionManager()
    }

   

    override fun onStart() {
        super.onStart()
        time = -1
        isOut = false
        mBind.root.post {
            syncDetial()
            setDefaultXY(type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        time = -1
        isOut = true
    }

    override fun initView() {
        mBind.root.setKeyEventCallback(this)
        mBind.longClickTip.visibility = if (useLongClick()) View.VISIBLE else View.GONE
        mBind.content.apply { post { requestFocus() } }

    }
   

    override fun onKeyDown(event: KeyEvent) {
        if (mBind.root.measuredWidth == 0 || mBind.root.measuredHeight == 0) return
        if (centerX == 0f || centerY == 0f) {
            syncDetial()
        }
        val keyCode = event.keyCode
        val action = event.action
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> if (action == 0) {
                if (type == TYPE_LT || type == TYPE_RT) {
                    y -= detialY
                } else {
                    y += detialY
                }
                if (y < 0) y = 0f
                if (y > centerY) y = centerY
                setValue(type, DIR_Y, y)
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> if (action == 0) {
                if (type == TYPE_LT || type == TYPE_RT) {
                    y += detialY
                } else {
                    y -= detialY
                }
                if (y > centerY) y = centerY
                if (y < 0) y = 0f
                setValue(type, DIR_Y, y)
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> if (action == 0) {
                if (type == TYPE_LT || type == TYPE_LB) {
                    x -= detialX
                } else {
                    x += detialX
                }
                if (x < 0) x = 0f
                if (x > centerX) x = centerX
                setValue(type, DIR_X, x)
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> if (action == 0) {
                if (type == TYPE_LT || type == TYPE_LB) {
                    x += detialX
                } else {
                    x -= detialX
                }
                if (x > centerX) x = centerX
                if (x < 0) x = 0f
                setValue(type, DIR_X, x)
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> if (event.action == 0) {
                if (time == -1L) time = System.currentTimeMillis()
                if (TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - time) >= 2000 && !isOut && useLongClick()) {
                    isOut = true
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_browse_fragment, GuideDateFragment.newInstance())
                        .addToBackStack(null).commit()
                }
            } else if (event.action == 1 && !isOut) {
                time = -1
                type = ++index % MAX_LOOP
                setDefaultXY(type)
            }

            KeyEvent.KEYCODE_MENU -> reset()
        }
    }

    private fun syncDetial() {
        centerX = (mBind.root.measuredWidth / 2).toFloat()
        centerY = (mBind.root.measuredHeight / 2).toFloat()
        detialX = centerX / MAX_PROGRESS
        detialY = centerY / MAX_PROGRESS
    }

    private fun setInstructionVisible(lt: Boolean, rt: Boolean, lb: Boolean, rb: Boolean) {
        mBind.ltAnchors.visibility = if (lt) View.VISIBLE else View.GONE
        mBind.rtAnchors.visibility = if (rt) View.VISIBLE else View.GONE
        mBind.lbAnchors.visibility = if (lb) View.VISIBLE else View.GONE
        mBind.rbAnchors.visibility = if (rb) View.VISIBLE else View.GONE
        mBind.ltLr.visibility = if (lt) View.VISIBLE else View.GONE
        mBind.ltTb.visibility = if (lt) View.VISIBLE else View.GONE
        mBind.rtLr.visibility = if (rt) View.VISIBLE else View.GONE
        mBind.rtTb.visibility = if (rt) View.VISIBLE else View.GONE
        mBind.lbLr.visibility = if (lb) View.VISIBLE else View.GONE
        mBind.lbTb.visibility = if (lb) View.VISIBLE else View.GONE
        mBind.rbLr.visibility = if (rb) View.VISIBLE else View.GONE
        mBind.rbTb.visibility = if (rb) View.VISIBLE else View.GONE
    }

    private fun setDefaultXY(type: Int) {
        when (type) {
            TYPE_LT -> {
                setInstructionVisible(true, false, false, false)
                x = getValue(KEYSTONE_LTX, 0f)
                y = getValue(KEYSTONE_LTY, 0f)
            }

            TYPE_LB -> {
                setInstructionVisible(false, false, true, false)
                x = getValue(KEYSTONE_LBX, 0f)
                y = getValue(KEYSTONE_LBY, 0f)
            }

            TYPE_RT -> {
                setInstructionVisible(false, true, false, false)
                x = getValue(KEYSTONE_RTX, 0f)
                y = getValue(KEYSTONE_RTY, 0f)
            }

            TYPE_RB -> {
                setInstructionVisible(false, false, false, true)
                x = getValue(KEYSTONE_RBX, 0f)
                y = getValue(KEYSTONE_RBY, 0f)
            }
        }
        val xP = (x / centerX * 100).toInt()
        val yP = (y / centerY * 100).toInt()
        when (type) {
            TYPE_LT -> {
                mBind.lrText.text = xP.toString()
                mBind.tbText.text = yP.toString()
            }

            TYPE_LB -> {
                mBind.tbText.text = xP.toString()
                mBind.lrText.text = yP.toString()
            }

            TYPE_RT -> {
                mBind.lrText.text = xP.toString()
                mBind.tbText.text = yP.toString()
            }

            TYPE_RB -> {
                mBind.tbText.text = xP.toString()
                mBind.lrText.text = yP.toString()
            }
        }
        switchDir(type)
    }

    private fun switchDir(type: Int) {
        when (type) {
            TYPE_LT -> {
                mBind.dirLr.rotation = 180f
                mBind.dirTb.rotation = 270f
            }

            TYPE_LB -> {
                mBind.dirLr.rotation = 90f
                mBind.dirTb.rotation = 180f
            }

            TYPE_RT -> {
                mBind.dirLr.rotation = 360f
                mBind.dirTb.rotation = 270f
            }

            TYPE_RB -> {
                mBind.dirLr.rotation = 90f
                mBind.dirTb.rotation = 360f
            }
        }
    }

    private val kv = KeystoneVertex()


    private fun setValue(type: Int, direction: Int, value: Float) {
        if (value < 0) return

        val ltx = if (type == TYPE_LT && direction == DIR_X) value else getValue(KEYSTONE_LTX, 0f)
        val lty = if (type == TYPE_LT && direction == DIR_Y) value else getValue(KEYSTONE_LTY, 0f)
        val lbx = if (type == TYPE_LB && direction == DIR_X) value else getValue(KEYSTONE_LBX, 0f)
        val lby = if (type == TYPE_LB && direction == DIR_Y) value else getValue(KEYSTONE_LBY, 0f)
        val rtx = if (type == TYPE_RT && direction == DIR_X) value else getValue(KEYSTONE_RTX, 0f)
        val rty = if (type == TYPE_RT && direction == DIR_Y) value else getValue(KEYSTONE_RTY, 0f)
        val rbx = if (type == TYPE_RB && direction == DIR_X) value else getValue(KEYSTONE_RBX, 0f)
        val rby = if (type == TYPE_RB && direction == DIR_Y) value else getValue(KEYSTONE_RBY, 0f)

        when{
            isRK3326()->{
                kv.getAllKeystoneVertex()
                kv.vTopLeft.x = ltx.toInt()
                kv.vTopLeft.y = lty.toInt()
                kv.vTopRight.x = rtx.toInt()
                kv.vTopRight.y = rty.toInt()
                kv.vBottomLeft.x = lbx.toInt()
                kv.vBottomLeft.y = lby.toInt()
                kv.vBottomRight.x = rbx.toInt()
                kv.vBottomRight.y = rby.toInt()
                kv.updateAllKeystoneVertex()
            }
            isH6()->{
                "坐标：$ltx=====$lty"
                kv.getAllKeystoneVertexForH6()
                kv.vTopLeft.x = ltx.toInt()
                kv.vTopLeft.y = lty.toInt()
                kv.vTopRight.x = rtx.toInt()
                kv.vTopRight.y = rty.toInt()
                kv.vBottomLeft.x = lbx.toInt()
                kv.vBottomLeft.y = lby.toInt()
                kv.vBottomRight.x = rbx.toInt()
                kv.vBottomRight.y = rby.toInt()
                kv.updateAllKeystoneVertexForH6()
            }
            else->{
                val correction = KeystoneCorrection(
                    lbx.toDouble(),
                    lby.toDouble(),
                    ltx.toDouble(),
                    lty.toDouble(),
                    rbx.toDouble(),
                    rby.toDouble(),
                    rtx.toDouble(),
                    rty.toDouble()
                )
                val success = keystone!!.setKeystoneCorrection(correction)
            }
        }


        mBind.surface.invalidate()
        when (type) {
            TYPE_LT -> {
                mBind.lrText.text = (ltx / centerX * 100).toInt().toString()
                mBind.tbText.text = (lty / centerY * 100).toInt().toString()
            }

            TYPE_LB -> {
                mBind.tbText.text = (lbx / centerX * 100).toInt().toString()
                mBind.lrText.text = (lby / centerY * 100).toInt().toString()
            }

            TYPE_RT -> {
                mBind.lrText.text = (rtx / centerX * 100).toInt().toString()
                mBind.tbText.text = (rty / centerY * 100).toInt().toString()
            }

            TYPE_RB -> {
                mBind.tbText.text = (rbx / centerX * 100).toInt().toString()
                mBind.lrText.text = (rby / centerY * 100).toInt().toString()
            }
        }
    }

    private fun reset() {
        x = 0f
        y = 0f
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
            isH6().yes {
                kv.getAllKeystoneVertexForH6()
                kv.vTopLeft.x = 0
                kv.vTopLeft.y = 0
                kv.vTopRight.x = 0
                kv.vTopRight.y = 0
                kv.vBottomLeft.x = 0
                kv.vBottomLeft.y = 0
                kv.vBottomRight.x = 0
                kv.vBottomRight.y = 0
                kv.updateAllKeystoneVertexForH6()
            }.otherwise {
                val correction = KeystoneCorrection(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                keystone!!.setKeystoneCorrection(correction)
            }

        }

        mBind.surface.invalidate()
        mBind.lrText.text = 0.toString()
        mBind.tbText.text = 0.toString()
    }

    private fun getValue(key: String, def: Float): Float {
        var value = def.toDouble()
        isRK3326().yes {
            kv.getAllKeystoneVertex()
            when (key) {
                KEYSTONE_LBX -> value = kv.vBottomLeft.x.toDouble()
                KEYSTONE_LBY -> value = kv.vBottomLeft.y.toDouble()
                KEYSTONE_LTX -> value = kv.vTopLeft.x.toDouble()
                KEYSTONE_LTY -> value = kv.vTopLeft.y.toDouble()
                KEYSTONE_RBX -> value = kv.vBottomRight.x.toDouble()
                KEYSTONE_RBY -> value = kv.vBottomRight.y.toDouble()
                KEYSTONE_RTX -> value = kv.vTopRight.x.toDouble()
                KEYSTONE_RTY -> value = kv.vTopRight.y.toDouble()
            }
        }.otherwise {
            isH6().yes {
                kv.getAllKeystoneVertexForH6()
                when (key) {
                    KEYSTONE_LBX -> value = kv.vBottomLeft.x.toDouble()
                    KEYSTONE_LBY -> value = kv.vBottomLeft.y.toDouble()
                    KEYSTONE_LTX -> value = kv.vTopLeft.x.toDouble()
                    KEYSTONE_LTY -> value = kv.vTopLeft.y.toDouble()
                    KEYSTONE_RBX -> value = kv.vBottomRight.x.toDouble()
                    KEYSTONE_RBY -> value = kv.vBottomRight.y.toDouble()
                    KEYSTONE_RTX -> value = kv.vTopRight.x.toDouble()
                    KEYSTONE_RTY -> value = kv.vTopRight.y.toDouble()
                }
            }.otherwise {
                val correction = keystone!!.keystoneCorrection
                when (key) {
                    KEYSTONE_LBX -> value = correction.leftBottomX
                    KEYSTONE_LBY -> value = correction.leftBottomY
                    KEYSTONE_LTX -> value = correction.leftTopX
                    KEYSTONE_LTY -> value = correction.leftTopY
                    KEYSTONE_RBX -> value = correction.rightBottomX
                    KEYSTONE_RBY -> value = correction.rightBottomY
                    KEYSTONE_RTX -> value = correction.rightTopX
                    KEYSTONE_RTY -> value = correction.rightTopY
                }
            }

        }

        return value.toFloat()
    }

    protected open fun useLongClick(): Boolean {
        return true
    }

    companion object {
        private const val DIR_X = 0
        private const val DIR_Y = 1
        private const val KEYSTONE_LBX = "persist.display.keystone_lbx"
        private const val KEYSTONE_LBY = "persist.display.keystone_lby"
        private const val KEYSTONE_RBX = "persist.display.keystone_rbx"
        private const val KEYSTONE_RBY = "persist.display.keystone_rby"
        private const val KEYSTONE_LTX = "persist.display.keystone_ltx"
        private const val KEYSTONE_LTY = "persist.display.keystone_lty"
        private const val KEYSTONE_RTX = "persist.display.keystone_rtx"
        private const val KEYSTONE_RTY = "persist.display.keystone_rty"

        const val KEYSTONE_RK3326_TOP_LEFT = "persist.sys.keystone.lt"
        const val KEYSTONE_RK3326_TOP_RIGHT = "persist.sys.keystone.rt"
        const val KEYSTONE_RK3326_BOTTOM_LEFT = "persist.sys.keystone.lb"
        const val KEYSTONE_RK3326_BOTTOM_RIGHT = "persist.sys.keystone.rb"

        const val TYPE_LT = 0
        const val TYPE_LB = 1
        const val TYPE_RB = 2
        const val TYPE_RT = 3
    }
}
