package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.scanformat.FileContents
import com.seanshubin.code.structure.scanformat.FileParser
import java.nio.file.Path

class FileParserBinaryClass(
    private val fileScanner: FileScanner,
    private val classBytesScanner: ClassBytesScanner
) : FileParser {
    override fun parseFile(baseDir: Path, file: Path): List<FileContents> {
        val bytesList = fileScanner.loadBytes(file)
        val dependenciesList = bytesList.map { classBytesScanner.parseDependencies(it) }
        val fileContentsList = dependenciesList.map { (name, dependencies) ->
            FileContents(name, file.toString(), dependencies)
        }
        return fileContentsList
    }
}
