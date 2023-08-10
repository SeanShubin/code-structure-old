package com.seanshubin.code.structure.scanformat

import java.nio.file.Path

interface FileListLoader {
    val dir: Path
    fun loadFileList(): List<Path>
}
