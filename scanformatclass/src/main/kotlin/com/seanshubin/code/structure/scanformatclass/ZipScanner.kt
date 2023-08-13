package com.seanshubin.code.structure.scanformatclass

import java.nio.file.Path

interface ZipScanner {
    fun loadBytes(path: Path): Iterable<List<Byte>>
}
