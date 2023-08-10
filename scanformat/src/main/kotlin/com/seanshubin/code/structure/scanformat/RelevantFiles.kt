package com.seanshubin.code.structure.scanformat

import java.nio.file.Path
import kotlin.io.path.extension

object RelevantFiles {
    val isTest: (Path) -> Boolean = { path ->
        path.map { it.toString().lowercase() }.any { it.contains("test") }
    }
    val isBinary: (Path) -> Boolean = { path ->
        val extension = path.extension
        val hasExtension = extension == "beam"
        val isTest = isTest(path)
        hasExtension && !isTest
    }
    val isSource: (Path) -> Boolean = { path ->
        val extension = path.extension
        (extension == "ex" || extension == "exs") && !isTest(path)
    }
}
