package com.seanshubin.code.structure.scanformat

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class CollectRelevantFileVisitor(
    private val baseDir: Path,
    private val isRelevant: (Path) -> Boolean,
    private val relevantFileFound: (Path) -> Unit = {}
) : DoNothingFileVisitor() {
    private val mutableFilesFoundList = mutableListOf<Path>()
    val filesFoundList: List<Path> = mutableFilesFoundList

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val relativePath = baseDir.relativize(file)
        if (isRelevant(relativePath)) {
            mutableFilesFoundList.add(file)
            relevantFileFound(file)
        }
        return FileVisitResult.CONTINUE
    }

    fun findRelevantFiles(files: FilesContract, baseDir: Path): List<Path> {
        val visitor = CollectRelevantFileVisitor(baseDir, isRelevant, relevantFileFound)
        files.walkFileTree(baseDir, visitor)
        val filesFound = visitor.filesFoundList
        return filesFound
    }
}
