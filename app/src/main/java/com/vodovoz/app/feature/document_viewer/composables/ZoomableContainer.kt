package com.vodovoz.app.feature.document_viewer.composables

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue




@Composable
fun ZoomableContainer(
    modifier: Modifier = Modifier,
    boundClip: Boolean = true,
    state: ZoomableViewState,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    state.apply {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = boundClip
                }
                .pointerInput(state) {
                    detectTransformGestures(
                        onDoubleTap = {  scope.launch { state.resetImmediately() } },
                        gestureStart = {
                            onGestureStart(scope)
                        },
                        gestureEnd = { transformOnly ->
                            onGestureEnd(scope, transformOnly)
                        },
                        onGesture = { center, pan, zoom, rotate, event ->
                            onGesture(scope, center, pan, zoom, rotate, event)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            density.apply {
                updateContainerSize(
                    Size(
                        width = maxWidth.toPx(),
                        height = maxHeight.toPx(),
                    )
                )
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        transformOrigin = TransformOrigin.Center
                        scaleX = scale.value
                        scaleY = scale.value
                        translationX = offsetX.value
                        translationY = offsetY.value
                        rotationZ = rotation.value
                    }
                    .width(density.run { displayWidth.toDp() })
                    .height(density.run { displayHeight.toDp() })
            ) {
                content()
            }
        }
    }
}


internal fun reachSide(pan: Float, offset: Float, bound: Pair<Float, Float>): Boolean {
    val reachRightSide = offset <= bound.first
    val reachLeftSide = offset >= bound.second
    return !(reachLeftSide && pan > 0)
            && !(reachRightSide && pan < 0)
            && !(reachLeftSide && reachRightSide)
}


fun limitToBound(offset: Float, bound: Pair<Float, Float>): Float {
    return when {
        offset <= bound.first -> {
            bound.first
        }

        offset > bound.second -> {
            bound.second
        }

        else -> {
            offset
        }
    }
}


fun inBound(offset: Float, bound: Pair<Float, Float>): Boolean {
    return if (offset > 0) {
        offset < bound.second
    } else if (offset < 0) {
        offset > bound.first
    } else {
        true
    }
}


fun getBound(scale: Float, bw: Float, dw: Float): Pair<Float, Float> {
    val rw = scale.times(dw)
    val bound = if (rw > bw) {
        var xb = (rw - bw).div(2)
        if (xb < 0) xb = 0F
        xb
    } else {
        0F
    }
    return Pair(-bound, bound)
}


fun sameDirection(a: Float, b: Float): Float {
    return if (a > 0) {
        if (b < 0) {
            b.absoluteValue
        } else {
            b
        }
    } else {
        if (b > 0) {
            -b
        } else {
            b
        }
    }
}


suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    gestureStart: () -> Unit = {},
    gestureEnd: (Boolean) -> Unit = {},
    onTap: (Offset) -> Unit = {},
    onDoubleTap: (Offset) -> Unit = {},
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float, event: PointerEvent) -> Boolean,
) {
    var lastReleaseTime = 0L
    var scope: CoroutineScope? = null
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        awaitFirstDown(requireUnconsumed = false)
        val t0 = System.currentTimeMillis()
        var releasedEvent: PointerEvent? = null
        var moveCount = 0
        gestureStart()
        do {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Release) releasedEvent = event
            if (event.type == PointerEventType.Move) moveCount++
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled) {
                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }
                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    ) {
                        if (!onGesture(
                                centroid,
                                panChange,
                                zoomChange,
                                effectiveRotation,
                                event
                            )
                        ) break
                    }
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })

        var t1 = System.currentTimeMillis()
        val dt = t1 - t0
        val dlt = t1 - lastReleaseTime

        if (moveCount == 0) releasedEvent?.let { e ->
            if (e.changes.isEmpty()) return@let
            val offset = e.changes.first().position
            if (dlt < 272) {
                t1 = 0L
                scope?.cancel()
                onDoubleTap(offset)
            } else if (dt < 200) {
                scope = MainScope()
                scope?.launch(Dispatchers.Main) {
                    delay(272)
                    onTap(offset)
                }
            }
            lastReleaseTime = t1
        }

        gestureEnd(moveCount != 0)
    }
}