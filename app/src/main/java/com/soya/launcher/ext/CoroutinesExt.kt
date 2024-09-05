
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

