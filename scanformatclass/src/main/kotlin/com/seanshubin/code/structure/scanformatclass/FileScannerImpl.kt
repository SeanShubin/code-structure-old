package com.seanshubin.code.structure.scanformatclass

import java.nio.file.Path

class FileScannerImpl(
    private val zipScanner: ZipScanner,
    private val classScanner: ClassScanner
) : FileScanner {
    override fun loadBytes(path: Path): Iterable<List<Byte>> =
        if (FileTypes.isCompressed(path.toString())) {
            zipScanner.loadBytes(path)
        } else {
            classScanner.loadBytes(path)
        }
}
