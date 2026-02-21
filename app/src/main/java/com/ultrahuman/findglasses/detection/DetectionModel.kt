package com.ultrahuman.findglasses.detection

/**
 * Available object detection models that can be loaded by MediaPipe.
 *
 * @param assetFileName TFLite model file bundled in assets/
 * @param displayName   Human-readable name shown in the UI
 */
enum class DetectionModel(
    val assetFileName: String,
    val displayName: String
) {
    EFFICIENTDET_LITE0(
        assetFileName = "efficientdet_lite0.tflite",
        displayName = "EfficientDet Lite0"
    ),
    EFFICIENTDET_LITE2(
        assetFileName = "efficientdet_lite2.tflite",
        displayName = "EfficientDet Lite2"
    ),
    SSD_MOBILENET_V2(
        assetFileName = "ssd_mobilenet_v2.tflite",
        displayName = "SSD MobileNet V2"
    )
}
