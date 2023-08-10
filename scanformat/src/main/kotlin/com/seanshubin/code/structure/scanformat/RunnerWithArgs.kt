package com.seanshubin.code.structure.scanformat

import com.seanshubin.code.structure.scanformat.Style.Companion.toStyle
import java.nio.file.Path
import java.nio.file.Paths

class RunnerWithArgs(
    private val args: Array<String>,
    private val binaryLoader: DependencyLoader,
    private val sourceLoader: DependencyLoader,
    private val createRunnable: (Path, Path, Path, DependencyLoader) -> Runnable
) : Runnable {
    override fun run() {
        val style = args.getOrNull(0)?.toStyle() ?: throw RuntimeException(styleErrorMessage())
        val generatedDirName =
            args.getOrNull(1) ?: throw RuntimeException("Second argument should be directory to generate files in")
        val binaryDirName =
            args.getOrNull(2) ?: throw RuntimeException("Third argument should be where the binary files are located")
        val sourceDirName =
            args.getOrNull(3) ?: throw RuntimeException("Fourth argument should be where the source files are located")
        val loader = when (style) {
            Style.SOURCE -> sourceLoader
            Style.BINARY -> binaryLoader
        }
        val runnable = createRunnable(
            Paths.get(generatedDirName),
            Paths.get(binaryDirName),
            Paths.get(sourceDirName),
            loader
        )
        runnable.run()
    }

    fun styleErrorMessage(): String {
        val stylesString = enumValues<Style>().joinToString(", ", "[", "]") {
            it.name
        }
        return "Invalid style, expected one of $stylesString"
    }
}
