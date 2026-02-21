package com.ultrahuman.findglasses.detection

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector

/**
 * CameraX [ImageAnalysis.Analyzer] that feeds each frame into a
 * MediaPipe [ObjectDetector] running in LIVE_STREAM mode.
 *
 * Results are delivered asynchronously through the listener registered
 * when the detector was created (see [GlassesDetector]).
 */
class GlassesAnalyzer(
    private val objectDetector: ObjectDetector
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val timestampMs = imageProxy.imageInfo.timestamp / 1_000 // microseconds -> milliseconds
            objectDetector.detectAsync(mpImage, timestampMs)
        } catch (e: Exception) {
            Log.e("GlassesAnalyzer", "Failed to run detection", e)
        } finally {
            imageProxy.close()
        }
    }
}
