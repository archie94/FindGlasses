package com.ultrahuman.findglasses.detection

import android.content.Context
import android.graphics.RectF
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

/**
 * Factory that creates a MediaPipe [ObjectDetector] configured for live-stream
 * (async) inference using the selected [DetectionModel].
 */
object GlassesDetector {

    private const val MAX_RESULTS = 5
    private const val SCORE_THRESHOLD = 0.5f

    fun create(
        context: Context,
        model: DetectionModel = DetectionModel.EFFICIENTDET_LITE0,
        onResult: (List<DetectionResult>, imageWidth: Int, imageHeight: Int) -> Unit
    ): ObjectDetector {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(model.assetFileName)
            .build()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
            .setMaxResults(MAX_RESULTS)
            .setScoreThreshold(SCORE_THRESHOLD)
            .setResultListener { result: ObjectDetectorResult, input: MPImage ->
                val detections = result.detections().map { detection ->
                    val category = detection.categories().firstOrNull()
                    val box = detection.boundingBox()
                    DetectionResult(
                        boundingBox = toRect(box),
                        label = category?.categoryName() ?: "Unknown",
                        confidence = category?.score() ?: 0f,
                        trackingId = null
                    )
                }
                onResult(detections, input.width, input.height)
            }
            .setErrorListener { e ->
                android.util.Log.e("GlassesDetector", "Detection error", e)
            }
            .build()

        return ObjectDetector.createFromOptions(context, options)
    }

    /** Converts a MediaPipe [RectF] bounding box to an Android [android.graphics.Rect]. */
    private fun toRect(box: RectF): android.graphics.Rect =
        android.graphics.Rect(
            box.left.toInt(),
            box.top.toInt(),
            box.right.toInt(),
            box.bottom.toInt()
        )
}
