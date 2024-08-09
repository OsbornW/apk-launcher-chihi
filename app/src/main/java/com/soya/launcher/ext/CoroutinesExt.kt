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
    pbUpdate.setProgressText("等待安装")
    return installMutex.withLock {
        pbUpdate.setProgressText("安装中")
        silentInstall()
    }
}