package com.vodovoz.app.feature.document_viewer.composables

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


fun ZoomableViewState.onGestureStart(scope: CoroutineScope) {
    if (allowGestureInput) {
        eventChangeCount = 0
        velocityTracker = VelocityTracker()
        scope.launch {
            offsetX.stop()
            offsetY.stop()
            offsetX.updateBounds(null, null)
            offsetY.updateBounds(null, null)
        }
    }
}


fun ZoomableViewState.onGestureEnd(scope: CoroutineScope, transformOnly: Boolean) {
    scope.apply {
        if (!transformOnly || !allowGestureInput || isRunning()) return
        var velocity = try {
            velocityTracker.calculateVelocity()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val nextScale = when {
            scale.value < 1 -> 1F
            scale.value > maxScale -> {
                velocity = null
                maxScale
            }

            else -> null
        }
        launch {
            if (inBound(offsetX.value, boundX) && velocity != null) {
                val velocityX = if (velocity.x.isNaN()) 0F else velocity.x
                val vx = sameDirection(lastPan.x, velocityX)
                offsetX.updateBounds(boundX.first, boundX.second)
                offsetX.animateDecay(vx, decay)
            } else {
                val targetX = if (nextScale != maxScale) {
                    offsetX.value
                } else {
                    panTransformAndScale(
                        offset = offsetX.value,
                        center = centroid.x,
                        bh = containerWidth,
                        uh = displayWidth,
                        fromScale = scale.value,
                        toScale = nextScale
                    )
                }
                offsetX.animateTo(limitToBound(targetX, boundX))
            }
        }
        launch {
            if (inBound(offsetY.value, boundY) && velocity != null) {
                val velocityY = if (velocity.y.isNaN()) 0F else velocity.y
                val vy = sameDirection(lastPan.y, velocityY)
                offsetY.updateBounds(boundY.first, boundY.second)
                offsetY.animateDecay(vy, decay)
            } else {
                val targetY = if (nextScale != maxScale) {
                    offsetY.value
                } else {
                    panTransformAndScale(
                        offset = offsetY.value,
                        center = centroid.y,
                        bh = containerHeight,
                        uh = displayHeight,
                        fromScale = scale.value,
                        toScale = nextScale
                    )
                }
                offsetY.animateTo(limitToBound(targetY, boundY))
            }
        }
        launch {
            rotation.animateTo(0F)
        }
        nextScale?.let {
            launch {
                scale.animateTo(nextScale)
            }
        }
    }
}


fun ZoomableViewState.onGesture(
    scope: CoroutineScope,
    center: Offset,
    pan: Offset,
    zoom: Float,
    rotate: Float,
    event: PointerEvent
): Boolean {
    if (!allowGestureInput) return true
    if (eventChangeCount <= event.changes.size) {
        eventChangeCount = event.changes.size
    } else {
        return false
    }

    var checkRotate = rotate
    var checkZoom = zoom
    if (event.changes.size == 2) {
        val fingerDistanceOffset =
            event.changes[0].position - event.changes[1].position
        if (
            fingerDistanceOffset.x.absoluteValue < MIN_GESTURE_FINGER_DISTANCE
            && fingerDistanceOffset.y.absoluteValue < MIN_GESTURE_FINGER_DISTANCE
        ) {
            checkRotate = 0F
            checkZoom = 1F
        }
    }

    gestureCenter.value = center

    val currentOffsetX = offsetX.value
    val currentOffsetY = offsetY.value
    val currentScale = scale.value
    val currentRotation = rotation.value

    var nextScale = currentScale.times(checkZoom)
    if (nextScale < MIN_SCALE) nextScale = MIN_SCALE

    lastPan = pan
    centroid = center
    boundScale =
        if (nextScale > maxScale) maxScale else nextScale
    boundX =
        getBound(
            boundScale,
            containerWidth,
            displayWidth,
        )
    boundY =
        getBound(
            boundScale,
            containerHeight,
            displayHeight,
        )

    var nextOffsetX = panTransformAndScale(
        offset = currentOffsetX,
        center = center.x,
        bh = containerWidth,
        uh = displayWidth,
        fromScale = currentScale,
        toScale = nextScale
    ) + pan.x
    var nextOffsetY = panTransformAndScale(
        offset = currentOffsetY,
        center = center.y,
        bh = containerHeight,
        uh = displayHeight,
        fromScale = currentScale,
        toScale = nextScale
    ) + pan.y

    if (eventChangeCount == 1) {
        nextOffsetX = limitToBound(nextOffsetX, boundX)
        nextOffsetY = limitToBound(nextOffsetY, boundY)
    }

    val nextRotation = if (nextScale < 1) {
        currentRotation + checkRotate
    } else currentRotation

    velocityTracker.addPosition(
        event.changes[0].uptimeMillis,
        Offset(nextOffsetX, nextOffsetY),
    )

    if (!isRunning()) scope.launch {
        scale.snapTo(nextScale)
        offsetX.snapTo(nextOffsetX)
        offsetY.snapTo(nextOffsetY)
        rotation.snapTo(nextRotation)
    }

    val canConsumeX = reachSide(pan.x, nextOffsetX, boundX)
    val canConsumeY = reachSide(pan.y, nextOffsetY, boundY)
    val canConsume = if (pan.x.absoluteValue > pan.y.absoluteValue) {
        canConsumeX
    } else {
        canConsumeY
    }
    if (canConsume || scale.value < 1) {
        event.changes.fastForEach {
            if (it.positionChanged()) {
                it.consume()
            }
        }
    }
    return true
}

fun panTransformAndScale(
    offset: Float,
    center: Float,
    bh: Float,
    uh: Float,
    fromScale: Float,
    toScale: Float,
): Float {
    val srcH = uh * fromScale
    val desH = uh * toScale
    val gapH = (bh - uh) / 2

    val py = when {
        uh >= bh -> {
            val upy = (uh * fromScale - uh).div(2)
            (upy - offset + center) / (fromScale * uh)
        }

        srcH > bh || bh > uh -> {
            val upy = (srcH - uh).div(2)
            (upy - gapH - offset + center) / (fromScale * uh)
        }

        else -> {
            val upy = -(bh - srcH).div(2)
            (upy - offset + center) / (fromScale * uh)
        }
    }
    return when {
        uh >= bh -> {
            val upy = (uh * toScale - uh).div(2)
            upy + center - py * toScale * uh
        }

        desH > bh -> {
            val upy = (desH - uh).div(2)
            upy - gapH + center - py * toScale * uh
        }

        else -> {
            val upy = -(bh - desH).div(2)
            upy + center - py * desH
        }
    }
}