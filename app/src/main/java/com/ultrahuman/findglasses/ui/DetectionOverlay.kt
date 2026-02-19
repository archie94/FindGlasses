package com.ultrahuman.findglasses.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.ultrahuman.findglasses.detection.DetectionResult

@Composable
fun DetectionOverlay(
    detections: List<DetectionResult>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (imageWidth == 0 || imageHeight == 0) return@Canvas

        // Scale factors: map from analysis image coords to canvas coords.
        // PreviewView uses FILL_CENTER which scales to fill and center-crops.
        val scaleX = size.width / imageWidth.toFloat()
        val scaleY = size.height / imageHeight.toFloat()
        val scale = maxOf(scaleX, scaleY)
        val offsetX = (size.width - imageWidth * scale) / 2f
        val offsetY = (size.height - imageHeight * scale) / 2f

        for (detection in detections) {
            val rect = detection.boundingBox

            val left = rect.left * scale + offsetX
            val top = rect.top * scale + offsetY
            val right = rect.right * scale + offsetX
            val bottom = rect.bottom * scale + offsetY

            val isFashionGood = detection.label.equals("Fashion good", ignoreCase = true)
            val boxColor = if (isFashionGood) Color.Green else Color.Yellow

            drawRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 4f)
            )

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = if (isFashionGood) {
                        android.graphics.Color.GREEN
                    } else {
                        android.graphics.Color.YELLOW
                    }
                    textSize = 40f
                    isAntiAlias = true
                    setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                }

                val labelText = buildString {
                    if (isFashionGood) {
                        append("Glasses?")
                    } else {
                        append(detection.label)
                    }
                    if (detection.confidence > 0f) {
                        append(" (${(detection.confidence * 100).toInt()}%)")
                    }
                }

                canvas.nativeCanvas.drawText(
                    labelText,
                    left,
                    (top - 10f).coerceAtLeast(40f),
                    paint
                )
            }
        }
    }
}
