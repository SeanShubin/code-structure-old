package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class ClassScannerImpl(private val files: FilesContract) : ClassScanner {
    override fun loadBytes(path: Path): Iterable<List<Byte>> = listOf(files.readAllBytes(path).toList())
}
