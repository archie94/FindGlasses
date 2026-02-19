package com.ultrahuman.findglasses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ultrahuman.findglasses.ui.CameraScreen
import com.ultrahuman.findglasses.ui.theme.FindGlassesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindGlassesTheme {
                CameraScreen()
            }
        }
    }
}
