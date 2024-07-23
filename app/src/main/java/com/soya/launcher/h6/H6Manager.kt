package com.soya.launcher.h6

import android.content.Context
import android.view.Surface
import com.soya.launcher.h6.H6Manager.Companion.writeFile_misc
import com.soya.launcher.rk3326.ReflectUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 提供管理TV图像的操作入口
 *
 * This class provides interfaces to manage picture of TV for application.
 * @since sdk-addon版本4
 * @author crabby.wang
 */
class H6Manager {
    /**
     * 图像模式
     * picture mode
     */
    enum class EN_KK_PICTURE_MODE {
        /**
         * DYNAMIC
         */
        DYNAMIC,

        /**
         * NORMAL
         */
        NORMAL,

        /**
         * SOFT
         */
        SOFT,

        /**
         * VIVID
         */
        VIVID,

        /**
         * 用户
         */
        USER,

        /**
         * 体育
         *
         * @author [赵曈](mailto:zhaotong@konka.com)，[陈梓江](mailto:chenzijiang@konka.com)
         * @since sdk-addon 版本62
         */
        SPORT
    }

    /**
     * 屏显模式
     * display mode
     */
    enum class EN_KK_DISPLAY_MODE {
        /**16:9 */
        R16X9,

        /**4:3 */
        R4X3,

        /**智能 */
        AUTO,

        /**电影 */
        MOVIE,

        /**字幕 */
        SUBTITLE,

        /**全景 */
        PANORAMA,

        /**全晰还原 */
        DOTBYDOT
    }

    /**
     * 色轮模式
     * color wheel mode
     */
    enum class EN_KK_COLOR_WHEEL_MODE {
        /**关 */
        OFF,

        /**开 */
        ON,

        /**演示 */
        DEMO
    }

    /**
     * 色温模式
     * color temperature mode
     */
    enum class EN_KK_COLOR_TEMPERATURE_MODE {
        /**亮丽 */
        COOL,

        /**标准 */
        STANDARD,

        /**自然 */
        WARM
    }

    /**
     * 肤色校正
     * skin tone correct mode
     */
    enum class EN_KK_SKIN_TONE_CORRECT_MODE {
        /**关 */
        OFF,

        /**RED_CORRECT */
        RED_CORRECT,

        /**YELLOW_CORRECT */
        YELLOW_CORRECT
    }

    /**
     * DNR（动态降噪）模式
     * DNR mode
     */
    enum class EN_KK_DNR_MODE {
        /**关 */
        OFF,

        /**低 */
        LOW,

        /**中 */
        MIDDLE,

        /**高 */
        HIGH,

        /**智能 */
        AUTO
    }

    /**
     * 高清眼模式
     */
    enum class EN_KK_HQ_EYE_MODE {
        /**开 */
        OPEN,

        /**关 */
        CLOSE,

        /**演示 */
        DEMO
    }

    /**
     * 图像尺寸
     *
     * @since sdk-addon版本7
     * @author crabby.wang
     */
    class KKPictureSize {
        /**宽 */
        var width: Int = 0

        /**高 */
        var height: Int = 0
    }

    /**
     * 设置图像模式
     * set picture mode
     * @param mode one of [EN_KK_PICTURE_MODE]
     * @return true if set successfully, false if failed.
     */
    fun setPictureMode(mode: EN_KK_PICTURE_MODE?): Boolean {
        return false
    }

    val pictureMode: EN_KK_PICTURE_MODE
        /**
         * 获取当前图像模式
         * get the current picture mode
         * @return the current picture mode [EN_KK_PICTURE_MODE]
         */
        get() = EN_KK_PICTURE_MODE.entries[0]

    /**
     * 设置背光。背光取值范围为0~100
     *
     *
     * set brightness
     * @param value the value of brightness.
     *
     *
     * the value ranges from 0 to 100
     * @return true if set successfully, false if failed.
     */
    fun setBrightness(value: Short): Boolean {
        return false
    }

    val brightness: Short
        /**
         * 获取当前亮度值.
         *
         *
         * get current brightness
         * @return the value of the brightness
         */
        get() = 0

    /**
     * 设置图像对比度
     * set contrast
     * @param value the value of contrast.
     *
     *
     * the value ranges from 0 to 100
     * @return true if set successfully, false if failed.
     */
    fun setContrast(value: Short): Boolean {
        return false
    }

    val contrast: Short
        /**
         * 获取当前图像对比度
         * get current contrast
         * @return the value of the contrast
         */
        get() = 0

    /**
     * 设置图像色度。注：取值范围为0~100
     * set color
     * @param value the value of color.
     *
     *
     * the value ranges from 0 to 100
     * @return true if set successfully, false if failed.
     */
    fun setColor(value: Short): Boolean {
        return false
    }

    val color: Short
        /**
         * 获取当前图像色度
         * get current color
         * @return the value of the color
         */
        get() = 0

    /**
     * 获取图像清晰度。注：取值范围为0~100
     * set sharpness
     * @param value the value of sharpness.
     *
     *
     * the value ranges from 0 to 100
     * @return true if set successfully, false if failed.
     */
    fun setSharpness(value: Short): Boolean {
        return false
    }

    val sharpness: Short
        /**
         * 获取当前图像清晰度
         * get current sharpness
         * @return the value of the sharpness
         */
        get() = 0

    /**
     * 设置色调。注：取值范围为0~100
     * set hue
     * @param value the value of hue.
     *
     *
     * the value ranges from 0 to 100
     * @return true if set successfully, false if failed.
     */
    fun setHue(value: Short): Boolean {
        return false
    }

    val hue: Short
        /**
         * 获取当前色调
         * get current hue
         * @return the value of the hue
         */
        get() = 0

    /**
     * 设置屏显模式
     * set display mode
     * @param mode the mode to be set [EN_KK_DISPLAY_MODE]
     * @return true is set successfully, false if failed.
     */
    fun setDisplayMode(mode: EN_KK_DISPLAY_MODE?): Boolean {
        return false
    }

    val displayMode: EN_KK_DISPLAY_MODE
        /**
         * 获取当前屏显模式
         *
         *
         * get current display mode
         * @return the current display mode [EN_KK_DISPLAY_MODE]
         */
        get() = EN_KK_DISPLAY_MODE.entries[0]

    /**
     * 设置背光值。注：取值范围为0~100
     *
     *
     * set back light value
     * @param value the value of the back light.
     *
     *
     * the value ranges from 0 to 100
     * @return true if succeed, false if failed.
     */
    fun setBacklight(value: Short): Boolean {
        return false
    }


    /**
     * 设置背光值,可选是否存数据库，是否执行功能。
     *
     *
     *
     * @param value 背光值，背光值范围0~100.
     * @param writeDB 是否存数据库
     * @param executeFunction 是否执行设置背光的功能
     * @return 是否执行成功
     */
    fun setBacklight(value: Short, writeDB: Boolean, executeFunction: Boolean): Boolean {
        return false
    }

    val backlight: Short
        /**
         * 获取背光值
         *
         *
         * get the value of back light.
         * @return the value of back light.
         */
        get() = 0

    /**
     * 设置色轮技术模式
     * set color wheel mode
     * @param mode the mode to be set [EN_KK_COLOR_WHEEL_MODE]
     * @return true if set successfully, false if failed.
     */
    fun setColorWheelMode(mode: EN_KK_COLOR_WHEEL_MODE?): Boolean {
        return false
    }

    val colorWheelMode: EN_KK_COLOR_WHEEL_MODE
        /**
         * 获取当前色轮技术模式
         * get the mode of color wheel
         * @return the mode of color wheel [EN_KK_COLOR_WHEEL_MODE]
         */
        get() = EN_KK_COLOR_WHEEL_MODE.entries[0]

    /**
     * 设置色温模式
     * set the color temperature mode
     * @param mode the color temperature mode to be set [EN_KK_COLOR_TEMPERATURE_MODE]
     * @return true if succeed, false if failed.
     */
    fun setColorTemperatureMode(mode: EN_KK_COLOR_TEMPERATURE_MODE?): Boolean {
        return false
    }

    val colorTemperatureMode: EN_KK_COLOR_TEMPERATURE_MODE
        /**
         * 获取色温模式
         * get the color temperature mode
         * @return the current color temperature mode [EN_KK_COLOR_TEMPERATURE_MODE]
         */
        get() = EN_KK_COLOR_TEMPERATURE_MODE.entries[0]

    /**
     * 设置肤色校正模式
     * set skin tone correct mode
     * @param mode the skin tone correct mode [EN_KK_SKIN_TONE_CORRECT_MODE]
     * @return true if succeed, false if failed.
     */
    fun setSkinToneCorrectMode(mode: EN_KK_SKIN_TONE_CORRECT_MODE?): Boolean {
        return false
    }

    val skinToneCorrectMode: EN_KK_SKIN_TONE_CORRECT_MODE
        /**
         * 获取当前肤色校正模式
         * get the skin tone correct mode
         * @return the skin tone correct mode [EN_KK_SKIN_TONE_CORRECT_MODE]
         */
        get() = EN_KK_SKIN_TONE_CORRECT_MODE.entries[0]

    /**
     * 设置是否启用细节增强
     * enable or disable detail enhance
     * @param enable true for enable, false for disable
     * @return true if succeed, false if failed.
     */
    fun setDetailEnhanceEnable(enable: Boolean): Boolean {
        return false
    }

    val isDetailEnhanceEnable: Boolean
        /**
         * 获取当前细节增强模式状态
         * get whether the detail enhance is enabled or disabled.
         * @return true if enable, false if disable.
         */
        get() = false

    /**
     * 设置动态降噪（DNR）模式
     * set DNR mode
     * @param mode the DNR mode [EN_KK_DNR_MODE]
     * @return true if succeed, false if failed.
     */
    fun setDNRMode(mode: EN_KK_DNR_MODE?): Boolean {
        return false
    }

    val dNRMode: EN_KK_DNR_MODE
        /**
         * 获取当前动态降噪（DNR）模式
         * get the DNR mode
         * @return the DNR mode [EN_KK_DNR_MODE]
         */
        get() = EN_KK_DNR_MODE.entries[0]

    /**
     * 设置是否启用动态靓彩功能
     * enable or disable dynamic color
     * @param enable true for enable, false for disable
     * @return true if succeed, false if failed.
     */
    fun setDynamicColorEnable(enable: Boolean): Boolean {
        return false
    }

    val isDynamicColorEnable: Boolean
        /**
         * 获取动态靓彩功能启用状态
         * get whether the dynamic color is enabled or disabled.
         * @return true if enable, false if disable.
         */
        get() = false

    /**
     * 设置水平位置。注：取值范围为 -50~50
     * set the offset of the horizontal position
     * @param value the offset of the horizontal position.
     *
     *
     * the offset ranges from -50 to 50
     * @return true if succeed, false if failed.
     */
    fun setPCHorizontalPostion(value: Short): Boolean {
        return false
    }

    val pCHorizontalPositon: Short
        /**
         * 获取当前水平模式
         *
         *
         * get the offset of horizontal position
         * @return the offset of horizontal position
         */
        get() = 0

    /**
     * 设置垂直位置。注：取值范围为 -50~50
     *
     *
     * set the offset of the vertical position
     * @param value the offset of the vertical position.
     *
     *
     * the offset ranges from -50 to 50
     * @return true if succeed, false if failed.
     */
    fun setPCVerticalPostion(value: Short): Boolean {
        return false
    }

    val pCVerticalPositon: Short
        /**
         * 获取当前垂直模式
         * get the offset of vertical position
         * @return the offset of vertical position
         */
        get() = 0

    /**
     * 设置PC模式下ADC水平总大小。注：取值范围为 0~100
     * set the ADC horizontal total size at PC mode.
     * @param value the value of ADC horizontal total size.
     *
     *
     * the value ranges from 0 to 100
     * @return true if succeed, false if failed.
     */
    fun setPCClock(value: Short): Boolean {
        return false
    }

    val pCClock: Short
        /**
         * 获取PC模式下的ADC水平总大小
         * get the ADC horizontal total size
         * @return the value of ADC horizontal total size
         */
        get() = 0

    /**
     * 设置PC模式或者YPBPR模式下的相位。注：取值范围为 0~100
     * set the ADC phase at PC mode or YPBPR mode.
     * @param value the value of ADC phase at PC mode or YPBPR mode.
     *
     *
     * the value ranges from 0 to 100
     * @return true if succeed, false if failed.
     */
    fun setPCPhase(value: Short): Boolean {
        return false
    }

    val pCPhase: Short
        /**
         * 获取PC模式或者YPBPR模式下的相位值
         * get the ADC phase at PC mode or YPBPR mode
         * @return the value of ADC phase at PC mode or YPBPR mode
         */
        get() = 0

    /**
     * 自动调整PC画面,这里没有使用回调表示自动调整已经完成
     * Auto tune the screen position at PC mode.
     * @return true if succeed, false if failed.
     */
    fun executeAutoPC(): Boolean {
        return false
    }

    /**
     * 设置动态背光开关
     * enable or disable dynamic back light
     * @param enable true for enable, false for disable.
     * @return true if succeed, false otherwise.
     */
    fun setDynamicBacklightEnable(enable: Boolean): Boolean {
        return false
    }

    val isDynamicBacklightEnable: Boolean
        /**
         * 获取当前动态背光启用状态
         * whether the dynamic back light is enabled or disabled.
         * @return true if enabled, false otherwise.
         */
        get() = false

    /**
     * 设置动态亮度控制(DLC)
     * enable or disable dynamic lumenance control.
     * @param enable true for enable, false for disable
     * @return true if succeed, false otherwise.
     */
    fun setDLCEnable(enable: Boolean): Boolean {
        return false
    }

    val dLCAverageLuminance: Int
        /**
         * 获取平均动态亮度
         * get average luminance of DLC.
         * @return true if succeed, false otherwise.
         */
        get() = 0

    /**
     * 设置是否启用图像静止
     * freeze or unfreeze the current image
     * @param enable true for freeze, false for unfreeze.
     * @return true if succeed, false if failed.
     */
    fun setFreezeEnable(enable: Boolean): Boolean {
        return false
    }


    val isCurrentVideoSupportScreenShot: Boolean
        /**
         * 当前图像是否支持截图
         * 例如3D，同步看，黄金左眼演示等状况不适合截图。
         * @return 是否支持接口[true/false]
         */
        get() = false

    /**
     * 设置高清眼(黄金左眼)模式
     * @param eMode [EN_KK_HQ_EYE_MODE]
     * @return true if success,else return false.
     */
    fun setHQEyeMode(eMode: EN_KK_HQ_EYE_MODE?): Boolean {
        return false
    }

    val hQEyeMode: EN_KK_HQ_EYE_MODE
        /**
         * 获取高清眼(黄金左眼)模式
         * @return [EN_KK_HQ_EYE_MODE]
         */
        get() = EN_KK_HQ_EYE_MODE.entries[0]

    /**
     * 4K2K模式是否处于打开状态
     * @return true for enable, false for disable.
     */
    fun is4K2KModeEnable(): Boolean {
        return false
    }

    /**
     * 打开或者关闭4K2K模式
     * @param enable true for open, false for close.
     * @return 是否执行成功
     */
    fun set4K2KModeEnable(enable: Boolean): Boolean {
        return false
    }

    /**
     * 设置4K2K下的surface，以显示4K2K的图片
     * @return 是否设置成功
     */
    fun setSurfaceBypassFor4K2K(surface: Surface?): Boolean {
        return false
    }

    /**
     * 显示4K2S预览窗口
     * @return 是否执行成功
     */
    fun show4K2KPreviewWindow(): Boolean {
        return false
    }

    /**
     * 关闭4K2S预览窗口
     * @return 是否执行成功
     */
    fun close4K2KPreviewWindow(): Boolean {
        return false
    }

    /**
     * 切换4K2S主窗口
     * 进入当前选择的预览窗口
     * @return 是否执行成功
     */
    fun switch4K2KMainWindow(): Boolean {
        return false
    }

    val isMFCByPassModeEnable: Boolean
        /**
         * 是否是MFC by pass 模式
         * @return true or false
         */
        get() = false

    /**
     * 设置MFC by pass 模式
     * @param enable MFC by pass 模式开关
     * @return 是否执行成功
     */
    fun setMFCByPassModeEnable(enable: Boolean): Boolean {
        return false
    }

    /**
     * 显示4K2K图片
     * @since sdk-addon版本5
     * @author wangfeng
     * @param filePath 4K2K图片路径
     * @return 是否执行成功
     */
    fun show4K2KPicture(filePath: String?): Boolean {
        return false
    }

    /**
     * 停止4K2K图片播放器
     * @since sdk-addon版本6
     * @author wangfeng
     * @return 是否执行成功
     */
    fun stop4K2KPicturePlayer(): Boolean {
        return false
    }

    /**
     * 获取图片宽和高
     * @param filePath 图片完整路径
     * @return 如果文件不存在或者不是可识别的图片格式，返回0，否则返回图片原始大小
     */
    fun getPictureSize(filePath: String?): KKPictureSize? {
        //TODO
        return null
    }

    /**
     * 打开或者关闭背光
     * @since sdk-addon版本19
     * @author wangfeng
     * @param enable 打开或者关闭
     * @return 是否设置成功
     */
    fun setBacklightEnable(enable: Boolean): Boolean {
        //TODO
        return false
    }

    val isBacklightEnable: Boolean
        /**
         * 获取背光的状态
         * @since sdk-addon版本19
         * @author wangfeng
         * @return true：打开 false:关闭
         */
        get() =//TODO
            true

    /**
     * 设置高色域演示状态
     * @since sdk-addon版本29
     * @author wangfeng
     * @param enable 打开或者关闭
     * @return 是否设置成功
     */
    fun setHighColorGamutDemoEnable(enable: Boolean): Boolean {
        return false
    }

    val isHighColorGamutDemoEnable: Boolean
        /**
         * 获取高色域演示状态
         * @since sdk-addon版本29
         * @author wangfeng
         * @return true:打开 false:关闭
         */
        get() = false

    /**
     * add by konka linpc
     */
    fun getAdvancedPQState(nu: Int): Int {
        return 1
    }

    var screenMirror: Int
        /**
         * 获取安装模式
         * @return  0：桌面正投   1：桌面背投   2：吊装正投   3：吊装背投
         */
        get() = ReflectUtils.getProperty("persist.sys.keystone.mirror", "0").toInt()
        /**
         * 设置安装模式
         * @param type：安装模式的类型(0：桌面正投   1：桌面背投   2：吊装正投   3：吊装背投)
         */
        set(type) {
            //System.out.println("yxp setScreenMirror:"+type);
            ReflectUtils.setProperty("persist.sys.keystone.mirror", type.toString())
            writeFile_misc(type.toString())
        }

    /**
     * 四点梯形矫正--设置左上角坐标
     */
    fun setLeftTopOffset(x: Float, y: Float) {
        println("yxp lefttop x:" + x + "yxp lefttop y:" + y)
        ReflectUtils.setProperty(
            "persist.sys.keystone.lb", x.toInt()
                .toString() + "," + y.toInt()
        )

        ReflectUtils.setProperty("persist.sys.keystone.update", "1")
    }

    val leftTopOffset: FloatArray
        /**
         * 四点梯形矫正--获取左上角坐标
         */
        get() {
            val temp = FloatArray(2)
            temp[0] = ReflectUtils.getProperty("persist.sys.keystone.lb", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                .toFloat()
            temp[1] = ReflectUtils.getProperty("persist.sys.keystone.lb", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
                .toFloat()
            return temp
        }

    /**
     * 四点梯形矫正--设置右上角坐标
     */
    fun setRightTopOffset(x: Float, y: Float) {
        println("yxp righttop x:" + x + "yxp righttop y:" + y)

        ReflectUtils.setProperty(
            "persist.sys.keystone.rb", x.toInt()
                .toString() + "," + y.toInt()
        )

        ReflectUtils.setProperty("persist.sys.keystone.update", "1")
    }

    val rightTopOffset: FloatArray
        /**
         * 四点梯形矫正--获取右上角坐标
         */
        get() {
            val temp = FloatArray(2)
            temp[0] = ReflectUtils.getProperty("persist.sys.keystone.rb", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                .toFloat()
            temp[1] = ReflectUtils.getProperty("persist.sys.keystone.rb", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
                .toFloat()
            return temp
        }

    /**
     * 四点梯形矫正--设置左下角坐标
     */
    fun setLeftBottomOffset(x: Float, y: Float) {
        println("yxp leftbottom x:" + x + "yxp leftbottom y:" + y)

        ReflectUtils.setProperty(
            "persist.sys.keystone.lt", x.toInt()
                .toString() + "," + y.toInt()
        )

        ReflectUtils.setProperty("persist.sys.keystone.update", "1")
    }

    val leftBottomOffset: FloatArray
        /**
         * 四点梯形矫正--获取左下角坐标
         */
        get() {
            val temp = FloatArray(2)
            temp[0] = ReflectUtils.getProperty("persist.sys.keystone.lt", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                .toFloat()
            temp[1] = ReflectUtils.getProperty("persist.sys.keystone.lt", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
                .toFloat()
            return temp
        }

    /**
     * 四点梯形矫正--设置右下角坐标
     */
    fun setRightBottomOffset(x: Float, y: Float) {
        println("yxp rightbottom x:" + x + "yxp rightbottom y:" + y)
        ReflectUtils.setProperty(
            "persist.sys.keystone.rt", x.toInt()
                .toString() + "," + y.toInt()
        )

        ReflectUtils.setProperty("persist.sys.keystone.update", "1")
    }

    val rightBottomOffset: FloatArray
        /**
         * 四点梯形矫正--获取右下角坐标
         */
        get() {
            val temp = FloatArray(2)
            temp[0] = ReflectUtils.getProperty("persist.sys.keystone.rt", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                .toFloat()
            temp[1] = ReflectUtils.getProperty("persist.sys.keystone.rt", "0")
                .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
                .toFloat()
            return temp
        }

    /**
     * 四点梯形矫正--复位
     */
    fun resetTrapezoid() {
        ReflectUtils.setProperty("persist.sys.keystone.lt", 0.toString() + "," + 0.toString())
        ReflectUtils.setProperty("persist.sys.keystone.lb", 0.toString() + "," + 0.toString())
        ReflectUtils.setProperty("persist.sys.keystone.rt", 0.toString() + "," + 0.toString())
        ReflectUtils.setProperty("persist.sys.keystone.rb", 0.toString() + "," + 0.toString())

        ReflectUtils.setProperty("persist.sys.keystone.update", "1")
    }

    var horizontalDegree: Float
        /**
         * 水平与垂直梯形矫正--获取水平方向角度
         */
        get() = 1.0f
        /**
         * 水平与垂直梯形矫正--设置水平方向角度
         */
        set(value) {
        }

    /**
     * 水平与垂直梯形矫正--设置垂直方向角度
     */
    fun setVerticalDegree(value: Float) {
    }

    val vertivalDegree: Float
        /**
         * 水平与垂直梯形矫正--获取垂直方向角度
         */
        get() = 1.0f

    /**
     * 水平与垂直梯形矫正--复位
     */
    fun resetDegree() {
    }

    fun reduceByPercentage(orValue: Double, percentaga: Double): Double {
        return orValue * (100 - percentaga) / 100
    }

    var zoomValue: Int
        /**
         * 缩放--获取缩放值
         */
        get() = ReflectUtils.getProperty("persist.sys.keystone.zoom", "0").toInt()
        /**
         * 缩放--设置缩放值
         */
        set(value) {
            var x = 960.0
            var y = 540.0
            ReflectUtils.setProperty("persist.sys.keystone.zoom", value.toString())
            x = reduceByPercentage(x, value.toDouble())
            y = reduceByPercentage(y, value.toDouble())
            ReflectUtils.setProperty(
                "persist.sys.keystone.lt",
                x.toInt().toString() + "," + (y.toInt())
            )
            ReflectUtils.setProperty(
                "persist.sys.keystone.lb",
                x.toInt().toString() + "," + (y.toInt())
            )
            ReflectUtils.setProperty(
                "persist.sys.keystone.rt",
                x.toInt().toString() + "," + (y.toInt())
            )
            ReflectUtils.setProperty(
                "persist.sys.keystone.rb",
                x.toInt().toString() + "," + (y.toInt())
            )

            ReflectUtils.setProperty("persist.sys.keystone.update", "1")
        }

    val zoomMinValue: Int
        /**
         * 缩放的最小值
         */
        get() = 50

    val horizontalMaxDegree: Int
        /**
         * 垂直水平校正时，水平方向上，可调整的最大角度
         */
        get() = 45

    val verticalMaxDegree: Int
        /**
         * 垂直水平校正时，垂直方向上，可调整的最大角度
         */
        get() = 45

    val fourPointXMaxValue: Float
        /**
         * 四点校正时，X坐标最大值
         */
        get() = 1920.0f

    val fourPointYMaxValue: Float
        /**
         * 四点校正时，Y坐标最大值
         */
        get() = 1080.0f


    /**
     * 设置 自动梯形矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动梯形矫正   false代表需要关闭自动梯形矫正
     */
    fun setTrapezoidCorrectEnable(enable: Boolean) {
        if (enable) {
            ReflectUtils.setProperty("persist.sys.trapezoid", "true")
        } else {
            ReflectUtils.setProperty("persist.sys.trapezoid", "false")
        }
    }

    val trapezoidCorrectStatus: Boolean
        /**
         * 获取 自动梯形矫正 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动梯形矫正   false代表当前关闭了自动梯形矫正
         */
        get() = ReflectUtils.getPropertyBoolean("persist.sys.trapezoid", false)

    /**
     * 设置 自动水平矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动水平矫正   false代表需要关闭自动水平矫正
     */
    fun setHorizontalCorrectEnable(enable: Boolean) {
    }

    val horizontalCorrectStatus: Boolean
        /**
         * 获取 自动水平矫正 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动水平矫正   false代表当前关闭了自动水平矫正
         */
        get() = true

    /**
     * 设置 自动垂直矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动垂直矫正   false代表需要关闭自动垂直矫正
     */
    fun setVerticalCorrectEnable(enable: Boolean) {
    }

    val verticalCorrectStatus: Boolean
        /**
         * 获取 自动垂直矫正 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动垂直矫正   false代表当前关闭了自动垂直矫正
         */
        get() = false

    /**
     * 设置 自动对焦 开启或者关闭
     * @param enable：true代表需要开启自动对焦   false代表需要关闭自动对焦
     */
    fun setAutoFocusEnable(enable: Boolean) {
        if (enable) {
            ReflectUtils.setProperty("persist.sys.autofocus", "true")
        } else {
            ReflectUtils.setProperty("persist.sys.autofocus", "false")
        }
    }

    val autoFocusStatus: Boolean
        /**
         * 获取 自动对焦 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动对焦   false代表当前关闭了自动对焦
         */
        get() = ReflectUtils.getPropertyBoolean("persist.sys.autofocus", false)

    /**
     * 设置 开机自动对焦 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启开机自动对焦   false代表需要关闭开机自动对焦
     */
    fun setPowerOnAutoFocusEnable(enable: Boolean) {
    }

    val powerOnAutoFocusStatus: Boolean
        /**
         * 获取 开机自动对焦 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了开机自动对焦   false代表当前关闭了开机自动对焦
         */
        get() = true

    /**
     * 设置 自动避障 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动避障   false代表需要关闭自动避障
     */
    fun setAutoObstacleAvoidanceEnable(enable: Boolean) {
        if (enable) {
            ReflectUtils.setProperty("persist.sys.ObstacleAvoidance", "true")
        } else {
            ReflectUtils.setProperty("persist.sys.ObstacleAvoidance", "false")
        }
    }

    val autoObstacleAvoidanceStatus: Boolean
        /**
         * 获取 自动避障 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动避障   false代表当前关闭了自动避障
         */
        get() = ReflectUtils.getPropertyBoolean("persist.sys.ObstacleAvoidance", false)

    /**
     * 设置 自动入慕 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动入慕   false代表需要关闭自动入慕
     */
    fun setAutoComeAdmireEnable(enable: Boolean) {
        if (enable) {
            ReflectUtils.setProperty("persist.sys.ComeAdmireEnable", "true")
        } else {
            ReflectUtils.setProperty("persist.sys.ComeAdmireEnable", "false")
        }
    }

    val autoComeAdmireStatus: Boolean
        /**
         * 获取 自动入慕 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表当前开启了自动入慕   false代表当前关闭了自动入慕
         */
        get() = ReflectUtils.getPropertyBoolean("persist.sys.ComeAdmireEnable", false)


    /**
     * 设置 全向自动校正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * * @param enable：true代表 需要开启全向自动校正   false代表 需要关闭全向自动校正
     */
    fun setAllDirAutoCorrectEnable(enable: Boolean) {
    }

    val allDirAutoCorrectStatus: Boolean
        /**
         * 获取 全向自动校正 开启或者关闭 的状态
         * 如使用康佳梯形矫正算法，此处无需填写
         * @return status: true代表 当前开启了全向自动校正   false代表 当前关闭了全向自动校正
         */
        get() = true

    val isShowFourPointTra: Boolean
        /**
         * 系统是否需要展示 手动四点梯形矫正
         * @return result: true代表 系统需要展示四点梯形矫正   false代表 系统不需要展示四点梯形矫正
         */
        get() = true

    val isShowHorizontalVerticalTra: Boolean
        /**
         * 系统是否需要展示 水平与垂直梯形矫正
         * @return result: true代表 系统需要展示水平与垂直梯形矫正   false代表 系统不需要展示水平与垂直梯形矫正
         */
        get() = false

    val isShowZoom: Boolean
        /**
         * 系统是否需要展示 缩放
         * @return result: true代表 系统需要展示缩放   false代表 系统不需要展示缩放
         */
        get() = true

    val isShowAutoTrapezoidCorrect: Boolean
        /**
         * 系统是否需要展示 自动梯形矫正开关
         * @return result: true代表 系统需要展示自动梯形矫正   false代表 系统不需要展示自动梯形矫正
         */
        get() = false

    val isShowAllDirAutoCorrect: Boolean
        /**
         * 系统是否需要展示 全向自动校正开关
         * @return result: true代表 系统需要展示全向自动校正   false代表 系统不需要展示全向自动校正
         */
        get() = false

    val isShowHorizontalAutoCorrect: Boolean
        /**
         * 系统是否需要展示 自动水平校正开关
         * @return result: true代表 系统需要展示自动水平校正   false代表 系统不需要展示自动水平校正
         */
        get() = false

    val isShowVerticalAutoCorrect: Boolean
        /**
         * 系统是否需要展示 自动垂直校正开关
         * @return result: true代表 系统需要展示自动垂直校正   false代表 系统不需要展示自动垂直校正
         */
        get() = false

    val isShowAutoFocus: Boolean
        /**
         * 系统是否需要展示 自动对焦开关
         * @return result: true代表 系统需要展示自动对焦   false代表 系统不需要展示自动对焦
         */
        get() = false

    val isShowPowerOnAutoFocus: Boolean
        /**
         * 系统是否需要展示 开机自动对焦
         * @return result: true代表 系统需要展示开机自动对焦   false代表 系统不需要展示开机自动对焦
         */
        get() = false

    val isShowAutoObstacleAvoidance: Boolean
        /**
         * 系统是否需要展示 自动避障
         * @return result: true代表 系统需要展示自动避障  false代表 系统不需要展示自动避障
         */
        get() = false

    val isShowAutoComeAdmire: Boolean
        /**
         * 系统是否需要展示 自动入慕
         * @return result: true代表 系统需要展示自动入慕  false代表 系统不需要展示自动入慕
         */
        get() = false


    /**
     * 自动对焦相关
     * 控制马达：Status=0：停止；1：后退 ；2：前进
     */
    fun setMotorStatus(Status: Int, bAuto: Boolean) {
    }


    val motorStatus: Int
        /**
         * 自动对焦相关
         * 获取马达的状态
         * @return  0：停止；1：后退 ；2：前进
         */
        get() = 0


    /**
     * 自动对焦相关
     * 转到马达到某点位
     */
    fun setMotorToPosition(pos: Int) {
    }

    val motorPosition: Int
        /**
         * 自动对焦相关
         * @return result: 返回马达当前点位
         */
        get() = 0

    val currentPowerOnChannel: String?
        /**
         * 获取当前已设置的 开机通道
         */
        get() = null

    val allPowerOnChannel: Array<String>?
        /**
         * 获取所有 开机通道数据
         */
        get() = null

    /**
     * 设置 开机通道
     */
    fun setPowerOnChannel(channel: String?) {
    }


    /**
     * 设置自动对焦梯形标定数据
     * Genser 数据信息
     * 建议存储到不可擦除的数据区域
     * @return 是否存储成功
     */
    fun setGenserData(VERTICAL_Y: Double?, VERTICAL_X: Double?, HORIZONTAL: Double?): Boolean {
        return false
    }


    /**
     * 获取自动对焦梯形标定数据
     * Genser 数据信息
     * 建议存储到不可擦除的数据区域
     * @return Double[]
     */
    fun getGenserData(
        VERTICAL_Y: Double?,
        VERTICAL_X: Double?,
        HORIZONTAL: Double?
    ): Array<Double>? {
        return null
    }

    companion object {
        //add by linpc
        private var h6Manager: H6Manager? = null
        fun getInstance(context: Context?): H6Manager? {
            if (h6Manager == null) {
                synchronized(H6Manager::class.java) {
                    if (h6Manager == null) {
                        h6Manager = H6Manager()
                    }
                }
            }
            return h6Manager
        }

        //梯形相关功能
        fun writeFile_misc(buffer: String) {
            // delete old version
            val file = File("/dev/block/bootdevice/by-name/misc") //if(!file.exists())t
            var fos: FileOutputStream? = null
            val mirror_buffer = buffer.toByteArray()

            try {
                file.createNewFile()
                fos = FileOutputStream(file)
                fos.write(mirror_buffer, 0, mirror_buffer.size)
                fos.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
