package com.jetbrains.kmpapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val nfcManager = getNFCManager()

        val trigger = remember { mutableStateOf(false) }
        val showDialog = remember { mutableStateOf(false) }
        val tag = remember { mutableStateOf("") }

        scope.launch {
            nfcManager.tags.collectLatest { tagData ->
                println("Test: I have detected a tag  $tagData")
                tag.value = tagData
                showDialog.value = true
            }
        }

        if (trigger.value) {
            nfcManager.registerApp()
            trigger.value = false
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Button(onClick = {
                    trigger.value = true
                }) {
                    Text("Read NFC Tag")
                }
                Text("Once the button is clicked, bring the NFC tag near the device. If the content is not empty, a popup will appear with the tag value.")
            }
        }

        if (showDialog.value) {
            AlertDialog(onDismissRequest = {
                showDialog.value = false
            }, title = {
                Text("NFC Tag")
            }, text = {
                if (tag.value.isNotEmpty()) {
                    Column {
                        Text("NFC tag value is: ")
                        Text(tag.value)
                    }

                }
            }, confirmButton = {
                Text(text = "OK", modifier = Modifier.padding(vertical = 8.dp).clickable {
                    showDialog.value = false
                })
            })
        }
    }
}
