package com.shudong.lib_base.ext.photo

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.pictureselector.CoilEngine
import com.shudong.lib_base.R
import com.shudong.lib_base.ext.loadPic
import com.shudong.lib_base.ext.yes
import com.thumbsupec.lib_base.ext.photo.ImageCropEngine
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


var option: PictureSelectionModel? = null
fun AppCompatActivity.openAlbum(
    picNum: Int,
    isCrop: Boolean = false,
    isShowCamera: Boolean = true,
    listener: ((pathList: List<String>) -> Unit)?,

    ) {
    setUIStyle(this)
    val selector = PictureSelector.create(this)

    option = selector.openGallery(SelectMimeType.TYPE_IMAGE)

    isCrop.yes {
        option!!.setCropEngine(ImageCropEngine())
    }
    option!!
        .isGif(false)
        .isWebp(false)
        .isBmp(false)
        .isDisplayCamera(isShowCamera)
        .setMaxSelectNum(picNum)
        .setSelectorUIStyle(selectorStyle)
        .setCameraInterceptListener { fragment, cameraMode, requestCode ->
            val camera = SimpleCameraX.of()
            camera.setCameraMode(cameraMode)
            //camera.setOutputPathDir(config.outPutCameraDir)
            camera.setImageEngine { context, url, imageView ->
                imageView.loadPic(url)
            }
            fragment?.requireContext()?.let { camera.start(it, fragment, requestCode) }
        }
        .setImageEngine(CoilEngine())
        .setCompressEngine(CompressFileEngine { context, source, call ->
            Luban.with(this).load(source).ignoreBy(100)
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(source: String?, compressFile: File?) {
                        call?.onCallback(source, compressFile?.absolutePath)
                    }

                    override fun onError(source: String?, e: Throwable?) {
                        call?.onCallback(source, null)
                    }

                }).launch()
        })

        .forResult(object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: ArrayList<LocalMedia?>) {
                val pathList = arrayListOf<String>()
                result.forEach {
                    if (it?.cutPath?.isNotEmpty() == true) {
                        pathList.add(it.cutPath)
                    } else {
                        if (it?.compressPath.isNullOrEmpty()) {
                            pathList.add(it?.realPath!!)
                        } else {
                            pathList.add(it?.compressPath!!)
                        }
                    }

                }
                listener?.invoke(pathList)

            }

            override fun onCancel() {}
        })
}


fun AppCompatActivity.openCamera(
    listener: ((pathList: List<String>) -> Unit)?,
) {
    PictureSelector.create(this)
        .openCamera(SelectMimeType.ofImage())
        .setCameraInterceptListener { fragment, cameraMode, requestCode ->
            val camera = SimpleCameraX.of()
            camera.setCameraMode(cameraMode)
            //camera.setOutputPathDir(config.outPutCameraDir)
            camera.setImageEngine { context, url, imageView ->
                imageView.loadPic(url)
            }
            fragment?.requireContext()?.let { camera.start(it, fragment, requestCode) }
        }
        .setCompressEngine(CompressFileEngine { context, source, call ->
            Luban.with(this).load(source).ignoreBy(100)
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(source: String?, compressFile: File?) {
                        call?.onCallback(source, compressFile?.absolutePath)
                    }

                    override fun onError(source: String?, e: Throwable?) {
                        call?.onCallback(source, null)
                    }

                }).launch()
        })
        .forResult(object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: ArrayList<LocalMedia?>) {
                val pathList = arrayListOf<String>()
                result.forEach {
                    if (it?.cutPath?.isNotEmpty() == true) {
                        pathList.add(it.cutPath)
                    } else {
                        if (it?.compressPath.isNullOrEmpty()) {
                            pathList.add(it?.realPath!!)
                        } else {
                            pathList.add(it?.compressPath!!)
                        }
                    }

                }
                listener?.invoke(pathList)

            }

            override fun onCancel() {}
        })
}


private val selectorStyle = PictureSelectorStyle()
fun setUIStyle(activity: Activity) {
    val whiteTitleBarStyle = TitleBarStyle()
    whiteTitleBarStyle.titleBackgroundColor =
        ContextCompat.getColor(activity, com.shudong.lib_res.R.color.white)
    whiteTitleBarStyle.titleDrawableRightResource = R.drawable.ic_orange_arrow_down
    whiteTitleBarStyle.titleLeftBackResource = com.luck.picture.lib.R.drawable.ps_ic_black_back
    whiteTitleBarStyle.titleTextColor =
        ContextCompat.getColor(activity, com.shudong.lib_res.R.color.black)
    whiteTitleBarStyle.titleCancelTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_53575e)
    whiteTitleBarStyle.isDisplayTitleBarLine = true

    val whiteBottomNavBarStyle = BottomNavBarStyle()
    whiteBottomNavBarStyle.bottomNarBarBackgroundColor = Color.parseColor("#EEEEEE")

    whiteBottomNavBarStyle.bottomPreviewNormalTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_9b)
    whiteBottomNavBarStyle.bottomPreviewSelectTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_fa632d)

    whiteBottomNavBarStyle.isCompleteCountTips = false
    whiteBottomNavBarStyle.bottomEditorTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_53575e)
    whiteBottomNavBarStyle.bottomOriginalTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_53575e)

    whiteBottomNavBarStyle.bottomNarBarHeight = 200

    val selectMainStyle = SelectMainStyle()
    selectMainStyle.statusBarColor = ContextCompat.getColor(activity, R.color.ps_color_white)
    selectMainStyle.isDarkStatusBarBlack = true
    selectMainStyle.selectNormalTextColor =
        ContextCompat.getColor(activity, R.color.ps_color_9b)
    selectMainStyle.selectTextColor = ContextCompat.getColor(activity, R.color.ps_color_fa632d)
    selectMainStyle.previewSelectBackground =
        R.drawable.ps_demo_white_preview_selector
    selectMainStyle.selectBackground = com.luck.picture.lib.R.drawable.ps_checkbox_selector
    selectMainStyle.selectText = activity.getString(com.luck.picture.lib.R.string.ps_done_front_num)
    selectMainStyle.mainListBackgroundColor =
        ContextCompat.getColor(activity, R.color.ps_color_white)
    //selectMainStyle.previewSelectTextSize = 20
    //selectMainStyle.selectNormalTextSize = 20
    selectMainStyle.selectTextSize = 22

    //val selectorStyle = PictureSelectorStyle()
    selectorStyle.titleBarStyle = whiteTitleBarStyle
    selectorStyle.bottomBarStyle = whiteBottomNavBarStyle
    selectorStyle.selectMainStyle = selectMainStyle
}
