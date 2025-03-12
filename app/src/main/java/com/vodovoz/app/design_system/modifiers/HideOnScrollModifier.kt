import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 *
 *  scrollState
 *
 *  hideOnScroll(scrollState)
 *
 * */

//@OptIn(ExperimentalMaterial3Api::class)
//private class EnterAlwaysScrollBehavior(
//    override val state: TopAppBarState,
//    override val snapAnimationSpec: AnimationSpec<Float>?,
//    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
//    val canScroll: () -> Boolean = { true }
//)  {
//     val isPinned: Boolean = false
//     var nestedScrollConnection =
//        object : NestedScrollConnection {
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                if (!canScroll()) return Offset.Zero
//                val prevHeightOffset = state.heightOffset
//                state.heightOffset += available.y
//                return if (prevHeightOffset != state.heightOffset) {
//                    // We're in the middle of top app bar collapse or expand.
//                    // Consume only the scroll on the Y axis.
//                    available.copy(x = 0f)
//                } else {
//                    Offset.Zero
//                }
//            }
//
//            override fun onPostScroll(
//                consumed: Offset,
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                if (!canScroll()) return Offset.Zero
//                state.contentOffset += consumed.y
//                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
//                    if (consumed.y == 0f && available.y > 0f) {
//                        state.contentOffset = 0f
//                    }
//                }
//                state.heightOffset += consumed.y
//                return Offset.Zero
//            }
//        }
//}
