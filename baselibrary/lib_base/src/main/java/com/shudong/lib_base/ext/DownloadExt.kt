package com.shudong.lib_base.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import rxhttp.toDownloadFlow
import rxhttp.wrapper.param.RxHttp

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/17 16:50
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */
fun String.downloadApk(
    scope: CoroutineScope,
    packageName: String,
    downloadError: ((error: String) -> Unit)? = null,
    downloadComplete: ((str: String,destPath:String) -> Unit)? = null,
    process: (progress: Int) -> Unit
) =
    scope.launch {
        val destPath = "${appContext.filesDir.absolutePath}/${packageName}_update.apk"
        RxHttp.get(this@downloadApk)
            .toDownloadFlow(destPath) {
                //it为Progress对象
                process.invoke(it.progress)
            }.catch {
                downloadError?.invoke(it.message ?: "")
            }.collect {
                downloadComplete?.invoke(it,destPath)
            }
    }






/*suspend fun String.silentInstall(): Boolean {
    return suspendCancellableCoroutine { continuation ->
        val packageInstaller = appContext.packageManager.packageInstaller
        val apkFile = File(this)
        val inputStream = FileInputStream(apkFile)
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

        var session: PackageInstaller.Session? = null
        try {
            val sessionId = packageInstaller.createSession(params)
            session = packageInstaller.openSession(sessionId)

            val out = session.openWrite("app_install", 0, -1)
            val buffer = ByteArray(65536)
            var c: Int
            while (inputStream.read(buffer).also { c = it } != -1) {
                out.write(buffer, 0, c)
            }
            session.fsync(out)
            out.close()
            inputStream.close()

            val installCallback = object : PackageInstaller.SessionCallback() {
                override fun onCreated(sessionId: Int) {}

                override fun onFinished(sessionId: Int, success: Boolean) {
                    Log.d("SilentInstall", "Installation finished: $success")
                    if (continuation.isActive) {
                        continuation.resume(success)
                    } else {
                        Log.w("SilentInstall", "Continuation already resumed or cancelled")
                    }
                }

                override fun onActiveChanged(sessionId: Int, active: Boolean) {}
                override fun onBadgingChanged(sessionId: Int) {}
                override fun onProgressChanged(sessionId: Int, progress: Float) {}
            }

            val handler = Handler(Looper.getMainLooper())
            packageInstaller.registerSessionCallback(installCallback, handler)

            val intent = Intent("INSTALL_ACTION").apply {
                // 设置 intent 的 Action 和其他数据
            }
            val pendingIntent = PendingIntent.getBroadcast(appContext, sessionId, intent, PendingIntent.FLAG_IMMUTABLE)
            session.commit(pendingIntent.intentSender)

            continuation.invokeOnCancellation {
                Log.d("SilentInstall", "Installation cancelled")
                session.close()
            }

        } catch (e: Exception) {
            Log.e("SilentInstall", "Exception occurred: ${e.message}", e)
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            } else {
                Log.w("SilentInstall", "Continuation already resumed or cancelled")
            }
        } finally {
            session?.close()
        }
    }
}*/


fun String.downloadPic(
    scope: CoroutineScope,
    path: String,
    downloadError: ((error: String) -> Unit)? = null,
    downloadComplete: ((str: String,destPath:String) -> Unit)? = null,
) =
    scope.launch {

        RxHttp.get(this@downloadPic)
            .toDownloadFlow(path) {
                //it为Progress对象
               // process.invoke(it.progress)
            }.catch {
                "显示错误:${it.printStackTrace()}==${it.message}==${it.cause?.message}".e("zengyue1")
                downloadError?.invoke(it.message ?: "")
            }.collect {
                downloadComplete?.invoke(it,path)
            }
    }



