package com.vodovoz.app.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


fun <T1, T2, T3> combineIntoTriple(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>
): Flow<Triple<T1, T2, T3>> {
    return combine(flow1, flow2, flow3) { t1, t2, t3 ->
        Triple(t1, t2, t3)
    }
}