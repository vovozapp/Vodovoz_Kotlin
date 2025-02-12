package com.vodovoz.app.feature.document_viewer.composables

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch



const val DEFAULT_OFFSET_X = 0F

const val DEFAULT_OFFSET_Y = 0F

const val DEFAULT_SCALE = 1F

const val DEFAULT_ROTATION = 0F

const val MIN_SCALE = 0.5F

const val MAX_SCALE_RATE = 3F

const val MIN_GESTURE_FINGER_DISTANCE = 200

@Immutable
open class ZoomableViewState(
    @FloatRange(from = 1.0) val maxScale: Float = MAX_SCALE_RATE,
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    scale: Float = DEFAULT_SCALE,
    rotation: Float = DEFAULT_ROTATION,
    animationSpec: AnimationSpec<Float>? = null,
) : CoroutineScope by MainScope() {

    private var defaultAnimateSpec: AnimationSpec<Float> = animationSpec ?: SpringSpec()

    val offsetX = Animatable(offsetX)

    val offsetY = Animatable(offsetY)

    val scale = Animatable(scale)

    val rotation = Animatable(rotation)

    var allowGestureInput = true

    private val contentSizeState = mutableStateOf<Size?>(null)

    val isSpecified: Boolean
        get() {
            return contentSizeState.value?.isSpecified == true
        }

    var contentSize: Size
        set(value) {
            contentSizeState.value = value
        }
        get() {
            return if (contentSizeState.value?.isSpecified == true) {
                contentSizeState.value!!
            } else {
                Size.Zero
            }
        }

    var containerSize = mutableStateOf(Size.Zero)

    val containerWidth: Float
        get() = containerSize.value.width

    val containerHeight: Float
        get() = containerSize.value.height

    private val containerRatio: Float
        get() = containerSize.value.run {
            width.div(height)
        }

    private val contentRatio: Float
        get() = contentSize.run {
            width.div(height)
        }

    private val widthFixed: Boolean
        get() = contentRatio > containerRatio

    internal val scale1x: Float
        get() {
            return if (widthFixed) {
                containerSize.value.width.div(contentSize.width)
            } else {
                containerSize.value.height.div(contentSize.height)
            }
        }

    val displaySize: Size
        get() {
            return Size(displayWidth, displayHeight)
        }

    val displayWidth: Float
        get() {
            return contentSize.width.times(scale1x)
        }

    val displayHeight: Float
        get() {
            return contentSize.height.times(scale1x)
        }

    val realSize: Size
        get() {
            return Size(
                width = displayWidth.times(scale.value),
                height = displayHeight.times(scale.value)
            )
        }

    val gestureCenter = mutableStateOf(Offset.Zero)

    var velocityTracker = VelocityTracker()

    var lastPan = Offset.Zero

    val decay = FloatExponentialDecaySpec(2f).generateDecayAnimationSpec<Float>()

    var boundX = Pair(0F, 0F)
    var boundY = Pair(0F, 0F)

    var boundScale = 1F

    var eventChangeCount = 0

    var centroid = Offset.Zero

    fun isRunning(): Boolean {
        return scale.isRunning
                || offsetX.isRunning
                || offsetY.isRunning
                || rotation.isRunning
    }

    internal fun updateContainerSize(size: Size) {
        containerSize.value = size
    }

    suspend fun resetImmediately() {
        rotation.snapTo(DEFAULT_ROTATION)
        offsetX.snapTo(DEFAULT_OFFSET_X)
        offsetY.snapTo(DEFAULT_OFFSET_Y)
        scale.snapTo(DEFAULT_SCALE)
    }

    suspend fun fixToBound() {
        boundX = getBound(
            scale.value,
            containerWidth,
            displayWidth,
        )
        boundY = getBound(
            scale.value,
            containerHeight,
            displayHeight,
        )
        val limitX = limitToBound(offsetX.value, boundX)
        val limitY = limitToBound(offsetY.value, boundY)
        coroutineScope {
            launch {
                offsetX.animateTo(limitX)
            }
            launch {
                offsetY.animateTo(limitY)
            }
        }
    }


    suspend fun reset(animationSpec: AnimationSpec<Float> = defaultAnimateSpec) {
        coroutineScope {
            listOf(
                async {
                    rotation.animateTo(DEFAULT_ROTATION, animationSpec)
                },
                async {
                    offsetX.animateTo(DEFAULT_OFFSET_X, animationSpec)
                },
                async {
                    offsetY.animateTo(DEFAULT_OFFSET_Y, animationSpec)
                },
                async {
                    scale.animateTo(DEFAULT_SCALE, animationSpec)
                },
            ).awaitAll()
        }
    }


    private suspend fun scaleToMax(
        offset: Offset,
        animationSpec: AnimationSpec<Float>? = null
    ) {
        val currentAnimateSpec = animationSpec ?: defaultAnimateSpec

        var nextOffsetX = (containerWidth / 2 - offset.x) * maxScale
        var nextOffsetY = (containerHeight / 2 - offset.y) * maxScale

        val boundX = getBound(maxScale, containerWidth, displayWidth)
        val boundY = getBound(maxScale, containerHeight, displayHeight)

        nextOffsetX = limitToBound(nextOffsetX, boundX)
        nextOffsetY = limitToBound(nextOffsetY, boundY)

        // 启动
        coroutineScope {
            listOf(
                async {
                    offsetX.updateBounds(null, null)
                    offsetX.animateTo(nextOffsetX, currentAnimateSpec)
                    offsetX.updateBounds(boundX.first, boundX.second)
                },
                async {
                    offsetY.updateBounds(null, null)
                    offsetY.animateTo(nextOffsetY, currentAnimateSpec)
                    offsetY.updateBounds(boundY.first, boundY.second)
                },
                async {
                    scale.animateTo(maxScale, currentAnimateSpec)
                },
            ).awaitAll()
        }
    }

    suspend fun toggleScale(
        offset: Offset,
        animationSpec: AnimationSpec<Float> = defaultAnimateSpec
    ) {
        if (scale.value != 1F) {
            reset(animationSpec)
        } else {
            scaleToMax(offset, animationSpec)
        }
    }

    companion object {
        val SAVER: Saver<ZoomableViewState, *> = listSaver(save = {
            listOf(it.offsetX.value, it.offsetY.value, it.scale.value, it.rotation.value)
        }, restore = {
            ZoomableViewState(
                offsetX = it[0],
                offsetY = it[1],
                scale = it[2],
                rotation = it[3],
            )
        })
    }

}


@Composable
fun rememberZoomableState(
    contentSize: Size? = null,
    @FloatRange(from = 1.0) maxScale: Float = MAX_SCALE_RATE,
    animationSpec: AnimationSpec<Float>? = tween(300, easing = LinearEasing),
): ZoomableViewState {
    val scope = rememberCoroutineScope()
    return rememberSaveable(saver = ZoomableViewState.SAVER) {
        ZoomableViewState(
            maxScale = maxScale,
            animationSpec = animationSpec,
        )
    }.apply {
        contentSize?.let {
            this.contentSize = it
            scope.launch { fixToBound() }
        }
    }
}