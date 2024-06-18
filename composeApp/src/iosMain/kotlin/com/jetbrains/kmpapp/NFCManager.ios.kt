package com.jetbrains.kmpapp

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlinx.coroutines.CoroutineExceptionHandler


/**
 * Created by abdulbasit on 16/06/2024.
 */
actual class NFCManager() : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private val scope = CoroutineScope(SupervisorJob())
    private val _tagData = MutableSharedFlow<String>()

    actual val tags: SharedFlow<String> = _tagData

    @Composable
    actual fun registerApp() {
        if (NFCNDEFReaderSession.readingAvailable()) {
                val session = NFCNDEFReaderSession(this, null, false)
                session.alertMessage = "Hold your iPhone near the item to learn more about it."
                session.beginSession()
        }
    }

    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {
        val message = didDetectNDEFs.firstOrNull() as? NFCNDEFMessage
        val records = message?.records as List<NFCNDEFPayload>

        records.forEach {
            scope.launch {
                _tagData.emit(it.payload.toByteArray().decodeToString())
            }
        }
        session.invalidateSession()
    }

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        println("reader session error ${didInvalidateWithError.description}")
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val data = this
    val d = memScoped { data }
    return ByteArray(d.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), d.bytes, d.length)
        }
    }
}

@Composable
actual fun getNFCManager(): NFCManager {
    return NFCManager()
}