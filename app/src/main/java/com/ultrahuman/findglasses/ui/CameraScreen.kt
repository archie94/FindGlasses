package com.ultrahuman.findglasses.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ultrahuman.findglasses.detection.DetectionModel
import com.ultrahuman.findglasses.detection.DetectionResult

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        var selectedModel by remember { mutableStateOf(DetectionModel.EFFICIENTDET_LITE0) }
        var detections by remember { mutableStateOf<List<DetectionResult>>(emptyList()) }
        var imageWidth by remember { mutableIntStateOf(0) }
        var imageHeight by remember { mutableIntStateOf(0) }

        Box(modifier = modifier.fillMaxSize()) {
            // Recreate the camera pipeline when the model changes
            key(selectedModel) {
                CameraPreview(
                    model = selectedModel,
                    modifier = Modifier.fillMaxSize(),
                    onDetections = { results, width, height ->
                        detections = results
                        imageWidth = width
                        imageHeight = height
                    }
                )
            }

            DetectionOverlay(
                detections = detections,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                modifier = Modifier.fillMaxSize()
            )

            // Model selector chips at the bottom of the screen
            ModelSelector(
                selected = selectedModel,
                onModelSelected = { model ->
                    if (model != selectedModel) {
                        // Clear stale detections before switching
                        detections = emptyList()
                        selectedModel = model
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .systemBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    } else {
        PermissionScreen(permissionState = cameraPermissionState)
    }
}

@Composable
private fun ModelSelector(
    selected: DetectionModel,
    onModelSelected: (DetectionModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        DetectionModel.entries.forEach { model ->
            FilterChip(
                selected = model == selected,
                onClick = { onModelSelected(model) },
                label = { Text(text = model.displayName, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
