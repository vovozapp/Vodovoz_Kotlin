package com.vodovoz.app.design_system.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope

@Suppress("NonSkippableComposable")
@Composable
fun LifecycleEffect(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    arg2: Any? = null,
    arg3: Any? = null,
    arg4: Any? = null,
    block: suspend CoroutineScope.() -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, arg2, arg3, arg4) {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}