# Instagram Like Carousel Indicators

출처 https://github.com/pushpalroy/JetDraggableIndicators

```kotlin
.pointerInput(Unit) {
		detectDragGesturesAfterLongPress(
				onDragStart = { .. },
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
				onDragEnd = { .. },
				onDragCancel = { .. },
		)
}
```

`PointerInputScope.detectDragGesturesAfterLongPress()`

```kotlin
suspend fun PointerInputScope.detectDragGesturesAfterLongPress(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitEachGesture {
        try {
            val down = awaitFirstDown(requireUnconsumed = false)
            val drag = awaitLongPressOrCancellation(down.id)
            if (drag != null) {
                onDragStart.invoke(drag.position)

                if (
                    drag(drag.id) {
                        onDrag(it, it.positionChange())
                        it.consume()
                    }
                ) {
                    // consume up if we quit drag gracefully with the up
                    currentEvent.changes.fastForEach {
                        if (it.changedToUp()) it.consume()
                    }
                    onDragEnd()
                } else {
                    onDragCancel()
                }
            }
        } catch (c: CancellationException) {
            onDragCancel()
            throw c
        }
    }
}
```

```kotlin
suspend fun AwaitPointerEventScope.drag(
    pointerId: PointerId,
    onDrag: (PointerInputChange) -> Unit
): Boolean {
    var pointer = pointerId
    while (true) {
        val change = awaitDragOrCancellation(pointer) ?: return false

        if (change.changedToUpIgnoreConsumed()) {
            return true
        }

        onDrag(change)
        pointer = change.id
    }
}
```