
import android.content.Context
import com.blankj.utilcode.util.CacheDiskStaticUtils.getString
import com.soya.launcher.R
import com.soya.launcher.ext.silentInstall
import com.soya.launcher.view.FlikerProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend fun repeatWithDelay(times: Int, delayMillis: Long, action: suspend () -> Unit) {
    repeat(times) {
        action()
        delay(delayMillis)
    }
}

private val installMutex = Mutex()
suspend fun String.silentInstallWithMutex(pbUpdate: FlikerProgressBar): Boolean {
    val context: Context = pbUpdate.context
    // pbUpdate.setProgressText("等待安装")
       pbUpdate.setProgressText(context.getString(R.string.The_author_of_The_New_York_Times))
     return installMutex.withLock {
        //pbUpdate.setProgressText("安装中")
        pbUpdate.setProgressText(context.getString(R.string.The_author))
        silentInstall()
    }
}