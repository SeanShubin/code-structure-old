package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.scanformat.FileContents
import java.nio.file.Path

interface Notifications {
    fun timeTaken(durationMilliseconds: Long)
    fun error(message: String)
    fun relevantFileFound(file: Path)
    fun fileSuccessfullyParsed(file: Path, text: String, contents: FileContents)
    fun wrongNumberOfModuleMatches(file: Path, text: String, quantity: Int)
    fun unableToParseDependencyLine(file: Path, dependencyLine: String)
    fun summarize()
}
