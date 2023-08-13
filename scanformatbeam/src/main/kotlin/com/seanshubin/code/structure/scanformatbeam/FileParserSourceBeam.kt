package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.scanformat.DependencyLineParser
import com.seanshubin.code.structure.scanformat.FileContents
import com.seanshubin.code.structure.scanformat.FileParser
import com.seanshubin.code.structure.scanformat.RegexPatterns
import java.nio.file.Path

class FileParserSourceBeam(
    private val files: FilesContract,
    private val dependencyLineParser: DependencyLineParser,
    private val fileSuccessfullyParsed: (Path, String, FileContents) -> Unit,
    private val wrongNumberOfModuleMatches: (Path, String, Int) -> Unit,
    private val unableToParseDependencies: (Path, String) -> Unit
) : FileParser {
    override fun parseFile(baseDir: Path, file: Path): List<FileContents> {
        val contents = files.readString(file)
        return listOfNotNull(parseFileContents(baseDir, file, contents))
    }

    private fun parseFileContents(baseDir: Path, file: Path, text: String): FileContents? {
        val module = parseModule(file, text) ?: return null
        val relativeDir = baseDir.relativize(file)
        val source = relativeDir.toString()
        val dependencies = parseDependencies(file, text)
        val contents = FileContents(module, source, dependencies)
        fileSuccessfullyParsed(file, text, contents)
        return contents
    }

    private fun parseModule(file: Path, text: String): String? {
        val matchResults = RegexPatterns.module.findAll(text).toList()
        val size = matchResults.size
        return if (size != 1) {
            wrongNumberOfModuleMatches(file, text, size)
            null
        } else {
            val matchResult = matchResults[0]
            val module = matchResult.groupValues[1]
            return module
        }
    }

    private fun parseDependencies(file: Path, text: String): List<String> {
        val matchResults = RegexPatterns.dependencyLine.findAll(text)
        val dependencyLines = matchResults.toList().map { it.groupValues[1].trim() }
        val result = dependencyLines.flatMap { parseDependencyLine(file, it) }
        return result
    }

    private fun parseDependencyLine(file: Path, dependencyLine: String): List<String> {
        val dependencies = dependencyLineParser.parseDependencies(dependencyLine)
        return if (dependencies == null) {
            unableToParseDependencies(file, dependencyLine)
            emptyList()
        } else {
            dependencies
        }
    }
}
