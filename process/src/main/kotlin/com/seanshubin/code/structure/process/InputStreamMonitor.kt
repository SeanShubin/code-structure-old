package com.seanshubin.code.structure.process

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

class InputStreamMonitor(inputStream: InputStream) {
    private val byteArrayOutputStream = ByteArrayOutputStream()
    private val job: Job

    init {
        job = GlobalScope.launch {
            var ch = inputStream.read()
            while (ch != -1) {
                byteArrayOutputStream.write(ch)
                ch = inputStream.read()
            }
        }
    }

    fun linesUtf8(): List<String> {
        runBlocking {
            job.join()
        }
        val lines = String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8).split("\r\n", "\r", "\n")
        return if (lines.last().isEmpty()) {
            lines.take(lines.size - 1)
        } else {
            lines
        }
    }
}
