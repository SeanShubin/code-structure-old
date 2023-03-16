package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.process.ProcessInput
import com.seanshubin.code.structure.process.ProcessRunner
import java.nio.file.Path

class SvgGeneratorImpl(
    private val processRunner: ProcessRunner
) : SvgGenerator {
    override fun generate(directory: Path, dotFileName: String, svgFileName: String) {
        val command = listOf(
            "dot",
            "-Tsvg",
            "-o$svgFileName",
            dotFileName
        )
        val processInput = ProcessInput(command, directory)
        val processOutput = processRunner.run(processInput)
        if (processOutput.exitCode != 0) {
            val messageLines = listOf(
                "unable to generate report",
                directory.toString(),
                dotFileName,
                svgFileName
            ) + processOutput.toLines()
            val message = messageLines.joinToString("\n")
            throw RuntimeException(message)
        }
    }
}
