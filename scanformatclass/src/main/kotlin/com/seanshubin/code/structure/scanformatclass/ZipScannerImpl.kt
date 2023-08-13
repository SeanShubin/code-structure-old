package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path
import java.util.zip.ZipEntry

class ZipScannerImpl(
    private val files: FilesContract,
    private val isCompressed: (String) -> Boolean,
    private val acceptName: (String) -> Boolean,
    private val warnNoRelevantClassesInPath: (Path) -> Unit
) : ZipScanner {
    override fun loadBytes(path: Path): Iterable<List<Byte>> =
        files.newInputStream(path).use { inputStream ->
            val iterator = ZipContentsIterator(inputStream, path.toString(), isCompressed, ::acceptEntry)
            val bytesIterable = iterator.asSequence().filter(::zipContentsRelevant).map { it.bytes }.toList()
            if (bytesIterable.isEmpty()) {
                warnNoRelevantClassesInPath(path)
            }
            reifyBeforeStreamCloses(bytesIterable)
        }

    private fun zipContentsRelevant(zipContents: ZipContents): Boolean =
        FileTypes.isClass(zipContents.zipEntry.name)

    private fun reifyBeforeStreamCloses(x: Iterable<List<Byte>>): Iterable<List<Byte>> =
        x.toList()

    fun acceptEntry(path: List<String>, entry: ZipEntry): Boolean = acceptName(entry.name)
}
