package com.thumbsupec.lib_base.ext.photo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.luck.picture.lib.engine.CropFileEngine
import com.shudong.lib_base.R
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.globalCoroutine
import com.shudong.lib_base.ext.loadBitmap
import com.shudong.lib_base.ext.loadPicCrop
import com.shudong.lib_base.ext.photo.ImageLoaderUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import kotlinx.coroutines.launch
import java.io.File

internal class ImageCropEngine : CropFileEngine {

    override fun onStartCrop(
        fragment: Fragment?,
        srcUri: Uri?,
        destinationUri: Uri?,
        dataSource: java.util.ArrayList<String>?,
        requestCode: Int
    ) {
        val options = buildOptions()
        val uCrop = UCrop.of(srcUri!!, destinationUri!!, dataSource)
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                if (!ImageLoaderUtils.assertValidRequest(context)) {
                    return
                }
                imageView.loadPicCrop(url)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>
            ) {
                globalCoroutine().launch {
                    val bitmap = url.loadBitmap(maxWidth, maxHeight)
                    call.onCall(bitmap)
                }

            }
        })
        uCrop.start(fragment!!.requireActivity(), fragment, requestCode)
    }

    private val sandboxPath: String
        private get() {
            val externalFilesDir: File = appContext.getExternalFilesDir("")!!
            val customFile = File(externalFilesDir.absolutePath, "Sandbox")
            if (!customFile.exists()) {
                customFile.mkdirs()
            }
            return customFile.absolutePath + File.separator
        }

    private fun buildOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setHideBottomControls(true)
        options.setFreeStyleCropEnabled(false)
        options.setShowCropFrame(false)
        options.setShowCropGrid(false)
        options.setCircleDimmedLayer(true)
        options.withAspectRatio(1f, 1f)
        options.setCropOutputPathDir(sandboxPath)
        options.isCropDragSmoothToCenter(true)
        options.isForbidSkipMultipleCrop(true)
        options.setMaxScaleMultiplier(100f)
        options.isDarkStatusBarBlack(true)
        options.setStatusBarColor(ContextCompat.getColor(appContext, com.shudong.lib_res.R.color.white))
        return options
    }


}