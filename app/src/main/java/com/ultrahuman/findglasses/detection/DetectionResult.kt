package com.ultrahuman.findglasses.detection

import android.graphics.Rect

data class DetectionResult(
    val boundingBox: Rect,
    val label: String,
    val confidence: Float,
    val trackingId: Int?
)
