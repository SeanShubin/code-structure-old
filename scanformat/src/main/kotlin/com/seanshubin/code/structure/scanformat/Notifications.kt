package com.seanshubin.code.structure.scanformat

import java.nio.file.Path

interface Notifications {
    fun relevantFileFound(file: Path)
    fun fileSuccessfullyParsed(file: Path, text: String, contents: FileContents)
    fun wrongNumberOfModuleMatches(file: Path, text: String, quantity: Int)
    fun unableToParseDependencyLine(file: Path, dependencyLine: String)
    fun summarize()
}
