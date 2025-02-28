package com.vodovoz.app.util.extensions

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

fun <T> Flow<T>.debounceWithMax(
    debounceMillis: Long,
    maxCount: Int = 10,
): Flow<T> = channelFlow {
    var isFirstValue = true
    var lastValue: T? = null
    var count = 0
    var debounceJob: Job? = null

    collect { value ->
        lastValue = value
        count++

        if (count >= maxCount) {
            debugLog { "debounce with max: $count" }
            debounceJob?.cancel()
            send(lastValue!!)
            count = 0
            lastValue = null
        } else {
            debounceJob?.cancel()
            debounceJob = launch {
                if (!isFirstValue) delay(debounceMillis)
                else isFirstValue = false

                lastValue?.let { value ->
                    send(value)
                    count = 0
                    lastValue = null
                }
            }
        }
    }

    awaitClose { debounceJob?.cancel() }
}
