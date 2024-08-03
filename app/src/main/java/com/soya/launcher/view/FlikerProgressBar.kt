package com.soya.launcher.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.soya.launcher.R
import kotlin.math.min

/**
 * Created by chenliu on 2016/8/26.<br></br>
 * 描述：添加圆角支持 on 2016/11/11
 *
 */
class FlikerProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Runnable {
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

    private val DEFAULT_HEIGHT_DP = 35

    private var borderWidth = 0

    private val maxProgress = 100f

    private var textPaint: Paint? = null

    private var bgPaint: Paint? = null

    private var pgPaint: Paint? = null

    private var progressText: String? = null

    private var textRect: Rect? = null

    private var bgRectf: RectF? = null

    /**
     * 左右来回移动的滑块
     */
    private var flikerBitmap: Bitmap? = null

    /**
     * 滑块移动最左边位置，作用是控制移动
     */
    private var flickerLeft = 0f

    /**
     * 进度条 bitmap ，包含滑块
     */
    private var pgBitmap: Bitmap? = null

    private var pgCanvas: Canvas? = null

    /**
     * 当前进度
     */
    private var progress = 0f

    var isFinish: Boolean = false
        private set

    private var isStop = false

    /**
     * 下载中颜色
     */
    private var loadingColor = 0

    /**
     * 暂停时颜色
     */
    private var stopColor = 0


    private var textSize = 0

    private var radius = 0

    private var thread: Thread? = null

    var bitmapShader: BitmapShader? = null

    // 在 FlikerProgressBar 类中添加 bgcolor 属性
    private var bgColor = 0
    private var borderColor = 0
    private var textColor = 0
    private var progressColor = 0

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FlikerProgressBar)
        try {
            textSize = ta.getDimension(R.styleable.FlikerProgressBar_textSize, 12f).toInt()
            loadingColor =
                ta.getColor(R.styleable.FlikerProgressBar_loadingColor, Color.parseColor("#40c4ff"))
            stopColor =
                ta.getColor(R.styleable.FlikerProgressBar_stopColor, Color.parseColor("#ff9800"))
            radius = ta.getDimension(R.styleable.FlikerProgressBar_radius, 0f).toInt()
            borderWidth = ta.getDimension(R.styleable.FlikerProgressBar_borderWidth, 1f).toInt()
            progressText = ta.getText(R.styleable.FlikerProgressBar_defaultText) as String
            bgColor = ta.getColor(R.styleable.FlikerProgressBar_bgcolor, Color.TRANSPARENT)
            borderColor = ta.getColor(R.styleable.FlikerProgressBar_borderColor, Color.TRANSPARENT)
            textColor = ta.getColor(R.styleable.FlikerProgressBar_textColor, Color.WHITE)
            progressColor = ta.getColor(R.styleable.FlikerProgressBar_progressColor, Color.parseColor("#40c4ff"))
        } finally {
            ta.recycle()
        }
    }

    private fun init() {
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        bgPaint!!.style = Paint.Style.STROKE
        bgPaint!!.strokeWidth = borderWidth.toFloat()

        pgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pgPaint!!.style = Paint.Style.FILL

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint!!.textSize = textSize.toFloat()

        textRect = Rect()
        bgRectf = RectF(
            borderWidth.toFloat(),
            borderWidth.toFloat(),
            (measuredWidth - borderWidth).toFloat(),
            (measuredHeight - borderWidth).toFloat()
        )

      /*  progressColor = if (isStop) {
            stopColor
        } else {
            loadingColor
        }*/

        flikerBitmap = BitmapFactory.decodeResource(resources, R.drawable.flicker)
        flickerLeft = -(flikerBitmap?.width?.toFloat()?:0f)

        initPgBimap()
    }

    private fun initPgBimap() {
        pgBitmap = Bitmap.createBitmap(
            measuredWidth - borderWidth,
            measuredHeight - borderWidth,
            Bitmap.Config.ARGB_8888
        )
        pgCanvas = Canvas(pgBitmap!!)
        thread = Thread(this)
        thread!!.start()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        when (heightSpecMode) {
            MeasureSpec.AT_MOST -> height = dp2px(DEFAULT_HEIGHT_DP)
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> height = heightSpecSize
        }
        setMeasuredDimension(widthSpecSize, height)

        if (pgBitmap == null) {
            init()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //背景
        drawBackGround(canvas)

        //进度
        drawProgress(canvas)

        //进度text
        drawProgressText(canvas)

        //变色处理
        if(!isFinish)drawColorProgressText(canvas)
    }

    /**
     * 边框
     * @param canvas
     */
    private fun drawBackGround(canvas: Canvas) {
        // 绘制背景颜色
        bgPaint!!.color = bgColor
        bgPaint!!.style = Paint.Style.FILL

        // 绘制背景矩形
        val bgRectf = RectF(
            borderWidth / 2f,
            borderWidth / 2f,
            measuredWidth - borderWidth / 2f,
            measuredHeight - borderWidth / 2f
        )
        canvas.drawRoundRect(bgRectf, radius.toFloat(), radius.toFloat(), bgPaint!!)

        // 绘制边框
        bgPaint!!.color = borderColor
        bgPaint!!.style = Paint.Style.STROKE

        // 计算边框的绘制区域
        val borderRectf = RectF(
            borderWidth / 2f,
            borderWidth / 2f,
            measuredWidth - borderWidth / 2f,
            measuredHeight - borderWidth / 2f
        )
        canvas.drawRoundRect(borderRectf, radius.toFloat(), radius.toFloat(), bgPaint!!)
    }




    /**
     * 进度
     */
    private fun drawProgress(canvas: Canvas) {
        pgPaint!!.color = progressColor

        val right = (progress / maxProgress) * measuredWidth
        pgCanvas!!.save()
        pgCanvas!!.clipRect(0f, 0f, right, measuredHeight.toFloat())
        pgCanvas!!.drawColor(progressColor)
        pgCanvas!!.restore()

        if (!isStop) {
            pgPaint!!.setXfermode(xfermode)
            pgCanvas!!.drawBitmap(flikerBitmap!!, flickerLeft, 0f, pgPaint)
            pgPaint!!.setXfermode(null)
        }

        //控制显示区域
        bitmapShader = BitmapShader(pgBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        pgPaint!!.setShader(bitmapShader)
        canvas.drawRoundRect(bgRectf!!, radius.toFloat(), radius.toFloat(), pgPaint!!)
    }

    /**
     * 进度提示文本
     * @param canvas
     */
    private fun drawProgressText(canvas: Canvas) {
        textPaint!!.color = textColor
        textPaint!!.getTextBounds(progressText, 0, progressText!!.length, textRect)
        val tWidth = textRect!!.width()
        //val tHeight = textRect!!.height()
        val xCoordinate = ((measuredWidth - tWidth) / 2).toFloat()

        // 计算基线位置
        val fontMetrics = textPaint!!.fontMetrics
        val yCoordinate = ((measuredHeight - fontMetrics.descent + fontMetrics.ascent) / 2 - fontMetrics.ascent).toFloat()

        canvas.drawText(progressText!!, xCoordinate, yCoordinate, textPaint!!)
    }

    /**
     * 变色处理
     * @param canvas
     */
    private fun drawColorProgressText(canvas: Canvas) {
        // 根据进度设置文本颜色
        val currentTextColor = if (progress >= maxProgress) {
            textColor // 进度达到 100% 时使用原始文本颜色
        } else {
            Color.WHITE // 进度未达到 100% 时使用白色
        }

        textPaint!!.color = currentTextColor

        val tWidth = textRect!!.width()
        val tHeight = textRect!!.height()
        val xCoordinate = ((measuredWidth - tWidth) / 2).toFloat()
        val yCoordinate = ((measuredHeight + tHeight) / 2).toFloat()
        val progressWidth = (progress / maxProgress) * measuredWidth
        if (progressWidth > xCoordinate) {
            canvas.save()
            val right = min(progressWidth.toDouble(), (xCoordinate + tWidth * 1.1f).toDouble())
                .toFloat()
            canvas.clipRect(xCoordinate, 0f, right, measuredHeight.toFloat())
            canvas.drawText(progressText!!, xCoordinate, yCoordinate, textPaint!!)
            canvas.restore()
        }

    }



    fun setProgress(progress: Float) {
        if (!isStop) {
            if (progress < maxProgress) {
                this.progress = progress
            } else {
                this.progress = maxProgress
                finishLoad()
            }
            invalidate()
        }
    }

    fun setProgressText(text: String?) {
        progressText = text
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate() // 刷新 View
    }

    fun setTextColor(color: Int) {
        textColor = color
        invalidate() // 刷新视图以应用新的文本颜色
    }

    fun setStop(stop: Boolean) {
        isStop = stop
        if (isStop) {
            progressColor = Color.TRANSPARENT
            thread!!.interrupt()
        } else {
            //progressColor = loadingColor
            thread = Thread(this)
            thread!!.start()
        }
        invalidate()
    }

    fun finishLoad() {
        isFinish = true
        setStop(true)
    }

    fun toggle() {
        if (!isFinish) {
            if (isStop) {
                setStop(false)
            } else {
                setStop(true)
            }
        }
    }

    override fun run() {
        val width = flikerBitmap!!.width
        try {
            while (!isStop && !thread!!.isInterrupted) {
                flickerLeft += dp2px(5).toFloat()
                val progressWidth = (progress / maxProgress) * measuredWidth
                if (flickerLeft >= progressWidth) {
                    flickerLeft = -width.toFloat()
                }
                postInvalidate()
                Thread.sleep(20)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 重置
     */
    fun reset() {
        setStop(true)
        progress = 0f
        isFinish = false
        isStop = false
       // progressColor = loadingColor
        //progressText = ""
        flickerLeft = -flikerBitmap!!.width.toFloat()
        initPgBimap()
    }

    fun getProgress(): Float {
        return progress
    }

    fun isStop(): Boolean {
        return isStop
    }

    private fun getProgressText(): String {
        var text = ""
        text = if (!isFinish) {
            if (!isStop) {
                "下载中$progress%"
            } else {
                "继续"
            }
        } else {
            "下载完成"
        }

        return text
    }

    private fun dp2px(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}