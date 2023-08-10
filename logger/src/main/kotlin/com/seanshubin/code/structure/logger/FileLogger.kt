package com.seanshubin.code.structure.logger

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileLogger(private val logDir: Path, private val files: FilesContract) : Logger {
    override fun emitLine(caption: String, line: String) {
        files.createDirectories(logDir)
        val fileName = "$caption.txt"
        val filePath = logDir.resolve(fileName)
        files.write(filePath, listOf(line), StandardOpenOption.APPEND, StandardOpenOption.CREATE)
    }
}
