package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.scanformat.CollectRelevantFileVisitor
import com.seanshubin.code.structure.scanformat.FileListLoader
import java.nio.file.Path

class RegexFileListLoader(
    override val dir: Path,
    private val files: FilesContract,
    private val includeRegexPatterns: List<String>,
    private val excludeRegexPatterns: List<String>
) : FileListLoader {
    override fun loadFileList(): List<Path> {
        val isRelevant = RegexPathFilter(includeRegexPatterns, excludeRegexPatterns)
        val visitor = CollectRelevantFileVisitor(dir, isRelevant)
        files.walkFileTree(dir, visitor)
        return visitor.filesFoundList
    }
}
