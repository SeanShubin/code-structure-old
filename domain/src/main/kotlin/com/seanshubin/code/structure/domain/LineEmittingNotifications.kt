package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.scanformat.FileContents
import java.nio.file.Path

class LineEmittingNotifications(
    private val emitLine: (String, String) -> Unit
) : Notifications {
    override fun timeTaken(durationMilliseconds: Long) {
        val formattedDuration = DurationFormat.milliseconds.format(durationMilliseconds)
        emitLine("time-taken", formattedDuration)
    }

    override fun error(message: String) {
        emitLine("error", message)
    }

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

    var noRelevantClassesInPathCount = 0
    override fun warnNoRelevantClassesInPath(path: Path) {
        noRelevantClassesInPathCount++
        emitLine("no-relevant-classes-in-path", path.toString())
    }

    override fun summarize() {
        emitLine("summary", "relevant files: $relevantFileFoundCount")
        emitLine("summary", "wrong number of modules: $wrongNumberOfModuleMatchesCount")
        emitLine("summary", "unable to parse dependency line: $unableToParseDependencyLineCount")
        emitLine("summary", "no relevant classes in path: $noRelevantClassesInPathCount")
        emitLine("summary", "successfully parsed: $fileSuccessfullyParsedCount")
    }
}