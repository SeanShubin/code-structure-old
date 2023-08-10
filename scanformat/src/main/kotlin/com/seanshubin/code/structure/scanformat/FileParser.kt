package com.seanshubin.code.structure.scanformat

import java.nio.file.Path

interface FileParser {
    fun parseFile(baseDir: Path, file: Path): FileContents?
}
