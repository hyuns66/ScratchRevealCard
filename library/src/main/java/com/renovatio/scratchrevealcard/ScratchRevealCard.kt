package com.renovatio.scratchrevealcard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScratchRevealCard(
    modifier: Modifier = Modifier,
    brushSize: Float = with(LocalDensity.current) { 10.dp.toPx() },            // 긁어 내는 크기
    onScratchProgress: (Float) -> Unit = {}, // 0f~1f
    onFullyRevealed: () -> Unit = {}        // 80% 이상 긁으면 콜백
) {
    var point by remember { mutableStateOf(Offset.Zero) } // point 위치 추적을 위한 State
    val points = remember { mutableListOf<Offset>() } // 새로 그려지는 path 표시하기 위한 points State
    var path by remember { mutableStateOf(Path()) } // 새로 그려지고 있는 중인 획 State
    val paths = remember { mutableStateListOf<Path>() } // 다 그려진 획 리스트 State

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { start ->
                            point = start
                            points.add(start)
                            path.moveTo(start.x, start.y)
                        },
                        onDrag = { _, dragAmount ->
                            path = Path()
                            point += dragAmount
                            points.add(point)
                            // onDrag가 호출될 때마다 현재 그리는 획을 새로 보여줌
                            points.forEachIndexed { index, point ->
                                if (index == 0) {
                                    path.moveTo(point.x, point.y)
                                } else {
                                    path.lineTo(point.x, point.y)
                                }
                            }
                        },
                        onDragEnd = {
                            paths.add(path)
                            points.clear()
                        },
                    )
                }
        ) {
            // 이미 완성된 획들
            paths.forEach { path ->
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = brushSize, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            // 현재 그려지고 있는 획
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = brushSize, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}