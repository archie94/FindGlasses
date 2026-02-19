package com.ultrahuman.findglasses.ui

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ultrahuman.findglasses.detection.DetectionResult

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        var detections by remember { mutableStateOf<List<DetectionResult>>(emptyList()) }
        var imageWidth by remember { mutableIntStateOf(0) }
        var imageHeight by remember { mutableIntStateOf(0) }

        Box(modifier = modifier.fillMaxSize()) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onDetections = { results, width, height ->
                    detections = results
                    imageWidth = width
                    imageHeight = height
                }
            )

            DetectionOverlay(
                detections = detections,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        PermissionScreen(permissionState = cameraPermissionState)
    }
}
