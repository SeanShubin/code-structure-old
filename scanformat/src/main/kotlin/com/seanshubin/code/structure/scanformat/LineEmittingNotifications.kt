package com.seanshubin.code.structure.scanformat

import java.nio.file.Path

class LineEmittingNotifications(
    private val emitLine: (String, String) -> Unit
) : Notifications {
    private var relevantFileFoundCount = 0
    override fun relevantFileFound(file: Path) {
        relevantFileFoundCount++
        emitLine("relevant-files", file.toString())
    }

    private var wrongNumberOfModuleMatchesCount = 0
    override fun wrongNumberOfModuleMatches(file: Path, contents: String, quantity: Int) {
        wrongNumberOfModuleMatchesCount++
        emitLine("module-matches", "$quantity in $file")
    }

    private var fileSuccessfullyParsedCount = 0
    override fun fileSuccessfullyParsed(file: Path, text: String, contents: FileContents) {
        fileSuccessfullyParsedCount++
        emitLine("parsed", "$file -> $contents")
    }

    var unableToParseDependencyLineCount = 0
    override fun unableToParseDependencyLine(file: Path, dependencyLine: String) {
        unableToParseDependencyLineCount++
        emitLine("bad-dependency-line", "$file: $dependencyLine")
    }

    override fun summarize() {
        emitLine("summary", "relevant files: $relevantFileFoundCount")
        emitLine("summary", "wrong number of modules: $wrongNumberOfModuleMatchesCount")
        emitLine("summary", "unable to parse dependency line: $unableToParseDependencyLineCount")
        emitLine("summary", "successfully parsed: $fileSuccessfullyParsedCount")
    }
}
