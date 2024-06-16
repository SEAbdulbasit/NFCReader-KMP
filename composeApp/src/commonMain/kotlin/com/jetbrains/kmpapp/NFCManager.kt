package com.jetbrains.kmpapp

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

expect class NFCManager {

    val tags: SharedFlow<String>

    @Composable
    fun registerApp()
}


@Composable
expect fun getNFCManager(): NFCManager