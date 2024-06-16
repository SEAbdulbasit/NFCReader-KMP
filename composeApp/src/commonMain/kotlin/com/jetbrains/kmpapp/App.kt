package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val nfcManager = getNFCManager()

        val trigger = remember { mutableStateOf(false) }
        val tag = MutableStateFlow<String>("")

        nfcManager.registerApp()

        scope.launch {
            nfcManager.tags.collectLatest {
                tag.emit(it)
            }
        }

        if (trigger.value) {
//            nfcManager.registerApp()
            trigger.value = false
        }

        Box {
            Button(onClick = {
//                trigger.value = true
            }) {
                Text("Read NFC Tag")
            }
        }

        if (tag.value.isNotEmpty()) {
            AlertDialog(onDismissRequest = {
                tag.value = ""
            }, title = {
                Text("NFC Tag")
            }, text = {
                tag.value.let {
                    if (it.isNotEmpty()) {
                        Column {
                            Text("NFC tag value is: ")
                            Text(it)
                        }
                    }
                }
            }, confirmButton = {
                Text("Ok")
            })
        }
    }
}
