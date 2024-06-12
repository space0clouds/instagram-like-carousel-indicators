package clouds.space.carousel.indicators

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun InstagramLikeCarouselIndicators(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onPageSelected: (Int) -> Unit,
) {
    var enableDrag by remember {
        mutableStateOf(false)
    }

    val lazyListState = rememberLazyListState()

    val hapticFeedback = LocalHapticFeedback.current
    val density = LocalDensity.current

    val pageCount = pagerState.pageCount

    val threshold = remember {
        with(density) {
            ((80.dp / (pageCount.coerceAtLeast(1))) + 10.dp).toPx()
        }
    }

    val accumulatedDragAmount = remember {
        mutableFloatStateOf(0F)
    }

    val currentPage = pagerState.currentPage

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentPage) {
        coroutineScope.launch {
            lazyListState.animateScrollToItem(currentPage)
        }
    }

    Box(
        modifier = modifier.background(
            color = if (enableDrag) {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1F)
            } else {
                Color.Transparent
            },
            shape = RoundedCornerShape(percent = 50)
        ),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier
                .padding(8.dp)
                .widthIn(max = 100.dp)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                            accumulatedDragAmount.floatValue = 0F

                            enableDrag = true
                        },
                        onDrag = { change, dragAmount ->
                            if (enableDrag) {
                                change.consume()

                                accumulatedDragAmount.floatValue += dragAmount.x

                                if (abs(accumulatedDragAmount.floatValue) >= threshold) {
                                    val destinationPage = if (accumulatedDragAmount.floatValue < 0) {
                                        pagerState.currentPage + 1
                                    } else {
                                        pagerState.currentPage - 1
                                    }.coerceIn(0, pageCount - 1)

                                    if (destinationPage != currentPage) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                                        onPageSelected(destinationPage)
                                    }

                                    accumulatedDragAmount.floatValue = 0F
                                }
                            }
                        },
                        onDragEnd = {
                            enableDrag = false

                            accumulatedDragAmount.floatValue = 0F
                        },
                        onDragCancel = {
                            enableDrag = false

                            accumulatedDragAmount.floatValue = 0F
                        }
                    )
                },
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(pageCount) { index ->
                val scaleFactor = 1F - (0.1F * abs(index - currentPage)).coerceAtMost(0.4F)

                val color = if (index == currentPage) {
                    Color(0xFF03A9F4)
                } else {
                    Color.Gray
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                        }
                        .drawBehind {
                            drawCircle(color)
                        }
                )
            }
        }
    }
}