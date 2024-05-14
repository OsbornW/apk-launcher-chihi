package com.shudong.lib_res.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import java.io.IOException
import java.util.regex.Pattern

/**
 * Created by xiaokai on 2017/02/07.
 * 字符串匹配表情
 */
object SpanStringUtils {
    /*
    * emotionNum 表情编号
    * */
    @JvmStatic
    fun getEmotionContent(context: Context, tv: TextView, source: String?): SpannableString {
        var emotionNum = 0
        val spannableString = SpannableString(source)
        val regexEmotion = "\\[[s]:\\d+\\]" //正则表达式规则 --> [d:数字]
        val patternEmotion = Pattern.compile(regexEmotion)
        val matcherEmotion = patternEmotion.matcher(spannableString)
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            val key = matcherEmotion.group()
            emotionNum = Integer.valueOf(key.substring(3, key.indexOf("]"))) //获取到表情的编号
            // 匹配字符串的开始位置
            val start = matcherEmotion.start()
            try {
                // 压缩表情图片
                val size = tv.textSize.toInt() * 13 / 10
                val `in` = context.assets.open("ems/$emotionNum.webp")
                val bitmap = BitmapFactory.decodeStream(`in`)
                val scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
                val span = ImageSpan(context, scaleBitmap)
                spannableString.setSpan(
                    span,
                    start,
                    start + key.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return spannableString
    }
}