package com.ultrahuman.findglasses.detection

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetector

class GlassesAnalyzer(
    private val objectDetector: ObjectDetector,
    private val onDetectionResult: (List<DetectionResult>, imageWidth: Int, imageHeight: Int) -> Unit
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        // Compute post-rotation dimensions for correct bounding box mapping
        val isRotated = rotationDegrees == 90 || rotationDegrees == 270
        val effectiveWidth = if (isRotated) imageProxy.height else imageProxy.width
        val effectiveHeight = if (isRotated) imageProxy.width else imageProxy.height

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                val results = detectedObjects.map { obj ->
                    val label = obj.labels.firstOrNull()
                    DetectionResult(
                        boundingBox = obj.boundingBox,
                        label = label?.text ?: "Unknown",
                        confidence = label?.confidence ?: 0f,
                        trackingId = obj.trackingId
                    )
                }
                onDetectionResult(results, effectiveWidth, effectiveHeight)
            }
            .addOnFailureListener { e ->
                Log.e("GlassesAnalyzer", "Detection failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
