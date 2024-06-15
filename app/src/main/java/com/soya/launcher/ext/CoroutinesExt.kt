import kotlinx.coroutines.delay

suspend fun repeatWithDelay(times: Int, delayMillis: Long, action: suspend () -> Unit) {
    repeat(times) {
        action()
        delay(delayMillis)
    }
}