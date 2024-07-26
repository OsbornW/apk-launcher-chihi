package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.softwinner.keystone.KeystoneCorrection
import com.softwinner.keystone.KeystoneCorrectionManager
import com.soya.launcher.R
import com.soya.launcher.ext.isH6
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.rk3326.KeystoneVertex
import com.soya.launcher.view.KeyEventFrameLayout
import com.soya.launcher.view.KeyEventFrameLayout.KeyEventCallback
import java.util.concurrent.TimeUnit

abstract class AbsGradientFragment : AbsFragment(), View.OnClickListener, KeyEventCallback {
    private var x = 0f
    private var y = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var detialX = 0f
    private var detialY = 0f
    private var mRootView: KeyEventFrameLayout? = null
    private var keystone: KeystoneCorrectionManager? = null
    private var mSurfaceView: View? = null
    private var mContentView: View? = null
    private var mLT: View? = null
    private var mRT: View? = null
    private var mLB: View? = null
    private var mRB: View? = null
    private var mLongTipView: View? = null
    private var mLRTextView: TextView? = null
    private var mTBTextView: TextView? = null
    private var mLTA: View? = null
    private var mLBA: View? = null
    private var mRTA: View? = null
    private var mRBA: View? = null
    private var mLT_LR: View? = null
    private var mLT_TB: View? = null
    private var mLB_LR: View? = null
    private var mLB_TB: View? = null
    private var mRT_LR: View? = null
    private var mRT_TB: View? = null
    private var mRB_LR: View? = null
    private var mRB_TB: View? = null
    private var mDirLR: ImageView? = null
    private var mDirTB: ImageView? = null
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

    override fun getLayoutId(): Int {
        return R.layout.fragment_gradient
    }

    override fun onStart() {
        super.onStart()
        time = -1
        isOut = false
        mRootView!!.post {
            syncDetial()
            setDefaultXY(type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        time = -1
        isOut = true
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mRootView = view.findViewById(R.id.root)
        mSurfaceView = view.findViewById(R.id.surface)
        mContentView = view.findViewById(R.id.content)
        mLT = view.findViewById(R.id.lt)
        mRT = view.findViewById(R.id.rt)
        mLB = view.findViewById(R.id.lb)
        mRB = view.findViewById(R.id.rb)
        mLTA = view.findViewById(R.id.lt_anchors)
        mLBA = view.findViewById(R.id.lb_anchors)
        mRTA = view.findViewById(R.id.rt_anchors)
        mRBA = view.findViewById(R.id.rb_anchors)
        mLT_LR = view.findViewById(R.id.lt_lr)
        mLT_TB = view.findViewById(R.id.lt_tb)
        mLB_LR = view.findViewById(R.id.lb_lr)
        mLB_TB = view.findViewById(R.id.lb_tb)
        mRT_LR = view.findViewById(R.id.rt_lr)
        mRT_TB = view.findViewById(R.id.rt_tb)
        mRB_LR = view.findViewById(R.id.rb_lr)
        mRB_TB = view.findViewById(R.id.rb_tb)
        mLongTipView = view.findViewById(R.id.long_click_tip)
        mTBTextView = view.findViewById(R.id.tb_text)
        mLRTextView = view.findViewById(R.id.lr_text)
        mDirLR = view.findViewById(R.id.dir_lr)
        mDirTB = view.findViewById(R.id.dir_tb)


    }

    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mRootView!!.setKeyEventCallback(this)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mLongTipView!!.visibility = if (useLongClick()) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mContentView)
    }

    override fun onClick(v: View) {}
    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    override fun onKeyDown(event: KeyEvent) {
        if (mRootView!!.measuredWidth == 0 || mRootView!!.measuredHeight == 0) return
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
        centerX = (mRootView!!.measuredWidth / 2).toFloat()
        centerY = (mRootView!!.measuredHeight / 2).toFloat()
        detialX = centerX / MAX_PROGRESS
        detialY = centerY / MAX_PROGRESS
    }

    private fun setInstructionVisible(lt: Boolean, rt: Boolean, lb: Boolean, rb: Boolean) {
        mLTA!!.visibility = if (lt) View.VISIBLE else View.GONE
        mRTA!!.visibility = if (rt) View.VISIBLE else View.GONE
        mLBA!!.visibility = if (lb) View.VISIBLE else View.GONE
        mRBA!!.visibility = if (rb) View.VISIBLE else View.GONE
        mLT_LR!!.visibility = if (lt) View.VISIBLE else View.GONE
        mLT_TB!!.visibility = if (lt) View.VISIBLE else View.GONE
        mRT_LR!!.visibility = if (rt) View.VISIBLE else View.GONE
        mRT_TB!!.visibility = if (rt) View.VISIBLE else View.GONE
        mLB_LR!!.visibility = if (lb) View.VISIBLE else View.GONE
        mLB_TB!!.visibility = if (lb) View.VISIBLE else View.GONE
        mRB_LR!!.visibility = if (rb) View.VISIBLE else View.GONE
        mRB_TB!!.visibility = if (rb) View.VISIBLE else View.GONE
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
                mLRTextView!!.text = xP.toString()
                mTBTextView!!.text = yP.toString()
            }

            TYPE_LB -> {
                mTBTextView!!.text = xP.toString()
                mLRTextView!!.text = yP.toString()
            }

            TYPE_RT -> {
                mLRTextView!!.text = xP.toString()
                mTBTextView!!.text = yP.toString()
            }

            TYPE_RB -> {
                mTBTextView!!.text = xP.toString()
                mLRTextView!!.text = yP.toString()
            }
        }
        switchDir(type)
    }

    private fun switchDir(type: Int) {
        when (type) {
            TYPE_LT -> {
                mDirLR!!.rotation = 180f
                mDirTB!!.rotation = 270f
            }

            TYPE_LB -> {
                mDirLR!!.rotation = 90f
                mDirTB!!.rotation = 180f
            }

            TYPE_RT -> {
                mDirLR!!.rotation = 360f
                mDirTB!!.rotation = 270f
            }

            TYPE_RB -> {
                mDirLR!!.rotation = 90f
                mDirTB!!.rotation = 360f
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
                "坐标：$ltx=====$lty".e("zengyue")
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


        mSurfaceView!!.invalidate()
        when (type) {
            TYPE_LT -> {
                mLRTextView!!.text = (ltx / centerX * 100).toInt().toString()
                mTBTextView!!.text = (lty / centerY * 100).toInt().toString()
            }

            TYPE_LB -> {
                mTBTextView!!.text = (lbx / centerX * 100).toInt().toString()
                mLRTextView!!.text = (lby / centerY * 100).toInt().toString()
            }

            TYPE_RT -> {
                mLRTextView!!.text = (rtx / centerX * 100).toInt().toString()
                mTBTextView!!.text = (rty / centerY * 100).toInt().toString()
            }

            TYPE_RB -> {
                mTBTextView!!.text = (rbx / centerX * 100).toInt().toString()
                mLRTextView!!.text = (rby / centerY * 100).toInt().toString()
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

        mSurfaceView!!.invalidate()
        mLRTextView!!.text = 0.toString()
        mTBTextView!!.text = 0.toString()
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
