package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainNavigationScreen
import com.example.ui.theme.MultiLinkTheme
import com.example.ui.viewmodel.MultiLinkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiLinkTheme {
                val viewModel: MultiLinkViewModel = viewModel()
                MainNavigationScreen(viewModel = viewModel)
            }
        }
    }
}
