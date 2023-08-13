package com.seanshubin.code.structure.scanformatclass

import java.nio.file.Path

interface ClassScanner {
    fun loadBytes(path: Path): Iterable<List<Byte>>
}
