package com.thumbsupec.lib_base.widget.verifycode

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.*
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.shudong.lib_base.R
import com.shudong.lib_base.view.verifycode.PasswordTransformMethod

/**
 * Ray
 * VerificationCodeView
 * 2017/10/31
 * update 2020/3/26
 */
class VerificationCodeView(private val mContext: Context, attrs: AttributeSet?) : LinearLayout(
    mContext, attrs
), TextWatcher, View.OnKeyListener, OnFocusChangeListener {
    var onTextChange: ((view: View, content: String?) -> Unit)? = null
    var onComplete: ((view: View, content: String) -> Unit)? = null

    /**
     * 输入框数量
     */
    private var mEtNumber: Int

    /**
     * 输入框类型
     */
    private var mEtInputType: VCInputType

    /**
     * 输入框的宽度
     */
    private var mEtWidth: Int

    /**
     * 文字颜色
     */
    private var mEtTextColor: Int

    /**
     * 文字大小
     */
    private var mEtTextSize: Float

    /**
     * 输入框背景
     */
    private var mEtTextBg: Int

    /**
     * 输入框间距
     */
    private var mEtSpacing = 0

    /**
     * 平分后的间距
     */
    private var mEtBisectSpacing = 0

    /**
     * 判断是否平分
     */
    private val isBisect: Boolean

    /**
     * 是否显示光标
     */
    private val cursorVisible: Boolean

    /**
     * 光标样式
     */
    private var mCursorDrawable: Int

    /**
     * 输入框宽度
     */
    private var mViewWidth = 0

    /**
     * 输入框间距
     */
    private val mViewMargin = 0
    fun getmEtNumber(): Int {
        return mEtNumber
    }

    fun setmEtNumber(mEtNumber: Int) {
        this.mEtNumber = mEtNumber
    }

    fun getmEtInputType(): VCInputType {
        return mEtInputType
    }

    fun setmEtInputType(mEtInputType: VCInputType) {
        this.mEtInputType = mEtInputType
    }

    fun getmEtWidth(): Int {
        return mEtWidth
    }

    fun setmEtWidth(mEtWidth: Int) {
        this.mEtWidth = mEtWidth
    }

    fun getmEtTextColor(): Int {
        return mEtTextColor
    }

    fun setmEtTextColor(mEtTextColor: Int) {
        this.mEtTextColor = mEtTextColor
    }

    fun getmEtTextSize(): Float {
        return mEtTextSize
    }

    fun setmEtTextSize(mEtTextSize: Float) {
        this.mEtTextSize = mEtTextSize
    }

    fun getmEtTextBg(): Int {
        return mEtTextBg
    }

    fun setmEtTextBg(mEtTextBg: Int) {
        this.mEtTextBg = mEtTextBg
    }

    fun getmCursorDrawable(): Int {
        return mCursorDrawable
    }

    fun setmCursorDrawable(mCursorDrawable: Int) {
        this.mCursorDrawable = mCursorDrawable
    }

    enum class VCInputType {
        /**
         * 数字类型
         */
        NUMBER,

        /**
         * 数字密码
         */
        NUMBERPASSWORD,

        /**
         * 文字
         */
        TEXT,

        /**
         * 文字密码
         */
        TEXTPASSWORD
    }

    init {
        @SuppressLint("Recycle", "CustomViewStyleable") val typedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.VerificationCodeView)
        mEtNumber = typedArray.getInteger(R.styleable.VerificationCodeView_vcv_et_number, 4)
        val inputType = typedArray.getInt(
            R.styleable.VerificationCodeView_vcv_et_inputType,
            VCInputType.NUMBER.ordinal
        )
        mEtInputType = VCInputType.values()[inputType]
        mEtWidth =
            typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_vcv_et_width, 120)
        mEtTextColor =
            typedArray.getColor(R.styleable.VerificationCodeView_vcv_et_text_color, Color.BLACK)
        mEtTextSize =
            typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_vcv_et_text_size, 16)
                .toFloat()
        mEtTextBg = typedArray.getResourceId(
            R.styleable.VerificationCodeView_vcv_et_bg,
            R.drawable.et_login_code
        )
        mCursorDrawable = typedArray.getResourceId(
            R.styleable.VerificationCodeView_vcv_et_cursor,
            R.drawable.et_cursor
        )
        cursorVisible =
            typedArray.getBoolean(R.styleable.VerificationCodeView_vcv_et_cursor_visible, true)
        isBisect = typedArray.hasValue(R.styleable.VerificationCodeView_vcv_et_spacing)
        if (isBisect) {
            mEtSpacing =
                typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_vcv_et_spacing, 0)
        }
        initView()
        //释放资源
        typedArray.recycle()
    }

    fun setOnCodeListener(onComplete:((view: View, content: String)->Unit)?=null,onChange:((view: View, content: String?)->Unit)?=null,){
        this.onTextChange = onChange
        this.onComplete = onComplete
    }

    @SuppressLint("ResourceAsColor")
    private fun initView() {
        for (i in 0 until mEtNumber) {
            val editText = EditText(mContext)
            initEditText(editText, i)
            addView(editText)
            //设置第一个editText获取焦点
            if (i == 0) {
                editText.isFocusable = true
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun initEditText(editText: EditText, i: Int) {
        editText.layoutParams = getETLayoutParams(i)
        editText.textAlignment = TEXT_ALIGNMENT_CENTER
        editText.gravity = Gravity.CENTER
        editText.id = i
        editText.isCursorVisible = false
        editText.maxEms = 1
        editText.setTextColor(mEtTextColor)
        editText.textSize = mEtTextSize
        editText.isCursorVisible = cursorVisible
        editText.maxLines = 1
        editText.filters = arrayOf<InputFilter>(LengthFilter(1))
        when (mEtInputType) {
            VCInputType.NUMBER -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            VCInputType.NUMBERPASSWORD -> {
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                editText.transformationMethod = PasswordTransformMethod()
            }
            VCInputType.TEXT -> editText.inputType = InputType.TYPE_CLASS_TEXT
            VCInputType.TEXTPASSWORD -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            else -> editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        editText.setPadding(0, 0, 0, 0)
        editText.setOnKeyListener(this)
        editText.setBackgroundResource(mEtTextBg)
        setEditTextCursorDrawable(editText)
        editText.addTextChangedListener(this)
        editText.onFocusChangeListener = this
    }

    /**
     * 获取EditText 的 LayoutParams
     */
    fun getETLayoutParams(i: Int): LayoutParams {
        val layoutParams = LayoutParams(mEtWidth, mEtWidth)
        if (!isBisect) {
            //平分Margin，把第一个EditText跟最后一个EditText的间距同设为平分
            mEtBisectSpacing = (mViewWidth - mEtNumber * mEtWidth) / (mEtNumber + 1)
            if (i == 0) {
                layoutParams.leftMargin = mEtBisectSpacing
                layoutParams.rightMargin = mEtBisectSpacing / 2
            } else if (i == mEtNumber - 1) {
                layoutParams.leftMargin = mEtBisectSpacing / 2
                layoutParams.rightMargin = mEtBisectSpacing
            } else {
                layoutParams.leftMargin = mEtBisectSpacing / 2
                layoutParams.rightMargin = mEtBisectSpacing / 2
            }
        } else {
            layoutParams.leftMargin = mEtSpacing / 2
            layoutParams.rightMargin = mEtSpacing / 2
        }
        layoutParams.gravity = Gravity.CENTER
        return layoutParams
    }

    fun setEditTextCursorDrawable(editText: EditText) {
        //修改光标的颜色（反射）
        if (cursorVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText.setTextCursorDrawable(mCursorDrawable)
            } else {
                try {
                    val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                    f.isAccessible = true
                    f[editText] = mCursorDrawable
                } catch (ignored: Exception) {
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mViewWidth = measuredWidth
        updateETMargin()
    }

    private fun updateETMargin() {
        for (i in 0 until mEtNumber) {
            val editText = getChildAt(i) as EditText
            editText.layoutParams = getETLayoutParams(i)
        }
    }

    override fun onFocusChange(view: View, b: Boolean) {
        if (b) {
            var et: EditText? = null
            if (cursorVisible) {
                et = view as EditText
                et.isCursorVisible = true
            } else {
                et!!.isCursorVisible = false
            }
            et.requestFocus()
            //focus01();
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (s.length != 0) {
            focus()
        }
        if (onTextChange != null) {
            onTextChange?.invoke(this, result)
            //如果最后一个输入框有字符，则返回结果
            val childCount = childCount
            var count = 0
            for (i in 0 until childCount) {
                val currentEditText = getChildAt(i) as EditText
                if (currentEditText.text.length > 0) {
                    ++count
                }
            }
            if (count == mEtNumber) {
                onComplete?.invoke(this, result)
            }
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            val editText = v as EditText

            backFocus(editText)
        }
        return false
    }

    override fun setEnabled(enabled: Boolean) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.isEnabled = enabled
        }
    }

    /**
     * 获取焦点
     */
    private fun focus01() {
        val count = childCount
        val editText: EditText
        //利用for循环找出还最前面那个还没被输入字符的EditText，并把焦点移交给它。
        for (i in 0 until count) {
            editText = getChildAt(i) as EditText
            editText.isCursorVisible = cursorVisible
            editText.requestFocus()
            return
        }
    }

    /**
     * 获取焦点
     */
    fun focus(callback:((et:EditText)->Unit)?=null) {
        val count = childCount
        var editText: EditText? = null
        //利用for循环找出还最前面那个还没被输入字符的EditText，并把焦点移交给它。
        for (i in 0 until count) {
            editText = getChildAt(i) as EditText
            if (editText.text.length < 1) {
                editText.isCursorVisible = cursorVisible
                editText.requestFocus()
                if (editText != null) {
                    callback?.invoke(editText)
                }
                return
            } else {
                editText.isCursorVisible = false
                if (i == count - 1) {
                    editText.requestFocus()
                }
            }
        }
        if (editText != null) {
            callback?.invoke(editText)
        }


    }

    private fun backFocus(et: EditText) {
        if (!TextUtils.isEmpty(et.text.toString())) {
            return
        }
        var editText: EditText
        //循环检测有字符的`editText`，把其置空，并获取焦点。
        for (i in mEtNumber - 1 downTo 0) {
            editText = getChildAt(i) as EditText
            val b = et === editText
            // ;
            if (b) {
                // if (editText.getText().length() >= 1) {
                if (i == 0) {
                    return
                }
                editText = getChildAt(i - 1) as EditText
                editText.setText("")
                editText.isCursorVisible = cursorVisible
                editText.requestFocus()
                return
                // }
            }

            //;
        }
    }

    val result: String
        get() {
            val stringBuffer = StringBuilder()
            var editText: EditText
            for (i in 0 until mEtNumber) {
                editText = getChildAt(i) as EditText
                stringBuffer.append(editText.text)
            }
            return stringBuffer.toString()
        }



    /**
     * 清空验证码输入框
     */
    fun setEmpty() {
        var editText: EditText
        for (i in mEtNumber - 1 downTo 0) {
            editText = getChildAt(i) as EditText
            if (editText == null) {

            } else {

            }
            editText.setText("")
            if (i == 0) {
                editText.isCursorVisible = cursorVisible
                editText.requestFocus()
            }
        }
    }
}