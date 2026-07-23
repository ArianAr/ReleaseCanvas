package com.releasecanvas.app.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class SignatureStroke(
    val points: List<Offset>,
)

class SignaturePadState {
    var strokes by mutableStateOf<List<SignatureStroke>>(emptyList())
        private set
    var currentPoints by mutableStateOf<List<Offset>>(emptyList())
        private set
    var size by mutableStateOf(IntSize.Zero)

    val hasStrokes: Boolean
        get() = strokes.isNotEmpty() || currentPoints.size > 1

    fun appendPoint(point: Offset) {
        currentPoints = currentPoints + point
    }

    fun finishStroke() {
        if (currentPoints.size > 1) {
            strokes = strokes + SignatureStroke(currentPoints)
        }
        currentPoints = emptyList()
    }

    fun undo() {
        if (currentPoints.isNotEmpty()) {
            currentPoints = emptyList()
        } else if (strokes.isNotEmpty()) {
            strokes = strokes.dropLast(1)
        }
    }

    fun clear() {
        strokes = emptyList()
        currentPoints = emptyList()
    }

    fun toBitmap(strokeColor: Int = android.graphics.Color.BLACK): Bitmap? {
        if (!hasStrokes || size.width == 0 || size.height == 0) return null
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
            color = strokeColor
            style = AndroidPaint.Style.STROKE
            strokeWidth = 6f
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
        }
        val all = strokes + listOfNotNull(
            currentPoints.takeIf { it.size > 1 }?.let { SignatureStroke(it) },
        )
        all.forEach { stroke ->
            val path = android.graphics.Path()
            stroke.points.forEachIndexed { index, offset ->
                if (index == 0) path.moveTo(offset.x, offset.y) else path.lineTo(offset.x, offset.y)
            }
            canvas.drawPath(path, paint)
        }
        return bitmap
    }
}

@Composable
fun rememberSignaturePadState(): SignaturePadState = remember { SignaturePadState() }

@Composable
fun SignaturePad(
    state: SignaturePadState,
    modifier: Modifier = Modifier,
    onStrokeChange: (hasStrokes: Boolean, bitmap: Bitmap?) -> Unit = { _, _ -> },
) {
    val strokeColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outline
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 3.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { state.size = it }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            state.appendPoint(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            state.appendPoint(change.position)
                        },
                        onDragEnd = {
                            state.finishStroke()
                            onStrokeChange(state.hasStrokes, state.toBitmap())
                        },
                        onDragCancel = {
                            state.finishStroke()
                            onStrokeChange(state.hasStrokes, state.toBitmap())
                        },
                    )
                },
        ) {
            fun pathFrom(points: List<Offset>): Path {
                val path = Path()
                points.forEachIndexed { index, point ->
                    if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                }
                return path
            }

            val strokeStyle = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            )
            state.strokes.forEach { stroke ->
                drawPath(pathFrom(stroke.points), color = strokeColor, style = strokeStyle)
            }
            if (state.currentPoints.size > 1) {
                drawPath(pathFrom(state.currentPoints), color = strokeColor, style = strokeStyle)
            }
        }
    }
}

/** Notify host after clear/undo with updated bitmap. */
fun SignaturePadState.emitBitmap(onStrokeChange: (Boolean, Bitmap?) -> Unit) {
    onStrokeChange(hasStrokes, toBitmap())
}
