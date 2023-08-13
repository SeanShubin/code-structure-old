package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.scanformat.FileContents
import com.seanshubin.code.structure.scanformat.FileParser
import java.nio.file.Path

class FileParserSourceClass(
    private val files: FilesContract
) : FileParser {
    override fun parseFile(baseDir: Path, file: Path): List<FileContents> {
        val text = files.readString(file)
        val packageName = parsePackage(file, text)
        val classNames = parseClassNames(file, text)
        val relativeFile = baseDir.relativize(file)
        val source = relativeFile.toString()
        val dependencies = emptyList<String>()
        val fileContentsList = classNames.map { className ->
            val name = "$packageName.$className"
            FileContents(name, source, dependencies)
        }
        return fileContentsList
    }

    private fun parsePackage(file: Path, text: String): String {
        val matches = findMatches(packageNameRegexList, text).filterNot {
            it == "object"
        }
        return matches.getOrNull(0) ?: "--root--"
    }

    private fun parseClassNames(file: Path, text: String): List<String> {
        val matches = findMatches(classNameRegexList, text)
        return matches
    }

    private fun findMatches(regexList: List<Regex>, text: String): List<String> =
        regexList.flatMap { regex ->
            regex.findAll(text).map { matchResult ->
                matchResult.groupValues[1]
            }
        }

    companion object {
        fun compileRegex(pattern: String): Regex = Regex(pattern, RegexOption.MULTILINE)
        val classNamePatternList = listOf(
            """^class (\w+)""",
            """^case class (\w+)""",
            """^object (\w+)""",
            """^trait (\w+)"""
        )
        val classNameRegexList = classNamePatternList.map(::compileRegex)
        val packageNamePatternList = listOf(
            """^package ([\.\w]+)"""
        )
        val packageNameRegexList = packageNamePatternList.map(::compileRegex)
    }
}
